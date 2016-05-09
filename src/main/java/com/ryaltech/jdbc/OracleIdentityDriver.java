package com.ryaltech.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.driver.OracleDriver;




/**
 * 
 * This JDBC driver establishes connection to Oracle via {@link OracleDriver}
 * using contents of the property file(after @ in url) for url, user and
 * password. Once connection is established it sets identity to the value passed
 * as user name to the OracleIdentityDriver
 * 
 * @author rykov
 *
 */
public class OracleIdentityDriver implements Driver {

	private static org.slf4j.Logger logger =LogFactory.getLogger(OracleIdentityDriver.class);
	//sample URL "jdbc:ryaltech:oraid:@/apps/db.properties"	
	public static final String driverPrefix = "jdbc:ryaltech:oraid:";
	public Driver originalOracleDriver;
	public OracleIdentityDriver(){
	}
	public OracleIdentityDriver(Driver driver){
		this.originalOracleDriver = driver;
	}
	private Driver getOriginalOracleDriver(){
		if(originalOracleDriver == null){
			originalOracleDriver = new oracle.jdbc.OracleDriver();
		}
		return originalOracleDriver;
	}
	
	private String extractProperyFileLocationFromUrl(String url){
		String [] chunks = url.split(":@");
		if(chunks.length>1)return chunks[1];
		else return null;
	}
	public Connection connect(String url, Properties info) throws SQLException {
		logger.debug("connect called url: {} info: {}", url, info);
				
		String propertiesFileLocation = info.getProperty("database", extractProperyFileLocationFromUrl(url));
		//consider caching
		StoreConnProperties cis = new StoreConnProperties(propertiesFileLocation);
		final JdbcConnInfo ci = cis.loadJdbcConnInfo();
		Connection conn = getOriginalOracleDriver().connect(
				ci.url, new Properties() {
					{
						put("user",ci.user);
						put("password", ci.password);
					}
				});
		//invoke setClientIdentifier
		((OracleConnection)conn).setClientIdentifier(info.getProperty("user"));
		return conn;
	}

	public static boolean supportsURL(String url) throws SQLException {		
		return ((url != null) && url.startsWith(driverPrefix));
	}
	
	public boolean acceptsURL(String url) throws SQLException {
		return supportsURL(url);
	}

	private static DriverPropertyInfo[] driverPropertyInfo = {
			new DriverPropertyInfo("user", null),
			new DriverPropertyInfo("password", null),
			new DriverPropertyInfo("database", null) };

	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
			throws SQLException {
		return driverPropertyInfo;
	}

	public int getMajorVersion() {

		return 1;
	}

	public int getMinorVersion() {

		return 0;
	}

	public boolean jdbcCompliant() {

		return true;
	}

	//not registering at the moment since it is only used in conjunction with OracleDriver at the moment
	/*
	static {
		try {
			DriverManager.registerDriver(new OracleIdentityDriver());
		} catch (SQLException ex) {
			logger.error("failed", ex);
			throw new RuntimeException();
		}

	}*/

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

}
