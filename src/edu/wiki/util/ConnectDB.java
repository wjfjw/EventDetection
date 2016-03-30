package edu.wiki.util;

import java.io.*;
import java.sql.*;

import edu.wiki.search.ESASearcher;

public class ConnectDB 
{
	public static Connection initDB() throws ClassNotFoundException, SQLException, IOException
	{
		Class.forName("com.mysql.jdbc.Driver");
		
		/*// read DB config
		InputStream is = ESASearcher.class.getResourceAsStream("/config/db.conf");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String serverName = br.readLine();
		String mydatabase = br.readLine();
		String username = br.readLine(); 
		String password = br.readLine();
		br.close();
		*/
		String serverName = "localhost";
		String mydatabase = "wikipedia";
		String username = "root";
		String password = "575986323";
		
		
		String url = "jdbc:mysql://" + serverName + "/" + mydatabase; // a JDBC url
		
		Connection conn = DriverManager.getConnection(url, username, password);
		
		return conn;
	}
}
