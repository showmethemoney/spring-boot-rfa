package com.mycompany.reuters.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.mycompany.bean.ItemIntSpec;
import com.mycompany.reuters.GenericOMMParser;
import com.mycompany.reuters.RobustFoundationAPI;
import com.mycompany.reuters.event.ItemEvent;
import com.mycompany.reuters.event.ItemMapEvent;
import com.reuters.rfa.common.Handle;
import com.reuters.rfa.omm.OMMMsg;
import com.reuters.rfa.omm.OMMPool;
import com.reuters.rfa.rdm.RDMInstrument;
import com.reuters.rfa.rdm.RDMMsgTypes;
import com.reuters.rfa.session.omm.OMMItemIntSpec;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;


@Component
public class ItemManager
{
	protected static final Logger logger = LoggerFactory.getLogger( ItemManager.class );
	// private List<Handle> itemHandles = new ArrayList<Handle>();
	@Autowired
	private RobustFoundationAPI instance = null;
	@Autowired
	private GenericOMMParser parser = null;
	@Autowired
	private CacheManager cacheManager = null;

	public ItemManager() {}

	public ItemManager(RobustFoundationAPI instance) {
		this.instance = instance;
	}

	/**
	 * 送 request 之前，確認是否有之前的發送紀錄
	 */
	public void sendRequest(Collection<String> identifiers, Collection<String> fields) {
		logger.info( "SendRequest: Sending item requests" );

		closeRequest();

		send( identifiers, fields );
	}

	/**
	 * 傳入每個 identify 有興趣的 fields
	 * 
	 * 送 request 之前，確認是否有之前的發送紀錄
	 */
	public void sendRequest(List<ItemIntSpec> itemIntSpecs) {
		logger.info( "SendRequest: Sending item requests" );

		closeRequest();

		// 取得這次要訂閱的 identifiers 給 ItemEvent 判斷是不是這次要的
		Map<String, ItemIntSpec> itemIntSpecMap = new HashMap<String, ItemIntSpec>();
		for (ItemIntSpec itemIntSpec : itemIntSpecs) {
			itemIntSpecMap.put( itemIntSpec.getIdentifiy(), itemIntSpec );
		}

		send( itemIntSpecMap );
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
			Cache cache = cacheManager.getCache( RobustFoundationAPI.NAMED_HANDLE_CACHE );
			Ehcache handlesCache = (Ehcache) cache.getNativeCache();

			if (0 != handlesCache.getSize()) {
				// 之前的 Handle, 要先 unregist
				Iterator<String> iter = (Iterator<String>) handlesCache.getKeys().iterator();

				String key =  null;
				while (iter.hasNext()) {
					key = iter.next();
					Element element = (Element) handlesCache.get( key );

					unregisterClient( (Handle) element.getObjectValue() );
				}

				// spring cache implements
				cache.clear();
			}

			// while (!itemHandles.isEmpty()) {
			// unregisterClient( itemHandles.remove( 0 ) );
			// }
			//
			// itemHandles.clear();
			// itemHandles = null;
		} catch (Throwable cause) {
			logger.error( cause.getMessage(), cause );
		}
	}

	protected void send(Collection<String> identifiers, Collection<String> fields) {
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

		Cache cache = cacheManager.getCache( RobustFoundationAPI.NAMED_HANDLE_CACHE );

		while (iterator.hasNext()) {
			itemName = iterator.next();
			logger.info( "Subscribing Identifier: " + itemName );

			ommmsg.setAttribInfo( serviceName, itemName, RDMInstrument.NameType.RIC );

			// Set the message into interest spec
			ommItemIntSpec.setMsg( ommmsg );

			ItemEvent event = new ItemEvent( instance, identifiers, fields, parser );

			Handle itemHandle = instance.getOmmConsumer().registerClient( instance.getEventQueue(), ommItemIntSpec, event, null );

			// 加到 Cache 裡面
			cache.put( itemName, itemHandle );

			// itemHandles.add( itemHandle );
		}

		pool.releaseMsg( ommmsg );
	}

	protected void send(Map<String, ItemIntSpec> itemIntSpecMap) {
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
		Set<String> intSpecKeys = itemIntSpecMap.keySet();
		Iterator<String> iterator = intSpecKeys.iterator();
		
		String itemName = null;

		Cache cache = cacheManager.getCache( RobustFoundationAPI.NAMED_HANDLE_CACHE );

		while (iterator.hasNext()) {
			itemName = iterator.next();
			logger.info( "Subscribing Identifier: " + itemName );

			ommmsg.setAttribInfo( serviceName, itemName, RDMInstrument.NameType.RIC );

			// Set the message into interest spec
			ommItemIntSpec.setMsg( ommmsg );

			ItemMapEvent event = new ItemMapEvent( instance, itemIntSpecMap, parser );

			Handle itemHandle = instance.getOmmConsumer().registerClient( instance.getEventQueue(), ommItemIntSpec, event, null );

			// 加到 Cache 裡面
			cache.put( itemName, itemHandle );

			// itemHandles.add( itemHandle );
		}

		pool.releaseMsg( ommmsg );
	}

	// public List<Handle> getItemHandles() {
	// return itemHandles;
	// }
	//
	// public void setItemHandles(List<Handle> itemHandles) {
	// this.itemHandles = itemHandles;
	// }
}
