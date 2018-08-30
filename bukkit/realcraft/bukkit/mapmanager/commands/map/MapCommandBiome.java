package realcraft.bukkit.mapmanager.commands.map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.mapmanager.MapPlayer;
import realcraft.bukkit.mapmanager.commands.MapCommand;
import realcraft.bukkit.mapmanager.map.MapPermission;

import java.util.ArrayList;
import java.util.List;

public class MapCommandBiome extends MapCommand {

	public MapCommandBiome(){
		super("biome");
	}

	@Override
	public void perform(Player player,String[] args){
		MapPlayer mPlayer = MapManager.getMapPlayer(player);
		if(mPlayer.getMap() == null || !mPlayer.getMap().getPermission(mPlayer).isMinimum(MapPermission.BUILD)){
			player.sendMessage("§cNemas opravneni spravovat tuto mapu.");
			return;
		}
		if(args.length == 0){
			player.sendMessage("Nastaveni biomu");
			player.sendMessage("§6/map biome §e<biome>");
			return;
		}
		Biome biome;
		try {
			biome = Biome.valueOf(args[0]);
		} catch (IllegalArgumentException e){
			player.sendMessage("§cNeplatny biome.");
			player.sendMessage("§7Biomes: "+StringUtils.join(Biome.values(),", ").toUpperCase());
			return;
		}
		mPlayer.getMap().getBiome().setBiome(biome);
		MapManager.sendMessage(player,"§dBiome nastaven na §f"+biome.toString());
	}

	@Override
	public List<String> tabCompleter(Player player,String[] args){
		ArrayList<String> list = new ArrayList<>();
		for(Biome biome : Biome.values()){
			if(args.length == 0 || biome.toString().startsWith(args[0].toUpperCase())) list.add(biome.toString().toUpperCase());
		}
		return list;
	}
}