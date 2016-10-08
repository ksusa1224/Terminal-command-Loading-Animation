package com.application.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.spi.AudioFileReader;

//import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cybozu.labs.langdetect.Detector; 
import com.cybozu.labs.langdetect.DetectorFactory; 
import com.cybozu.labs.langdetect.LangDetectException; 
import com.cybozu.*;
import com.application.controller.dao.KaitouDao;
import com.application.controller.dao.MondaiDao;
import com.application.controller.dao.QADao;
import com.application.controller.dao.QAPlusDao;
import com.application.controller.dao.QaTagRelationDao;
import com.application.controller.dao.SeitouDao;
import com.application.controller.dao.TagDao;
import com.application.model.LoginInfoModel;
import com.application.model.dao.KaitouModel;
import com.application.model.dao.MondaiModel;
import com.application.model.dao.QAModel;
import com.application.model.dao.QAPlusModel;
import com.application.model.dao.QaTagRelationModel;
import com.application.model.dao.SeitouModel;
import com.application.model.dao.TagModel;
import com.common.AES;
import com.common.Constant;
import com.common.Log;
import com.common.StopWatch;
import com.common.StringBuilderPlus;
import com.common.Util;
import com.dao.SQliteDAO;
import com.slime.SlimeSerif;
import com.sun.medialib.mlib.Constants;

import net.arnx.jsonic.JSON;

@Controller
public class MainPageController{

	/**
	 * メインページ（暗記ノート本体）
	 * 
	 * Spring MVC　@PathVariableを使ってURLに含まれる動的なパラメータを取得
	 * http://blog.codebook-10000.com/entry/20140301/1393628782
	 * @param user_id
	 * @return
	 */
	@RequestMapping(value={"/{owner_id}", 
						   "/{owner_id}/", 
						   "/{owner_id}/main.html", 
						   "/{owner_id}/main.htm", 
						   "/{owner_id}/main"},
							method=RequestMethod.GET)
	public String main(@PathVariable("owner_id") String owner_id,
						HttpServletRequest request, 
						HttpSession session,
						Model model) {
		Log log = new Log();
		log.insert_error_log("INFO", "main method start.");
		
		String request_url = request.getRequestURI();
		String response_url = "/"+ owner_id + "/main.html";
		   
		// TODO 認証されてるかどうかはsessionに入れると書き換えられてしまうから毎回DBに接続した方がいいかな
		Boolean is_authenticated = (Boolean)session.getAttribute("is_authenticated");
		String session_owner_id = (String)session.getAttribute("owner_id");
		
		log.insert_error_log("INFO", "session_owner_id:" + session_owner_id);
		
		/**
		 * アクセスログ記録
		 */
		String request_uri = request.getRequestURI();
		String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
		String client_ip = Log.getClientIpAddress(request);
		String client_os = Log.getClientOS(request);
		String client_browser = Log.getClientBrowser(request);

		log.insert_access_log(owner_id, request_uri, method_name, client_ip, client_os, client_browser);
		
		if(owner_id.equals(session_owner_id) && is_authenticated == true)		
		{
			if (request_url.equals(response_url))
			{
				byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
				AES aes = new AES();
				String owner_db = aes.decrypt(encrypted_owner_db);
				int limit = Constant.QA_NUM_PER_PAGE;
				int left_offset = 0;
				List<QAPlusModel> qa_plus_list_left = new ArrayList<QAPlusModel>();
				qa_plus_list_left = select_qa_plus(owner_db, limit, left_offset);
				String qa_html = "";
				if (qa_plus_list_left.size() > 0)
				{
					qa_html = generate_qa_html(qa_plus_list_left,owner_db);
				}

				String qa_html_right = "";
				int right_offset = Constant.QA_NUM_PER_PAGE;
				List<QAPlusModel> qa_plus_list_right= new ArrayList<QAPlusModel>();
				qa_plus_list_right = select_qa_plus(owner_db, limit, right_offset);
				if (qa_plus_list_right.size() > 0)
				{
					qa_html_right = generate_qa_html(qa_plus_list_right,owner_db);
				}
				model.addAttribute("qa_html", qa_html);
				model.addAttribute("qa_html_right", qa_html_right);
				
				//model.addAttribute("total_pages", qa_plus_map.size());
				
//				model.addAttribute("qa_html_per_pages", qa_html_per_pages);
				//model.addAttribute("page", 1);
				// 正答総数
				SeitouDao seitou_dao = new SeitouDao();
				int seitou_sum = seitou_dao.get_seitou_cnt(owner_db);
				model.addAttribute("seitou_sum", seitou_sum);
				
				// 正解総数
				int seikai_sum = seitou_dao.get_seikai_cnt(owner_db);
				model.addAttribute("seikai_sum", seikai_sum);
				
				// 付箋
				String husen_html = generate_husen_html(owner_db);
				model.addAttribute("tags", husen_html);
				
				// ページング総数
				QADao qa_dao = new QADao();
				int total_pages = qa_dao.get_pages(owner_db, "");
				model.addAttribute("total_pages", total_pages);
				
				return "main";
			}
			else
			{
				return "redirect:" + response_url;
			}
		}		
		else
		{
			return "error";
		}
	}

	/**
	 * 検索中の全QAを未正解の状態に戻す
	 * @param husen_names
	 * @param request
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(value={"/to_miseikai.html"},
			method=RequestMethod.GET)
	public @ResponseBody String to_miseikai(
			@RequestParam(value="husen_names", required=false) String husen_names,
			@RequestParam(value="now_page_left", required=false) String now_page_left,
			HttpServletRequest request, 
			HttpSession session) {

		// TODO 認証されてるかどうかはsessionに入れると書き換えられてしまうから毎回DBに接続した方がいいかな
		Boolean is_authenticated = (Boolean)session.getAttribute("is_authenticated");
		String owner_id = (String)session.getAttribute("owner_id");
		
		/**
		* アクセスログ記録
		*/
		String request_uri = request.getRequestURI();
		String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
		String client_ip = Log.getClientIpAddress(request);
		String client_os = Log.getClientOS(request);
		String client_browser = Log.getClientBrowser(request);
		Log log = new Log();
		log.insert_access_log(owner_id, request_uri, method_name, client_ip, client_os, client_browser);

