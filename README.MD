Oracle Identity JDBC Driver
======================

For background on why this driver was created check the blog post [here](http://blog.ryaltech.com/2016/05/06/crystal-identitypropagation).

This is a wrapper around Oracle JDBC driver that connects to the database using jdbc connection information stored in property file and uses passed user id to set client identity on a newly created connection. 

Url format: `jdbc:ryaltech:oraid:@<path_to_properties>`. For example `jdbc:ryaltech:oraid:@/opt/creproperties/mydb.properties`

Usage.  Driver can be used in one of two ways:

1. Add the driver to the classpath and then use it as any other JDBC driver. Driver class is com.ryaltech.jdbc.OracleIdentityDriver
2. Add the driver JAR passing -javaagent to the JVM. For example -javaagent:/home/arykov/identityoraclejdbc/target/OracleIdentityDriver.jar. In this case you should be using oracle.jdbc.OracleDriver and calls will be routed to com.ryaltech.jdbc.OracleIdentityDriver for urls starting with `jdbc:ryaltech:oraid:`.

To create this property file, use com.ryaltech.jdbc.StoreConnProperties utility `java -classpath OracleIdentityDriver.jar com.ryaltech.jdbc.StoreConnProperties -url jdbc:oracle:thin:@oraclehost:1521:XE -user dbuser -s /opt/creproperties/mydb.properties -password`. 

Note that:

* Password gets encrypted. 
* You will be prompted for password. Do not enter it after -password option.
* It is important to use the property file from the same location as it gets created at.
* Ensure that all files in the directory properties get created in can only be read by the user that will eventually run the process from which JDBC driver will be used.


