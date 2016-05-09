package com.ryaltech.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exists simply for common initialization
 * 
 * @author rykov
 *
 */
public class LogFactory {
	static{
		//System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
		//System.setProperty("org.slf4j.simpleLogger.logFile", "/tmp/crjdbc.log");
		System.setProperty("org.slf4j.simpleLogger.showDateTime", System.getProperty("org.slf4j.simpleLogger.showDateTime", "true"));
		System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", System.getProperty("org.slf4j.simpleLogger.dateTimeFormat","yyyy-MM-dd HH:mm:ss:SSS Z"));
	}
	public static Logger getLogger(Class<?> klass){
		return LoggerFactory.getLogger(klass);
	}
	

}
