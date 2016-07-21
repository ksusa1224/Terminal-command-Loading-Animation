package com.application.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.dao.SQliteDAO;
import com.application.*;
import com.application.model.TopPageModel;

@Controller
public class ApplicationController  {
	
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
	 * http://blog.codebook-10000.com/entry/20140301/1393628782
	 * @param user_id
	 * @return
	 */
	@RequestMapping(value={"/{user_id}", 
						   "/{user_id}/", 
						   "/{user_id}/main.html", 
						   "/{user_id}/main.htm", 
						   "/{user_id}/main"},
							method=RequestMethod.GET)
	public String main(@PathVariable("user_id") String user_id,HttpServletRequest request) {
		
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
