package realcraft.bukkit.falling.commands;

import realcraft.bukkit.falling.FallManager;
import realcraft.bukkit.falling.FallPlayer;
import realcraft.bukkit.falling.arena.FallArena;
import realcraft.bukkit.falling.exceptions.FallArenaLockedException;

import java.util.List;

public class FallCommandCreate extends FallCommand {

	public FallCommandCreate(){
		super("create");
	}

	@Override
	public void perform(FallPlayer fPlayer,String[] args){
		FallArena arena = fPlayer.getOwnArena();
		if(arena == null){
			FallManager.createArena(fPlayer);
			FallManager.sendMessage(fPlayer,"§fVytvareni ostrovu, prosim vyckejte ...");
		} else {
			try {
				fPlayer.joinArena(arena);
			} catch (FallArenaLockedException e){
			}
		}
	}

	@Override
	public List<String> tabCompleter(FallPlayer fPlayer,String[] args){
		return null;
	}
}