package realcraft.bukkit.falling;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.mapmanager.MapPlayer;

public class Falling {

	private static final String PREFIX = "§6[Maps]§r ";

	private static World world;

	public Falling(){
		world = Bukkit.getWorld("world_falling");
	}

	public static World getWorld(){
		return world;
	}

	public static void sendMessage(String message){
		Bukkit.broadcastMessage(PREFIX+message);
	}

	public static void sendMessage(Player player,String message){
		player.sendMessage(PREFIX+message);
	}

	public static void sendMessage(MapPlayer mPlayer,String message){
		MapManager.sendMessage(mPlayer.getPlayer(),message);
	}
}
