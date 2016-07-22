package com.application.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
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

		String filepath = "static/css/index.css"; // src/main/resources 配下の相対パス
        Resource resource = resourceLoader.getResource("classpath:" + filepath);
        try {
			File file = resource.getFile();
			System.out.println("----------------------");
			System.out.println(file.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (request_url.equals(response_url))
		{
			return "index";
		}
		else
		{
			return "redirect:" + response_url;
		}
	}

	@Autowired
    ResourceLoader resourceLoader;
	
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
						   "/{user_id}/main"})
	public String main(@PathVariable("user_id") String user_id,
						HttpServletRequest request) {
		
		String request_url = request.getRequestURI();
		String response_url = "/"+ user_id + "/main.html";
		System.out.println("----------------------");
		System.out.println(request_url);
		
//		String path = "";
		
		String filepath = "static/css/main.css"; // src/main/resources 配下の相対パス
        Resource resource = resourceLoader.getResource("classpath:" + filepath);
//        try {
//			File file = resource.getFile();
//			System.out.println("----------------------");
//			System.out.println(file.getAbsolutePath());
//			//path = file.getAbsolutePath();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
		// TODO セッションから取得
		// TODO ログインチェック
		// TODO DBに存在するかチェック
		if(user_id.equals("vsky"))		
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
//			return "redirect:../css/main2.css";
//			return "redirect:" + "/css/main.css";
//			return "../css/main.css";
		}
//		else if (request_url.equals("/css/main.css"))
//		{
//			return path;			
//		}
//		else
//		{
////			return "error";
//			return path;
//		}
	}	    
}
