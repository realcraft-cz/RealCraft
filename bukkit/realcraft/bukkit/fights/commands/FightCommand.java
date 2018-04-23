package realcraft.bukkit.fights.commands;

import java.util.List;

import org.bukkit.entity.Player;

import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.fights.Fights;

public abstract class FightCommand {

	private String[] names;

	public FightCommand(String... names){
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

	protected FightPlayer findFriendPlayer(FightPlayer fPlayer,String name){
		FightPlayer victim = Fights.getFightPlayer(name);
		if(victim != null /*&& !victim.equals(fPlayer)*/) return victim;
		return null;
	}

	public List<String> onTabComplete(Player player,String name){
		return null;
	}

	public abstract void perform(Player player,String[] args);
}