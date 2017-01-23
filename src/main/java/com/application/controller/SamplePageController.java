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
import com.cybozu.*;
import com.GPix.GPix;
import com.GPix.GPix.GPixException;
import com.GPix.Image;
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

public class SamplePageController extends MainPageController {

	public void sample(String owner_id,
						HttpServletRequest request, 
						HttpServletResponse response, 
						HttpSession session,
						Model model) {
		Log log = new Log();
		log.insert_error_log("INFO", "sample method start.");
		
		System.out.println("---------------------------");
		
		TopPageController top = new TopPageController();
		top.setAutoLoginToken(owner_id,session,request,response);
		
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
		
		// ソート用付箋
		String husen_sort_html = generate_husen_sort_html(owner_db);
		model.addAttribute("tags_sort", husen_sort_html);
		
		// ページング総数
		QADao qa_dao = new QADao();
		int total_pages = qa_dao.get_pages(owner_db, "");
		model.addAttribute("total_pages", total_pages);			
	}
}