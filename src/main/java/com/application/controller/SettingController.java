package com.application.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.application.controller.dao.SystemDao;
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
			@RequestParam(value="edit", required=false) String from_edit,			
			@RequestParam(value="email", required=false) String email,			
			@RequestParam(value="owner_name", required=false) String owner_name,			
			@RequestParam(value="login_password_new", required=false) String new_password,			
			@RequestParam(value="default_search_order", required=false) String default_search_order,			
			Model model) 
	{
		TopPageController top = new TopPageController();
		if ((top.isLogin(request,response,session,owner_id) == false &&
			owner_id.equals("sample") == false))
		{
			return "redirect:/";
		}
				
		System.out.println("from_edit:"+from_edit);
		
		// 更新画面から戻ってきたときは、設定情報を更新する
		if (from_edit != null && from_edit.equals("true"))
		{
			H2dbDao dao = new H2dbDao();
			dao.update_settings(owner_id, email, owner_name, new_password);
			System.out.println("PPPPPPPPPPPPP");
			SystemDao sys_dao = new SystemDao();
			byte[] encrypted_db = dao.get_owner_db(owner_id);
			AES aes = new AES();
			String owner_db = aes.decrypt(encrypted_db);
			top.insert_system_initial_data(owner_db, owner_id);
			sys_dao.update_value(owner_db, "0001", "デフォルトソート順", default_search_order);
		}
		
		if (session.getAttribute("owner_id") != null)
		{
			if (!owner_id.equals(session.getAttribute("owner_id")))
			{
				return "setting_error";
			}
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
		
//		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		byte[] encrypted_owner_db = h2dao.get_owner_db(owner_id);
		AES aes2 = new AES();
		String owner_db = aes2.decrypt(encrypted_owner_db);

//		SystemDao system_dao = new SystemDao();
//		default_search_order =  system_dao.get_value(owner_db, "0001", "デフォルトソート順");
//		model.addAttribute("default_search_order", default_search_order);
		
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
		TopPageController top = new TopPageController();
		if ((top.isLogin(request,response,session,owner_id) == false &&
			owner_id.equals("sample") == false))
		{
			return "redirect:/";
		}
		
		model.addAttribute("owner_id", "Owner ID : " + owner_id);
		
		H2dbDao h2dao = new H2dbDao();
		LoginInfoModel login_info = new LoginInfoModel();
		login_info = h2dao.select_login_info(owner_id);
		model.addAttribute("email_edit", login_info.getEmail());
		model.addAttribute("owner_name", login_info.getOwner_name());
		AES aes = new AES();
		String password = aes.decrypt(login_info.getEncrypted_password());
		model.addAttribute("password", "Password（now） : "+password);
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
		
//		byte[] encrypted_owner_db = (byte[])session.getAttribute("owner_db");
		byte[] encrypted_owner_db = h2dao.get_owner_db(owner_id);
		AES aes2 = new AES();
		String owner_db = aes2.decrypt(encrypted_owner_db);

//		SystemDao system_dao = new SystemDao();
//		String default_search_order =  system_dao.get_value(owner_db, "0001", "デフォルトソート順");
//		model.addAttribute("default_search_order", default_search_order);

		return "edit_settings";
	}

}
