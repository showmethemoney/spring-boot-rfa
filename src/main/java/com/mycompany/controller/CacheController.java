package com.mycompany.controller;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Controller
public class CacheController
{
	protected static final Logger logger = LoggerFactory.getLogger( IndexController.class );
	@Autowired
	private CacheManager cacheManager = null;
	
	@RequestMapping("/cache")
	@ResponseBody
	public String cache() {
		for (String cacheName : cacheManager.getCacheNames()) {
			logger.info( "cacheName : {}", cacheName );
			
			Cache cache = cacheManager.getCache( cacheName );
			
			Ehcache ehCache = (Ehcache) cache.getNativeCache();
			
			Iterator<String> iter = (Iterator<String>) ehCache.getKeys().iterator();
			
			while(iter.hasNext()) {
				String key = iter.next();
				String value = null;
				Element element = (Element) ehCache.get( key );
				
				if (null != element) {
					value = (String) element.getObjectValue();
				}
				logger.info( "key : {}, value : {}", key, value );
			}
		}
		
		return "Hello Cache";
	}
}
