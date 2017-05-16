package com.mycompany.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.reuters.rfa.common.EventQueue;

public class DispatchThread extends Thread
{
	protected static final Logger logger = LoggerFactory.getLogger( DispatchThread.class );
	private EventQueue eventQueue = null;
	
	public DispatchThread() {
	}

	public DispatchThread(EventQueue eventQueue) {
		this.eventQueue = eventQueue;
	}

	@Override
	public void run() {
		while (true) {
			try {
				eventQueue.dispatch( 10000 );
				logger.info( "run..." );
				Thread.sleep( 10000 );
			} catch (Throwable cause) {
				logger.error( cause.getMessage(), cause );

				throw new RuntimeException( cause );
			}
		}
	}
}