		if(is_authenticated == true)		
		{
			byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
			AES aes = new AES();
			String owner_db = aes.decrypt(encrypted_owner_db);

//			int limit_mihiraki = Constant.QA_NUM_PER_PAGE * 2;
//			int offset = Integer.parseInt(now_page_left) * Constant.QA_NUM_PER_PAGE;
			
			KaitouDao kaitou_dao = new KaitouDao();
			List<KaitouModel> kaitou_list = new ArrayList<KaitouModel>();
			kaitou_list = kaitou_dao.select_kaitou_list_by_tag(owner_db, kaitou_list, husen_names);
			kaitou_dao.bulk_insert(owner_db, kaitou_list);
			
			SeitouDao seitou_dao = new SeitouDao();
			seitou_dao.to_huseikai_by_tag(owner_db, husen_names);
			
			// 再検索
			int limit = Constant.QA_NUM_PER_PAGE;
			int offset_left = 0;
			List<QAPlusModel> qa_list_left = new ArrayList<QAPlusModel>();
			qa_list_left = select_qa_plus_by_tag(owner_db, husen_names, limit, offset_left);
			String qa_html = "";
			if (qa_list_left.size() > 0)
			{
				qa_html = generate_qa_html(qa_list_left,owner_db);	
			}
			int offset_right = limit;
			List<QAPlusModel> qa_list_right = new ArrayList<QAPlusModel>();
			qa_list_right = select_qa_plus_by_tag(owner_db, husen_names, limit, offset_right);
			String qa_html_right = "";
			if (qa_list_right.size() > 0)
			{
				qa_html_right = generate_qa_html(qa_list_right,owner_db);	
			}
											
			// 付箋
			String husen_html = generate_husen_html(owner_db);
			
			// ページング総数
			QADao qa_dao = new QADao();
			String total_pages = String.valueOf(qa_dao.get_pages(owner_db, husen_names));			

			// 正答総数
			String seitou_cnt = String.valueOf(seitou_dao.get_seitou_cnt(owner_db, husen_names));

			// 正解総数
			String seikai_cnt = String.valueOf(seitou_dao.get_seikai_cnt(owner_db, husen_names));
			
			String json = JSON.encode(
					new String[] 
					{qa_html,qa_html_right,seitou_cnt,seikai_cnt,total_pages});
			return json;
		}		
		else
		{
			return "error";
		}		
	}

	
	/**
	 * タグで検索
	 * @param husen_names
	 * @param request
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(value={"/tag_search.html"},
				method=RequestMethod.GET)
	public @ResponseBody String tag_search(@RequestParam("husen_names") String husen_names,
				HttpServletRequest request, 
				HttpSession session,
				Model model) {
		
		// TODO 認証されてるかどうかはsessionに入れると書き換えられてしまうから毎回DBに接続した方がいいかな
		Boolean is_authenticated = (Boolean)session.getAttribute("is_authenticated");
		String owner_id = (String)session.getAttribute("owner_id");
		
		
		/**
		* アクセスログ記録
		*/
		String request_uri = request.getRequestURI();
		String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
		String client_ip = Log.getClientIpAddress(request);
		String client_os = Log.getClientOS(request);
		String client_browser = Log.getClientBrowser(request);
		Log log = new Log();
		log.insert_access_log(owner_id, request_uri, method_name, client_ip, client_os, client_browser);
		
		
		if(is_authenticated == true)		
		{
			byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
			AES aes = new AES();
			String owner_db = aes.decrypt(encrypted_owner_db);
			int limit = Constant.QA_NUM_PER_PAGE;
			int offset_left = 0;
			List<QAPlusModel> qa_list_left = new ArrayList<QAPlusModel>();
			qa_list_left = select_qa_plus_by_tag(owner_db, husen_names, limit, offset_left);
			String qa_html = "";
			if (qa_list_left.size() > 0)
			{
				qa_html = generate_qa_html(qa_list_left,owner_db);	
			}
			int offset_right = limit;
			List<QAPlusModel> qa_list_right = new ArrayList<QAPlusModel>();
			qa_list_right = select_qa_plus_by_tag(owner_db, husen_names, limit, offset_right);
			String qa_html_right = "";
			if (qa_list_right.size() > 0)
			{
				qa_html_right = generate_qa_html(qa_list_right,owner_db);	
			}
			
			model.addAttribute("qa_html", qa_html);
			
			// 正答総数
			SeitouDao seitou_dao = new SeitouDao();
			int seitou_sum = seitou_dao.get_seitou_cnt(owner_db);
			model.addAttribute("seitou_sum", seitou_sum);
			
			// 正解総数
			int seikai_sum = seitou_dao.get_seikai_cnt(owner_db);
			model.addAttribute("seikai_sum", seikai_sum);
			
			// 付箋
			String husen_html = generate_husen_html(owner_db);
			model.addAttribute("tags", husen_html);
			
			// ページング総数
			QADao qa_dao = new QADao();
			String total_pages = String.valueOf(qa_dao.get_pages(owner_db, husen_names));			

			String seitou_cnt = String.valueOf(seitou_dao.get_seitou_cnt(owner_db, husen_names));
			String seikai_cnt = String.valueOf(seitou_dao.get_seikai_cnt(owner_db, husen_names));
			
			String json = JSON.encode(
					new String[] 
					{qa_html,qa_html_right,seitou_cnt,seikai_cnt,total_pages});
			return json;
		}		
		else
		{
			return "error";
		}
	}
	
	
	/**
	 * 付箋ボードに付箋を表示するHTMLを生成
	 * @param owner_db
	 * @return
	 */
	public String generate_husen_html(String owner_db) {
		TagDao tag_dao = new TagDao();
		List<TagModel> tag_list = new ArrayList<TagModel>();
		String husen_html = "<div id='blank_husen' class='husen' contenteditable='true' onkeypress='javascript:husen_touroku(this);'></div>";
		tag_list = tag_dao.select_tag_list(owner_db, tag_list);
		for (TagModel tag : tag_list)
		{
			husen_html += ("<div class='husen'>" + tag.getTag_name() + "</div>");
		}
		return husen_html;
	}	    
	
	
	/**
	 * 問題登録
	 * Spring MVC　@PathVariableを使ってURLに含まれる動的なパラメータを取得
	 * http://blog.codebook-10000.com/entry/20140301/1393628782
	 * @param user_id
	 * @return
	 */
	@RequestMapping(value={"/{owner_id}", 
						   "/{owner_id}/", 
						   "/{owner_id}/main.html", 
						   "/{owner_id}/main.htm", 
						   "/{owner_id}/main"},
							method=RequestMethod.POST, 
							params={"register"})
	public String mondai_touroku(@PathVariable("owner_id") String owner_id,
						HttpServletRequest request, 
						HttpSession session,
						Model model,
						@RequestParam("qa_input_hidden") String qa_input,
						@RequestParam(value="qa_husen",required=false) String qa_husen,
						@RequestParam(value="qa_id", required=false) String qa_id,
						@RequestParam(value="yomudake_flg", required=false) String yomudake_flg,
						@RequestParam(value="reversible_flg", required=false) String reversible_flg) {
		
		/**
		 * アクセスログ記録
		 */
		String request_uri = request.getRequestURI();
		String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
		String client_ip = Log.getClientIpAddress(request);
		String client_os = Log.getClientOS(request);
		String client_browser = Log.getClientBrowser(request);
		Log log = new Log();
		log.insert_access_log(owner_id, request_uri, method_name, client_ip, client_os, client_browser);

		String request_url = request.getRequestURI();
		String response_url = "/"+ owner_id + "/main.html";
		model.addAttribute("owner_id", owner_id);
		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		AES aes = new AES();
		String owner_db = aes.decrypt(encrypted_owner_db);
		   
		// TODO 認証されてるかどうかはsessionに入れると書き換えられてしまうから毎回DBに接続した方がいいかな
		Boolean is_authenticated = (Boolean)session.getAttribute("is_authenticated");
		if (is_authenticated == false)
		{
			return "index";
		}
		String session_owner_id = (String)session.getAttribute("owner_id");
		
		if(owner_id.equals(session_owner_id) && is_authenticated == true)		
		{
			if (yomudake_flg == null)
			{
				yomudake_flg = "off";
			}
			if (reversible_flg == null)
			{
				reversible_flg = "off";
			}
			// 正答総数
			SeitouDao seitou_dao = new SeitouDao();
			int seitou_sum = seitou_dao.get_seitou_cnt(owner_db);
			model.addAttribute("seitou_sum", seitou_sum);
			
			// 正解総数
			int seikai_sum = seitou_dao.get_seikai_cnt(owner_db);
			model.addAttribute("seikai_sum", seikai_sum);
			
			if (qa_id == null || qa_id.equals(""))
			{
				create_qa(owner_id, owner_db, qa_input, qa_husen,yomudake_flg, reversible_flg);
			}
			else
			{
				edit_qa(owner_id, owner_db, qa_input, qa_husen,yomudake_flg, reversible_flg, qa_id);
			}
			
			// 付箋
			String husen_html = generate_husen_html(owner_db);
			model.addAttribute("tags", husen_html);

			int limit = Constant.QA_NUM_PER_PAGE;
			int left_offset = 0;
			List<QAPlusModel> qa_plus_list_left = new ArrayList<QAPlusModel>();
			qa_plus_list_left = select_qa_plus(owner_db, limit, left_offset);
			String qa_html = "";
			if (qa_plus_list_left.size() > 0)
			{
				qa_html = generate_qa_html(qa_plus_list_left,owner_db);
			}

			String qa_html_right = "";
			int right_offset = Constant.QA_NUM_PER_PAGE;
			List<QAPlusModel> qa_plus_list_right= new ArrayList<QAPlusModel>();
			qa_plus_list_right = select_qa_plus(owner_db, limit, right_offset);
			if (qa_plus_list_right.size() > 0)
			{
				qa_html_right = generate_qa_html(qa_plus_list_right,owner_db);
			}
			model.addAttribute("qa_html", qa_html);
			model.addAttribute("qa_html_right", qa_html_right);
			
			model.addAttribute("qa_html", qa_html);
			model.addAttribute("qa_html_right", qa_html_right);
			
//			model.addAttribute("total_pages", qa_plus_map.size());
			
			if (request_url.equals(response_url))
			{
				return "main";
			}
			else
			{
				return "redirect:" + response_url;
			}
		}		
		else
		{
			return "error";
		}
	}

	/**
	 * AjaxでQA登録、再検索
	 * 誤動作多い、かつ重いため現在未使用
	 * @param a_input
	 * @return
	 */
