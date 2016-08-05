package com.application.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.common.AES;
import com.dao.SQliteDAO;

@Controller
public class TopPageController {
	  // TODO 入力された文字列でDBを作成する
	  @RequestMapping(value="/", method=RequestMethod.POST)
	  public String createUser(HttpSession session) {
			SQliteDAO sqlite_dao = new SQliteDAO();
			String db_name = sqlite_dao.createSQliteDB("vsky");
			
			AES aes = new AES();
			byte[] encrypted_db_name = aes.encrypt(db_name);
			
			// セッションに暗号化されたユーザ専用DB名を格納
			session.setAttribute("db", encrypted_db_name);
			byte[] a = (byte[])session.getAttribute("db");
			String b = aes.decrypt(a);
			System.out.println("a:"+a);
			System.out.println("b:"+b);
			String c = session.getId();
			System.out.println("c:"+c);
			
			String original = aes.decrypt(encrypted_db_name);
//			System.out.print(original);

	      return "index";
	  }
}
