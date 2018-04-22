package realcraft.share.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bukkit.configuration.file.FileConfiguration;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class DB {

	public static Connection conn = null;
	public static boolean connected = false;

	public static void init(){
		try {
			Class.forName("net.md_5.bungee.config.YamlConfiguration");
			BungeeDB.loadBungee();
		} catch (ClassNotFoundException e){
			BukkitDB.loadBukkit();
		}
	}

	public static File getConfig(){
		return new File("../global/RealCraft/database.yml");
	}

	public static void onDisable(){
		if(!connected) return;
		try {
			conn.close();
		}
		catch(Exception e){
			System.out.println("Error when closing connecting to MySQL!");
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
		return query(query,(Object)null);
	}

	public static ResultSet query(String query,Object... params){
		if(!connected) return null;
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			for(int i=0;i<params.length;i++){
				if(params[i] != null) stmt.setObject(i+1,params[i]);
			}
			return stmt.executeQuery();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static int update(String query){
		return update(query,(Object)null);
	}

	public static int update(String query,Object... params){
		if(!connected) return 0;
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			for(int i=0;i<params.length;i++){
				if(params[i] != null) stmt.setObject(i+1,params[i]);
			}
			return stmt.executeUpdate();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}

	public static ResultSet insert(String query){
		return insert(query,(Object)null);
	}

	public static ResultSet insert(String query,Object... params){
		if(!connected) return null;
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			for(int i=0;i<params.length;i++){
				if(params[i] != null) stmt.setObject(i+1,params[i]);
			}
			stmt.executeUpdate(query);
			return stmt.getGeneratedKeys();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private static class BungeeDB {

		public static void loadBungee(){
			try {
				Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(DB.getConfig());
				String hostname = config.getString("database.hostname");
				int port = config.getInt("database.port");
				String database = config.getString("database.database");
				String username = config.getString("database.username");
				String password = config.getString("database.password");
				DB.connect(hostname,port,database,username,password);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	private static class BukkitDB {

		public static void loadBukkit(){
			try {
				FileConfiguration config = new org.bukkit.configuration.file.YamlConfiguration();
				config.load(DB.getConfig());
				String hostname = config.getString("database.hostname");
				int port = config.getInt("database.port");
				String database = config.getString("database.database");
				String username = config.getString("database.username");
				String password = config.getString("database.password");
				DB.connect(hostname,port,database,username,password);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}