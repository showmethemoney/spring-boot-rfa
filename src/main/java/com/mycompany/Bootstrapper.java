package com.mycompany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import com.mycompany.reuters.RobustFoundationAPI;

@SpringBootApplication
public class Bootstrapper
{
	protected static final Logger logger = LoggerFactory.getLogger( Bootstrapper.class );

	public static void main(String[] args) {
		try {
			SpringApplication.run( Bootstrapper.class, args );
		} catch (Throwable cause) {
			logger.error( cause.getMessage(), cause );
		}
	}
	
}
