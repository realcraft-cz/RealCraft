package realcraft.bukkit.falling.commands;

import realcraft.bukkit.falling.FallManager;
import realcraft.bukkit.falling.FallPlayer;
import realcraft.bukkit.falling.arena.FallArena;
import realcraft.bukkit.falling.arena.FallArenaPermission;

import java.util.List;

public class FallCommandRegen extends FallCommand {

	public FallCommandRegen(){
		super("regen");
	}

	@Override
	public void perform(FallPlayer fPlayer,String[] args){
		FallArena arena = fPlayer.getOwnArena();
		if(arena == null || fPlayer.getArena() == null || fPlayer.getArena().getPermission(fPlayer) != FallArenaPermission.OWNER){
			fPlayer.sendMessage("§cNemas zadny vlastni ostrov.");
			return;
		}
		if(arena.getRegion().isGenerating()){
			fPlayer.sendMessage("§cOstrov se jiz generuje.");
			return;
		}
		FallManager.sendMessage(fPlayer,"§fGeneruji novy ostrov, prosim vyckejte ...");
		arena.getRegion().setGenerating(true);
		arena.getRegion().generate();
	}

	@Override
	public List<String> tabCompleter(FallPlayer fPlayer,String[] args){
		return null;
	}
}