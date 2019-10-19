package realcraft.bukkit.falling.commands;

import realcraft.bukkit.falling.FallManager;
import realcraft.bukkit.falling.FallPlayer;
import realcraft.bukkit.falling.arena.FallArena;
import realcraft.bukkit.falling.arena.FallArenaPermission;

import java.util.List;

public class FallCommandUnlock extends FallCommand {

	public FallCommandUnlock(){
		super("unlock");
	}

	@Override
	public void perform(FallPlayer fPlayer,String[] args){
		FallArena arena = fPlayer.getOwnArena();
		if(arena == null || fPlayer.getArena() == null || fPlayer.getArena().getPermission(fPlayer) != FallArenaPermission.OWNER){
			fPlayer.sendMessage("§cNemas zadny vlastni ostrov.");
			return;
		}
		if(!arena.isLocked()){
			fPlayer.sendMessage("§cOstrov je jiz odemknuty.");
			return;
		}
		FallManager.sendMessage(fPlayer,"§fOstrov odemknuty, kdokoliv se muze pripojit.");
		arena.setLocked(false);
		arena.save();
	}

	@Override
	public List<String> tabCompleter(FallPlayer fPlayer,String[] args){
		return null;
	}
}