package com.application.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.application.controller.dao.ForumDao;
import com.application.model.dao.CommentModel;
import com.application.model.dao.ForumModel;
import com.common.Constant;
import com.common.Log;
import com.common.Util;
import com.email.MailSend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ForumController {

	private Log log = new Log();
	
	@RequestMapping(value={"/forum/index.html"})
	public String index(HttpServletRequest request, Model model)
	{
		log.insert_access_log(request, "", "");

		ForumDao dao = new ForumDao();
		dao.create_forum_db();
		
		List<ForumModel> forums = new ArrayList<ForumModel>();
		forums = dao.get_forums();
		
		model.addAttribute("forums", forums);
		
		return "forum/index";
	}
	
	@RequestMapping(value={"/forum/bug_report.html"})
	public String bug_report(
			HttpServletRequest request,
			Model model,
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="email", required=false) String email,
			@RequestParam(value="comment", required=false) String comment)
	{
		log.insert_access_log(request, "", "");

		String forum_name = "不具合報告";
		String file_name = "bug_report.html";
		
		ForumDao dao = new ForumDao();
		dao.create_forum_db();
		if (Util.isNullOrEmpty(comment))
		{
			dao.insert_forum_data(
					forum_name, 
					file_name, 
					Constant.FORUM_ID_BUG_REPORT, 
					null, 
					null, 
					null);
		}
		else
		{
			dao.insert_forum_data(
					forum_name, 
					file_name, 
					Constant.FORUM_ID_BUG_REPORT, 
					name,
					email, 
					comment);			
			
			MailSend mail = new MailSend();
			mail.forum(forum_name, name, email, comment);			
		}
		
		List<CommentModel> comments = new ArrayList<CommentModel>();
		comments = dao.get_comments(Constant.FORUM_ID_BUG_REPORT);
		
		model.addAttribute("comments", comments);		
		
		return "forum/bug_report";
	}
	
	@RequestMapping(value={"/forum/chat.html"})
	public String chat(
			HttpServletRequest request,
			Model model,
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="email", required=false) String email,
			@RequestParam(value="comment", required=false) String comment)
	{
		log.insert_access_log(request, "", "");

		String forum_name = "雑談";
		String file_name = "chat.html";
		
		ForumDao dao = new ForumDao();
		dao.create_forum_db();
		if (Util.isNullOrEmpty(comment))
		{
			dao.insert_forum_data(
					forum_name, 
					file_name, 
					Constant.FORUM_ID_CHAT, 
					null, 
					null, 
					null);
		}
		else
		{
			dao.insert_forum_data(
					forum_name, 
					file_name, 
					Constant.FORUM_ID_CHAT, 
					name,
					email, 
					comment);			
			
			MailSend mail = new MailSend();
			mail.forum(forum_name, name, email, comment);			
		}
		
		List<CommentModel> comments = new ArrayList<CommentModel>();
		comments = dao.get_comments(Constant.FORUM_ID_CHAT);
		
		model.addAttribute("comments", comments);
				
		return "forum/chat";
	}
	
	@RequestMapping(value={"/forum/impression.html"})
	public String impression(
			HttpServletRequest request,
			Model model,
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="email", required=false) String email,
			@RequestParam(value="comment", required=false) String comment)
	{
		log.insert_access_log(request, "", "");

		String forum_name = "感想";
		String file_name = "impression.html";
		
		ForumDao dao = new ForumDao();
		dao.create_forum_db();
		if (Util.isNullOrEmpty(comment))
		{
			dao.insert_forum_data(
					forum_name, 
					file_name, 
					Constant.FORUM_ID_IMPRESSION, 
					null, 
					null, 
					null);
		}
		else
		{
			dao.insert_forum_data(
					forum_name, 
					file_name, 
					Constant.FORUM_ID_IMPRESSION, 
					name,
					email, 
					comment);			
			
			MailSend mail = new MailSend();
			mail.forum(forum_name, name, email, comment);			
		}
		
		List<CommentModel> comments = new ArrayList<CommentModel>();
		comments = dao.get_comments(Constant.FORUM_ID_IMPRESSION);
		
		model.addAttribute("comments", comments);
		
		return "forum/impression";
	}
	
	@RequestMapping(value={"/forum/request.html"})
	public String request(
			HttpServletRequest request,
			Model model,
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="email", required=false) String email,
			@RequestParam(value="comment", required=false) String comment)
	{
		log.insert_access_log(request, "", "");

		String forum_name = "要望";
		String file_name = "request.html";
		
		ForumDao dao = new ForumDao();
		dao.create_forum_db();
		if (Util.isNullOrEmpty(comment))
		{
			dao.insert_forum_data(
					forum_name, 
					file_name, 
					Constant.FORUM_ID_REQUEST, 
					null, 
					null, 
					null);
		}
		else
		{
			dao.insert_forum_data(
					forum_name, 
					file_name, 
					Constant.FORUM_ID_REQUEST, 
					name,
					email, 
					comment);			
			
			MailSend mail = new MailSend();
			mail.forum(forum_name, name, email, comment);			
		}
		
		List<CommentModel> comments = new ArrayList<CommentModel>();
		comments = dao.get_comments(Constant.FORUM_ID_REQUEST);
		
		model.addAttribute("comments", comments);
		
		return "forum/request";
	}	

	@RequestMapping(value={"/forum/question.html"})
	public String question(
			HttpServletRequest request,
			Model model,
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="email", required=false) String email,
			@RequestParam(value="comment", required=false) String comment)
	{
		log.insert_access_log(request, "", "");

		String forum_name = "質問";
		String file_name = "question.html";
		
		ForumDao dao = new ForumDao();
		dao.create_forum_db();
		if (Util.isNullOrEmpty(comment))
		{
			dao.insert_forum_data(
					forum_name, 
					file_name, 
					Constant.FORUM_ID_QUESTION, 
					null, 
					null, 
					null);
		}
		else
		{
			dao.insert_forum_data(
					forum_name, 
					file_name, 
					Constant.FORUM_ID_QUESTION, 
					name,
					email, 
					comment);			
			
			MailSend mail = new MailSend();
			mail.forum(forum_name, name, email, comment);			
		}
		
		List<CommentModel> comments = new ArrayList<CommentModel>();
		comments = dao.get_comments(Constant.FORUM_ID_QUESTION);
		
		model.addAttribute("comments", comments);
		
		return "forum/question";
	}
}
