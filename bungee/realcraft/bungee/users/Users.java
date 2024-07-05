package realcraft.bungee.users;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import realcraft.bungee.RealCraftBungee;
import realcraft.bungee.sockets.SocketData;
import realcraft.bungee.sockets.SocketDataEvent;
import realcraft.bungee.sockets.SocketManager;
import realcraft.bungee.users.auth.UsersAuthentication;
import realcraft.share.ServerType;
import realcraft.share.users.User;

import java.util.ArrayList;

public class Users extends realcraft.share.users.Users implements Listener {

	public Users(){
		ProxyServer.getInstance().getPluginManager().registerListener(RealCraftBungee.getInstance(),this);
		new UsersAuthentication();
	}

	public static User getUser(ProxiedPlayer player){
		return Users.getUser(player.getUniqueId());
	}

	@EventHandler
	public void ServerSwitchEvent(ServerSwitchEvent event){
		ProxiedPlayer player = event.getPlayer();
		User user = Users.getUser(player);
		user.setServer(ServerType.getByName(player.getServer().getInfo().getName()));
		SocketData data = new SocketData(CHANNEL_BUNGEE_SWITCH);
        data.setInt("id",user.getId());
        SocketManager.sendToAll(data);
	}

	@EventHandler
	public void PlayerDisconnectEvent(PlayerDisconnectEvent event){
		ProxiedPlayer player = event.getPlayer();
		User user = Users.getUser(player);
		Users.disconnectUser(user);
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(CHANNEL_BUNGEE_USERS_REQUEST)){
			SocketData data2 = new SocketData(CHANNEL_BUNGEE_USERS_LIST);
			ArrayList<Integer> list = new ArrayList<Integer>();
			for(User user : Users.getOnlineUsers()){
				list.add(user.getId());
			}
			data2.setIntList("players",list);
	        SocketManager.send(event.getServer(),data2);
		}
	}

	public static void connectUser(User user){
		user.connect();
		SocketData data = new SocketData(CHANNEL_BUNGEE_CONNECT);
        data.setInt("id",user.getId());
        SocketManager.sendToAll(data);
	}

	private static void disconnectUser(User user){
		if(user.isLogged()) user.logout();
		SocketData data = new SocketData(CHANNEL_BUNGEE_DISCONNECT);
        data.setInt("id",user.getId());
        SocketManager.sendToAll(data);
	}

	public static void updatePlayTime(){
		for(User user : Users.getUsers()){
			if(user.isLogged()) user.updatePlayTime();
		}
	}
}