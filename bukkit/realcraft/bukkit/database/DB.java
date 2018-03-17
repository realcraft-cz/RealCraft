package realcraft.bukkit.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import realcraft.bukkit.RealCraft;

public class DB {

	public static Connection conn = null;
	public static boolean connected = false;

	public static void onDisable(){
		if(!connected) return;
		try {
			conn.close();
		}
		catch(Exception e){
			System.out.println("Error when closing connecting to MySQL!");
		}
	}

	public static void init(){
		if(!connected){
			String hostname = RealCraft.getInstance().config.getString("database.hostname");
			int port = RealCraft.getInstance().config.getInt("database.port");
			String database = RealCraft.getInstance().config.getString("database.database");
			String username = RealCraft.getInstance().config.getString("database.username");
			String password = RealCraft.getInstance().config.getString("database.password");
			DB.connect(hostname,port,database,username,password);
		}
	}

	public static boolean connect(String hostname,int port,String database,String username,String password){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://"+hostname+":"+port+"/"+database+"?useUnicode=true&characterEncoding=utf-8",username,password);
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

	public static ResultSet query(String query){
		if(!connected) return null;
		try {
			return conn.createStatement().executeQuery(query);
		}
		catch(Exception e){
		}
		return null;
	}

	public static int update(String query){
		if(!connected) return 0;
		try {
			return conn.createStatement().executeUpdate(query);
		}
		catch(Exception e){
		}
		return 0;
	}

	public static ResultSet insert(String query){
		if(!connected) return null;
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
			return stmt.getGeneratedKeys();
		}
		catch(Exception e){
		}
		return null;
	}
}