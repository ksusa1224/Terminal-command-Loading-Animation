package com.application.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
						@RequestParam("mondai") String mondai,
						@RequestParam("seitou") String seitou) {
		System.out.println("test");
		
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
			create_1_on_1_qa(owner_id, owner_db, mondai, seitou);

			

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
	 * TODO 現状１問１答式問題しか対応していない
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
			List<SeitouModel> seitou_list = new ArrayList<SeitouModel>();
			seitou_list = qa_plus.getSeitou_list();
			String mondai = mondai_list.get(0).getQ_parts_text();
			String seitou = seitou_list.get(0).getSeitou();
			String q_lang = mondai_list.get(0).getLanguage();
			String a_lang = seitou_list.get(0).getLanguage();
			qa_html += "<span class='qa'>";
			qa_html += "<span class='q' onclick=\"control_qa_saisei('" + mondai + "', '" + seitou + "','" + q_lang + "','" + a_lang + "');\"'>" + mondai + "</span>";			
			qa_html += "<span class='a' onmouseover='this.style.opacity=1' onmouseout='this.style.opacity=0'>" + seitou + "</span>";
			qa_html += "</span>";
			
//			// 問題から始まる場合
//			if (qa.getIs_start_with_q() == 1)
//			{
//				if(mondai_list.size() > 0)
//				{
//					int index = 0;
//					qa_html.appendLine(mondai_list.get(index).getQ_parts_text());
//					qa_html.appendLine(seitou_list.get(index).getSeitou());
//				}
//			}		
		}
		
		return qa_html;
	}
	
	
	/**
	 * １問１答式問題を作成する
	 * @param owner_id
	 * @param owner_db
	 */
	public void create_1_on_1_qa(
								String owner_id, 
								String owner_db, 
								String mondai_input, 
								String seitou_input) 
	{
		if (mondai_input.equals("") || seitou_input.equals(""))
		{
			return;
		}
		
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
		qa.setQa_type(Constant.QA_TYPE_1_ON_1);
		// 読むだけ問題フラグ
		qa.setYomudake_flg(0);
	    // 問題と正答を入れ替えた結果生成された問題かどうか
		qa.setIs_reversible(0);
	    // 広告問題フラグ
		qa.setKoukoku_flg(0);
		// 重要度（５段階）
		qa.setJuyoudo(3);
		// 難易度（５段階）
		qa.setNanido(3);
		// 問題文と正答のうち問題から始まるかのフラグ
		qa.setIs_start_with_q(1);
		// 正答がたくさんある場合の問題文を分割した時の個数
		qa.setQ_split_cnt(1);
		// 問題に紐づく正答の個数
		qa.setSeitou_cnt(1);
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
		MondaiModel mondai = new MondaiModel();

		// 行番号・Q_ID生成用
		MondaiDao mondai_dao = new MondaiDao();
		int q_max_no = mondai_dao.get_mondai_max_row_no(owner_db);

	    // 行番号
		mondai.setRow_no(q_max_no + 1);
	    // 問題ID
		String q_id = mondai.generate_q_id(q_max_no + 1, owner_id);
		mondai.setQ_id(q_id);
	    // QA ID
		mondai.setQa_id(qa_id);
	    // QA内での問題パーツの順番
		mondai.setJunban(1);
	    // 問題パーツが文字であるかのフラグ
		mondai.setIs_text_flg(1);
	    // 問題パーツがバイナリであるかのフラグ
		mondai.setIs_binary_flg(0);
	    // 分割された問題文
		mondai.setQ_parts_text(mondai_input);
	    // QAの中に出てくる音声や画像などのバイナリファイル
		mondai.setQ_parts_binary(null);
	    // 言語
		mondai.setLanguage(Util.check_japanese_or_english(mondai_input));
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
		
		qa_plus.setMondai_list(mondai_list);

		/**
		 * 正答
		 */
		List<SeitouModel> seitou_list = new ArrayList<SeitouModel>();
		SeitouModel seitou = new SeitouModel();
		
		// 行番号・Q_ID生成用
		SeitouDao seitou_dao = new SeitouDao();
		int s_max_no = seitou_dao.get_seitou_max_row_no(owner_db);
		
	    // 行番号
		seitou.setRow_no(s_max_no + 1);
	    // 正答ID
		String s_id = seitou.generate_s_id(s_max_no + 1, owner_id);
		seitou.setS_id(s_id);
	    // QA ID
		seitou.setQa_id(qa_id);
	    // QA内での正答の順番
		seitou.setJunban(2);
	    // 正答が文字であるかのフラグ
		seitou.setIs_text_flg(1);
	    // 正答がバイナリであるかのフラグ
		seitou.setIs_binary_flg(0);
	    // 正答
		seitou.setSeitou(seitou_input);
	    // 正答が画像などのバイナリである場合に格納する
		seitou.setSeitou_binary(null);
	    // 重要度（５段階）
		seitou.setJuyoudo(3);
	    // 難易度（５段階）
		seitou.setNanido(3);
	    // 言語
//		seitou.setLanguage(Util.langDetect(seitou_input));
		seitou.setLanguage(Util.check_japanese_or_english(seitou_input));
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
		
		qa_plus.setSeitou_list(seitou_list);
		
		QAPlusDao qa_plus_dao = new QAPlusDao();
		qa_plus_dao.insert_qa_plus(owner_db, qa_plus);		
	}	    
}
