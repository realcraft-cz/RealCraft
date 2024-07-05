package realcraft.bukkit.lobby;

import org.bukkit.entity.Player;
import realcraft.bukkit.others.AbstractCommand;
import realcraft.bukkit.utils.BungeeMessages;
import realcraft.share.ServerType;

public class LobbyTeleport extends AbstractCommand {

	public LobbyTeleport(){
		super("hub","lobby","loby");
	}

	@Override
	public void perform(Player player,String[] args){
		BungeeMessages.connectPlayerToServer(player,ServerType.LOBBY);
	}
}