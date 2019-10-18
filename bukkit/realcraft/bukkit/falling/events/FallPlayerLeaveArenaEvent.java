package realcraft.bukkit.falling.events;

import realcraft.bukkit.falling.FallPlayer;
import realcraft.bukkit.falling.arena.FallArena;

public class FallPlayerLeaveArenaEvent extends FallEvent {

	private FallPlayer fPlayer;
	private FallArena arena;

	public FallPlayerLeaveArenaEvent(FallPlayer fPlayer,FallArena arena){
		this.fPlayer = fPlayer;
		this.arena = arena;
	}

	public FallPlayer getPlayer(){
		return fPlayer;
	}

	public FallArena getArena(){
		return arena;
	}
}