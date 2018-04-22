package realcraft.bukkit.friends;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.sockets.SocketData;
import realcraft.bukkit.sockets.SocketDataEvent;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

public class Friends implements Listener {

	public static final int MAX_FRIENDS = 28;

	public static final String FRIENDS = "friends_friends";
	public static final String FRIENDS_REQUESTS = "friends_requests";
	public static final String FRIENDS_SETTINGS = "friends_settings";

	public static final String CHANNEL_BUNGEE_LOGIN = "bungeeLogin";
	public static final String CHANNEL_BUNGEE_DISCONNECT = "bungeeDisconnect";
	public static final String CHANNEL_BUNGEE_SWITCH = "bungeeSwitch";
	public static final String CHANNEL_FRIEND_RELOAD = "friendsFriendReload";
	public static final String CHANNEL_FRIEND_SETCHAT = "friendsFriendSetChat";

	private static HashMap<Integer,FriendPlayer> players = new HashMap<Integer,FriendPlayer>();

	public Friends(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		new FriendCommands();
		new FriendRequests();
		new FriendNotices();
		new FriendList();
		new FriendChat();
	}

	public static FriendPlayer getFriendPlayer(Player player){
		return getFriendPlayer(Users.getUser(player).getId());
	}

	public static FriendPlayer getFriendPlayer(String name){
		FriendPlayer player = null;
		User user = Users.getUser(name);
		if(user != null){
			player = getFriendPlayer(user.getId());
		}
		return player;
	}

	public static FriendPlayer getFriendPlayer(int id){
		if(!players.containsKey(id)) players.put(id,new FriendPlayer(id));
		return players.get(id);
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		Friends.getFriendPlayer(event.getPlayer()).reload();
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(CHANNEL_BUNGEE_LOGIN)){
			FriendPlayer fPlayer = Friends.getFriendPlayer(data.getInt("id"));
			fPlayer.reload();
		}
		else if(data.getChannel().equalsIgnoreCase(CHANNEL_BUNGEE_DISCONNECT)){
			FriendPlayer fPlayer = Friends.getFriendPlayer(data.getInt("id"));
			fPlayer.reload();
		}
		else if(data.getChannel().equalsIgnoreCase(CHANNEL_BUNGEE_SWITCH)){
			FriendPlayer fPlayer = Friends.getFriendPlayer(data.getInt("id"));
			fPlayer.reload();
		}
		else if(data.getChannel().equalsIgnoreCase(CHANNEL_FRIEND_RELOAD)){
			FriendPlayer fPlayer = Friends.getFriendPlayer(data.getInt("id"));
			fPlayer.reload();
		}
		else if(data.getChannel().equalsIgnoreCase(CHANNEL_FRIEND_SETCHAT)){
			FriendPlayer fPlayer = Friends.getFriendPlayer(data.getInt("id"));
			fPlayer.setFriendChat(data.getBoolean("enabled"));
		}
	}
}