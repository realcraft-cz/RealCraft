package realcraft.bukkit.others;

import org.bukkit.entity.Player;
import realcraft.bukkit.utils.BungeeMessages;
import realcraft.share.ServerType;

public class MapServerTeleport extends AbstractCommand {

	public MapServerTeleport(){
		super("maps","map");
	}

	@Override
	public void perform(Player player,String[] args){
		BungeeMessages.connectPlayerToServer(player,ServerType.MAPS);
	}
}