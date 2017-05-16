package com.mycompany.reuters.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.reuters.RobustFoundationAPI;
import com.mycompany.reuters.event.ItemEvent;
import com.reuters.rfa.common.Handle;
import com.reuters.rfa.omm.OMMMsg;
import com.reuters.rfa.omm.OMMPool;
import com.reuters.rfa.rdm.RDMInstrument;
import com.reuters.rfa.rdm.RDMMsgTypes;
import com.reuters.rfa.session.omm.OMMItemIntSpec;

public class ItemManager
{
	protected static final Logger logger = LoggerFactory.getLogger( ItemManager.class );
	private RobustFoundationAPI instance = null;
	private List<Handle> itemHandles = new ArrayList<Handle>();

	public ItemManager() {
	}

	public ItemManager(RobustFoundationAPI instance) {
		this.instance = instance;
	}

	public void sendRequest(Collection<String> identifiers, Collection<String> fields) {
		logger.info( "SendRequest: Sending item requests" );

		String serviceName = instance.getServiceName();

		OMMItemIntSpec ommItemIntSpec = new OMMItemIntSpec();

		// Preparing item request message
		OMMPool pool = instance.getPool();
		OMMMsg ommmsg = pool.acquireMsg();

		ommmsg.setMsgType( OMMMsg.MsgType.REQUEST );
		ommmsg.setMsgModelType( RDMMsgTypes.MARKET_PRICE );
		ommmsg.setIndicationFlags( OMMMsg.Indication.REFRESH );
		ommmsg.setPriority( (byte) 1, 1 );

		// Setting OMMMsg with negotiated version info from login handle
		if (null != instance.getLoginClient().getHandler()) {
			ommmsg.setAssociatedMetaInfo( instance.getLoginClient().getHandler() );
		}

		// register for each item
		Iterator<String> iterator = identifiers.iterator();

		String itemName = null;

		while (iterator.hasNext()) {
			itemName = iterator.next();
			logger.info( "Subscribing Identifier: " + itemName );

			ommmsg.setAttribInfo( serviceName, itemName, RDMInstrument.NameType.RIC );

			// Set the message into interest spec
			ommItemIntSpec.setMsg( ommmsg );

			ItemEvent event = new ItemEvent( instance, identifiers, fields );

			Handle itemHandle = instance.getOmmConsumer().registerClient( instance.getEventQueue(), ommItemIntSpec, event, null );
			itemHandles.add( itemHandle );
		}

		// QuoteStream.addItem = false;

		pool.releaseMsg( ommmsg );
	}
		
	public boolean unregisterClient(Handle handle) {
		boolean result = false;
		
		try {
			instance.getOmmConsumer().unregisterClient( handle );
			result = true;
		} catch (Throwable cause) {
			logger.error( cause.getMessage(), cause );
		}
		
		return result;
	}
	
	public void closeRequest() {
		try {
			while (!itemHandles.isEmpty()) {
				unregisterClient( itemHandles.remove( 0 ) );
			}
			
			itemHandles.clear();
			itemHandles = null;
		} catch (Throwable cause) {
			logger.error( cause.getMessage(), cause );
		}
	}

	public List<Handle> getItemHandles() {
		return itemHandles;
	}

	public void setItemHandles(List<Handle> itemHandles) {
		this.itemHandles = itemHandles;
	}
}
