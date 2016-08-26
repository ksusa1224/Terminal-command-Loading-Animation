package com.application.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

public class MainPageController {

	/**
	 * メインページ（暗記ノート本体）
	 * 
	 * Spring MVC　@PathVariableを使ってURLに含まれる動的なパラメータを取得
	 * http://blog.codebook-10000.com/entry/20140301/1393628782
	 * @param user_id
	 * @return
	 */
//	@RequestMapping(value={"/{owner_id}", 
//						   "/{owner_id}/", 
//						   "/{owner_id}/main.html", 
//						   "/{owner_id}/main.htm", 
//						   "/{owner_id}/main"})
//	public String main(@PathVariable("owner_id") String owner_id,
//						HttpServletRequest request, 
//						HttpSession session) {
//		
//		String request_url = request.getRequestURI();
//		String response_url = "/"+ owner_id + "/main.html";
//		   
//		// TODO 認証されてるかどうかはsessionに入れると書き換えられてしまうから毎回DBに接続した方がいいかな
//		Boolean is_authenticated = (Boolean)session.getAttribute("is_authenticated");
//		String session_owner_id = (String)session.getAttribute("owner_id");
//		
//		if(owner_id.equals(session_owner_id) && is_authenticated == true)		
//		{
//			System.out.println("問題登録しました");
//			if (request_url.equals(response_url))
//			{
//				return "main";
//			}
//			else
//			{
//				return "redirect:" + response_url;
//			}
//		}		
//		else
//		{
//			return "error";
//		}
//	}	    

}
