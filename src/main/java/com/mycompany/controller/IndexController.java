package com.mycompany.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mycompany.reuters.client.ItemManager;

@Controller
public class IndexController
{
	protected static final Logger logger = LoggerFactory.getLogger( IndexController.class );
	@Autowired
	private ItemManager itemManager = null;
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

	@RequestMapping("/run")
	@ResponseBody
	public String run() {
		logger.info( "send request" );

		List<String> identifiers = new ArrayList<String>();
		List<String> fields = new ArrayList<String>();

		identifiers.add( "JPY=" );
		fields.add( "ASK" );
		fields.add( "BID" );

		itemManager.sendRequest( identifiers, fields );

		return "Hello World!";
	}
	
	@RequestMapping("/run2")
	@ResponseBody
	public String run2() {
		logger.info( "send request 2 " );

		List<String> identifiers = new ArrayList<String>();
		List<String> fields = new ArrayList<String>();

		identifiers.add( "USD=" );
		fields.add( "ASK" );
		fields.add( "BID" );

		itemManager.sendRequest( identifiers, fields );

		return "Hello World! 2";
	}
}
