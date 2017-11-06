package com.application.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.common.Log;
import com.dao.H2dbDao;

@Controller
public class LogoutController {

	@RequestMapping(value={"/logout.html"},method=RequestMethod.GET)
	public String logout(HttpSession session, HttpServletRequest request)
	{
		String owner_id = (String)session.getAttribute("owner_id");

		session.invalidate();
		
		H2dbDao dao = new H2dbDao();
		dao.update_token_for_logout(owner_id, Log.getClientOS(request), Log.getClientBrowser(request));
		
		return "redirect:index.html";
	}
}
