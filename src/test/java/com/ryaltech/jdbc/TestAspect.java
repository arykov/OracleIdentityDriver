package com.ryaltech.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.Properties;

import oracle.jdbc.OracleDriver;
import oracle.net.ns.NetException;

import org.junit.Test;

public class TestAspect {

	@Test
	public void testWrongUrl() {
		assertFalse(new OracleDriver().acceptsURL("jdbc:whoknows"));
	}
	@Test
	public void testOracleUrl() {
		assertTrue(new OracleDriver().acceptsURL("jdbc:oracle:thin:@localhost:1521:XE"));
	}
	@Test
	public void testOracleIdentityUrl() {
		assertTrue(new OracleDriver().acceptsURL("jdbc:ryaltech:oraid:/tmp/xyz.properties"));
	}
	
	
	static final String oracle_url="jdbc:oracle:thin:@localhost:1521:xe";
	static final String property_file_location="target/bad.properties";
	@Test
	public void testConnectRyaltech()throws Exception{
		new File("target/sys.dat").delete();
		new StoreConnProperties(property_file_location).saveJdbcConnInfo(new JdbcConnInfo(){{user="user";password="password";url=oracle_url;}});
		
		try{
			new oracle.jdbc.OracleDriver().connect("jdbc:ryaltech:oraid:@"+property_file_location, new Properties(){{put("user", "hahaha");}});
			fail("should have thrown an exception");
		}catch(SQLRecoverableException re){
			assertTrue(re.getCause() instanceof NetException);			
		}catch(SQLException ex){
			assertEquals("ORA-01017: invalid username/password; logon denied", ex.getMessage().trim());			
		}
		
	}
	
	@Test
	public void testConnectThin()throws Exception{	
		try{
			new oracle.jdbc.OracleDriver().connect(oracle_url, new Properties(){{put("user", "user");put("password", "password");}});
			fail("should have thrown an exception");
		}catch(SQLRecoverableException re){
			assertTrue(re.getCause() instanceof NetException);			
		}catch(SQLException ex){
			assertEquals("ORA-01017: invalid username/password; logon denied", ex.getMessage().trim());			
		}
		
	}

	
}
