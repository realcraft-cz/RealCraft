package com.realcraft.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.realcraft.RealCraft;

public class MySQL {
	private RealCraft plugin;
	public Connection conn = null;
	public Statement stmt = null;
	public boolean connected = false;

	public MySQL(RealCraft realcraft){
		plugin = realcraft;
		if(plugin.config.getBoolean("database.enabled")) this.connect();
	}

	public void onDisable(){
		if(!connected) return;
		try {
			conn.close();
		}
		catch(Exception e){
			System.out.println("Error when closing connecting to MySQL!");
		}
	}

	public boolean connect(){
		String hostname = plugin.config.getString("database.hostname");
		String port = plugin.config.getString("database.port");
		String database = plugin.config.getString("database.database");
		String username = plugin.config.getString("database.username");
		String password = plugin.config.getString("database.password");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://"+ hostname + ":" + port + "/" + database+"?useUnicode=true&characterEncoding=utf-8",username,password);
			conn.setAutoCommit(true);
			connected = true;
		}
		catch(Exception e){
			System.out.println("Error when connecting to MySQL!");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public ResultSet query(String query){
		if(!connected) return null;
		try {
			stmt = conn.createStatement();
			return stmt.executeQuery(query);
		}
		catch(Exception e){
		}
		return null;
	}

	public int update(String query){
		if(!connected) return 0;
		try {
			stmt = conn.createStatement();
			return stmt.executeUpdate(query);
		}
		catch(Exception e){
		}
		return 0;
	}

	public ResultSet insert(String query){
		if(!connected) return null;
		try {
			stmt.executeUpdate(query);
			return stmt.getGeneratedKeys();
		}
		catch(Exception e){
		}
		return null;
	}
}