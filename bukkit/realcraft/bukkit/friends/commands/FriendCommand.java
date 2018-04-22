package realcraft.bukkit.friends.commands;

import java.util.List;

import org.bukkit.entity.Player;

import realcraft.bukkit.friends.FriendPlayer;
import realcraft.bukkit.friends.Friends;

public abstract class FriendCommand {

	private String[] names;

	public FriendCommand(String... names){
		this.names = names;
	}

	public String[] getNames(){
		return names;
	}

	public boolean match(String command){
		for(String name : names){
			if(name.equalsIgnoreCase(command)) return true;
		}
		return false;
	}

	public boolean startsWith(String command){
		for(String name : names){
			if(name.startsWith(command.toLowerCase())) return true;
		}
		return false;
	}

	protected FriendPlayer findFriendPlayer(FriendPlayer fPlayer,String name){
		FriendPlayer victim = Friends.getFriendPlayer(name);
		if(victim != null && victim.getId() != fPlayer.getId()) return victim;
		return null;
	}

	public List<String> onTabComplete(Player player,String name){
		return null;
	}

	public abstract void perform(Player player,String[] args);
}