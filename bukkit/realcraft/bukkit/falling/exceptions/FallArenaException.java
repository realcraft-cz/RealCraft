package realcraft.bukkit.falling.exceptions;

import realcraft.bukkit.falling.arena.FallArena;

public abstract class FallArenaException extends Exception {

	private FallArena arena;

	public FallArenaException(FallArena arena){
		this.arena = arena;
	}

	public FallArena getArena(){
		return arena;
	}
}