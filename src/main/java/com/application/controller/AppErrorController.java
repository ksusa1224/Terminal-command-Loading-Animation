package com.application.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;

import com.common.Log;

@Controller("error")
public class AppErrorController implements ErrorController {

	private static final String PATH = "main";
	
	@Override
    public String getErrorPath() {
		Log log = new Log();
		log.insert_error_log("ERROR", "application error");		
        return PATH;
    }
}
