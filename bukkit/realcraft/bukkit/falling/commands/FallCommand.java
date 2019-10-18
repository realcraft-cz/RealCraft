package realcraft.bukkit.falling.commands;

import realcraft.bukkit.falling.FallPlayer;

import java.util.List;

public abstract class FallCommand {

	private String[] names;

	public FallCommand(String... names){
		this.names = names;
	}

	public String getName(){
		return names[0];
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

	public abstract void perform(FallPlayer fPlayer,String[] args);
	public abstract List<String> tabCompleter(FallPlayer fPlayer,String[] args);
}