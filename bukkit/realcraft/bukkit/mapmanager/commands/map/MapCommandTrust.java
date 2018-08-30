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

public class MapCommandTrust extends MapCommand {

	public MapCommandTrust(){
		super("trust");
	}

	@Override
	public void perform(Player player,String[] args){
		MapPlayer mPlayer = MapManager.getMapPlayer(player);
		if(mPlayer.getMap() == null || !mPlayer.getMap().getPermission(mPlayer).isMinimum(MapPermission.OWNER)){
			player.sendMessage("§cNemas opravneni spravovat tuto mapu.");
			return;
		}
		if(args.length == 0){
			player.sendMessage("Pridat spoluhrace");
			player.sendMessage("§6/map trust §e<player>");
			return;
		}
		User user = Users.getUser(args[0]);
		if(user == null || user.equals(mPlayer.getUser())){
			player.sendMessage("§cHrac nenalezen.");
			return;
		}
		if(mPlayer.getMap().getPermission(MapManager.getMapPlayer(user)).isMinimum(MapPermission.BUILD)){
			player.sendMessage("§cHrac je jiz pridany.");
			return;
		}
		mPlayer.getMap().getTrusted().add(new MapDataInteger(user.getId()));
		mPlayer.getMap().save();
		MapManager.sendMessage(player,"§dSpoluhrac §f"+user.getName()+" §dpridan");
	}

	@Override
	public List<String> tabCompleter(Player player,String[] args){
		List<String> players = new ArrayList<>();
		for(Player target : Bukkit.getOnlinePlayers()) players.add(target.getName());
		return players;
	}
}