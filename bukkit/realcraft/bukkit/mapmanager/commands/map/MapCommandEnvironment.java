package realcraft.bukkit.mapmanager.commands.map;

import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.mapmanager.MapPlayer;
import realcraft.bukkit.mapmanager.commands.MapCommand;
import realcraft.bukkit.mapmanager.map.MapPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapCommandEnvironment extends MapCommand {

	public MapCommandEnvironment(){
		super("env");
	}

	@Override
	public void perform(Player player,String[] args){
		MapPlayer mPlayer = MapManager.getMapPlayer(player);
		if(mPlayer.getMap() == null || !mPlayer.getMap().getPermission(mPlayer).isMinimum(MapPermission.BUILD)){
			player.sendMessage("§cNemas opravneni spravovat tuto mapu.");
			return;
		}
		if(args.length == 0){
			player.sendMessage("Nastaveni prostredi");
			player.sendMessage("§6/map env §e<environment>");
			return;
		}
		Environment environment;
		try {
			environment = Environment.valueOf(args[0].toUpperCase());
		} catch (IllegalArgumentException e){
			player.sendMessage("§cNeplatne prostredi.");
			player.sendMessage("§7Environments: "+String.join(", ", Arrays.toString(Environment.values())).toUpperCase());
			return;
		}
		mPlayer.getMap().getEnvironment().setEnvironment(environment);
		MapManager.sendMessage(player,"§7Prostredi nastaveno na §f"+environment.toString());
	}

	@Override
	public List<String> tabCompleter(Player player,String[] args){
		ArrayList<String> list = new ArrayList<>();
		for(Environment environment : Environment.values()){
			if(args.length == 0 || environment.toString().startsWith(args[0].toUpperCase())) list.add(environment.toString().toUpperCase());
		}
		return list;
	}
}