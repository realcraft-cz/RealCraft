package realcraft.bukkit.falling.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import realcraft.bukkit.falling.FallManager;
import realcraft.bukkit.others.AbstractCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FallCommands extends AbstractCommand {

	private FallCommand[] commands;

	public FallCommands(){
		super("ff","fall","falling");
		commands = new FallCommand[]{
		};
	}

	@Override
	public void perform(Player player,String[] args){
		if(args.length == 0){
			player.sendMessage("§7§m"+StringUtils.repeat(" ",10)+"§r §6§lFalling FallManager §7§m"+StringUtils.repeat(" ",47-"FallManager FallManager".length()));
			player.sendMessage("§6/ff create §e<type> §f- Vytvorit novou mapu");
			if(FallManager.getFallPlayer(player).getArena() != null){
				player.sendMessage("§7---------------");
				player.sendMessage("§6/ff name §e<name> §f- Nastaveni nazvu");
			}
			return;
		}
		String subcommand = args[0].toLowerCase();
		args = Arrays.copyOfRange(args,1,args.length);
		for(FallCommand command : commands){
			if(command.match(subcommand)){
				command.perform(player,args);
			}
		}
	}

	@Override
	public List<String> tabCompleter(Player player,String[] args){
		if(args.length <= 1){
			ArrayList<String> cmds = new ArrayList<>();
			if(args.length == 0){
				for(FallCommand command : commands) cmds.add(command.getName());
			} else {
				for(FallCommand command : commands){
					if(command.startsWith(args[0])){
						cmds.add(command.getName());
					}
				}
			}
			return cmds;
		}
		String subcommand = args[0].toLowerCase();
		args = Arrays.copyOfRange(args,1,args.length);
		for(FallCommand command : commands){
			if(command.match(subcommand)){
				return command.tabCompleter(player,args);
			}
		}
		return null;
	}
}