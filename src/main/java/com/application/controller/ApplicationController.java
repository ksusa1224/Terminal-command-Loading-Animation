package com.application.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.common.Constant;
import com.common.StopWatch;
import com.common.Util;
import com.dao.H2dbDao;

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
	 * 3DCGサンプル
	 * @return
	 */
	@RequestMapping(value={
			   "/slime",
			   "/slime.html",
			   "/slime.htm"}, 
			   method=RequestMethod.GET)
	public String cat(HttpServletRequest request)
	{
		String request_url = request.getRequestURI();
		String response_url = "/slime.html";
		
		if (request_url.equals(response_url))
		{
			return "slime";
		}
		else
		{
			return "redirect:" + response_url;
		}
	}
}
