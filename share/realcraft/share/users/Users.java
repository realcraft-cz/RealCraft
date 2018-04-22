package realcraft.share.users;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import realcraft.share.ServerType;
import realcraft.share.database.DB;

public abstract class Users {

	public static final String USERS = "authme";
	public static final String CHANNEL_BUNGEE_CONNECT = "bungeeConnect";
	public static final String CHANNEL_BUNGEE_LOGIN = "bungeeLogin";
	public static final String CHANNEL_BUNGEE_DISCONNECT = "bungeeDisconnect";
	public static final String CHANNEL_BUNGEE_SWITCH = "bungeeSwitch";

	private static ConcurrentHashMap<Integer,User> users = new ConcurrentHashMap<Integer,User>();

	public static ArrayList<User> getUsers(){
		return new ArrayList<User>(users.values());
	}

	public static ArrayList<User> getOnlineUsers(){
		ArrayList<User> users = new ArrayList<User>();
		for(User user : Users.getUsers()){
			if(user.isLogged()) users.add(user);
		}
		return users;
	}

	public static User getOnlineUser(String name){
		for(User user : Users.getOnlineUsers()){
			if(user.getName().equalsIgnoreCase(name)) return user;
		}
		return null;
	}

	public static User getUser(String name){
		for(User user : users.values()){
			if(user.getName().equalsIgnoreCase(name)) return user;
		}
		User user = null;
		ResultSet rs = DB.query("SELECT user_id FROM "+USERS+" WHERE user_name = ?",name);
		try {
			if(rs.next()) user = Users.getUser(rs.getInt("user_id"));
			rs.close();
		} catch (SQLException e){
		}
		return user;
	}

	public static User getUser(UUID uuid){
		for(User user : users.values()){
			if(user.getUniqueId().equals(uuid)) return user;
		}
		User user = null;
		ResultSet rs = DB.query("SELECT user_id FROM "+USERS+" WHERE user_uuid = ?",uuid.toString());
		try {
			if(rs.next()) user = Users.getUser(rs.getInt("user_id"),uuid);
			rs.close();
		} catch (SQLException e){
		}
		return user;
	}

	public static User getUser(int id){
		return Users.getUser(id,null);
	}

	public static User getUser(int id,UUID uuid){
		if(!users.containsKey(id)) users.put(id,new User(id,uuid));
		return users.get(id);
	}

	public static void createUser(String name,UUID uuid,String address){
		DB.update("INSERT INTO "+USERS+" (user_name,user_lowername,user_uuid,user_ip,user_skin,user_server) VALUES(?,?,?,?,?,?)",
			name,
			name.toLowerCase(),
			uuid.toString(),
			address,
			name,
			ServerType.LOBBY.toString()
		);
		Users.getUser(uuid).setAddress(address);
	}
}