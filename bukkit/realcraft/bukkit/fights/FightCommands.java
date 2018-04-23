package realcraft.bukkit.fights;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.fights.commands.FightCommand;
import realcraft.bukkit.fights.commands.FightCommandDuel;

public class FightCommands implements CommandExecutor, TabCompleter {

	private FightCommand[] commands;

	public FightCommands(){
		RealCraft.getInstance().getCommand("fight").setExecutor(this);
		commands = new FightCommand[]{
			new FightCommandDuel(),
		};
	}

	@Override
	public boolean onCommand(CommandSender sender,Command command,String label,String[] args){
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("fight")){
			if(args.length == 0 || this.getCommand(args[0]) == null){
				this.showHelpPage(player);
				return true;
			}
			String[] arguments = new String[args.length-1];
			System.arraycopy(args,1,arguments,0,args.length-1);
			this.getCommand(args[0]).perform(player,arguments);
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender,Command command,String alias,String[] args){
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("fight")){
			if(args.length == 1) return this.findCommands(args[0]);
			else if(args.length == 2 && this.getCommand(args[0]) != null) return this.getCommand(args[0]).onTabComplete(player,args[1]);
		}
		return null;
	}

	private List<String> findCommands(String name){
		List<String> cmds = new ArrayList<String>();
		for(FightCommand command : commands){
			if(command.startsWith(name)) cmds.add(command.getNames()[0]);
		}
		return cmds;
	}

	private FightCommand getCommand(String name){
		for(FightCommand command : commands){
			if(command.match(name)) return command;
		}
		return null;
	}

	private void showHelpPage(Player player){
		player.sendMessage("§7§m"+StringUtils.repeat(" ",10)+"§r §a§lFights §7§m"+StringUtils.repeat(" ",47-"Fights".length()));
		player.sendMessage("§6/fight duel §e<player> §f- Vyzvat hrace na duel");
		player.sendMessage("§7§m"+StringUtils.repeat(" ",62));
	}
}