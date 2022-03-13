package realcraft.bukkit.mapmanager.commands;

import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.mapmanager.MapPlayer;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.data.MapDataInteger;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.DateUtil;
import realcraft.share.users.User;

import java.util.List;

public class MapCommandInfo extends MapCommand {

	public MapCommandInfo(){
		super("info");
	}

	@Override
	public void perform(Player player,String[] args){
		int id;
		MapPlayer mPlayer = MapManager.getMapPlayer(player);
		if(args.length == 0){
			if(mPlayer.getMap() == null){
				player.sendMessage("Informace o mape");
				player.sendMessage("§6/map info §e[<id>]");
				return;
			} else {
				id = mPlayer.getMap().getId();
			}
		} else {
			try {
				id = Integer.valueOf(args[0]);
			} catch (NumberFormatException e){
				player.sendMessage("§cZadej cele cislo.");
				return;
			}
		}
		Map map = MapManager.getMap(id);
		if(map == null){
			player.sendMessage("§cMapa #"+id+" neexistuje.");
			return;
		}

		String[] trusted = new String[map.getTrusted().size()];
		int idx = 0;
		for(MapDataInteger value : map.getTrusted().getValues()){
			User user = Users.getUser(value.getValue());
			if(user != null) trusted[idx++] = user.getRank().getChatColor()+"§l"+user.getName();
		}

		player.sendMessage("§7§m"+" ".repeat(10)+"§r §f§l"+map.getName()+" §7[#"+map.getId()+"] ("+map.getType().getColor()+map.getType().getName()+"§7) §7§m"+" ".repeat(47-+(map.getName()+" §7[#"+map.getId()+"] ("+map.getType().getColor()+map.getType().getName()+"§7)").length()));
		player.sendMessage("§7Autor: "+map.getUser().getRank().getChatColor()+"§l"+map.getUser().getName());
		if(trusted.length > 0) player.sendMessage("§7Trusted: "+String.join(", ", trusted));
		player.sendMessage("§7Stav: "+map.getState().getColor()+"§l"+map.getState().getName());
		player.sendMessage("§7Vytvoreno: §f"+DateUtil.lastTime(map.getCreated()));
		player.sendMessage("§7Upraveno: §f"+DateUtil.lastTime(map.getUpdated()));
	}

	@Override
	public List<String> tabCompleter(Player player,String[] args){
		return null;
	}
}