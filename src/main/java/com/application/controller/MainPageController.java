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

import com.application.controller.dao.QADao;
import com.application.model.LoginInfoModel;
import com.application.model.dao.QAModel;
import com.common.AES;
import com.common.StringBuilderPlus;
import com.dao.SQliteDAO;

@Controller
public class MainPageController {

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
	public String main(@PathVariable("owner_id") String owner_id,
						HttpServletRequest request, 
						HttpSession session,
						Model model) {
		
		String request_url = request.getRequestURI();
		String response_url = "/"+ owner_id + "/main.html";
		model.addAttribute("owner_id", owner_id);
		   
		// TODO 認証されてるかどうかはsessionに入れると書き換えられてしまうから毎回DBに接続した方がいいかな
		Boolean is_authenticated = (Boolean)session.getAttribute("is_authenticated");
		if (is_authenticated == false)
		{
			return "index";
		}
		String session_owner_id = (String)session.getAttribute("owner_id");
		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		AES aes = new AES();
		String owner_db = aes.decrypt(encrypted_owner_db);
		
		if(owner_id.equals(session_owner_id) && is_authenticated == true)		
		{
			QAModel qa = new QAModel();
			SQliteDAO dao = new SQliteDAO();
			QADao qa_dao = new QADao();
			StringBuilderPlus sql = new StringBuilderPlus();
			sql = qa_dao.insert_qa(qa);
			//dao.update(owner_db, sql);
			System.out.println("問題登録しました");
			List<QAModel> qa_list = new ArrayList<QAModel>();
			qa_list = qa_dao.select_qa_list(owner_db, qa_list);
			model.addAttribute("qa_list", qa_list);
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

}
