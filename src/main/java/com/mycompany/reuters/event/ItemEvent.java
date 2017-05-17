package com.mycompany.reuters.event;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mycompany.reuters.GenericOMMParser;
import com.mycompany.reuters.RobustFoundationAPI;
import com.reuters.rfa.common.Client;
import com.reuters.rfa.common.Event;
import com.reuters.rfa.omm.OMMAttribInfo;
import com.reuters.rfa.omm.OMMMsg;
import com.reuters.rfa.session.omm.OMMItemEvent;

@Component
public class ItemEvent implements Client
{
	protected static final Logger logger = LoggerFactory.getLogger( ItemEvent.class );
	private RobustFoundationAPI instance = null;
	private Collection<String> identifiers = null;
	private Collection<String> fields = null;
	private GenericOMMParser parser = null;

	public ItemEvent() {
	}

	public ItemEvent(RobustFoundationAPI instance, Collection<String> identifiers, Collection<String> fields, GenericOMMParser parser) {
		this.instance = instance;
		this.identifiers = identifiers;
		this.fields = fields;
		this.parser = parser;
	}

	/**
	 * MESSAGE Msg Type: MsgType.UPDATE_RESP Msg Model Type: MARKET_PRICE Indication Flags: DO_NOT_CONFLATE Hint Flags: HAS_ATTRIB_INFO | HAS_RESP_TYPE_NUM |
	 * HAS_SEQ_NUM SeqNum: 22782 RespTypeNum: 0 (UNSPECIFIED) AttribInfo ServiceName: API_ELEKTRON_EPD_RSSL ServiceId: 2115 Name: JPY= NameType: 1 (RIC)
	 * Payload: 13 bytes FIELD_LIST FIELD_ENTRY 114/BID_NET_CH: -0.28 FIELD_ENTRY 372/IRGPRC: -0.25
	 */
	public void processEvent(Event event) {
		logger.info( "event type : {}", event.getType() );
		if (event.getType() == Event.COMPLETION_EVENT) {
			return;
		}

		// check for an event type; it should be item event.
		if (event.getType() != Event.OMM_ITEM_EVENT) {
			instance.cleanup();
			return;
		}

		OMMItemEvent ie = (OMMItemEvent) event;
		OMMMsg respMsg = ie.getMsg();
		OMMAttribInfo info = respMsg.getAttribInfo();

		if (null != info) {
			String eventItemName = info.getName();

			logger.info( "event item name : {}", eventItemName );

			if (identifiers.contains( eventItemName )) {
				// GenericOMMParser parser = new GenericOMMParser();
				parser.setFields( fields );

				parser.parse( respMsg );
			}
		}
	}
}
