package realcraft.bukkit.friends;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import realcraft.bukkit.database.DB;

public class FriendPlayerSettings {

	private FriendPlayer fPlayer;
	private HashMap<FriendPlayerSettingsType,Boolean> values = new HashMap<FriendPlayerSettingsType,Boolean>();

	public FriendPlayerSettings(FriendPlayer fPlayer){
		this.fPlayer = fPlayer;
	}

	public boolean getValue(FriendPlayerSettingsType type){
		return values.get(type);
	}

	public void setValue(FriendPlayerSettingsType type,boolean value){
		values.put(type,value);
		if(type == FriendPlayerSettingsType.CHATS && !value){
			fPlayer.setFriendChat(false);
			FriendNotices.showToggleFriendChat(fPlayer);
		}
		this.save();
	}

	public void save(){
		DB.update("UPDATE "+Friends.FRIENDS_SETTINGS+" SET "
				+ "settings_requests = '"+(this.getValue(FriendPlayerSettingsType.REQUESTS) ? 1 : 0)+"',"
				+ "settings_joins = '"+(this.getValue(FriendPlayerSettingsType.JOINS) ? 1 : 0)+"',"
				+ "settings_quits = '"+(this.getValue(FriendPlayerSettingsType.QUITS) ? 1 : 0)+"',"
				+ "settings_teleports = '"+(this.getValue(FriendPlayerSettingsType.TELEPORTS) ? 1 : 0)+"',"
				+ "settings_chats = '"+(this.getValue(FriendPlayerSettingsType.CHATS) ? 1 : 0)+"'"
				+ "WHERE user_id = '"+fPlayer.getId()+"'"
		);
		fPlayer.sendReload();
	}

	public void reload(){
		ResultSet rs = DB.query("SELECT * FROM "+Friends.FRIENDS_SETTINGS+" WHERE user_id = '"+fPlayer.getId()+"'");
		try {
			if(rs.next()){
				values.put(FriendPlayerSettingsType.REQUESTS,rs.getBoolean("settings_requests"));
				values.put(FriendPlayerSettingsType.JOINS,rs.getBoolean("settings_joins"));
				values.put(FriendPlayerSettingsType.QUITS,rs.getBoolean("settings_quits"));
				values.put(FriendPlayerSettingsType.TELEPORTS,rs.getBoolean("settings_teleports"));
				values.put(FriendPlayerSettingsType.CHATS,rs.getBoolean("settings_chats"));
			} else {
				DB.update("INSERT INTO "+Friends.FRIENDS_SETTINGS+" (user_id) VALUES('"+fPlayer.getId()+"')");
				values.put(FriendPlayerSettingsType.REQUESTS,true);
				values.put(FriendPlayerSettingsType.JOINS,true);
				values.put(FriendPlayerSettingsType.QUITS,true);
				values.put(FriendPlayerSettingsType.TELEPORTS,true);
				values.put(FriendPlayerSettingsType.CHATS,true);
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	public enum FriendPlayerSettingsType {
		REQUESTS, JOINS, QUITS, TELEPORTS, CHATS;

		public String getName(){
			switch(this){
				case REQUESTS: return "Povolit nove zadosti o pratelstvi";
				case JOINS: return "Upozornit na pripojeni pratel na server";
				case QUITS: return "Upozornit na odpojeni pratel ze serveru";
				case TELEPORTS: return "Povolit teleportaci ostatnich pratel";
				case CHATS: return "Prijimat hromadne zpravy ostatnich pratel";
			}
			return null;
		}

		public static FriendPlayerSettingsType fromName(String name){
			return FriendPlayerSettingsType.valueOf(name.toUpperCase());
		}
	}
}