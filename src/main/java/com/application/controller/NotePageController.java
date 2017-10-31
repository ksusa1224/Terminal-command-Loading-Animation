package com.application.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.application.service.NotePageService;
import com.common.Log;

@Controller
public class NotePageController {
	
	@Autowired
	private NotePageService notePageService;
	
	@RequestMapping(value={"/{owner_id}/",
			   "/{owner_id}/note.html", 
			   "/{owner_id}/note.html#", 
			   "/{owner_id}/note.htm", 
			   "/{owner_id}/note"},
				method=RequestMethod.GET)
	public String main(@PathVariable("owner_id") String owner_id,
				HttpServletRequest request, 
				HttpServletResponse response, 
				HttpSession session,
				@RequestParam(value="husen_str", required=false) String husen_names,
				Model model) 
	{
		Log log = new Log();
		log.insert_error_log("INFO", "note page main method start.");
		String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
		log.insert_access_log(request, owner_id, method_name);
		if(Log.getClientOS(request).equals("Android") || Log.getClientOS(request).equals("iPhone"))
		{
			model.addAttribute("pc", false);
		}
		else
		{
			model.addAttribute("pc", true);			
		}
		
		TopPageController top = new TopPageController();
		if ((top.isLogin(request,response,session,owner_id) == false &&
			owner_id.equals("sample") == false))
		{
			return "redirect:/";
		}
		
		String html = notePageService.getNoteHtml(session, owner_id, husen_names);			

		model.addAttribute("html", html);
		
		return "note";
	}
}
