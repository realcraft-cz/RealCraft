package realcraft.bukkit.mapmanager.commands.map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.mapmanager.MapPlayer;
import realcraft.bukkit.mapmanager.commands.MapCommand;
import realcraft.bukkit.mapmanager.map.MapPermission;

import java.util.List;

public class MapCommandTime extends MapCommand {

	public MapCommandTime(){
		super("time");
	}

	@Override
	public void perform(Player player,String[] args){
		MapPlayer mPlayer = MapManager.getMapPlayer(player);
		if(mPlayer.getMap() == null || !mPlayer.getMap().getPermission(mPlayer).isMinimum(MapPermission.BUILD)){
			player.sendMessage("§cNemas opravneni spravovat tuto mapu.");
			return;
		}
		if(args.length == 0){
			player.sendMessage("Nastaveni casu");
			player.sendMessage("§6/map time §e<time>");
			return;
		}
		int time;
		try {
			time = Integer.valueOf(args[0])%24000;
		} catch (NumberFormatException e){
			player.sendMessage("§cZadej cele cislo.");
			return;
		}
		mPlayer.getMap().getTime().setValue(time);
		mPlayer.getMap().save();
		MapManager.sendMessage(player,"§dCas nastaven na §f"+time+"ticks");
		for(Player player2 : Bukkit.getOnlinePlayers()){
			if(MapManager.getMapPlayer(player).getMap().equals(mPlayer.getMap())){
				player2.setPlayerTime(mPlayer.getMap().getTime().getValue(),false);
			}
		}
	}

	@Override
	public List<String> tabCompleter(Player player,String[] args){
		return null;
	}
}