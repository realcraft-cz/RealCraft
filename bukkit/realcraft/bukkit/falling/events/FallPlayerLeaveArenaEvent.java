package realcraft.bukkit.falling.events;

import realcraft.bukkit.falling.FallPlayer;
import realcraft.bukkit.falling.arena.FallArena;

public class FallPlayerLeaveArenaEvent extends FallEvent {

	private FallPlayer mPlayer;
	private FallArena arena;

	public FallPlayerLeaveArenaEvent(FallPlayer mPlayer,FallArena arena){
		this.mPlayer = mPlayer;
		this.arena = arena;
	}

	public FallPlayer getPlayer(){
		return mPlayer;
	}

	public FallArena getArena(){
		return arena;
	}
}