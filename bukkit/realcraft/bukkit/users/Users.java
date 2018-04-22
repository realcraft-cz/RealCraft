package realcraft.bukkit.users;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.auth.AuthLoginEvent;
import realcraft.bukkit.sockets.SocketData;
import realcraft.bukkit.sockets.SocketDataEvent;
import realcraft.share.users.User;

public class Users extends realcraft.share.users.Users implements Listener {

	public Users(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	public static User getUser(Player player){
		return Users.getUser(player.getUniqueId());
	}

	public static Player getPlayer(User user){
		return Bukkit.getPlayer(user.getUniqueId());
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void PlayerJoinEvent(PlayerJoinEvent event){
		Users.getUser(event.getPlayer()).reload();
	}

	@EventHandler
	public void AuthLoginEvent(AuthLoginEvent event){
		Users.getUser(event.getPlayer()).setLogged(true);
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(CHANNEL_BUNGEE_CONNECT)){
			Users.getUser(data.getInt("id")).reload();
		}
		else if(data.getChannel().equalsIgnoreCase(CHANNEL_BUNGEE_LOGIN)){
			Users.getUser(data.getInt("id")).reload();
		}
		else if(data.getChannel().equalsIgnoreCase(CHANNEL_BUNGEE_DISCONNECT)){
			Users.getUser(data.getInt("id")).reload();
		}
		else if(data.getChannel().equalsIgnoreCase(CHANNEL_BUNGEE_SWITCH)){
			Users.getUser(data.getInt("id")).reload();
		}
	}
}