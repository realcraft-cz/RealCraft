package realcraft.bukkit.mapmanager.commands.map;

import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.mapmanager.MapPlayer;
import realcraft.bukkit.mapmanager.commands.MapCommand;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapPermission;

import java.util.List;

public class MapCommandRegenerate extends MapCommand {

	public MapCommandRegenerate(){
		super("regenerate");
	}

	@Override
	public void perform(Player player,String[] args){
		MapPlayer mPlayer = MapManager.getMapPlayer(player);
		if(mPlayer.getMap() == null || !mPlayer.getMap().getPermission(mPlayer).isMinimum(MapPermission.OWNER)){
			player.sendMessage("§cNemas opravneni spravovat tuto mapu.");
			return;
		}
		Map map = mPlayer.getMap();
		if(map.getRegion().isLoading()) {
			player.sendMessage("§cMapa se prave nacita.");
			return;
		}

		MapManager.sendMessage("§7Nacitani mapy §e"+map.getName()+" §7[#"+map.getId()+"] ...");
		map.getRegion().load();
	}

	@Override
	public List<String> tabCompleter(Player player,String[] args){
		return null;
	}
}