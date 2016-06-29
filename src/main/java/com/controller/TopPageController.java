package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dao.SQliteDAO;

@Controller
public class TopPageController {
	  @RequestMapping(value="/", method=RequestMethod.POST)
		public String createUser(Model model) {
			SQliteDAO sqlite_dao = new SQliteDAO();
			sqlite_dao.createSQliteDB();
	      return "index";
	  }
}
