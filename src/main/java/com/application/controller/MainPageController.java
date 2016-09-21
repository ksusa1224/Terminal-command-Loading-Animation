package com.application.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

import com.application.controller.dao.MondaiDao;
import com.application.controller.dao.QADao;
import com.application.controller.dao.QAPlusDao;
import com.application.controller.dao.SeitouDao;
import com.application.model.LoginInfoModel;
import com.application.model.dao.MondaiModel;
import com.application.model.dao.QAModel;
import com.application.model.dao.QAPlusModel;
import com.application.model.dao.SeitouModel;
import com.common.AES;
import com.common.Constant;
import com.common.StringBuilderPlus;
import com.common.Util;
import com.dao.SQliteDAO;
import com.slime.SlimeSerif;

@Controller
public class MainPageController {

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
		
		String request_url = request.getRequestURI();
		String response_url = "/"+ owner_id + "/main.html";
		   
		// TODO 認証されてるかどうかはsessionに入れると書き換えられてしまうから毎回DBに接続した方がいいかな
		Boolean is_authenticated = (Boolean)session.getAttribute("is_authenticated");
		String session_owner_id = (String)session.getAttribute("owner_id");
		
		if(owner_id.equals(session_owner_id) && is_authenticated == true)		
		{
			if (request_url.equals(response_url))
			{
				byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
				AES aes = new AES();
				String owner_db = aes.decrypt(encrypted_owner_db);
				String qa_html = generate_qa_html(select_qa_plus(owner_db));			
				model.addAttribute("qa_html", qa_html);
				// 正答総数
				QADao qa_dao = new QADao();
				int seitou_sum = qa_dao.get_seitou_sum(owner_db);
				model.addAttribute("seitou_sum", seitou_sum);
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
	 * メインページ（暗記ノート本体）
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
						@RequestParam(value="yomudake_flg", required=false) String yomudake_flg,
						@RequestParam(value="reversible_flg", required=false) String reversible_flg) {
		
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
			
			create_qa(owner_id, owner_db, qa_input, yomudake_flg, reversible_flg);
			

			//model.addAttribute("qa_plus_list", select_qa_plus(owner_db));
			
			String qa_html = generate_qa_html(select_qa_plus(owner_db));			
			model.addAttribute("qa_html", qa_html);
			
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
	 * @param a_input
	 * @return
	 */
	@RequestMapping(value={"/register_qa.html"}, method=RequestMethod.GET)
	public @ResponseBody String ajax_reload(
			HttpSession session,
			Model model,
			@RequestParam("qa_input") String qa_input,
			@RequestParam(value="yomudake_flg", required=false) String yomudake_flg,
			@RequestParam(value="reversible_flg", required=false) String reversible_flg
			) {

		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		AES aes = new AES();
		String owner_db = aes.decrypt(encrypted_owner_db);
		   
		// TODO 認証されてるかどうかはsessionに入れると書き換えられてしまうから毎回DBに接続した方がいいかな
		Boolean is_authenticated = (Boolean)session.getAttribute("is_authenticated");
		if (is_authenticated == false)
		{
			return "index";
		}
		String owner_id = (String)session.getAttribute("owner_id");
		System.out.println(owner_id);
		
		if (yomudake_flg == null)
		{
			yomudake_flg = "off";
		}
		if (reversible_flg == null)
		{
			reversible_flg = "off";
		}
		System.out.println(yomudake_flg);
		System.out.println(reversible_flg);
			
		create_qa(owner_id, owner_db, qa_input, yomudake_flg, reversible_flg);
		
		String qa_html = generate_qa_html(select_qa_plus(owner_db));			

//		// 正答総数
//		QADao qa_dao = new QADao();
//		int seitou_sum = qa_dao.get_seitou_sum(owner_db);
//		model.addAttribute("seitou_sum", seitou_sum);

		return qa_html;
	}	

	/**
	 * Ajaxで正答総数を取得
	 * @param a_input
	 * @return
	 */
	@RequestMapping(value={"/seitou_sum.html"}, method=RequestMethod.GET)
	public @ResponseBody int ajax_get_seitou_sum(HttpSession session) {

		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		AES aes = new AES();
		String owner_db = aes.decrypt(encrypted_owner_db);
		   
		// 正答総数
		QADao qa_dao = new QADao();
		int seitou_sum = qa_dao.get_seitou_sum(owner_db);
		
		return seitou_sum;
	}	
	
	/**
	 * すらスラ〜のセリフAjaxページ
	 * @return
	 */
	@RequestMapping(value={"/serif.html"}, method=RequestMethod.GET)
	public @ResponseBody String serif(@RequestParam(value = "a") String a_input) {
		SlimeSerif slime_serif = new SlimeSerif();
		String serif = slime_serif.RamdomSerifArg1(a_input);		
		return serif;
	}	
	
	/**
	 * 
	 * @param owner_db
	 * @return
	 */
	public List<QAPlusModel> select_1_on_1_qa_plus(String owner_db) {
		QAPlusDao qa_plus_dao = new QAPlusDao();
		List<QAPlusModel> qa_plus_list = new ArrayList<QAPlusModel>();
		qa_plus_list = qa_plus_dao.select_1_on_1_qa_plus_list_speedy(owner_db, qa_plus_list);
		return qa_plus_list;
	}
	
	
	/**
	 * 
	 * @param owner_db
	 * @return
	 */
	public List<QAPlusModel> select_qa_plus(String owner_db) {
		QAPlusDao qa_plus_dao = new QAPlusDao();
		List<QAPlusModel> qa_plus_list = new ArrayList<QAPlusModel>();
		qa_plus_list = qa_plus_dao.select_qa_plus_list(owner_db, qa_plus_list);
		return qa_plus_list;
	}

	/**
	 * 
	 * @return
	 */
	public String generate_qa_html(List<QAPlusModel> qa_plus_list)
	{
		String qa_html = "";

		System.out.println(qa_plus_list.size());
		
		for (QAPlusModel qa_plus : qa_plus_list)
		{
//			QAModel qa = qa_plus.getQa();
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
				String html = "<span class='q' onclick=\"control_qa_saisei('" + mondai + "', '" + "　" + "','" + q_lang + "','" + Constant.ENGLISH + "');\"'>" + mondai + "</span>";			
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
				String html = "<span class='a' onmouseover='this.style.opacity=1' onmouseout='this.style.opacity=0'>" + seitou + "</span>";
				a_html.add(html);
			}	
			
			qa_html += "<span class='qa'>";
			for (int i = 0; i < (mondai_list.size() + seitou_list.size()); i++)
			{
				if (qa_plus.getQa().getIs_start_with_q() == 1)
				{
					if (i < q_html.size())
					{
						qa_html += q_html.get(i);
					}
					if (i < a_html.size())
					{
						qa_html += a_html.get(i);
					}
				}
				else
				{
					if (i < a_html.size())
					{
						qa_html += a_html.get(i);
					}
					if (i < q_html.size())
					{
						qa_html += q_html.get(i);
					}
				}
			}
			qa_html += "</span>";
			//qa_html += "<div id='bottom_border' style='width:100%'></div>";		
		}
		
		return qa_html;
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
			String yomudake_flg,
			String reversible_flg)
	{
		System.out.println("qa_input"+qa_input);
		
		// 順番、問題文/正答
		Map<String,String> qa_map = new HashMap<String,String>();
		
		Document doc = Jsoup.parse(qa_input);
		Elements spans = doc.getElementsByTag("span");

		int is_start_with_q = 0;
		
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
		    System.out.println(entry.getKey() + "/" + entry.getValue());
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
		qa.setQa_type(qa_type);
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
			
			// 行番号・Q_ID生成用
			SeitouDao seitou_dao = new SeitouDao();
			int s_max_no = seitou_dao.get_seitou_max_row_no(owner_db);
			
		    // 行番号
			seitou.setRow_no(s_max_no + a_idx);
		    // 正答ID
			String s_id = seitou.generate_s_id(s_max_no + a_idx, owner_id);
			a_idx++;
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
		}
		
		qa_plus.setSeitou_list(seitou_list);
		
		QAPlusDao qa_plus_dao = new QAPlusDao();
		qa_plus_dao.insert_qa_plus(owner_db, qa_plus);		
	}	
}
