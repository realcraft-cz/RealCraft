package realcraft.bukkit.mapmanager.commands;

import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.mapmanager.MapPlayer;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapCommandCreate extends MapCommand {

	public MapCommandCreate(){
		super("create");
	}

	@Override
	public void perform(Player player,String[] args){
		if(args.length == 0){
			player.sendMessage("Vytvorit novou mapu");
			player.sendMessage("§6/map create §e<type>");
			player.sendMessage("§7Types: "+String.join(", ", Arrays.toString(MapType.values())).toUpperCase());
			return;
		}
		MapType type;
		try {
			type = MapType.getByName(args[0]);
		} catch (IllegalArgumentException e){
			player.sendMessage("§cNeznamy typ mapy");
			player.sendMessage("§7Types: "+String.join(", ", Arrays.toString(MapType.values())).toUpperCase());
			return;
		}
		MapPlayer mPlayer = MapManager.getMapPlayer(player);
		Map map = MapManager.createMap(mPlayer,type);
		mPlayer.joinMap(map);
		MapManager.sendMessage("§b"+player.getName()+"§a vytvoril novou "+map.getType().getColor()+map.getType().getName()+"§a mapu §7[#"+map.getId()+"]");
	}

	@Override
	public List<String> tabCompleter(Player player,String[] args){
		ArrayList<String> list = new ArrayList<>();
		for(MapType type : MapType.values()){
			if(args.length == 0 || type.toString().startsWith(args[0].toLowerCase())) list.add(type.toString().toUpperCase());
		}
		return list;
	}
}