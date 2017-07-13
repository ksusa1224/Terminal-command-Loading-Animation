package com.application.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.application.model.dao.TagModel;
import com.application.service.WorkbookService;
import com.common.AES;

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
		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		AES aes = new AES();
		String owner_db = aes.decrypt(encrypted_owner_db);
		List<TagModel> tags_kouho_list = workbookService.getTagsKouhoForMondaishu(owner_db, qa_id_list);
		String husen_html = workbookService.createTagsKouhoForMondaishuHtml(tags_kouho_list);
		model.addAttribute("husen_html", husen_html);
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
