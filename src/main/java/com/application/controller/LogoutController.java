package com.application.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dao.H2dbDao;

@Controller
public class LogoutController {

	@RequestMapping(value={"/logout.html"},method=RequestMethod.GET)
	public String logout(HttpSession session)
	{
		String owner_id = (String)session.getAttribute("owner_id");

		session.invalidate();
		
		H2dbDao dao = new H2dbDao();
		dao.update_token_for_logout(owner_id);
		
		return "redirect:index.html";
	}
}
