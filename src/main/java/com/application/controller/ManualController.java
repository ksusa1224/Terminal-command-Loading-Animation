package com.application.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dao.H2dbDao;

@Controller
public class ManualController {

	@RequestMapping(value={"/manual/{file_name}.html"},method=RequestMethod.GET)
	public String logout(
			HttpSession session,
			@PathVariable String file_name)
	{		
		return "manual/"+file_name;
	}
}
