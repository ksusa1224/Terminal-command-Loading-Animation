package com.application.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.application.model.LoginInfoModel;
import com.common.AES;
import com.common.Constant;
import com.dao.H2dbDao;

@Controller
public class SettingController {

	@RequestMapping(value={"/{owner_id}/settings.html"},
					method=RequestMethod.GET)
	public String main(@PathVariable("owner_id") String owner_id,
			HttpServletRequest request, 
			HttpServletResponse response, 
			HttpSession session,
			Model model) 
	{
		if (session.getAttribute("owner_id") != null)
		{
			owner_id = (String)session.getAttribute("owner_id");
		}
		model.addAttribute("owner_id", owner_id);
		
		H2dbDao h2dao = new H2dbDao();
		LoginInfoModel login_info = new LoginInfoModel();
		login_info = h2dao.select_login_info(owner_id);
		model.addAttribute("email", login_info.getEmail());
		model.addAttribute("owner_name", login_info.getOwner_name());
		AES aes = new AES();
		String password = aes.decrypt(login_info.getEncrypted_password());
		model.addAttribute("password", password.replaceAll(".", "●"));
		String owner_type = null;
		if (login_info.getKakin_type() == Integer.parseInt(Constant.KAKIN_TYPE_FREE))
		{
			owner_type = Constant.OWNER_TYPE_GENERAL;
		}
		else if (login_info.getKakin_type() == Integer.parseInt(Constant.KAKIN_TYPE_PREMIUM))
		{
			owner_type = Constant.OWNER_TYPE_PREMIUM;
		}
		else if (login_info.getKakin_type() == Integer.parseInt(Constant.KAKIN_TYPE_FREE_PREIMIUM))
		{
			owner_type = Constant.OWNER_TYPE_FREE_PREMIUM;
		}
		model.addAttribute("owner_type", owner_type);
		
		return "settings";
	}

	@RequestMapping(value={"/{owner_id}/edit_settings.html"},
			method=RequestMethod.GET)
	public String edit_settings(@PathVariable("owner_id") String owner_id,
		HttpServletRequest request, 
		HttpServletResponse response, 
		HttpSession session,
		Model model) 
	{	
//		if (session.getAttribute("owner_id") != null)
//		{
//			owner_id = (String)session.getAttribute("owner_id");
//		}
		model.addAttribute("owner_id", "Owner ID : " + owner_id);
		
		H2dbDao h2dao = new H2dbDao();
		LoginInfoModel login_info = new LoginInfoModel();
		login_info = h2dao.select_login_info(owner_id);
		model.addAttribute("email_edit", login_info.getEmail());
		model.addAttribute("owner_name", login_info.getOwner_name());
		AES aes = new AES();
		String password = aes.decrypt(login_info.getEncrypted_password());
		model.addAttribute("password", password);
		String owner_type = null;
		if (login_info.getKakin_type() == Integer.parseInt(Constant.KAKIN_TYPE_FREE))
		{
			owner_type = Constant.OWNER_TYPE_GENERAL;
		}
		else if (login_info.getKakin_type() == Integer.parseInt(Constant.KAKIN_TYPE_PREMIUM))
		{
			owner_type = Constant.OWNER_TYPE_PREMIUM;
		}
		else if (login_info.getKakin_type() == Integer.parseInt(Constant.KAKIN_TYPE_FREE_PREIMIUM))
		{
			owner_type = Constant.OWNER_TYPE_FREE_PREMIUM;
		}
		model.addAttribute("owner_type", owner_type);
		
		return "edit_settings";
	}

}
