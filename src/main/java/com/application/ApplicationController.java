package com.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.dao.SQliteDAO;
import com.model.TopModel;
import com.application.*;

@Controller
public class ApplicationController  {
	@RequestMapping("/")
	public String index() {
        return "index";
	}
	    
    @RequestMapping(value="/", method=RequestMethod.POST)
	public String createUser(Model model) {
		SQliteDAO sqlite_dao = new SQliteDAO();
		sqlite_dao.createSQliteDB();
        return "index";
    }
}
