package realcraft.bukkit.mapmanager.commands;

import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.mapmanager.commands.map.*;
import realcraft.bukkit.others.AbstractCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapCommands extends AbstractCommand {

	private MapCommand[] commands;

	public MapCommands(){
		super("map","maps","Map","MAP","MAPS");
		commands = new MapCommand[]{
				new MapCommandCreate(),
				new MapCommandTp(),
				new MapCommandInfo(),
				new MapCommandList(),
				new MapCommandName(),
				new MapCommandTime(),
				new MapCommandBiome(),
				new MapCommandEnvironment(),
				new MapCommandTrust(),
				new MapCommandUnTrust(),
				new MapCommandData(),
				new MapCommandReady()
		};
	}

	@Override
	public void perform(Player player,String[] args){
		if(args.length == 0){
			player.sendMessage("§7§m"+" ".repeat(10)+"§r §6§lMaps §7§m"+" ".repeat(47-"Maps".length()));
			player.sendMessage("§6/map create §e<type> §f- Vytvorit novou mapu");
			player.sendMessage("§6/map tp §e<id> §f- Pripojit se do mapy");
			player.sendMessage("§6/map info §e[<id>] §f- Informace o mape");
			player.sendMessage("§6/map list §f- Seznam map");
			if(MapManager.getMapPlayer(player).getMap() != null){
				player.sendMessage("§7---------------");
				player.sendMessage("§6/map name §e<name> §f- Nastaveni nazvu");
				player.sendMessage("§6/map time §e<time> §f- Nastaveni casu");
				player.sendMessage("§6/map biome §e<biome> §f- Nastaveni biomu");
				player.sendMessage("§6/map env §e<environment> §f- Nastaveni prostredi");
				player.sendMessage("§6/map (un)trust §e<player> §f- (Odebrat) Pridat spoluhrace");
				player.sendMessage("§6/map data §e<key> §f- Nastaveni dat");
				player.sendMessage("§6/map ready§f- Dokonceni mapy");

			}
			return;
		}
		String subcommand = args[0].toLowerCase();
		args = Arrays.copyOfRange(args,1,args.length);
		for(MapCommand command : commands){
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
				for(MapCommand command : commands) cmds.add(command.getName());
			} else {
				for(MapCommand command : commands){
					if(command.startsWith(args[0])){
						cmds.add(command.getName());
					}
				}
			}
			return cmds;
		}
		String subcommand = args[0].toLowerCase();
		args = Arrays.copyOfRange(args,1,args.length);
		for(MapCommand command : commands){
			if(command.match(subcommand)){
				return command.tabCompleter(player,args);
			}
		}
		return null;
	}
}