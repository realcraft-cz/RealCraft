package realcraft.bukkit.mapmanager.commands.map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.mapmanager.MapPlayer;
import realcraft.bukkit.mapmanager.commands.MapCommand;
import realcraft.bukkit.mapmanager.map.MapPermission;
import realcraft.bukkit.mapmanager.map.data.MapDataInteger;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

import java.util.ArrayList;
import java.util.List;

public class MapCommandUnTrust extends MapCommand {

	public MapCommandUnTrust(){
		super("untrust");
	}

	@Override
	public void perform(Player player,String[] args){
		MapPlayer mPlayer = MapManager.getMapPlayer(player);
		if(mPlayer.getMap() == null || !mPlayer.getMap().getPermission(mPlayer).isMinimum(MapPermission.OWNER)){
			player.sendMessage("§cNemas opravneni spravovat tuto mapu.");
			return;
		}
		if(args.length == 0){
			player.sendMessage("Odebrat spoluhrace");
			player.sendMessage("§6/map untrust §e<player>");
			return;
		}
		User user = Users.getUser(args[0]);
		if(user == null || user.equals(mPlayer.getUser())){
			player.sendMessage("§cHrac nenalezen.");
			return;
		}
		if(mPlayer.getMap().getPermission(MapManager.getMapPlayer(user)).isMaximum(MapPermission.NONE)){
			player.sendMessage("§cHrac je jiz odebrany.");
			return;
		}
		mPlayer.getMap().getTrusted().remove(new MapDataInteger(user.getId()));
		mPlayer.getMap().save();
		MapManager.sendMessage(player,"§dSpoluhrac §f"+user.getName()+" §dodebran");
	}

	@Override
	public List<String> tabCompleter(Player player,String[] args){
		List<String> players = new ArrayList<>();
		for(Player target : Bukkit.getOnlinePlayers()) players.add(target.getName());
		return players;
	}
}