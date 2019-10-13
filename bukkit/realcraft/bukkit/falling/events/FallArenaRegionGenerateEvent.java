package realcraft.bukkit.falling.events;

import realcraft.bukkit.falling.arena.FallArena;

public class FallArenaRegionGenerateEvent extends FallEvent {

	private FallArena arena;

	public FallArenaRegionGenerateEvent(FallArena arena){
		this.arena = arena;
	}

	public FallArena getArena(){
		return arena;
	}
}