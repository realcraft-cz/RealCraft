package realcraft.bukkit.falling.commands;

import org.bukkit.entity.Player;
import realcraft.bukkit.falling.FallManager;
import realcraft.bukkit.falling.FallPlayer;
import realcraft.bukkit.falling.arena.FallArena;

import java.util.List;

public class FallCommandCreate extends FallCommand {

	public FallCommandCreate(){
		super("create");
	}

	@Override
	public void perform(Player player,String[] args){
		FallPlayer fPlayer = FallManager.getFallPlayer(player);
		FallArena arena = fPlayer.getOwnArena();
		if(arena == null){
			FallManager.createArena(fPlayer);
			FallManager.sendMessage(player,"§fVytvareni ostrovu, prosim vyckejte ...");
		} else {
			FallManager.getFallPlayer(player).joinArena(arena);
		}
	}

	@Override
	public List<String> tabCompleter(Player player,String[] args){
		return null;
	}
}