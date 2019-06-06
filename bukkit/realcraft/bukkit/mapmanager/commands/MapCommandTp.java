package realcraft.bukkit.mapmanager.commands;

import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.mapmanager.map.Map;

import java.util.List;

public class MapCommandTp extends MapCommand {

	public MapCommandTp(){
		super("tp","join");
	}

	@Override
	public void perform(Player player,String[] args){
		if(args.length == 0){
			player.sendMessage("Pripojit se do mapy");
			player.sendMessage("§6/map tp §e<id>");
			return;
		}
		int id;
		try {
			id = Integer.valueOf(args[0]);
		} catch (NumberFormatException e){
			player.sendMessage("§cZadej cele cislo.");
			return;
		}
		Map map = MapManager.getMap(id);
		if(map == null){
			player.sendMessage("§cMapa #"+id+" neexistuje.");
			return;
		}
		MapManager.getMapPlayer(player).joinMap(map);
	}

	@Override
	public List<String> tabCompleter(Player player,String[] args){
		return null;
	}
}