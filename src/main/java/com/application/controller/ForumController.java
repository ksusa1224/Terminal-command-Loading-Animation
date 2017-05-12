package com.application.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dao.H2dbDao;

@Controller
public class ForumController {

	@RequestMapping(value={"/forum/index.html"},method=RequestMethod.GET)
	public String index(HttpSession session)
	{
		return "forum/index";
	}
	
	@RequestMapping(value={"/forum/bug_report.html"})
	public String bug_report(HttpSession session)
	{
		return "forum/bug_report";
	}
	
	@RequestMapping(value={"/forum/chat.html"})
	public String chat(HttpSession session)
	{
		return "forum/chat";
	}
	
	@RequestMapping(value={"/forum/impression.html"})
	public String impression(HttpSession session)
	{
		return "forum/impression";
	}
	
	@RequestMapping(value={"/forum/request.html"})
	public String request(HttpSession session)
	{
		return "forum/request";
	}	
}
