package realcraft.bukkit.gameparty;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.sockets.SocketData;
import realcraft.bukkit.sockets.SocketDataEvent;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.BungeeMessages;
import realcraft.share.ServerType;
import realcraft.share.users.User;

public class GamePartyListeners implements Listener {

	public GamePartyListeners(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		if(RealCraft.getServerType() == ServerType.LOBBY || RealCraft.getServerType() == ServerType.SURVIVAL || RealCraft.getServerType() == ServerType.CREATIVE || RealCraft.getServerType() == ServerType.FIGHTS){
			User user = Users.getUser(event.getPlayer());
			GameParty.removeUser(user);
		}
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(GameParty.CHANNEL_SERVER)){
			GameParty.setServer(ServerType.getByName(data.getString("server")));
			if(ServerType.getByName(data.getString("oldserver")) == RealCraft.getServerType()){
				for(User user : GameParty.getUsers()){
					if(Users.getPlayer(user) != null){
						Users.getPlayer(user).sendMessage("§d[Party]§f Teleportuji hrace na "+GameParty.getServer().getColor()+GameParty.getServer().getName());
						BungeeMessages.connectPlayerToServer(Users.getPlayer(user),GameParty.getServer());
					}
				}
			}
		}
		else if(data.getChannel().equalsIgnoreCase(GameParty.CHANNEL_GAME_END)){
			if(ServerType.getByName(data.getServer()) == GameParty.getServer()) GameParty.chooseNextServer();
		}
		else if(data.getChannel().equalsIgnoreCase(GameParty.CHANNEL_PLAYER_ADD)){
			GameParty.getUsers().remove(Users.getUser(data.getInt("id")));
			GameParty.getUsers().add(Users.getUser(data.getInt("id")));
		}
		else if(data.getChannel().equalsIgnoreCase(GameParty.CHANNEL_PLAYER_REMOVE)){
			GameParty.getUsers().remove(Users.getUser(data.getInt("id")));
		}
		else if(data.getChannel().equalsIgnoreCase(Users.CHANNEL_BUNGEE_DISCONNECT)){
			GameParty.getUsers().remove(Users.getUser(data.getInt("id")));
		}
	}
}