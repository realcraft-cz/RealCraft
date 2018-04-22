package realcraft.bukkit.fights;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

public class Fights implements Listener {

	private static HashMap<User,FightPlayer> players = new HashMap<User,FightPlayer>();

	public Fights(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	public static FightPlayer getFightPlayer(Player player){
		return Fights.getFightPlayer(Users.getUser(player));
	}

	public static FightPlayer getFightPlayer(User user){
		if(!players.containsKey(user)) players.put(user,new FightPlayer(user));
		return players.get(user);
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		Fights.getFightPlayer(event.getPlayer()).reload();
	}
}