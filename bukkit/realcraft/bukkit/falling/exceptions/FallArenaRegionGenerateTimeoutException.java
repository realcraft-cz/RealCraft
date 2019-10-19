package realcraft.bukkit.falling.exceptions;

import realcraft.bukkit.falling.arena.FallArena;

public class FallArenaRegionGenerateTimeoutException extends FallArenaException {

	private int remainingSeconds;

	public FallArenaRegionGenerateTimeoutException(FallArena arena,int remainingSeconds){
		super(arena);
		this.remainingSeconds = remainingSeconds;
	}

	public int getRemainingSeconds(){
		return remainingSeconds;
	}
}