package com.mycompany.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mycompany.reuters.RobustFoundationAPI;


@Controller
public class IndexController
{
	protected static final Logger logger = LoggerFactory.getLogger( IndexController.class );
	@Autowired
	private RobustFoundationAPI robustFoundationAPI = null;
	
	@RequestMapping("/hello")
	@ResponseBody
	public String home() {
		logger.info( "hello" );
		
		return "Hello World!";
	}
	
	@RequestMapping("/run")
	@ResponseBody
	public String run() {
		logger.info( "send request" );
		
		List<String> identifiers = new ArrayList<String>();
		List<String> fields = new ArrayList<String>();
		
		identifiers.add( "JPY=" );
		identifiers.add( "CNH=" );
		identifiers.add( "TWD=" );
		fields.add( "ASK" );
		fields.add( "BID" );
		
		robustFoundationAPI.sendRequest( identifiers, fields );
		
		return "Hello World!";
	}
}
