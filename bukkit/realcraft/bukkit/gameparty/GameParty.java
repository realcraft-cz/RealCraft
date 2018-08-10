package realcraft.bukkit.gameparty;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.sockets.SocketData;
import realcraft.bukkit.sockets.SocketManager;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.BungeeMessages;
import realcraft.share.ServerType;
import realcraft.share.users.User;

public class GameParty implements Runnable {

	public static final String CHANNEL_SERVER = "gamePartyServer";
	public static final String CHANNEL_GAME_END = "gamePartyGameEnd";
	public static final String CHANNEL_PLAYER_ADD = "gamePartyPlayerAdd";
	public static final String CHANNEL_PLAYER_REMOVE = "gamePartyPlayerRemove";

	private static final ServerType[] SERVERS = new ServerType[]{
		ServerType.BEDWARS,
		ServerType.HIDENSEEK,
		ServerType.BLOCKPARTY,
		ServerType.RAGEMODE,
		ServerType.PAINTBALL,
		ServerType.DOMINATE,
		ServerType.RACES
	};

	private static ServerType server = ServerType.BEDWARS;
	private static ArrayList<User> users = new ArrayList<User>();
	private static GamePartySheep sheep;

	public GameParty(){
		new GamePartyListeners();
		if(RealCraft.getServerType() == ServerType.LOBBY){
			sheep = new GamePartySheep();
			Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,30*20,30*20);
		}
	}

	@Override
	public void run(){
		int count = 0;
		for(User user : users){
			if(user.isLogged()) count ++;
		}
		if(count < 4){
			ServerType server = GameParty.getLargestServer();
			if(server != null){
				ServerType oldServer = GameParty.getServer();
				if(oldServer != server){
					GameParty.setServer(server);
					GameParty.sendNextServer(oldServer);
				}
			}
		}
	}

	public static ServerType getServer(){
		return server;
	}

	public static void setServer(ServerType server){
		GameParty.server = server;
		if(sheep != null) sheep.update();
	}

	private static ServerType getNextServer(){
		int index = 0;
		for(ServerType server : SERVERS){
			if(GameParty.getServer() == server && index+1 < SERVERS.length) return SERVERS[index+1];
			index ++;
		}
		return SERVERS[0];
	}

	private static ServerType getLargestServer(){
		ServerType largestServer = null;
		int largestCount = 0;
		for(ServerType server : SERVERS){
			int count = 0;
			for(User user : Users.getOnlineUsers()){
				if(user.getServer() == server) count ++;
			}
			if(count > 0 && (largestServer == null || largestCount < count)){
				largestServer = server;
				largestCount = count;
			}
		}
		return largestServer;
	}

	public static void chooseNextServer(){
		ServerType oldServer = GameParty.getServer();
		GameParty.setServer(GameParty.getNextServer());
		GameParty.sendNextServer(oldServer);
	}

	public static void sendNextServer(ServerType oldServer){
		SocketData data = new SocketData(CHANNEL_SERVER);
		data.setString("oldserver",oldServer.toString());
		data.setString("server",GameParty.getServer().toString());
		SocketManager.sendToAll(data);
	}

	public static void sendPartyGameEnd(){
		SocketData data = new SocketData(CHANNEL_GAME_END);
		SocketManager.send(ServerType.LOBBY,data);
	}

	public static ArrayList<User> getUsers(){
		return users;
	}

	public static void addUser(User user){
		users.remove(user);
		users.add(user);
		SocketData data = new SocketData(CHANNEL_PLAYER_ADD);
		data.setInt("id",user.getId());
		SocketManager.sendToAll(data);
		BungeeMessages.connectPlayerToServer(Users.getPlayer(user),GameParty.getServer());
		Users.getPlayer(user).sendMessage("§d[Party]§f Pripojil jsi se do globalni party");
		if(sheep != null) sheep.update();
	}

	public static void removeUser(User user){
		users.remove(user);
		SocketData data = new SocketData(CHANNEL_PLAYER_REMOVE);
		data.setInt("id",user.getId());
		SocketManager.sendToAll(data);
		if(sheep != null) sheep.update();
	}
}