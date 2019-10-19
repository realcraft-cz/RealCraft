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
				new FallCommandCreate(),
				new FallCommandRegen(),
				new FallCommandJoin(),
				new FallCommandTrust(),
				new FallCommandUntrust(),
				new FallCommandLock(),
				new FallCommandUnlock(),
		};
	}

	@Override
	public void perform(Player player,String[] args){
		if(args.length == 0){
			player.sendMessage("�7�m"+StringUtils.repeat(" ",10)+"�r �6�lFalling �7�m"+StringUtils.repeat(" ",47-"Falling".length()));
			player.sendMessage("�6/ff create �f- Vytvorit novy ostrov");
			player.sendMessage("�6/ff regen �f- Pregenerovat ostrov");
			player.sendMessage("�6/ff join �e<player> �f- Teleport na hracuv ostrov");
			player.sendMessage("�6/ff (un)trust �e<player> �f- (Odebrat) Pridat spoluhrace");
			player.sendMessage("�6/ff (un)lock �f- (Odemknout) Zamknout ostrov");
			return;
		}
		String subcommand = args[0].toLowerCase();
		args = Arrays.copyOfRange(args,1,args.length);
		for(FallCommand command : commands){
			if(command.match(subcommand)){
				command.perform(FallManager.getFallPlayer(player),args);
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
				return command.tabCompleter(FallManager.getFallPlayer(player),args);
			}
		}
		return null;
	}
}