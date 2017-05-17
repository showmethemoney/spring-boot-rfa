package com.mycompany.reuters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class RFAConfiguration
{
	
	@Bean
	@Scope("singleton")
	public RobustFoundationAPI getRobustfoundationAPI() {
		String sessionName = "myNS::RSSLSession";
		String serviceName = "IDN_RDF";
		String userName = "MikeTest";
		String fieldDictionaryFilename = "C:/Users/ethan/workspace/rfa/src/main/resources/RDMFieldDictionary";
		String enumDictionaryFilename = "C:/Users/ethan/workspace/rfa/src/main/resources/enumtype.def";
		String feedConfigFilename = "C:/Users/ethan/workspace/rfa/src/main/resources/FeedConfig.xml";
		
		return new RobustFoundationAPI(sessionName, serviceName, userName, fieldDictionaryFilename, enumDictionaryFilename, feedConfigFilename);
	}
}
