package com.application.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.SequenceInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.spi.AudioFileReader;

import org.apache.commons.io.FileUtils;
import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;
import org.json.JSONException;

import com.ibm.icu.text.Transliterator;

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
import com.dao.H2dbDao;
import com.dao.SQliteDAO;
import com.cybozu.*;
import com.application.controller.dao.KaitouDao;
import com.application.controller.dao.MondaiDao;
import com.application.controller.dao.QADao;
import com.application.controller.dao.QAPlusDao;
import com.application.controller.dao.QaTagRelationDao;
import com.application.controller.dao.SeitouDao;
import com.application.controller.dao.TagDao;
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
import com.common.Util;
import com.slime.SlimeSerif;

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
						   "/{owner_id}/main.html#", 
						   "/{owner_id}/main.htm", 
						   "/{owner_id}/main"},
							method=RequestMethod.GET)
	public String main(@PathVariable("owner_id") String owner_id,
						HttpServletRequest request, 
						HttpServletResponse response, 
						HttpSession session,
						Model model) {
		Log log = new Log();
		log.insert_error_log("INFO", "main method start.");
				
		TopPageController top = new TopPageController();
		if (top.isLogin(request) == false)
		{
			return "redirect:/";
		}
		top.setAutoLoginToken(owner_id,session,request,response);
		
		String request_url = request.getRequestURI();
		String response_url = "/"+ owner_id + "/main.html";

		if (owner_id.equals("sample"))
		{
//			SamplePageController sample = new SamplePageController();
//			sample.sample(owner_id, request, response, session, model);
//			if (request_url.equals(response_url))
//			{
//				return "main";
//			}
//			else
//			{
//				return "redirect:" + response_url;				
//			}
		}
		
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
				session.setAttribute("owner_db", encrypted_owner_db);
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
				
				// ソート用付箋
				String husen_sort_html = generate_husen_sort_html(owner_db);
				model.addAttribute("tags_sort", husen_sort_html);
				
				// ページング総数
				QADao qa_dao = new QADao();
				int total_pages = qa_dao.get_pages(owner_db, "");
				model.addAttribute("total_pages", total_pages);
				
//				try
//				{
//					// 問題と正答へのリバーシブルフラグ追加パッチ
//					SQliteDAO dao = new SQliteDAO();
//					dao.is_reversible_patch(owner_db);
//					H2dbDao h2dao = new H2dbDao();
//					h2dao.is_reversible_patch();
//				}
//				catch(Exception ex)
//				{
//					ex.printStackTrace();
//				}
//				// patch
//				try(Stream<Path> paths = Files.walk(Paths.get(Constant.SPEECH_DATA_FOLDER_PATH.substring(0,Constant.SPEECH_DATA_FOLDER_PATH.length()-1)))) {
//				    paths.forEach(filePath -> {
//				    	System.out.println("filepath:"+filePath.toFile());
//				    	try
//				    	{
//					    	if (Files.isRegularFile(filePath)) {
//					        	File speech_file = filePath.toFile();
//					        	String file_name = speech_file.getName();
//					        	//String now_path = speech_file.getAbsolutePath();
//					        	System.out.println("file_name:"+file_name);
//				        		String id = "";
//					        	String qa_id = "";
//				        		if (file_name.split("\\.").length > 0)
//				        		{
//				        			id = file_name.split("\\.")[0].replace("_q", "");
//				        			id = file_name.split("\\.")[0].replace("_a", "");
//				        		}
//			        			System.out.println("id:"+id);
//					        	if (id.startsWith("s"))
//					        	{
//					        		System.out.println("this is s.");
//						        	SeitouDao sdao = new SeitouDao();
//						        	qa_id = sdao.get_qa_id(owner_db, id);
//					        	}
//					        	else if (id.startsWith("q"))
//					        	{
//					        		System.out.println("this is q.");
//						        	MondaiDao qdao = new MondaiDao();
//						        	qa_id = qdao.get_qa_id(owner_db, id);			        		
//					        	}
//					        	String new_path = Constant.SPEECH_DATA_FOLDER_PATH + qa_id;
//					        	System.out.println("qa_id:"+qa_id);
//					        	System.out.println(new_path);
//								File theDir = new File(new_path);
//								// if the directory does not exist, create it
//								if (!theDir.exists()) {
//								    boolean result = false;
//								    try{
//								        theDir.mkdir();
//								        result = true;
//								    } 
//								    catch(SecurityException se){
//								        //handle it
//								    }        
//								}
//								speech_file.renameTo(new File(new_path + "/" + file_name));
//					    	}
//				    	}
//				    	catch(Exception ex)
//				    	{
//				    		ex.printStackTrace();
//				    	}
//				    });
//				} 
//				catch(Exception ex)
//				{
//					ex.printStackTrace();			
//				}
				
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
			@RequestParam(value="refresh_by_date", required=false) String date,
			@RequestParam(value="now_page_left", required=false) String now_page_left,
			@RequestParam("owner_id") String owner_id,
			HttpServletRequest request, 
			HttpServletResponse response, 
			HttpSession session) {
		// オートログイン
		if (session.getAttribute("owner_id") == null)
		{
			TopPageController top = new TopPageController();
			top.setAutoLoginToken(owner_id,session,request,response);
		}

		// TODO 認証されてるかどうかはsessionに入れると書き換えられてしまうから毎回DBに接続した方がいいかな
		Boolean is_authenticated = (Boolean)session.getAttribute("is_authenticated");
		owner_id = (String)session.getAttribute("owner_id");
		
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
			System.out.println("ひづけ"+date);
			seitou_dao.to_huseikai_by_tag(owner_db, husen_names, date);
			
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
	 * 検索中の全QAを正解の状態にする
	 * @param husen_names
	 * @param request
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(value={"/to_seikai.html"},
			method=RequestMethod.GET)
	public @ResponseBody String to_seikai(
			@RequestParam(value="husen_names", required=false) String husen_names,
			@RequestParam(value="refresh_by_date", required=false) String date,
			@RequestParam(value="now_page_left", required=false) String now_page_left,
			@RequestParam("owner_id") String owner_id,
			HttpServletRequest request, 
			HttpServletResponse response, 
			HttpSession session) {

		// オートログイン
		if (session.getAttribute("owner_id") == null)
		{
			TopPageController top = new TopPageController();
			top.setAutoLoginToken(owner_id,session,request,response);
		}
		
		// TODO 認証されてるかどうかはsessionに入れると書き換えられてしまうから毎回DBに接続した方がいいかな
		Boolean is_authenticated = (Boolean)session.getAttribute("is_authenticated");
		owner_id = (String)session.getAttribute("owner_id");
		
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
			//System.out.println("ひづけ"+date);
			seitou_dao.to_seikai_by_tag(owner_db, husen_names, date);
			
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
	 * 選択したQAを１件削除
	 * @param husen_names
	 * @param now_page_left
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(value={"/qa_delete.html"},
			method=RequestMethod.GET)
	public @ResponseBody String qa_delete(
			@RequestParam(value="qa_id", required=false) String qa_id,
			@RequestParam(value="husen_str", required=false) String husen_names,
			@RequestParam("owner_id") String owner_id,
			HttpServletRequest request, 
			HttpServletResponse response, 
			HttpSession session) {

		// オートログイン
		if (session.getAttribute("owner_id") == null)
		{
			TopPageController top = new TopPageController();
			top.setAutoLoginToken(owner_id,session,request,response);
		}

		// TODO 認証されてるかどうかはsessionに入れると書き換えられてしまうから毎回DBに接続した方がいいかな
		Boolean is_authenticated = (Boolean)session.getAttribute("is_authenticated");
		owner_id = (String)session.getAttribute("owner_id");
		
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
			
			// 物理削除
			QAPlusDao qa_plus_dao = new QAPlusDao();
			qa_plus_dao.delete_qa(owner_db, qa_id);
			
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
		
			// ページング総数
			QADao qa_dao = new QADao();
			String total_pages = String.valueOf(qa_dao.get_pages(owner_db, husen_names));			

			SeitouDao seitou_dao = new SeitouDao();
			
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

//    @RequestMapping(value={"/image_search.html"},
//    			method=RequestMethod.GET)
//    public @ResponseBody String image_search(
//    		@RequestParam(value="keywords", required=false) String keywords,
//			@RequestParam("owner_id") String owner_id,
//    		HttpServletRequest request, 
//    		HttpServletResponse response, 
//    		HttpSession session)
//    {
//		// オートログイン
//		if (session.getAttribute("owner_id") == null)
//		{
//			TopPageController top = new TopPageController();
//			top.setAutoLoginToken(owner_id,session,request,response);
//		}
//
////		GPix gpix = GPix.getInstance();
////    	List<Image> image_list = new ArrayList<Image>();
////    	try {
////    		image_list = gpix.search(keywords.replace(" ", "+"), 1);
////    		
////		} catch (GPixException | IOException | JSONException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////    	String img_url = "";
////    	if (image_list.size() > 0)
////    	{
////    		Image image = image_list.get(0);
////    		img_url = image.getThumbImageUrl();
////    	}
////    	
//////    	System.out.println("image.getImageUrl():"+image.getThumbImageUrl());
//    	String json = JSON.encode(
//				new String[] 
//				{img_url});
//		return json;
//    }
	
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
	public @ResponseBody String tag_search(
				@RequestParam("husen_names") String husen_names,
				@RequestParam("owner_id") String owner_id,
				HttpServletRequest request, 
				HttpServletResponse response, 
				HttpSession session,
				Model model) {
		
		// オートログイン
		if (session.getAttribute("owner_id") == null)
		{
			TopPageController top = new TopPageController();
			top.setAutoLoginToken(owner_id,session,request,response);
		}

		// TODO 認証されてるかどうかはsessionに入れると書き換えられてしまうから毎回DBに接続した方がいいかな
		Boolean is_authenticated = (Boolean)session.getAttribute("is_authenticated");
		owner_id = (String)session.getAttribute("owner_id");
				
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
		String husen_html = "<div id='blank_husen' class='husen' contenteditable='true' title='クリック+入力+Enterで&#013;新規付箋を登録' onkeypress='javascript:husen_touroku(this);'></div>";
		tag_list = tag_dao.select_tag_list(owner_db, tag_list);
		for (TagModel tag : tag_list)
		{
			String system_tag = "";
			if (tag.getSystem_tag_flg() == 1)
			{
				system_tag = " blue";
			}

			husen_html += ("<div id='" + tag.getTag_id() + "' class='husen" + system_tag + "'>" + tag.getTag_name() + "</div>");
		}
		return husen_html;
	}	    

	/**
	 * 
	 * @param owner_db
	 * @return
	 */
	public String generate_husen_sort_html(String owner_db) {
		TagDao tag_dao = new TagDao();
		List<TagModel> tag_list = new ArrayList<TagModel>();
		String husen_html = "";
		tag_list = tag_dao.select_tag_list(owner_db, tag_list);
		for (TagModel tag : tag_list)
		{
			String system_tag = "";
			if (tag.getSystem_tag_flg() == 1)
			{
				system_tag = " blue";
			}
			husen_html += ("<li id='" + tag.getTag_id() + "' class='husen_sort" + system_tag + "'>" + tag.getTag_name() + "</li>");
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
						HttpServletResponse response, 
						Model model,
						@RequestParam("qa_input_hidden") String qa_input,
						@RequestParam(value="qa_husen",required=false) String qa_husen,
						@RequestParam(value="qa_id", required=false) String qa_id,
						@RequestParam(value="yomudake_flg", required=false) String yomudake_flg,
						@RequestParam(value="reversible_flg", required=false) String reversible_flg) {
		
		// オートログイン
		if (session.getAttribute("owner_id") == null)
		{
			TopPageController top = new TopPageController();
			top.setAutoLoginToken(owner_id,session,request,response);
		}

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
	 * 作成途中
	 */
	@RequestMapping(value={"/register_qa.html"}, method=RequestMethod.GET)
	public @ResponseBody String mondai_touroku_ajax
			(HttpServletRequest request, 
			HttpServletResponse response, 
			HttpSession session,
			@RequestParam("owner_id") String owner_id,
			@RequestParam("qa_input_hidden") String qa_input,
			@RequestParam(value="qa_husen",required=false) String qa_husen,
			@RequestParam(value="husen_str",required=false) String husen_str,
			@RequestParam(value="qa_id", required=false) String qa_id,
			@RequestParam(value="yomudake_flg", required=false) String yomudake_flg,
			@RequestParam(value="reversible_flg", required=false) String reversible_flg) {

		// オートログイン
		if (session.getAttribute("owner_id") == null)
		{
			TopPageController top = new TopPageController();
			top.setAutoLoginToken(owner_id,session,request,response);
		}

		owner_id = (String)session.getAttribute("owner_id");
		
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
		
		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		AES aes = new AES();
		String owner_db = aes.decrypt(encrypted_owner_db);
				
		
		if (yomudake_flg == null)
		{
			yomudake_flg = "off";
		}
		if (reversible_flg == null)
		{
			reversible_flg = "off";
		}
		if (qa_husen == null)
		{
			qa_husen = "";
		}
		
		if (qa_id == null || qa_id.equals(""))
		{
			create_qa(owner_id, owner_db, qa_input, qa_husen,yomudake_flg, reversible_flg);
		}
		else
		{
			edit_qa(owner_id, owner_db, qa_input, qa_husen,yomudake_flg, reversible_flg, qa_id);
		}
				
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
		
		SeitouDao seitou_dao = new SeitouDao();
		
		// ページング総数
		QADao qa_dao = new QADao();
		String total_pages = String.valueOf(qa_dao.get_pages(owner_db, husen_str));			
			
		String seitou_cnt = String.valueOf(seitou_dao.get_seitou_cnt(owner_db, husen_str));
		String seikai_cnt = String.valueOf(seitou_dao.get_seikai_cnt(owner_db, husen_str));
		
		String json = JSON.encode(
				new String[] 
			{qa_html,qa_html_right,seitou_cnt,seikai_cnt,total_pages});
		return json;
	}
	
	/**
	 * 正答の色を変更する（白⇒赤、赤⇒白）
	 * @param session
	 * @param now_opacity
	 * @return
	 */
	@RequestMapping(value={"/change_seitou_color.html"}, method=RequestMethod.GET)
	public @ResponseBody String ajax_change_seitou_color(
			HttpServletRequest request, 
			HttpServletResponse response, 
			HttpSession session,
			@RequestParam("owner_id") String owner_id,
			@RequestParam("qa_id") String qa_id,
			@RequestParam("s_id") String s_id,
			@RequestParam("is_seikai_now") int is_seikai_now) {

		// オートログイン
		if (session.getAttribute("owner_id") == null)
		{
			TopPageController top = new TopPageController();
			top.setAutoLoginToken(owner_id,session,request,response);
		}

		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		AES aes = new AES();
		String owner_db = aes.decrypt(encrypted_owner_db);

		owner_id = (String)session.getAttribute("owner_id");
		
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
	public @ResponseBody int ajax_get_seitou_sum(
			@RequestParam("owner_id") String owner_id,
			HttpServletRequest request, 
			HttpServletResponse response, 
			HttpSession session) {

		// オートログイン
		if (session.getAttribute("owner_id") == null)
		{
			TopPageController top = new TopPageController();
			top.setAutoLoginToken(owner_id,session,request,response);
		}

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
			@RequestParam("owner_id") String owner_id,
			HttpServletRequest request, 
			HttpServletResponse response, 
			HttpSession session,
			@RequestParam(value = "tag_name", required=false) String tag_name) {

		// オートログイン
		if (session.getAttribute("owner_id") == null)
		{
			TopPageController top = new TopPageController();
			top.setAutoLoginToken(owner_id,session,request,response);
		}
		
		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		AES aes = new AES();
		String owner_db = aes.decrypt(encrypted_owner_db);

		owner_id = (String)session.getAttribute("owner_id");
		
		return insert_tag(tag_name, owner_db, owner_id);
	}

	@RequestMapping(value={"/edit_husen.html"}, method=RequestMethod.GET)
	public @ResponseBody String edit_husen(
			@RequestParam("owner_id") String owner_id,
			HttpServletRequest request, 
			HttpServletResponse response, 
			HttpSession session,
			@RequestParam(value = "tag_id", required=false) String tag_id,
			@RequestParam(value = "tag_name", required=false) String tag_name) {

		// オートログイン
		if (session.getAttribute("owner_id") == null)
		{
			TopPageController top = new TopPageController();
			top.setAutoLoginToken(owner_id,session,request,response);
		}

		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		AES aes = new AES();
		String owner_db = aes.decrypt(encrypted_owner_db);

		owner_id = (String)session.getAttribute("owner_id");
		
		return edit_tag(tag_id, tag_name, owner_db, owner_id);
	}
	
	@RequestMapping(value={"/husen_order.html"}, method=RequestMethod.GET)
	public @ResponseBody String husen_order(
			@RequestParam("owner_id") String owner_id,
			HttpSession session,
			HttpServletRequest request, 
			HttpServletResponse response, 
			@RequestParam(value = "husen_ids_in_order", required=false) String husen_ids_in_order) {

		// オートログイン
		if (session.getAttribute("owner_id") == null)
		{
			TopPageController top = new TopPageController();
			top.setAutoLoginToken(owner_id,session,request,response);
		}

		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		AES aes = new AES();
		String owner_db = aes.decrypt(encrypted_owner_db);

		owner_id = (String)session.getAttribute("owner_id");
		
		String husen_html = order_tag(husen_ids_in_order, owner_db);
		
		String json = JSON.encode(
				new String[] 
				{husen_html});
		return json;
	}
	
	/**
	 * 
	 * @param husen_ids_in_order
	 * @param owner_db
	 * @return
	 */
	public static String order_tag(String husen_ids_in_order, String owner_db)
	{
		TagDao tag_dao = new TagDao();
		List<TagModel> tag_list = new ArrayList<TagModel>();
		tag_list = tag_dao.order_tag(owner_db, husen_ids_in_order);
		
		String husen_html = "<div id='blank_husen' class='husen' contenteditable='true' title='クリック+入力+Enterで&#013;新規付箋を登録' onkeypress='javascript:husen_touroku(this);'></div>";
//		tag_list = tag_dao.select_tag_list(owner_db, new ArrayList<TagModel>());
		for (TagModel tag : tag_list)
		{
			String system_tag = "";
			if (tag.getSystem_tag_flg() == 1)
			{
				system_tag = " blue";
			}

			husen_html = husen_html + "<div id='" + tag.getTag_id() + "' class='husen" + system_tag + "'>" + tag.getTag_name() + "</div>";
		}
		return husen_html;		
	}

	
	@RequestMapping(value={"/husen_delete.html"}, method=RequestMethod.GET)
	public @ResponseBody String husen_delete(
			@RequestParam("owner_id") String owner_id,
			HttpServletRequest request, 
			HttpServletResponse response, 
			HttpSession session,
			@RequestParam(value = "tag_id", required=false) String tag_id) {

		// オートログイン
		if (session.getAttribute("owner_id") == null)
		{
			TopPageController top = new TopPageController();
			top.setAutoLoginToken(owner_id,session,request,response);
		}
		
		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		AES aes = new AES();
		String owner_db = aes.decrypt(encrypted_owner_db);

		owner_id = (String)session.getAttribute("owner_id");		
		
		TagDao tag_dao = new TagDao();
		tag_dao.delete_tag(owner_db, tag_id);
		
		// 付箋
		String husen_html = generate_husen_html(owner_db);

		String json = JSON.encode(
				new String[] 
				{husen_html});
		return json;
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
				tag.setJunban(0);
			    // 表示フラグ
				tag.setDisplay_flg(1);
			    // 重要度（５段階）
				tag.setJuyoudo(3);
			    // 難易度（５段階）
				tag.setNanido(3);
			    // システムタグフラグ
				tag.setSystem_tag_flg(0);
			    // タグ種別
				tag.setTag_type(0);
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
				tag_dao.refresh_tags_junban(owner_db, tag);
			}
		}
		return tag_name;
	}		

	public String edit_tag(String tag_id, String tag_name, String owner_db, String owner_id) {
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
				
				tag_dao.update_tag_name(owner_db, tag_id, tag_name);
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
			@RequestParam("owner_id") String owner_id,
			HttpServletRequest request, 
			HttpServletResponse response, 
			HttpSession session,
			@RequestParam(value="args_num", required=false) String args_num,
			@RequestParam(value = "a", required=false) String a_input) {
		// オートログイン
		if (session.getAttribute("owner_id") == null)
		{
			TopPageController top = new TopPageController();
			top.setAutoLoginToken(owner_id,session,request,response);
		}

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
			HttpServletRequest request, 
			HttpServletResponse response, 
			Model model,
			@RequestParam("owner_id") String owner_id,
			@RequestParam(value="now_page") int now_page,
			@RequestParam(value = "next_or_prev") String next_or_prev,
			@RequestParam(value = "husen_str", required=false) String husen_str) {
		// オートログイン
		if (session.getAttribute("owner_id") == null)
		{
			TopPageController top = new TopPageController();
			top.setAutoLoginToken(owner_id,session,request,response);
		}

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
	public @ResponseBody String edit_qa(
			HttpServletRequest request, 
			HttpServletResponse response, 
			HttpSession session,
			@RequestParam("owner_id") String owner_id,
			@RequestParam(value="qa_id", required=false) String qa_id) {
		// オートログイン
		if (session.getAttribute("owner_id") == null)
		{
			TopPageController top = new TopPageController();
			top.setAutoLoginToken(owner_id,session,request,response);
		}

		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		AES aes = new AES();
		String owner_db = aes.decrypt(encrypted_owner_db);

		QADao qa_dao = new QADao();
		String qa_html = qa_dao.get_qa_html(owner_db, qa_id);
		
		QaTagRelationDao qa_tag_relation_dao = new QaTagRelationDao();
		List<TagModel> tag_list = new ArrayList<TagModel>();
		tag_list = qa_tag_relation_dao.select_tags_by_qa_id(owner_db, qa_id);
		String husen_html = "";
		for (TagModel tag : tag_list)
		{
			String system_tag = "";
			if (tag.getSystem_tag_flg() == 1)
			{
				system_tag = " blue";
			}
			husen_html += ("<div id='" + tag.getTag_id() + "' class='husen" + system_tag + "'>" + tag.getTag_name() + "</div>");
		}
		String json = JSON.encode(
				new String[] 
				{qa_html,husen_html});
		return json;		
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
		
		Map<String, Integer> qa_id_with_date = new HashMap<String, Integer>();
		qa_id_with_date = get_qa_count_per_date(qa_plus_list);
		System.out.println("サイズ："+qa_id_with_date.size());
		
		for (QAPlusModel qa_plus : qa_plus_list)
		{
			List<MondaiModel> mondai_list = new ArrayList<MondaiModel>();
			mondai_list = qa_plus.getMondai_list();
						
			/**
			 * 問題HTML
			 */
			List<String> q_html = new ArrayList<String>();
			for (int i = 0; i < mondai_list.size(); i++)
			{
				String mondai = mondai_list.get(i).getQ_parts_text();
				String q_lang = mondai_list.get(i).getLanguage();
				String html = "<span id='" + mondai_list.get(i).getQ_id() + "' class='q' data-language='" + q_lang + "'>" + mondai + "</span>";			
				q_html.add(html);
			}
			
			/**
			 * 正答HTML
			 */
			List<SeitouModel> seitou_list = new ArrayList<SeitouModel>();
			seitou_list = qa_plus.getSeitou_list();
			
			List<String> a_html = new ArrayList<String>();
			for (int i = 0; i < seitou_list.size(); i++)
			{
				String seitou = seitou_list.get(i).getSeitou();
//				KaitouDao kaitou_dao = new KaitouDao();
//				int opacity = kaitou_dao.is_seikai(owner_db, seitou_list.get(i).getS_id());
				int opacity = seitou_list.get(i).getSeikai_flg();
				String mouseout = "";
				String checked = "";
				String a_lang = seitou_list.get(i).getLanguage();
				if (opacity == 0)
				{
					mouseout = "onmouseout='this.style.opacity=0'";
				}
				else
				{
					checked = "<img src='../img/check.png' class='check' />";					
				}
				String html = "<span id='" + seitou_list.get(i).getS_id() + "' class='a' style='opacity:" + opacity + "' onmouseover='this.style.opacity=1' " + mouseout + " onclick='change_seitou_color(this)' data-language='" + a_lang + "'>" + checked + seitou + "</span>";				
				a_html.add(html);
			}			
			
			if (seitou_list.size() > 0 && mondai_list.size() > 0)
			{
				String yomudake = "";
				for (SeitouModel seitou : seitou_list)
				{
					if (seitou.getSeitou().equals("読んだ"))
					{
						yomudake = " data-yomudake='true'";
					}
				}
				if (qa_id_with_date.containsKey(qa_plus.getQa().getQa_id()))
				{
					Util util = new Util();
					String sakuseibi_yyyyMMdd = util.getDay(qa_plus.getQa().getCreate_timestamp());
					String sakuseibi_MMdd = sakuseibi_yyyyMMdd.split("/")[1] + "/" + sakuseibi_yyyyMMdd.split("/")[2];
					int qa_cnt_per_date = qa_id_with_date.get(qa_plus.getQa().getQa_id());
					qa_html.append("<span id='" + sakuseibi_yyyyMMdd + "' class='date'>" + sakuseibi_MMdd + "（" + qa_cnt_per_date + "問）" + "</span>");					
				}
				qa_html.append("<span id='" + qa_plus.getQa().getQa_id() + "' class='qa' onmouseover='qa_mouseover(this)'" + yomudake + ">");
			}

			for (int i = 0; i < (mondai_list.size() + seitou_list.size()); i++)
			{
				if (qa_plus.getQa().getIs_start_with_q() == 1)
				{
					if (i < mondai_list.size())
					{
						qa_html.append(q_html.get(i));
					}
					if (i < seitou_list.size())
					{
						qa_html.append(a_html.get(i));
					}
				}
				else
				{
					if (i < seitou_list.size())
					{
						qa_html.append(a_html.get(i));
					}
					if (i < mondai_list.size())
					{
						qa_html.append(q_html.get(i));
					}
				}
			}
			if (seitou_list.size() > 0 && mondai_list.size() > 0)
			{
				qa_html.append("</span>");
			}
		}
	    stopwatch.stop(new Object(){}.getClass().getEnclosingMethod().getName());
		
		return qa_html.toString();
	}

	public Map<String, Integer> get_qa_count_per_date(List<QAPlusModel> qa_plus_list) {
		List<QAModel> qa_list = new ArrayList<QAModel>();
		for (QAPlusModel qa_plus : qa_plus_list)
		{
			QAModel qa = new QAModel();
			qa.setQa_id(qa_plus.getQa().getQa_id());
			Util util = new Util();
			String sakuseibi = util.getDay(qa_plus.getQa().getCreate_timestamp());
			qa.setCreate_timestamp(sakuseibi);
			//System.out.println("作成日"+sakuseibi);
			qa_list.add(qa);
		}
		
		Map<String, List<QAModel>> qa_id_per_date = 
				qa_list
				.stream()
				.filter(p -> p.getCreate_timestamp() != null)
				.collect(Collectors.groupingBy(QAModel::getCreate_timestamp));
		
		Map<String, Integer> qa_count_per_date = new HashMap<String, Integer>();
		for (Map.Entry<String, List<QAModel>> entry : qa_id_per_date.entrySet())
		{
			qa_count_per_date.put(entry.getValue().get(0).getQa_id(), 
							entry.getValue().size());
		}
				
		return qa_count_per_date;
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
		
		qa_map = create_qa_map(qa_map, spans);
		
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
			System.out.println("is_start_with_q:"+is_start_with_q);
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
		
		// 正答がないと強制的に読むだけ問題として扱う
		if (a_map.size() == 0)
		{
			a_map.put(2, "読んだ");
			yomudake_flg = "on";
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
		if (qa_type == Constant.QA_TYPE_1_ON_1)
		{
			qa.setIs_reversible(1);
		}
		else
		{
			qa.setIs_reversible(0);			
		}
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
		 * 1:1問題の場合、リバーシブル問題を作る
		 */
		if (qa_type == Constant.QA_TYPE_1_ON_1 && yomudake == 0)
		{
			QAModel qa_reversible = new QAModel();

			// 行番号・QA_ID生成用
			qa_max_no++;
			// 行番号
			qa_reversible.setRow_no(qa_max_no + 1); 
			// QA ID
			qa_id = qa_reversible.generate_qa_id(qa_max_no + 1, owner_id);
			qa_reversible.setQa_id(qa_id);
			// QAタイプ
			qa_reversible.setQa_type(qa_type);
		    // QA入力エリアのHTML
			qa_reversible.setQa_html(qa_input);
			// 読むだけ問題フラグ
			qa_reversible.setYomudake_flg(0);
		    // 問題と正答を入れ替えた結果生成された問題かどうか
			qa_reversible.setIs_reversible(1);
		    // 広告問題フラグ
			qa_reversible.setKoukoku_flg(0);
			// 重要度（５段階）
			qa_reversible.setJuyoudo(3);
			// 難易度（５段階）
			qa_reversible.setNanido(3);
			// 問題文と正答のうち問題から始まるかのフラグ
			qa_reversible.setIs_start_with_q(is_start_with_q);
			// 正答がたくさんある場合の問題文を分割した時の個数
			qa_reversible.setQ_split_cnt(q_map.size());
			// 問題に紐づく正答の個数
			qa_reversible.setSeitou_cnt(a_map.size());
			// 公開範囲
			qa_reversible.setKoukai_level(Constant.KOUKAI_LEVEL_SELF_ONLY);
			// 無料販売フラグ
			qa_reversible.setFree_flg(0);
			// 無料配布した数
			qa_reversible.setFree_sold_num(0);
			// 有料販売フラグ
			qa_reversible.setCharge_flg(0);
			// 有料で売った数
			qa_reversible.setCharge_sold_num(0);
			// 削除フラグ
			qa_reversible.setDel_flg(0);
			// 作成者
			qa_reversible.setCreate_owner(owner_id);
			// 更新者
			qa_reversible.setUpdate_owner(owner_id);
			// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
			qa_reversible.setCreate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
			// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
			qa_reversible.setUpdate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
			qa_plus.setQa(qa_reversible);
		}

		String reversed_mondai = "";
		String reversed_seitou = "";
		if (qa_type == Constant.QA_TYPE_1_ON_1 && yomudake == 0)
		{
			for (Map.Entry<Integer, String> entry : a_map.entrySet())
			{
				reversed_mondai = entry.getValue();
			}
			for (Map.Entry<Integer, String> entry : q_map.entrySet())
			{
				reversed_seitou = entry.getValue();
			}
		}
		
		/**
		 * 問題
		 */
		List<MondaiModel> mondai_list = new ArrayList<MondaiModel>();
		
		System.out.println("q_map.size():"+q_map.size());
				
		List<String> q_id_list = new ArrayList<String>();
		List<String> serif_q_list = new ArrayList<String>();
		
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
			q_id_list.add(q_id);
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
			serif_q_list.add(entry.getValue());
		    // QAの中に出てくる音声や画像などのバイナリファイル
			mondai.setQ_parts_binary(null);
			String language = Util.check_japanese_or_english(entry.getValue());
		    // 言語
			mondai.setLanguage(Util.check_japanese_or_english(entry.getValue()));
	//		mondai.setLanguage(Util.langDetect(mondai_input));
		    // テキスト読み上げデータ
			mondai.setYomiage(null);
		    // リバーシブル問題かどうか
		    mondai.setIs_reversible(0);
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
			
			if (qa_type == Constant.QA_TYPE_1_ON_1 && yomudake == 0)
			{
				MondaiModel mondai_reversed = new MondaiModel();
				
				// 行番号・Q_ID生成用
				q_max_no++;
		
			    // 行番号
				mondai_reversed.setRow_no(q_max_no + q_idx);
			    // 問題ID
				q_id = mondai_reversed.generate_q_id(q_max_no + q_idx, owner_id);
				q_id_list.add(q_id);
				q_idx++;
				mondai_reversed.setQ_id(q_id);
			    // QA ID
				mondai_reversed.setQa_id(qa_id);
			    // QA内での問題パーツの順番
				mondai_reversed.setJunban(entry.getKey());
			    // 問題パーツが文字であるかのフラグ
				mondai_reversed.setIs_text_flg(1);
			    // 問題パーツがバイナリであるかのフラグ
				mondai_reversed.setIs_binary_flg(0);
			    // 分割された問題文
				mondai_reversed.setQ_parts_text(reversed_mondai);
				serif_q_list.add(reversed_mondai);
			    // QAの中に出てくる音声や画像などのバイナリファイル
				mondai_reversed.setQ_parts_binary(null);
				language = Util.check_japanese_or_english(reversed_mondai);
			    // 言語
				mondai_reversed.setLanguage(Util.check_japanese_or_english(reversed_mondai));
		//		mondai.setLanguage(Util.langDetect(mondai_input));
			    // テキスト読み上げデータ
				mondai_reversed.setYomiage(null);
			    // リバーシブル問題かどうか
			    mondai_reversed.setIs_reversible(1);
			    // 削除フラグ
				mondai_reversed.setDel_flg(0);
			    // 作成者
				mondai_reversed.setCreate_owner(owner_id);
			    // 更新者
				mondai_reversed.setUpdate_owner(owner_id);
			    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
				mondai_reversed.setCreate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
			    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
				mondai_reversed.setUpdate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
				
				mondai_list.add(mondai_reversed);	
			}
		}
		
		System.out.println("mondai_list.size()aaaaa:"+mondai_list.size());
		qa_plus.setMondai_list(mondai_list);

		
		/**
		 * 正答
		 */
		List<SeitouModel> seitou_list = new ArrayList<SeitouModel>();
		List<String> s_id_list = new ArrayList<String>();
		List<String> serif_a_list = new ArrayList<String>();

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
			s_id_list.add(s_id);
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
			serif_a_list.add(entry.getValue());
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
			seitou.setYomiage(yomiage);
		    // リバーシブル問題かどうか
			seitou.setIs_reversible(0);
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
			
			if (qa_type == Constant.QA_TYPE_1_ON_1 && yomudake == 0)
			{
				SeitouModel seitou_reversed = new SeitouModel();
				
				// 行番号・S_ID生成用
				s_max_no++;
				
			    // 行番号
				seitou_reversed.setRow_no(s_max_no + a_idx);
			    // 正答ID
				s_id = seitou_reversed.generate_s_id(s_max_no + a_idx, owner_id);
				seitou_reversed.setS_id(s_id);
				s_id_list.add(s_id);
			    // QA ID
				seitou_reversed.setQa_id(qa_id);
			    // QA内での正答の順番
				seitou_reversed.setJunban(entry.getKey());
			    // 正答が文字であるかのフラグ
				seitou_reversed.setIs_text_flg(1);
			    // 正答がバイナリであるかのフラグ
				seitou_reversed.setIs_binary_flg(0);
			    // 正答
				seitou_reversed.setSeitou(reversed_seitou);
				serif_a_list.add(reversed_seitou);
			    // 正答が画像などのバイナリである場合に格納する
				seitou_reversed.setSeitou_binary(null);
			    // 重要度（５段階）
				seitou_reversed.setJuyoudo(3);
			    // 難易度（５段階）
				seitou_reversed.setNanido(3);
			    // 言語
		//		seitou.setLanguage(Util.langDetect(seitou_input));
				language = Util.check_japanese_or_english(reversed_seitou);
				seitou_reversed.setLanguage(language);
			    // テキスト読み上げデータ
				yomiage = null;
				seitou_reversed.setYomiage(yomiage);
			    // リバーシブル問題かどうか
				seitou_reversed.setIs_reversible(1);
			    // 削除フラグ
				seitou_reversed.setDel_flg(0);
			    // 作成者
				seitou_reversed.setCreate_owner(owner_id);
			    // 更新者
				seitou_reversed.setUpdate_owner(owner_id);
			    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
				seitou_reversed.setCreate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
			    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
				seitou_reversed.setUpdate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
		
				seitou_list.add(seitou_reversed);
				
				/**
				 * 回答の新規登録を追加
				 */
				KaitouModel kaitou_for_reversed = new KaitouModel();

				// 行番号・K_ID生成用
				k_max_no++;
			    // 行番号
				kaitou_for_reversed.setRow_no(k_max_no + a_idx);
				// 回答ID
				k_id = kaitou_for_reversed.generate_k_id(k_max_no + a_idx, owner_id);
				a_idx++;
				kaitou_for_reversed.setK_id(k_id);
				// QA ID
				kaitou_for_reversed.setQa_id(qa_id);
				// 正答ID
				kaitou_for_reversed.setS_id(s_id);
				// 正解フラグ
				kaitou_for_reversed.setSeikai_flg(0);
				// アクション・・・チェックを入れた、外した、解いて正解した、解いて不正解、等
				kaitou_for_reversed.setAction(Constant.ACTION_QA_TOUROKU);
				// アクション日時（H2DBのtimestampと同じフォーマットにする）
				kaitou_for_reversed.setAction_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
				// ユーザーが入力した回答
				kaitou_for_reversed.setKaitou("");
				// 言語
				kaitou_for_reversed.setLanguage("");
				// 削除フラグ
				kaitou_for_reversed.setDel_flg(0);
			    // 作成者
				kaitou_for_reversed.setCreate_owner(owner_id);
			    // 更新者
				kaitou_for_reversed.setUpdate_owner(owner_id);
			    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
				kaitou_for_reversed.setCreate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
			    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
				kaitou_for_reversed.setUpdate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
				
				kaitou_dao.insert_kaitou(owner_db, kaitou_for_reversed);
			}
		}
		
		qa_plus.setSeitou_list(seitou_list);
		
		QAPlusDao qa_plus_dao = new QAPlusDao();
		qa_plus_dao.insert_qa_plus(owner_db, qa_plus, false);		
		
		if (yomudake_flg.equals("on"))
		{
			QaTagRelationDao qa_tag_relation_dao = new QaTagRelationDao();
			Document huden_doc = Jsoup.parse(qa_husen);
			QaTagRelationModel qa_tag_relation = new QaTagRelationModel();
			TagDao tag_dao = new TagDao();
		    // 行番号
			qa_tag_relation.setRow_no(qa_tag_relation_dao.get_max_row_no(owner_db)+1);
		    // QA ID
			qa_tag_relation.setQa_id(qa_id);
		    // タグID
			qa_tag_relation.setTag_id(tag_dao.select_tag_id(owner_db, "読むだけ問題"));
		    // タグ内でのQAの順番
			qa_tag_relation.setJunban(qa_tag_relation_dao.get_max_junban(owner_db, qa_tag_relation.getTag_id())+1);
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
		
		if (qa_husen != null)
		{
			if (!qa_husen.equals(""))
			{
				System.out.println("qa_husen:"+qa_husen);
				QaTagRelationDao qa_tag_relation_dao = new QaTagRelationDao();
				Document huden_doc = Jsoup.parse(qa_husen);
				Elements husen_spans = huden_doc.getElementsByTag("span");
				for (Element husen_span: husen_spans) {
					// 読むだけ問題のタグは上で登録しているため、二重登録を防ぐ
					if (husen_span.text().equals("読むだけ問題"))
					{
						continue;
					}
					QaTagRelationModel qa_tag_relation = new QaTagRelationModel();
					TagDao tag_dao = new TagDao();
				    // 行番号
					qa_tag_relation.setRow_no(qa_tag_relation_dao.get_max_row_no(owner_db)+1);
				    // QA ID
					qa_tag_relation.setQa_id(qa_id);
				    // タグID
					qa_tag_relation.setTag_id(tag_dao.select_tag_id(owner_db, husen_span.text()));
				    // タグ内でのQAの順番
					qa_tag_relation.setJunban(qa_tag_relation_dao.get_max_junban(owner_db, qa_tag_relation.getTag_id())+1);
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
		
		create_speach(
				qa_id, 
				q_id_list, 
				s_id_list, 
				serif_q_list,
				serif_a_list);
		
		stop_watch.stop("create_qa");
	}
	
	private List<Thread> thread_list = new ArrayList<Thread>();

	private void create_speach(
			String qa_id, 
			List<String> q_id_list, 
			List<String> s_id_list, 
			List<String> serif_q_list,
			List<String> serif_a_list) {
		// 重いので非同期の別スレッドで処理
		new Thread(new Runnable() {
		    @Override
		    public void run() {
				try {		
					// q
					int idx = 0;
					for (String q_id : q_id_list)
					{
						String serif_q = serif_q_list.get(idx);
						String q_file_name = Constant.SPEECH_DATA_FOLDER_PATH + q_id + "_q.m4a";
						String q_language = Util.check_japanese_or_english(serif_q);
						String q_speaker = "";
						if (q_language.equals(Constant.ENGLISH))
						{
							q_speaker = "Alex";
						}
						else
						{
							q_speaker = "Kyoko";
						}
						String q_command = "say -v " + q_speaker + " '" + serif_q + "' -o " + q_file_name;
						Process p = Runtime.getRuntime().exec(q_command);
						p.waitFor();
						set_executable(q_file_name, qa_id);		
						String q_tmp_file = Constant.SPEECH_DATA_TEMP_FOLDER_PATH + "/" + q_id + "_q.m4a";
						FileUtils.copyFile(new File(q_file_name), new File(q_tmp_file));
						FileUtils.copyFile(new File(q_tmp_file), new File(q_file_name));						
						idx++;
					}
					
					// a
					idx = 0;
					for (String s_id : s_id_list)
					{
						String serif_a = serif_a_list.get(idx);
						String a_file_name = Constant.SPEECH_DATA_FOLDER_PATH + s_id + "_a.m4a";
						String s_language = Util.check_japanese_or_english(serif_a);
						String s_speaker = "";
						if (s_language.equals(Constant.ENGLISH))
						{
							s_speaker = "Alex";
						}
						else
						{
							s_speaker = "Kyoko";
						}
						String a_command = "say -v " + s_speaker + " '" + serif_a + "' -o " + a_file_name;
						Process p2 = Runtime.getRuntime().exec(a_command);
						p2.waitFor();
						set_executable(a_file_name, qa_id);	
						String a_tmp_file = Constant.SPEECH_DATA_TEMP_FOLDER_PATH + "/" + s_id + "_a.m4a";
						FileUtils.copyFile(new File(a_file_name), new File(a_tmp_file));
						FileUtils.copyFile(new File(a_tmp_file), new File(a_file_name));						
					
						// slime
						String speaker = "Vicki";
						if (s_language.equals(Constant.JAPANESE))
						{
							serif_a = SlimeSerif.Japanese_to_Roman(serif_a).replace("'", "");
						}
						String file_name = Constant.SPEECH_DATA_FOLDER_PATH + s_id + ".wav";
						String command = "say --data-format=LEF32@8000 -r 50 -v " + speaker + " '" + serif_a + "' -o " + file_name;
						System.out.println(command);
						Process p3 = Runtime.getRuntime().exec(command);
						p3.waitFor();
						set_executable(file_name, qa_id);
						String command2 = "/usr/local/bin/ffmpeg -i " + file_name + " -filter:a asetrate=r=18K -vn " + file_name.replace("wav", "m4a");
						Process p4 = Runtime.getRuntime().exec(command2);
						p4.waitFor();
						set_executable(file_name, qa_id);
						String slime_file = Constant.SPEECH_DATA_FOLDER_PATH + s_id + ".m4a";
						String slime_tmp_file = Constant.SPEECH_DATA_TEMP_FOLDER_PATH + "/" + s_id + ".m4a";
						FileUtils.copyFile(new File(slime_file), new File(slime_tmp_file));
						FileUtils.copyFile(new File(slime_tmp_file), new File(slime_file));						
						idx++;
					}
//					String speech = Constant.SPEECH_DATA_FOLDER_PATH
//					.substring(0, Constant.SPEECH_DATA_FOLDER_PATH.length()-1);
//					String speech_tmp = speech + "tmp";
//					File speech_folder = new File(speech);
//					File speech_tmp_folder = new File(speech_tmp);
//					FileUtils.copyDirectory(speech_folder,speech_tmp_folder);
//					FileUtils.deleteDirectory(new File(speech));
//					File speech_folder2 = new File(speech);
//					FileUtils.copyDirectory(speech_tmp_folder,speech_folder2);					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}).start();
	}

	private void create_slime_speech(String qa_id, String id, String serif) {
		// 重いので非同期の別スレッドで処理
		new Thread(new Runnable() {
		    @Override
		    public void run() {
				try {				
					String speaker = "Vicki";
					String file_name = Constant.SPEECH_DATA_FOLDER_PATH + id + ".wav";
					String command = "say --data-format=LEF32@8000 -r 50 -v " + speaker + " '" + serif + "' -o " + file_name;
					System.out.println(command);
					Process p = Runtime.getRuntime().exec(command);
					p.waitFor();
					set_executable(file_name, qa_id);
					String command2 = "/usr/local/bin/ffmpeg -i " + file_name + " -filter:a asetrate=r=18K -vn " + file_name.replace("wav", "m4a");
					Process p2 = Runtime.getRuntime().exec(command2);
					p2.waitFor();
					set_executable(file_name, qa_id);

//					String speech = Constant.SPEECH_DATA_FOLDER_PATH
//							.substring(0, Constant.SPEECH_DATA_FOLDER_PATH.length()-1);
//					String speech_tmp = speech + "tmp";
//					File speech_folder = new File(speech);
//					File speech_tmp_folder = new File(speech_tmp);
//					FileUtils.copyDirectory(speech_folder,speech_tmp_folder);
//					FileUtils.deleteDirectory(new File(speech));
//					File speech_folder2 = new File(speech);
//					FileUtils.copyDirectory(speech_tmp_folder,speech_folder2);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}).start();
	}

	public void set_executable(String file_name, String qa_id) throws InterruptedException {
		Path path = Paths.get(file_name);
		if (Files.notExists(path)) {
			Thread.sleep(100);
			set_executable(file_name, qa_id);		  
		}
		else
		{
			Boolean a = path.toFile().setExecutable(true, false);
		}
//		String folder = Constant.SPEECH_DATA_FOLDER_PATH
//				.substring(Constant.SPEECH_DATA_FOLDER_PATH.length()-1);
//		File speech_folder = new File(folder);
//		if (speech_folder.exists())
//		{
//			try(Stream<Path> paths = Files.walk(Paths.get(folder))) {
//			    paths.forEach(filePath -> {
//			        if (Files.isRegularFile(filePath)) {
//			        	File speech_file = filePath.toFile();
//			    		Boolean a = speech_file.setExecutable(true, false);
//			        }
//			    });
//			} 
//			catch(Exception ex)
//			{
//				ex.printStackTrace();			
//			}
//			return;
//		}
//		else
//		{
//			set_executable(file_name, qa_id);
//			return;
//		}
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
		
		qa_map = create_qa_map(qa_map, spans);
		
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

		// 正答がないと強制的に読むだけ問題として扱う
		if (a_map.size() == 0)
		{
			a_map.put(2, "読んだ");
			yomudake_flg = "on";
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
		qa_plus_dao.insert_qa_plus(owner_db, qa_plus, true);		
		
		if (yomudake_flg.equals("on"))
		{
			QaTagRelationDao qa_tag_relation_dao = new QaTagRelationDao();
			Document huden_doc = Jsoup.parse(qa_husen);
			QaTagRelationModel qa_tag_relation = new QaTagRelationModel();
			TagDao tag_dao = new TagDao();
		    // 行番号
			qa_tag_relation.setRow_no(qa_tag_relation_dao.get_max_row_no(owner_db)+1);
		    // QA ID
			qa_tag_relation.setQa_id(qa_id);
		    // タグID
			qa_tag_relation.setTag_id(tag_dao.select_tag_id(owner_db, "読むだけ問題"));
		    // タグ内でのQAの順番
			qa_tag_relation.setJunban(qa_tag_relation_dao.get_max_junban(owner_db, qa_tag_relation.getTag_id())+1);
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

		if (qa_husen != null)
		{
			if (!qa_husen.equals(""))
			{
				QaTagRelationDao qa_tag_relation_dao = new QaTagRelationDao();
				Document huden_doc = Jsoup.parse(qa_husen);
				Elements husen_spans = huden_doc.getElementsByTag("span");
				for (Element husen_span: husen_spans) {
					// 読むだけ問題のタグは上で登録しているため、二重登録を防ぐ
					if (husen_span.text().equals("読むだけ問題"))
					{
						continue;
					}					
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


	public Map<String, String> create_qa_map(Map<String, String> qa_map, Elements spans) {
		/**
		 * qが連続した場合はつなげる、aが連続した場合はつなげる
		 */
		int idx = 0;
		int junban = 1;
		
		
		for (Element span: spans) {
			System.out.println("span.className():"+span.className());
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
		System.out.println("qa_map.size():"+qa_map.size());
		return qa_map;
	}	
}

