package realcraft.bukkit.falling;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import realcraft.bukkit.falling.commands.FallCommands;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

import java.util.HashMap;

public class FallManager {

	private static final String PREFIX = "§6[Falling]§r ";

	private static World world;
	private static HashMap<User,FallPlayer> players = new HashMap<>();

	public FallManager(){
		world = Bukkit.getWorld("world_falling");
		new FallCommands();
	}

	public static World getWorld(){
		return world;
	}

	public static FallPlayer getFallPlayer(User user){
		if(!players.containsKey(user)) players.put(user,new FallPlayer(user));
		return players.get(user);
	}

	public static FallPlayer getFallPlayer(Player player){
		return FallManager.getFallPlayer(Users.getUser(player));
	}

	public static void sendMessage(String message){
		Bukkit.broadcastMessage(PREFIX+message);
	}

	public static void sendMessage(Player player,String message){
		player.sendMessage(PREFIX+message);
	}

	public static void sendMessage(FallPlayer mPlayer,String message){
		FallManager.sendMessage(mPlayer.getPlayer(),message);
	}
}