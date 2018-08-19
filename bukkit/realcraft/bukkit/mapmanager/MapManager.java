package realcraft.bukkit.mapmanager;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

import java.util.HashMap;

public class MapManager {

	private static World world;
	private static HashMap<User,MapPlayer> players = new HashMap<>();

	public MapManager(){
		world = Bukkit.getWorld("world_maps");
	}

	public static World getWorld(){
		return world;
	}

	public static MapPlayer getMapPlayer(User user){
		if(!players.containsKey(user)) players.put(user,new MapPlayer(user));
		return players.get(user);
	}

	public static MapPlayer getMapPlayer(Player player){
		return MapManager.getMapPlayer(Users.getUser(player));
	}
}