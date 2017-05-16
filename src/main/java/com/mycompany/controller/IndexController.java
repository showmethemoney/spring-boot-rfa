package com.mycompany.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class IndexController
{
	protected static final Logger logger = LoggerFactory.getLogger( IndexController.class );

	@RequestMapping("/hello")
	@ResponseBody
	public String home() {
		logger.info( "hello" );
		return "Hello World!";
	}
}
