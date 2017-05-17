package com.mycompany.controller;

import java.util.Calendar;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class IndexController
{
	protected static final Logger logger = LoggerFactory.getLogger( IndexController.class );
	// @Autowired
	// private RobustFoundationAPI robustFoundationAPI = null;
	@Autowired
	private CacheManager cacheManager = null;

	@RequestMapping("/hello")
	@ResponseBody
	public String home() {
		logger.info( "hello" );

		return "Hello World!";
	}

	@RequestMapping("/sendCache")
	@ResponseBody
	public String sendCache() {
		logger.info( "sendCache" );
		try {

			Cache cache = cacheManager.getCache( "indexes" );
			String datetime = DateFormatUtils.format( Calendar.getInstance(), "yyyy-MM-dd hh:mm:ss" );
			cache.put( "abc", datetime );
			
			Thread.sleep( 1000 );
			datetime = DateFormatUtils.format( Calendar.getInstance(), "yyyy-MM-dd hh:mm:ss" );
			cache.put( "def", datetime );
			
			Thread.sleep( 2000 );
			datetime = DateFormatUtils.format( Calendar.getInstance(), "yyyy-MM-dd hh:mm:ss" );
			cache.put( "ghi", datetime );
			
			Thread.sleep( 3000 );
			datetime = DateFormatUtils.format( Calendar.getInstance(), "yyyy-MM-dd hh:mm:ss" );
			cache.put( "abc", datetime );
		} catch (Throwable cause) {
			logger.error( cause.getMessage(), cause );
		}

		return "Hello Send Cache!";
	}

	// @RequestMapping("/run")
	// @ResponseBody
	// public String run() {
	// logger.info( "send request" );
	//
	// List<String> identifiers = new ArrayList<String>();
	// List<String> fields = new ArrayList<String>();
	//
	// identifiers.add( "JPY=" );
	// identifiers.add( "CNH=" );
	// identifiers.add( "TWD=" );
	// fields.add( "ASK" );
	// fields.add( "BID" );
	//
	// robustFoundationAPI.sendRequest( identifiers, fields );
	//
	// return "Hello World!";
	// }
}
