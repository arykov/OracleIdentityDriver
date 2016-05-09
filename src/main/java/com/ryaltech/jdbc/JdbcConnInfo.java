package com.ryaltech.jdbc;

import com.beust.jcommander.Parameter;

/**
 * JCommander parameters that are directly related to db connectivity
 * 
 * @author rykov
 *
 */
public class JdbcConnInfo {
	@Parameter(names = { "-url"}, description = "JDBC URL. For example jdbc:oracle:thin:@localhost:1521:XE", required=true)
	String url;
	@Parameter(names = { "-user", "-u"}, description = "Database user", required=true)
	String user;
	@Parameter(names = { "-password", "-p"}, description = "Database password",  password = true, echoInput=false, required=true)
	String password;
}
