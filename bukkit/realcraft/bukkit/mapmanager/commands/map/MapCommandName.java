package realcraft.bukkit.mapmanager.commands.map;

import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.mapmanager.MapPlayer;
import realcraft.bukkit.mapmanager.commands.MapCommand;
import realcraft.bukkit.mapmanager.exceptions.MapInvalidNameException;
import realcraft.bukkit.mapmanager.exceptions.MapNameExistsException;
import realcraft.bukkit.mapmanager.map.MapPermission;

import java.util.List;

public class MapCommandName extends MapCommand {

	public MapCommandName(){
		super("name");
	}

	@Override
	public void perform(Player player,String[] args){
		MapPlayer mPlayer = MapManager.getMapPlayer(player);
		if(mPlayer.getMap() == null || !mPlayer.getMap().getPermission(mPlayer).isMinimum(MapPermission.OWNER)){
			player.sendMessage("§cNemas opravneni spravovat tuto mapu.");
			return;
		}
		if(args.length == 0){
			player.sendMessage("Nastavit nazev mapy");
			player.sendMessage("§6/map name §e<name>");
			return;
		}
		try {
			mPlayer.getMap().setName(args[0]);
		} catch (MapInvalidNameException e){
			player.sendMessage("§cNazev obsahuje nepovolene znaky [a-zA-Z0-9] (max 32 znaku).");
		} catch (MapNameExistsException e){
			player.sendMessage("§cZadany nazev jiz existuje.");
		}
		MapManager.sendMessage(player,"§dNazev mapy zmenen na §f§l"+args[0]);
	}

	@Override
	public List<String> tabCompleter(Player player,String[] args){
		return null;
	}
}