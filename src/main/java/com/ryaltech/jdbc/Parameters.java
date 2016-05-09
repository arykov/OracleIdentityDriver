package com.ryaltech.jdbc;

import com.beust.jcommander.Parameter;

/**
 * JCommander parameters that are needed for {@link StoreConnProperties}
 * functionality but are not database connection parameters
 * 
 * @author rykov
 *
 */
public class Parameters extends JdbcConnInfo{
	@Parameter(names = { "-saveTo", "-s"}, description = "Property file location to save db connection information to", required=true)
	String stringpropertiesFileLocation;
	@Parameter(names = { "-help", "-h", "-?"}, description = "Help", help=true)
	boolean  help;


}
