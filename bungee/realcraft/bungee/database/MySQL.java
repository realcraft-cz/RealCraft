package realcraft.bungee.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import realcraft.bungee.RealCraftBungee;

public class MySQL {
	private RealCraftBungee plugin;
	public Connection conn = null;
	public boolean connected = false;

	public MySQL(RealCraftBungee realcraft){
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
		int port = plugin.config.getInt("database.port");
		String database = plugin.config.getString("database.database");
		String username = plugin.config.getString("database.username");
		String password = plugin.config.getString("database.password");
		System.out.println("jdbc:mysql://"+ hostname + ":" + port + "/" + database+"?useUnicode=true&characterEncoding=utf-8");
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
			return conn.createStatement().executeQuery(query);
		}
		catch(Exception e){
		}
		return null;
	}

	public int update(String query){
		if(!connected) return 0;
		try {
			return conn.createStatement().executeUpdate(query);
		}
		catch(Exception e){
		}
		return 0;
	}
}