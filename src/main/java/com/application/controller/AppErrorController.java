package com.application.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;

@Controller("error")
public class AppErrorController implements ErrorController {

	private static final String PATH = "error";
	
	@Override
    public String getErrorPath() {
        return PATH;
    }
}
