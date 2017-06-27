package com.application.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.application.controller.dao.QAPlusDao;
import com.application.model.LoginInfoModel;
import com.application.model.dao.QAPlusModel;
import com.common.AES;
import com.dao.H2dbDao;

@Service
public class NotePageService {
		
	@Autowired
	MainPageService mainPageService;

	public String getNoteHtml(HttpSession session, String owner_id, String husen_names)
	{
		H2dbDao h2dao = new H2dbDao();
		LoginInfoModel login_info = new LoginInfoModel();
		login_info = h2dao.select_login_info(owner_id);
		byte[] encrypted_owner_db = null;
		if (session.getAttribute("owner_db") != null)
		{
			encrypted_owner_db = (byte[])session.getAttribute("owner_db");					
		}
		else
		{
			encrypted_owner_db = login_info.getEncrypted_db_name();
		}
		AES aes = new AES();
		String owner_db = aes.decrypt(encrypted_owner_db);
		List<QAPlusModel> qa_plus_list = new ArrayList<QAPlusModel>();
		if (StringUtils.isEmpty(husen_names))
		{
			qa_plus_list = mainPageService.select_qa_plus(owner_db, null, null);		
		}
		else
		{
			qa_plus_list = mainPageService.select_qa_plus_by_tag(owner_db, husen_names, null, null);
		}
		return mainPageService.generate_qa_html(qa_plus_list, owner_db);
	}
}
