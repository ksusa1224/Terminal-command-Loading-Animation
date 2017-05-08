package com.application.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.application.model.LoginInfoModel;
import com.dao.H2dbDao;
import com.email.MailSend;

@Controller
public class WithdrawController {

	@RequestMapping(value={"/{owner_id}/withdraw.html"},method=RequestMethod.GET)
	public String withdraw(HttpSession session, @PathVariable("owner_id") String owner_id)
	{
		String session_owner_id = (String)session.getAttribute("owner_id");

		if (owner_id.equals(session_owner_id))
		{
			H2dbDao dao = new H2dbDao();
			LoginInfoModel info = new LoginInfoModel();
			info = dao.select_login_info(owner_id);
			MailSend mail = new MailSend();
			mail.withdraw(owner_id, info.getEmail(), info.getOwner_name());
			dao.withdraw(owner_id);
			TopPageController top = new TopPageController();
			top.stop_paypal(owner_id);
		}
		
		return "redirect:../index.html?withdraw=true";
	}
}
