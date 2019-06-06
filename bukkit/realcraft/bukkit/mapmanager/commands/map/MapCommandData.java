package realcraft.bukkit.mapmanager.commands.map;

import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.mapmanager.MapPlayer;
import realcraft.bukkit.mapmanager.commands.MapCommand;
import realcraft.bukkit.mapmanager.map.MapPermission;

import java.util.List;

public class MapCommandData extends MapCommand {

	public MapCommandData(){
		super("data");
	}

	@Override
	public void perform(Player player,String[] args){
		MapPlayer mPlayer = MapManager.getMapPlayer(player);
		if(mPlayer.getMap() == null || !mPlayer.getMap().getPermission(mPlayer).isMinimum(MapPermission.BUILD)){
			player.sendMessage("§cNemas opravneni spravovat tuto mapu.");
			return;
		}
		mPlayer.getMap().performCommand(player,args);
	}

	@Override
	public List<String> tabCompleter(Player player,String[] args){
		MapPlayer mPlayer = MapManager.getMapPlayer(player);
		if(mPlayer.getMap() == null) return null;
		return mPlayer.getMap().tabCompleter(player,args);
	}
}