//	@RequestMapping(value={"/register_qa.html"}, method=RequestMethod.GET)
//	public @ResponseBody String ajax_reload(
//			HttpServletRequest request,
//			HttpSession session,
//			Model model,
//			@RequestParam("qa_input") String qa_input,
//			@RequestParam(value="yomudake_flg", required=false) String yomudake_flg,
//			@RequestParam(value="reversible_flg", required=false) String reversible_flg
//			) {
//
//		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
//		AES aes = new AES();
//		String owner_db = aes.decrypt(encrypted_owner_db);
//		   
//		// TODO 認証されてるかどうかはsessionに入れると書き換えられてしまうから毎回DBに接続した方がいいかな
//		Boolean is_authenticated = (Boolean)session.getAttribute("is_authenticated");
//		if (is_authenticated == false)
//		{
//			return "index";
//		}
//		String owner_id = (String)session.getAttribute("owner_id");
//		//System.out.println(owner_id);
//		
//		if (yomudake_flg == null)
//		{
//			yomudake_flg = "off";
//		}
//		if (reversible_flg == null)
//		{
//			reversible_flg = "off";
//		}
//		System.out.println(yomudake_flg);
//		System.out.println(reversible_flg);
//			
//		create_qa(owner_id, owner_db, qa_input, yomudake_flg, reversible_flg);
//		
//		String qa_html = generate_qa_html(select_qa_plus(owner_db),owner_db);			
//
//		// 正答総数
//		SeitouDao seitou_dao = new SeitouDao();
//		int seitou_sum = seitou_dao.get_seitou_cnt(owner_db);
//		model.addAttribute("seitou_sum", seitou_sum);
//		
//		// 正解総数
//		int seikai_sum = seitou_dao.get_seikai_cnt(owner_db);
//		model.addAttribute("seikai_sum", seikai_sum);
//		
////		JSONObject obj = new JSONObject();
////		obj.put("qa_html", qa_html);
////		obj.put("seitou_sum", seitou_sum);
//		
//		/**
//		 * アクセスログ記録
//		 */
//		String request_uri = request.getRequestURI();
//		String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//		String client_ip = Log.getClientIpAddress(request);
//		String client_os = Log.getClientOS(request);
//		String client_browser = Log.getClientBrowser(request);
//		Log log = new Log();
//		log.insert_access_log(owner_id, request_uri, method_name, client_ip, client_os, client_browser);
//		
//		return qa_html;
//	}	

	/**
	 * 正答の色を変更する（白⇒赤、赤⇒白）
	 * @param session
	 * @param now_opacity
	 * @return
	 */
	@RequestMapping(value={"/change_seitou_color.html"}, method=RequestMethod.GET)
	public @ResponseBody String ajax_change_seitou_color(
			HttpSession session,
			@RequestParam("qa_id") String qa_id,
			@RequestParam("s_id") String s_id,
			@RequestParam("is_seikai_now") int is_seikai_now) {

		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		AES aes = new AES();
		String owner_db = aes.decrypt(encrypted_owner_db);

		String owner_id = (String)session.getAttribute("owner_id");
		
		KaitouModel kaitou = new KaitouModel();

		KaitouDao kaitou_dao = new KaitouDao();
		
		// 現在の正解状況（色）
		SeitouDao seitou_dao = new SeitouDao();
		int is_seikai = seitou_dao.is_seikai(owner_db, s_id);

		// 行番号・K_ID生成用
		int k_max_no = kaitou_dao.get_kaitou_max_row_no(owner_db);

		// 行番号
		kaitou.setRow_no(k_max_no + 1);
		// 回答ID
		String k_id = kaitou.generate_k_id(k_max_no + 1, owner_id);
		kaitou.setK_id(k_id);
		// QA ID
		kaitou.setQa_id(qa_id);
		// 正答ID
		kaitou.setS_id(s_id);
		// 正解フラグ
		if (is_seikai == 0)
		{
			seitou_dao.update_seikai_flg(owner_db, s_id, 1);
			kaitou.setSeikai_flg(1);
		}
		else
		{
			seitou_dao.update_seikai_flg(owner_db, s_id, 0);
			kaitou.setSeikai_flg(0);			
		}
		// アクション・・・チェックを入れた、外した、解いて正解した、解いて不正解、等
		if (is_seikai == 0)
		{
			kaitou.setAction(Constant.ACTION_CHANGE_RED_CLICK);
		}
		else
		{
			kaitou.setAction(Constant.ACTION_CHANGE_WHITE_CLICK);			
		}
		// アクション日時（H2DBのtimestampと同じフォーマットにする）
		kaitou.setAction_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
		// ユーザーが入力した回答
		kaitou.setKaitou("");
		// 言語
		kaitou.setLanguage("");
		// 削除フラグ
		kaitou.setDel_flg(0);
	    // 作成者
		kaitou.setCreate_owner(owner_id);
	    // 更新者
		kaitou.setUpdate_owner(owner_id);
	    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		kaitou.setCreate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
	    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		kaitou.setUpdate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
		
		kaitou_dao.insert_kaitou(owner_db, kaitou);
		
		//is_seikai = kaitou_dao.is_seikai(owner_db, s_id);
		
		if (kaitou.getSeikai_flg() == 0)
		{
			return "1";
		}
		else
		{
			return "0";
		}
	}	

	/**
	 * Ajaxで正答総数を取得（TODO 不具合あり）
	 * @param a_input
	 * @return
	 */
	@RequestMapping(value={"/seitou_sum.html"}, method=RequestMethod.GET)
	public @ResponseBody int ajax_get_seitou_sum(HttpSession session) {

		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		AES aes = new AES();
		String owner_db = aes.decrypt(encrypted_owner_db);
		   
		// 正答総数
		SeitouDao seitou_dao = new SeitouDao();
		int seitou_sum = seitou_dao.get_seitou_cnt(owner_db);
		
		return seitou_sum;
	}	
	
	/**
	 * 付箋登録
	 * @param tag_name
	 * @return
	 */
	@RequestMapping(value={"/tag_touroku.html"}, method=RequestMethod.GET)
	public @ResponseBody String tag_touroku(
			HttpSession session,
			@RequestParam(value = "tag_name", required=false) String tag_name) {

		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		AES aes = new AES();
		String owner_db = aes.decrypt(encrypted_owner_db);

		String owner_id = (String)session.getAttribute("owner_id");
		
		return insert_tag(tag_name, owner_db, owner_id);
	}


	/**
	 * タグを新規作成
	 * @param tag_name
	 * @param owner_db
	 * @param owner_id
	 * @return
	 */
	public String insert_tag(String tag_name, String owner_db, String owner_id) {
		if (tag_name != null)
		{
			if (!tag_name.equals(""))
			{
				TagDao tag_dao = new TagDao();
				
				// すでに存在するタグ名の場合、deplicateを返却
				if (tag_dao.is_exist(owner_db, tag_name))
				{
					return "deplicate";
				}
				
				TagModel tag = new TagModel();
			    // 行番号
				tag.setRow_no(tag_dao.get_max_row_no(owner_db)+1);
			    // タグID
				tag.setTag_id(tag.generate_tag_id(tag.getRow_no() + 1, owner_id));
			    // タグ名
				tag.setTag_name(tag_name);
			    // 表示順
//				tag.setJunban(junban);
			    // 表示フラグ
				tag.setDisplay_flg(1);
			    // 重要度（５段階）
				tag.setJuyoudo(3);
			    // 難易度（５段階）
				tag.setNanido(3);
			    // システムタグフラグ
				tag.setSystem_tag_flg(0);
			    // タグ種別
//				tag.setTag_type(tag_type);
			    // デザイン種別
				tag.setDesign_type(0);
			    // 公開範囲
				tag.setKoukai_level(Constant.KOUKAI_LEVEL_SELF_ONLY);
			    // 言語
				tag.setLanguage(Util.check_japanese_or_english(tag_name));
			    // 削除フラグ
				tag.setDel_flg(0);
			    // 作成者
				tag.setCreate_owner(owner_id);
			    // 更新者
				tag.setUpdate_owner(owner_id);
			    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
				tag.setCreate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
			    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
				tag.setUpdate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
				
				tag_dao.insert_tag(owner_db, tag);
			}
		}
		return tag_name;
	}		

	/**
	 * すらスラ〜のセリフAjaxページ
	 * @return
	 */
	@RequestMapping(value={"/serif.html"}, method=RequestMethod.GET)
	public @ResponseBody String serif(
			@RequestParam(value="args_num", required=false) String args_num,
			@RequestParam(value = "a", required=false) String a_input) {
		SlimeSerif slime_serif = new SlimeSerif();
		String serif = "";
		if (args_num != null)
		{
			serif = slime_serif.RamdomSerifArg0();
		}
		else
		{
			serif = slime_serif.RamdomSerifArg1(a_input);
		}
		return serif;
	}		

	/**
	 * ページング
	 * @param now_page
	 * @param next_or_prev
	 * @param husen_str
	 * @return
	 */
	@RequestMapping(value={"/paging.html"}, method=RequestMethod.GET)
	public @ResponseBody String paging(
			HttpSession session,
			Model model,
			@RequestParam(value="now_page") int now_page,
			@RequestParam(value = "next_or_prev") String next_or_prev,
			@RequestParam(value = "husen_str", required=false) String husen_str) {

		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		AES aes = new AES();
		String owner_db = aes.decrypt(encrypted_owner_db);
		int limit = Constant.QA_NUM_PER_PAGE;
		int offset_left = 0;
		int offset_right = 0;
		
		int left_page = 0;
		int right_page = 0;
		if (next_or_prev.equals("next"))
		{
			left_page = now_page + 2;
			right_page = now_page + 3;
		}
		else
		{
			left_page = now_page - 2;
			right_page = now_page - 1;
		}
		offset_left = left_page * Constant.QA_NUM_PER_PAGE - Constant.QA_NUM_PER_PAGE;
		offset_right = right_page * Constant.QA_NUM_PER_PAGE - Constant.QA_NUM_PER_PAGE;

		String left_page_html = "";
		List<QAPlusModel> qa_plus_list_left = new ArrayList<QAPlusModel>();
		qa_plus_list_left = select_qa_plus_by_tag(owner_db, husen_str, limit, offset_left);
		if (qa_plus_list_left.size() > 0)
		{
			left_page_html = generate_qa_html(qa_plus_list_left,owner_db);	
		}
		String right_page_html = "";
		List<QAPlusModel> qa_plus_list_right = new ArrayList<QAPlusModel>();
		qa_plus_list_right = select_qa_plus_by_tag(owner_db,husen_str,limit,offset_right);
		if (qa_plus_list_right.size() > 0)
		{
			right_page_html = generate_qa_html(qa_plus_list_right,owner_db);	
		}

		// ページング総数
		QADao qa_dao = new QADao();
		String total_pages = String.valueOf(qa_dao.get_pages(owner_db, husen_str));			
		
		//model.addAttribute("qa_html", left_page_html);
		SeitouDao seitou_dao = new SeitouDao();
		String seitou_cnt = String.valueOf(seitou_dao.get_seitou_cnt(owner_db, husen_str));
		String seikai_cnt = String.valueOf(seitou_dao.get_seikai_cnt(owner_db, husen_str));
		//		Map<Integer, String> qa_html_per_pages = new HashMap<Integer, String>();
//		qa_plus_map.get(i),owner_db);
		String json = JSON.encode(
				new String[] 
				{left_page_html, right_page_html, seitou_cnt, seikai_cnt,total_pages});
		return json;
	}		
	
	/**
	 * 
	 * @param session
	 * @param qa_id
	 * @return
	 */
	@RequestMapping(value={"/edit_qa.html"}, method=RequestMethod.GET)
	public @ResponseBody String edit_qa(HttpSession session,
			@RequestParam(value="qa_id", required=false) String qa_id) {
		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		AES aes = new AES();
		String owner_db = aes.decrypt(encrypted_owner_db);

		QADao qa_dao = new QADao();
		String qa_html = qa_dao.get_qa_html(owner_db, qa_id);
		return qa_html;
	}		
	
	
	/**
	 * 
	 * @param owner_db
	 * @return
	 */
	public List<QAPlusModel> select_qa_plus(String owner_db, int limit, int offset) {
		QAPlusDao qa_plus_dao = new QAPlusDao();
		List<QAPlusModel> qa_plus_list = new ArrayList<QAPlusModel>();
		qa_plus_list = qa_plus_dao.select_qa_plus_map(owner_db, qa_plus_list, limit, offset);
		return qa_plus_list;
	}

	/**
	 * 
	 * @param owner_db
	 * @param husen_name
	 * @return
	 */
	public List<QAPlusModel> select_qa_plus_by_tag(String owner_db, String husen_names, int limit, int offset) {
		QAPlusDao qa_plus_dao = new QAPlusDao();
		List<QAPlusModel> qa_plus_list = new ArrayList<QAPlusModel>();
		qa_plus_list = qa_plus_dao.select_qa_plus_list(owner_db, qa_plus_list, husen_names, limit , offset);
		return qa_plus_list;
	}	
	
	/**
	 * 
	 * @return
	 */
	public String generate_qa_html(List<QAPlusModel> qa_plus_list, String owner_db)
	{
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();

		StringBuffer qa_html = new StringBuffer();

		//System.out.println(qa_plus_list.size());
		
		for (QAPlusModel qa_plus : qa_plus_list)
		{
//			QAModel qa = qa_plus.getQa();
			List<MondaiModel> mondai_list = new ArrayList<MondaiModel>();
			mondai_list = qa_plus.getMondai_list();
			
	//		System.out.println("mondai_list:"+mondai_list.size());
			
			/**
			 * 問題HTML
			 */
			List<String> q_html = new ArrayList<String>();
			for (int i = 0; i < mondai_list.size(); i++)
			{
				String mondai = mondai_list.get(i).getQ_parts_text();
				String q_lang = mondai_list.get(i).getLanguage();
				String html = "<span id='" + mondai_list.get(i).getQ_id() + "' class='q' onclick='edit_qa(this);'>" + mondai + "</span>";			
				q_html.add(html);
			}
			
			/**
			 * 正答HTML
			 */
			List<SeitouModel> seitou_list = new ArrayList<SeitouModel>();
			seitou_list = qa_plus.getSeitou_list();
			
			//System.out.println("seitou_list:"+seitou_list.size());

			List<String> a_html = new ArrayList<String>();
			for (int i = 0; i < seitou_list.size(); i++)
			{
				String seitou = seitou_list.get(i).getSeitou();
//				KaitouDao kaitou_dao = new KaitouDao();
//				int opacity = kaitou_dao.is_seikai(owner_db, seitou_list.get(i).getS_id());
				int opacity = seitou_list.get(i).getSeikai_flg();
				String mouseout = "";
				if (opacity == 0)
				{
					mouseout = "onmouseout='this.style.opacity=0'";
				}
				String html = "<span id='" + seitou_list.get(i).getS_id() + "' class='a' style='opacity:" + opacity + "' onmouseover='this.style.opacity=1' " + mouseout + " onclick='change_seitou_color(this)'>" + seitou + "</span>";				
				a_html.add(html);
			}	
			
			qa_html.append("<span id='" + qa_plus.getQa().getQa_id() + "' class='qa' onmouseover='qa_mouseover(this)'>");
		//	StopWatch watch2 = new StopWatch();
		//	System.out.println("mondai_list.size() + seitou_list.size()" + mondai_list.size() + seitou_list.size());
			for (int i = 0; i < (mondai_list.size() + seitou_list.size()); i++)
			{
			//	System.out.println("i:"+i);
				if (qa_plus.getQa().getIs_start_with_q() == 1)
				{
					if (i < q_html.size())
					{
//						qa_html += q_html.get(i);
						qa_html.append(q_html.get(i));
					}
					if (i < a_html.size())
					{
//						qa_html += a_html.get(i);
						qa_html.append(a_html.get(i));
					}
				}
				else
				{
					if (i < a_html.size())
					{
//						qa_html += a_html.get(i);
						qa_html.append(a_html.get(i));
					}
					if (i < q_html.size())
					{
//						qa_html += q_html.get(i);
						qa_html.append(q_html.get(i));
					}
				}
			}
//			watch2.stop("for文");
//			qa_html += "</span>";
			qa_html.append("</span>");
			//qa_html += "<div id='bottom_border' style='width:100%'></div>";		
		}
	    stopwatch.stop(new Object(){}.getClass().getEnclosingMethod().getName());
		
		return qa_html.toString();
	}
	
	/**
	 * 問題登録（1:1,1:n,n:1,n:n全対応）
	 * TODO リバーシブル問題
	 * @param owner_id
	 * @param owner_db
	 * @param qa_input
	 */
	public void create_qa(
			String owner_id, 
			String owner_db, 
			String qa_input,
			String qa_husen,
			String yomudake_flg,
			String reversible_flg)
	{
		StopWatch stop_watch = new StopWatch();
		//System.out.println("qa_input"+qa_input);
		
		// 順番、問題文/正答
		Map<String,String> qa_map = new HashMap<String,String>();
		
		Document doc = Jsoup.parse(qa_input);
		Elements spans = doc.getElementsByTag("span");

		int is_start_with_q = 0;
		
		create_qa_map(qa_map, spans);
		
		// 順番（QA内での正答も含めた順番）、問題パーツ
		Map<Integer, String> q_map = new HashMap<Integer,String>();
		// 順番（QA内での問題も含めた順番）、正答パーツ
		Map<Integer, String> a_map = new HashMap<Integer,String>();
		
		for (Map.Entry<String, String> entry : qa_map.entrySet())
		{
			if (entry.getKey().contains("q"))
			{
				q_map.put(Integer.parseInt(entry.getKey().replace("q", "")), 
						  entry.getValue());
			}
			else if (entry.getKey().contains("a"))
			{
				a_map.put(Integer.parseInt(entry.getKey().replace("a", "")), 
						  entry.getValue());		
			}
			if (entry.getKey().equals("q1"))
			{
				is_start_with_q = 1;
			}
		  //  System.out.println(entry.getKey() + "/" + entry.getValue());
		}
		
		// 問題文がない場合は登録しない
		// TODO エラーメッセージを出す
		if (q_map.size() == 0)
		{
			return;
		}
		
		// 正答がなく、読むだけ問題でもない場合は登録しない
		// TODO エラーメッセージを出す
		System.out.println(yomudake_flg);
		if (a_map.size() == 0 && yomudake_flg.equals("off"))
		{
			return;
		}
		
		/**
		 * モデルにデータを挿入
		 */
		QAPlusModel qa_plus = new QAPlusModel();
		
		/**
		 * QA
		 */
		QAModel qa = new QAModel();

		// 行番号・QA_ID生成用
		QADao qa_dao = new QADao();
		int qa_max_no = qa_dao.get_qa_max_row_no(owner_db);

		// 行番号
		qa.setRow_no(qa_max_no + 1); 
		// QA ID
		String qa_id = qa.generate_qa_id(qa_max_no + 1, owner_id);
		qa.setQa_id(qa_id);
		// QAタイプ
		int qa_type = set_qa_type(q_map, a_map);
		qa.setQa_type(qa_type);
	    // QA入力エリアのHTML
		qa.setQa_html(qa_input);
		// 読むだけ問題フラグ
		int yomudake = 0;
		if (yomudake_flg.equals("on"))
		{
			yomudake = 1;
		}
		qa.setYomudake_flg(yomudake);
	    // 問題と正答を入れ替えた結果生成された問題かどうか
		qa.setIs_reversible(0);
	    // 広告問題フラグ
		qa.setKoukoku_flg(0);
		// 重要度（５段階）
		qa.setJuyoudo(3);
		// 難易度（５段階）
		qa.setNanido(3);
		// 問題文と正答のうち問題から始まるかのフラグ
		qa.setIs_start_with_q(is_start_with_q);
		// 正答がたくさんある場合の問題文を分割した時の個数
		qa.setQ_split_cnt(q_map.size());
		// 問題に紐づく正答の個数
		qa.setSeitou_cnt(a_map.size());
		// 公開範囲
		qa.setKoukai_level(Constant.KOUKAI_LEVEL_SELF_ONLY);
		// 無料販売フラグ
		qa.setFree_flg(0);
		// 無料配布した数
		qa.setFree_sold_num(0);
		// 有料販売フラグ
		qa.setCharge_flg(0);
		// 有料で売った数
		qa.setCharge_sold_num(0);
		// 削除フラグ
		qa.setDel_flg(0);
		// 作成者
		qa.setCreate_owner(owner_id);
		// 更新者
		qa.setUpdate_owner(owner_id);
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		qa.setCreate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		qa.setUpdate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
		qa_plus.setQa(qa);
		
		
		/**
		 * 問題
		 */
		List<MondaiModel> mondai_list = new ArrayList<MondaiModel>();
		
		int q_idx = 1;
		for (Map.Entry<Integer, String> entry : q_map.entrySet())
		{
			MondaiModel mondai = new MondaiModel();
	
			// 行番号・Q_ID生成用
			MondaiDao mondai_dao = new MondaiDao();
			int q_max_no = mondai_dao.get_mondai_max_row_no(owner_db);
	
		    // 行番号
			mondai.setRow_no(q_max_no + q_idx);
		    // 問題ID
			String q_id = mondai.generate_q_id(q_max_no + q_idx, owner_id);
			q_idx++;
			mondai.setQ_id(q_id);
		    // QA ID
			mondai.setQa_id(qa_id);
		    // QA内での問題パーツの順番
			mondai.setJunban(entry.getKey());
		    // 問題パーツが文字であるかのフラグ
			mondai.setIs_text_flg(1);
		    // 問題パーツがバイナリであるかのフラグ
			mondai.setIs_binary_flg(0);
		    // 分割された問題文
			mondai.setQ_parts_text(entry.getValue());
		    // QAの中に出てくる音声や画像などのバイナリファイル
			mondai.setQ_parts_binary(null);
		    // 言語
			mondai.setLanguage(Util.check_japanese_or_english(entry.getValue()));
	//		mondai.setLanguage(Util.langDetect(mondai_input));
		    // テキスト読み上げデータ
			mondai.setYomiage(null);
		    // 削除フラグ
			mondai.setDel_flg(0);
		    // 作成者
			mondai.setCreate_owner(owner_id);
		    // 更新者
			mondai.setUpdate_owner(owner_id);
		    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
			mondai.setCreate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
		    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
			mondai.setUpdate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
			
			mondai_list.add(mondai);
		}
		
		qa_plus.setMondai_list(mondai_list);

		
		/**
		 * 正答
		 */
		List<SeitouModel> seitou_list = new ArrayList<SeitouModel>();

		int a_idx = 1;
		
		for (Map.Entry<Integer, String> entry : a_map.entrySet())
		{

			SeitouModel seitou = new SeitouModel();
			
			// 行番号・S_ID生成用
			SeitouDao seitou_dao = new SeitouDao();
			int s_max_no = seitou_dao.get_seitou_max_row_no(owner_db);
			
		    // 行番号
			seitou.setRow_no(s_max_no + a_idx);
		    // 正答ID
			String s_id = seitou.generate_s_id(s_max_no + a_idx, owner_id);
			seitou.setS_id(s_id);
		    // QA ID
			seitou.setQa_id(qa_id);
		    // QA内での正答の順番
			seitou.setJunban(entry.getKey());
		    // 正答が文字であるかのフラグ
			seitou.setIs_text_flg(1);
		    // 正答がバイナリであるかのフラグ
			seitou.setIs_binary_flg(0);
		    // 正答
			seitou.setSeitou(entry.getValue());
		    // 正答が画像などのバイナリである場合に格納する
			seitou.setSeitou_binary(null);
		    // 重要度（５段階）
			seitou.setJuyoudo(3);
		    // 難易度（５段階）
			seitou.setNanido(3);
		    // 言語
	//		seitou.setLanguage(Util.langDetect(seitou_input));
			String language = Util.check_japanese_or_english(entry.getValue());
			seitou.setLanguage(language);
		    // テキスト読み上げデータ
			byte[] yomiage = null;
			if (language == Constant.ENGLISH)
			{
				try {
					String speaker = "Vicki";
					String file_name = Constant.SPEECH_DATA_FOLDER_PATH + seitou.getS_id() + ".wav";
					String command = "say --data-format=LEF32@8000 -r 50 -v " + speaker + " '" + seitou.getSeitou() + "' -o " + file_name;
					System.out.println(command);
					Runtime.getRuntime().exec(command);
					File file = new File(file_name);
					Thread.sleep(seitou.getSeitou().length() * 30);
					Boolean a = file.setExecutable(true, false);
					System.out.println("setexec:"+a);
					String command2 = "/usr/local/bin/ffmpeg -i " + file_name + " -filter:a asetrate=r=18K -vn " + file_name.replace("wav", "m4a");
					Runtime.getRuntime().exec(command2);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			seitou.setYomiage(yomiage);
		    // 削除フラグ
			seitou.setDel_flg(0);
		    // 作成者
			seitou.setCreate_owner(owner_id);
		    // 更新者
			seitou.setUpdate_owner(owner_id);
		    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
			seitou.setCreate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
		    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
			seitou.setUpdate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
	
			seitou_list.add(seitou);
			
			/**
			 * 回答の新規登録を追加
			 */
			KaitouModel kaitou = new KaitouModel();

			// 行番号・K_ID生成用
			KaitouDao kaitou_dao = new KaitouDao();
			int k_max_no = kaitou_dao.get_kaitou_max_row_no(owner_db);
		    // 行番号
			kaitou.setRow_no(k_max_no + a_idx);
			// 回答ID
			String k_id = kaitou.generate_k_id(k_max_no + a_idx, owner_id);
			a_idx++;
			kaitou.setK_id(k_id);
			// QA ID
			kaitou.setQa_id(qa_id);
			// 正答ID
			kaitou.setS_id(s_id);
			// 正解フラグ
			kaitou.setSeikai_flg(0);
			// アクション・・・チェックを入れた、外した、解いて正解した、解いて不正解、等
			kaitou.setAction(Constant.ACTION_QA_TOUROKU);
			// アクション日時（H2DBのtimestampと同じフォーマットにする）
			kaitou.setAction_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
			// ユーザーが入力した回答
			kaitou.setKaitou("");
			// 言語
			kaitou.setLanguage("");
			// 削除フラグ
			kaitou.setDel_flg(0);
		    // 作成者
			kaitou.setCreate_owner(owner_id);
		    // 更新者
			kaitou.setUpdate_owner(owner_id);
		    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
			kaitou.setCreate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
		    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
			kaitou.setUpdate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
			
			kaitou_dao.insert_kaitou(owner_db, kaitou);
		}
		
		qa_plus.setSeitou_list(seitou_list);
		
		QAPlusDao qa_plus_dao = new QAPlusDao();
		qa_plus_dao.insert_qa_plus(owner_db, qa_plus);		
		
		if (qa_husen != null)
		{
			if (!qa_husen.equals(""))
			{
				QaTagRelationDao qa_tag_relation_dao = new QaTagRelationDao();
				Document huden_doc = Jsoup.parse(qa_husen);
				Elements husen_spans = huden_doc.getElementsByTag("span");
				for (Element husen_span: husen_spans) {
					QaTagRelationModel qa_tag_relation = new QaTagRelationModel();
					TagDao tag_dao = new TagDao();
				    // 行番号
					qa_tag_relation.setRow_no(qa_tag_relation_dao.get_max_row_no(owner_db)+1);
				    // QA ID
					qa_tag_relation.setQa_id(qa_id);
				    // タグID
					qa_tag_relation.setTag_id(tag_dao.select_tag_id(owner_db, husen_span.text()));
				    // タグ内でのQAの順番
					qa_tag_relation.setJunban(0);
				    // 公開範囲
					qa_tag_relation.setKoukai_level(Constant.KOUKAI_LEVEL_SELF_ONLY);
				    // 作成者
					qa_tag_relation.setCreate_owner(owner_id);
				    // 更新者
					qa_tag_relation.setUpdate_owner(owner_id);
				    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
					qa_tag_relation.setCreate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
				    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
					qa_tag_relation.setUpdate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
					
					qa_tag_relation_dao.insert_qa_tag_relation(owner_db, qa_tag_relation);
				}
			}
		}
		stop_watch.stop("create_qa");
	}

	private AudioFormat getOutFormat(AudioFormat inFormat) {
		int ch = inFormat.getChannels();
		float rate = inFormat.getSampleRate();	
		return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 72000, 16, ch, ch * 2, rate,
				inFormat.isBigEndian());
	}
	
	public void edit_qa(
			String owner_id, 
			String owner_db, 
			String qa_input,
			String qa_husen,
			String yomudake_flg,
			String reversible_flg,
			String qa_id)
	{
		//System.out.println("qa_input"+qa_input);
		
		// 順番、問題文/正答
		Map<String,String> qa_map = new HashMap<String,String>();
		
		Document doc = Jsoup.parse(qa_input);
		Elements spans = doc.getElementsByTag("span");

		int is_start_with_q = 0;
		
		create_qa_map(qa_map, spans);
		
		// 順番（QA内での正答も含めた順番）、問題パーツ
		Map<Integer, String> q_map = new HashMap<Integer,String>();
		// 順番（QA内での問題も含めた順番）、正答パーツ
		Map<Integer, String> a_map = new HashMap<Integer,String>();
		
		for (Map.Entry<String, String> entry : qa_map.entrySet())
		{
			if (entry.getKey().contains("q"))
			{
				q_map.put(Integer.parseInt(entry.getKey().replace("q", "")), 
						  entry.getValue());
			}
			else if (entry.getKey().contains("a"))
			{
				a_map.put(Integer.parseInt(entry.getKey().replace("a", "")), 
						  entry.getValue());		
			}
			if (entry.getKey().equals("q1"))
			{
				is_start_with_q = 1;
			}
		  //  System.out.println(entry.getKey() + "/" + entry.getValue());
		}
		
		// 問題文がない場合は登録しない
		// TODO エラーメッセージを出す
		if (q_map.size() == 0)
		{
			return;
		}
		
		// 正答がなく、読むだけ問題でもない場合は登録しない
		// TODO エラーメッセージを出す
		System.out.println(yomudake_flg);
		if (a_map.size() == 0 && yomudake_flg.equals("off"))
		{
			return;
		}
		
		/**
		 * モデルにデータを挿入
		 */
		QAPlusModel qa_plus = new QAPlusModel();
		
		/**
		 * QA
		 */
		QAModel qa = new QAModel();

		// 行番号・QA_ID生成用
		QADao qa_dao = new QADao();
		int qa_max_no = qa_dao.get_qa_max_row_no(owner_db);

		// 行番号
		qa.setRow_no(qa_max_no + 1); 
		// QA ID
		qa.setQa_id(qa_id);
		// QAタイプ
		int qa_type = set_qa_type(q_map, a_map);
		qa.setQa_type(qa_type);
	    // QA入力エリアのHTML
		qa.setQa_html(qa_input);
		// 読むだけ問題フラグ
		int yomudake = 0;
		if (yomudake_flg.equals("on"))
		{
			yomudake = 1;
		}
		qa.setYomudake_flg(yomudake);
	    // 問題と正答を入れ替えた結果生成された問題かどうか
		qa.setIs_reversible(0);
	    // 広告問題フラグ
		qa.setKoukoku_flg(0);
		// 重要度（５段階）
		qa.setJuyoudo(3);
		// 難易度（５段階）
		qa.setNanido(3);
		// 問題文と正答のうち問題から始まるかのフラグ
		qa.setIs_start_with_q(is_start_with_q);
		// 正答がたくさんある場合の問題文を分割した時の個数
		qa.setQ_split_cnt(q_map.size());
		// 問題に紐づく正答の個数
		qa.setSeitou_cnt(a_map.size());
		// 公開範囲
		qa.setKoukai_level(Constant.KOUKAI_LEVEL_SELF_ONLY);
		// 無料販売フラグ
		qa.setFree_flg(0);
		// 無料配布した数
		qa.setFree_sold_num(0);
		// 有料販売フラグ
		qa.setCharge_flg(0);
		// 有料で売った数
		qa.setCharge_sold_num(0);
		// 削除フラグ
		qa.setDel_flg(0);
		// 作成者
		qa.setCreate_owner(owner_id);
		// 更新者
		qa.setUpdate_owner(owner_id);
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		qa.setCreate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		qa.setUpdate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
		qa_plus.setQa(qa);
		
		
		/**
		 * 問題
		 */
		List<MondaiModel> mondai_list = new ArrayList<MondaiModel>();
		
		int q_idx = 1;
		for (Map.Entry<Integer, String> entry : q_map.entrySet())
		{
			MondaiModel mondai = new MondaiModel();
	
			// 行番号・Q_ID生成用
			MondaiDao mondai_dao = new MondaiDao();
			int q_max_no = mondai_dao.get_mondai_max_row_no(owner_db);
	
		    // 行番号
			mondai.setRow_no(q_max_no + q_idx);
		    // 問題ID
			String q_id = mondai.generate_q_id(q_idx, owner_id);
			q_idx++;
			mondai.setQ_id(q_id);
		    // QA ID
			mondai.setQa_id(qa_id);
		    // QA内での問題パーツの順番
			mondai.setJunban(entry.getKey());
		    // 問題パーツが文字であるかのフラグ
			mondai.setIs_text_flg(1);
		    // 問題パーツがバイナリであるかのフラグ
			mondai.setIs_binary_flg(0);
		    // 分割された問題文
			mondai.setQ_parts_text(entry.getValue());
		    // QAの中に出てくる音声や画像などのバイナリファイル
			mondai.setQ_parts_binary(null);
		    // 言語
			mondai.setLanguage(Util.check_japanese_or_english(entry.getValue()));
	//		mondai.setLanguage(Util.langDetect(mondai_input));
		    // テキスト読み上げデータ
			mondai.setYomiage(null);
		    // 削除フラグ
			mondai.setDel_flg(0);
		    // 作成者
			mondai.setCreate_owner(owner_id);
		    // 更新者
			mondai.setUpdate_owner(owner_id);
		    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
			mondai.setCreate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
		    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
			mondai.setUpdate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
			
			mondai_list.add(mondai);
		}
		
		qa_plus.setMondai_list(mondai_list);

		
		/**
		 * 正答
		 */
		List<SeitouModel> seitou_list = new ArrayList<SeitouModel>();

		int a_idx = 1;
		
		for (Map.Entry<Integer, String> entry : a_map.entrySet())
		{

			SeitouModel seitou = new SeitouModel();
			
			// 行番号・S_ID生成用
			SeitouDao seitou_dao = new SeitouDao();
			int s_max_no = seitou_dao.get_seitou_max_row_no(owner_db);
			
		    // 行番号
			seitou.setRow_no(s_max_no + a_idx);
		    // 正答ID
			String s_id = seitou.generate_s_id(a_idx, owner_id);
			seitou.setS_id(s_id);
		    // QA ID
			seitou.setQa_id(qa_id);
		    // QA内での正答の順番
			seitou.setJunban(entry.getKey());
		    // 正答が文字であるかのフラグ
			seitou.setIs_text_flg(1);
		    // 正答がバイナリであるかのフラグ
			seitou.setIs_binary_flg(0);
		    // 正答
			seitou.setSeitou(entry.getValue());
		    // 正答が画像などのバイナリである場合に格納する
			seitou.setSeitou_binary(null);
		    // 重要度（５段階）
			seitou.setJuyoudo(3);
		    // 難易度（５段階）
			seitou.setNanido(3);
		    // 言語
	//		seitou.setLanguage(Util.langDetect(seitou_input));
			seitou.setLanguage(Util.check_japanese_or_english(entry.getValue()));
		    // テキスト読み上げデータ
			seitou.setYomiage(null);
		    // 削除フラグ
			seitou.setDel_flg(0);
		    // 作成者
			seitou.setCreate_owner(owner_id);
		    // 更新者
			seitou.setUpdate_owner(owner_id);
		    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
			seitou.setCreate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
		    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
			seitou.setUpdate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
	
			seitou_list.add(seitou);
			
			/**
			 * 回答の新規登録を追加
			 */
			KaitouModel kaitou = new KaitouModel();

			// 行番号・K_ID生成用
			KaitouDao kaitou_dao = new KaitouDao();
			int k_max_no = kaitou_dao.get_kaitou_max_row_no(owner_db);
		    // 行番号
			kaitou.setRow_no(k_max_no + a_idx);
			// 回答ID
			String k_id = kaitou.generate_k_id(k_max_no + a_idx, owner_id);
			a_idx++;
			kaitou.setK_id(k_id);
			// QA ID
			kaitou.setQa_id(qa_id);
			// 正答ID
			kaitou.setS_id(s_id);
			// 正解フラグ
			kaitou.setSeikai_flg(0);
			// アクション・・・チェックを入れた、外した、解いて正解した、解いて不正解、等
			kaitou.setAction(Constant.ACTION_QA_TOUROKU);
			// アクション日時（H2DBのtimestampと同じフォーマットにする）
			kaitou.setAction_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
			// ユーザーが入力した回答
			kaitou.setKaitou("");
			// 言語
			kaitou.setLanguage("");
			// 削除フラグ
			kaitou.setDel_flg(0);
		    // 作成者
			kaitou.setCreate_owner(owner_id);
		    // 更新者
			kaitou.setUpdate_owner(owner_id);
		    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
			kaitou.setCreate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
		    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
			kaitou.setUpdate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
			
			kaitou_dao.insert_kaitou(owner_db, kaitou);
		}
		
		qa_plus.setSeitou_list(seitou_list);
		
		QAPlusDao qa_plus_dao = new QAPlusDao();
		qa_plus_dao.insert_qa_plus(owner_db, qa_plus);		
		
		if (qa_husen != null)
		{
			if (!qa_husen.equals(""))
			{
				QaTagRelationDao qa_tag_relation_dao = new QaTagRelationDao();
				Document huden_doc = Jsoup.parse(qa_husen);
				Elements husen_spans = huden_doc.getElementsByTag("span");
				for (Element husen_span: husen_spans) {
					QaTagRelationModel qa_tag_relation = new QaTagRelationModel();
					TagDao tag_dao = new TagDao();
				    // 行番号
					qa_tag_relation.setRow_no(qa_tag_relation_dao.get_max_row_no(owner_db)+1);
				    // QA ID
					qa_tag_relation.setQa_id(qa_id);
				    // タグID
					qa_tag_relation.setTag_id(tag_dao.select_tag_id(owner_db, husen_span.text()));
				    // タグ内でのQAの順番
					qa_tag_relation.setJunban(0);
				    // 公開範囲
					qa_tag_relation.setKoukai_level(Constant.KOUKAI_LEVEL_SELF_ONLY);
				    // 作成者
					qa_tag_relation.setCreate_owner(owner_id);
				    // 更新者
					qa_tag_relation.setUpdate_owner(owner_id);
				    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
					qa_tag_relation.setCreate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
				    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
					qa_tag_relation.setUpdate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
					
					qa_tag_relation_dao.insert_qa_tag_relation(owner_db, qa_tag_relation);
				}
			}
		}
		
	}
	
	
	/**
	 * QA種別を返却する
	 * @param q_map
	 * @param a_map
	 * @return
	 */
	public int set_qa_type(Map<Integer, String> q_map, Map<Integer, String> a_map) {
		int qa_type = 0;
		if (q_map.size() == 1 && a_map.size() == 1)
		{
			qa_type = Constant.QA_TYPE_1_ON_1;
		}
		else if (q_map.size() == 1 && a_map.size() > 1)
		{
			qa_type = Constant.QA_TYPE_1_ON_N;
		}
		else if (q_map.size() > 1 && q_map.size() == 1)
		{
			qa_type = Constant.QA_TYPE_N_ON_1;
		}
		else if (q_map.size() > 1 && q_map.size() > 1)
		{
			qa_type = Constant.QA_TYPE_N_ON_N;
		}
		return qa_type;
	}


	public void create_qa_map(Map<String, String> qa_map, Elements spans) {
		/**
		 * qが連続した場合はつなげる、aが連続した場合はつなげる
		 */
		int idx = 0;
		int junban = 1;
		for (Element span: spans) {
			String class_name = span.className();
			String next_class_name = "";
			String back_class_name = "";
			if (idx < spans.size() - 1)
			{
				next_class_name = spans.get(idx + 1).className();
			}
			if (0 < idx)
			{
				back_class_name = spans.get(idx - 1).className();
			}
			if (class_name.equals("q_input"))
			{
				if (back_class_name.equals(class_name))
				{
					qa_map.put("q" + (junban), spans.get(idx - 1).text() + span.text());
					idx++;
					continue;
				}
				else if (class_name.equals(next_class_name))
				{
					qa_map.put("q" + (junban), span.text() + spans.get(idx + 1).text());
					idx++;
					continue;
				}
				else
				{
					qa_map.put("q" + (junban), span.text());
					idx++;
				}
			}
			else if (span.className().equals("a_input"))
			{
				if (back_class_name.equals(class_name))
				{
					qa_map.put("a" + (junban), spans.get(idx - 1).text() + span.text());
					idx++;
					continue;
				}
				else if (class_name.equals(next_class_name))
				{
					qa_map.put("a" + (junban), span.text() + spans.get(idx + 1).text());
					idx++;
					continue;
				}
				else
				{
					qa_map.put("a" + junban, span.text());
					idx++;
				}
			}
			junban++;
		}
	}	
}
