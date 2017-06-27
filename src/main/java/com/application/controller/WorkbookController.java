package com.application.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.application.service.WorkbookService;

@Controller
public class WorkbookController {

	@Autowired
	private WorkbookService workbookService;
	
	@RequestMapping(value={"/workbook.html"}, method=RequestMethod.GET)
	public String workbook(HttpSession session)
	{
		String owner_id = (String)session.getAttribute("owner_id");		
		return "index";
	}
}
