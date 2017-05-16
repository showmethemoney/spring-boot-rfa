package com.mycompany.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.mycompany.reuters.RobustFoundationAPI;

@Component
public class RFAApplicationListener implements ApplicationListener<ApplicationReadyEvent>
{
	protected static final Logger logger = LoggerFactory.getLogger( RFAApplicationListener.class );

	@Autowired(required = true)
	private RobustFoundationAPI robustFoundationAPI = null;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		logger.info( "{}", null == robustFoundationAPI );

		if (null != robustFoundationAPI) {
			new DispatchThread( robustFoundationAPI.getEventQueue() ).run();
		}
	}

}
