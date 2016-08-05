package com.application.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.common.Constant;
import com.common.StopWatch;
import com.common.Util;

@Controller
public class ApplicationController {
	
	/**
	 * コンストラクタ
	 */
	ApplicationController()
	{
		// TODO どのタイミングでどのフォルダのパーミッションを変更するか
		Util.lock_folder_or_file(Constant.DB_FOLDER_PATH);
		Util.unlock_folder_or_file(Constant.DB_FOLDER_PATH);
	}
	
	/**
	 * トップページ
	 * @return
	 */
	@RequestMapping(value={"",
						   "/",
						   "/index",
						   "/index.html",
						   "/index.htm"}, 
						   method=RequestMethod.GET)
	public String index(HttpServletRequest request) {
		String request_url = request.getRequestURI();
		String response_url = "/index.html";
		
		if (request_url.equals(response_url))
		{
			return "index";
		}
		else
		{
			return "redirect:" + response_url;
		}
	}
	
	/**
	 * メインページ（暗記ノート本体）
	 * 
	 * Spring MVC　@PathVariableを使ってURLに含まれる動的なパラメータを取得
	 * http://blog.codebook-10000.com/entry/20140301/1393628782
	 * @param user_id
	 * @return
	 */
	@RequestMapping(value={"/{user_id}", 
						   "/{user_id}/", 
						   "/{user_id}/main.html", 
						   "/{user_id}/main.htm", 
						   "/{user_id}/main"})
	public String main(@PathVariable("user_id") String user_id,
						HttpServletRequest request) {
		
		String request_url = request.getRequestURI();
		String response_url = "/"+ user_id + "/main.html";
		   
		// TODO セッションから取得
		// TODO ログインチェック
		// TODO DBに存在するかチェック
		if(user_id.equals("vsky") || user_id.equals("miho"))		
		{
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
