package stockmanagement;

import java.sql.Connection;

public class Connector {

	static Connection conn=null;
	public static Connection getConnection() {
	try {
		com.mysql.jdbc.jdbc2.optional.MysqlDataSource ds= new com.mysql.jdbc.jdbc2.optional.MysqlDataSource();
		ds.setServerName(System.getenv("ICSI518_SERVER"));
		ds.setPortNumber(Integer.parseInt(System.getenv("ICSI518_PORT")));
		ds.setDatabaseName(System.getenv("ICSI518_DB"));
		ds.setUser(System.getenv("ICSI518_USER"));
		ds.setPassword(System.getenv("ICSI518_PASSWORD"));
		conn=ds.getConnection();
		
		
	}
	catch(Exception e)
	{
		
	}
	return conn;
}
	
}
