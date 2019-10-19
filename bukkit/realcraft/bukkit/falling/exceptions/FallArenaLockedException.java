package realcraft.bukkit.falling.exceptions;

import realcraft.bukkit.falling.arena.FallArena;

public class FallArenaLockedException extends FallArenaException {

	public FallArenaLockedException(FallArena arena){
		super(arena);
	}
}