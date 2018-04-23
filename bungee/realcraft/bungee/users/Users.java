package realcraft.bungee.users;

import java.lang.reflect.Field;
import java.util.UUID;

import com.google.common.base.Charsets;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import realcraft.bungee.RealCraftBungee;
import realcraft.bungee.sockets.SocketData;
import realcraft.bungee.sockets.SocketManager;
import realcraft.bungee.users.auth.UsersAuthentication;
import realcraft.share.ServerType;
import realcraft.share.users.User;

public class Users extends realcraft.share.users.Users implements Listener {

	public Users(){
		BungeeCord.getInstance().getPluginManager().registerListener(RealCraftBungee.getInstance(),this);
		new UsersAuthentication();
	}

	public static User getUser(ProxiedPlayer player){
		return Users.getUser(player.getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void LoginEvent(LoginEvent event){
		try {
			UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:"+event.getConnection().getName()).getBytes(Charsets.UTF_8));
			Field idField = InitialHandler.class.getDeclaredField("uniqueId");
	        idField.setAccessible(true);
			idField.set(event.getConnection(),offlineUUID);
			User user = Users.getUser(event.getConnection().getUniqueId());
			Users.connectUser(user);
		} catch (IllegalArgumentException e){
			e.printStackTrace();
		} catch (IllegalAccessException e){
			e.printStackTrace();
		} catch (NoSuchFieldException e){
			e.printStackTrace();
		} catch (SecurityException e){
			e.printStackTrace();
		}
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

	private static void connectUser(User user){
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