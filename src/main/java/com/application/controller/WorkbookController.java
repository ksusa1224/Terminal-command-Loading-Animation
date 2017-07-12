package com.application.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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
		return "workbook";
	}

	@RequestMapping(value={"/{owner_id}/create_workbook.html"}, method=RequestMethod.GET)
	public String create_workbook(
			HttpSession session,
			Model model,
			@PathVariable("owner_id") String owner_id)
	{
		model.addAttribute("message", "検索中の全問題から問題集を作成します。");
		List<String> qa_id_list = (List<String>)session.getAttribute("qa_id_list");
		return "create_workbook";
	}

	@RequestMapping(value={"/{owner_id}/create_workbook.html"}, method=RequestMethod.POST)
	public String create_workbook_complete(
			HttpSession session,
			@PathVariable("owner_id") String owner_id)
	{
		return "create_workbook";
	}
}
