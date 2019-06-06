package realcraft.bukkit.mapmanager.commands.map;

import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.mapmanager.MapPlayer;
import realcraft.bukkit.mapmanager.commands.MapCommand;
import realcraft.bukkit.mapmanager.map.MapPermission;
import realcraft.bukkit.mapmanager.map.MapState;

import java.util.List;

public class MapCommandReady extends MapCommand {

	public MapCommandReady(){
		super("ready");
	}

	@Override
	public void perform(Player player,String[] args){
		MapPlayer mPlayer = MapManager.getMapPlayer(player);
		if(mPlayer.getMap() == null || !mPlayer.getMap().getPermission(mPlayer).isMinimum(MapPermission.OWNER)){
			player.sendMessage("§cNemas opravneni spravovat tuto mapu.");
			return;
		}
		if(!mPlayer.getMap().isValid()){
			player.sendMessage("§cNastaveni mapy neni validni.");
			return;
		}
		mPlayer.getMap().setState((mPlayer.getMap().getState() == MapState.BUILD ? MapState.READY : MapState.BUILD));
		mPlayer.getMap().save();
		MapManager.sendMessage(player,"§dStav mapy zmenen na "+mPlayer.getMap().getState().getColor()+mPlayer.getMap().getState().getName());
	}

	@Override
	public List<String> tabCompleter(Player player,String[] args){
		return null;
	}
}