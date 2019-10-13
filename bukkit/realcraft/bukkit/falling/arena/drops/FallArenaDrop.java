package realcraft.bukkit.falling.arena.drops;

import org.bukkit.Location;
import realcraft.bukkit.falling.FallManager;

public abstract class FallArenaDrop {

	private long ticks;
	private long lastTick;

	public FallArenaDrop(long ticks){
		this.ticks = ticks;
		this.lastTick = FallManager.getWorld().getFullTime();
	}

	public long getTicks(){
		return ticks;
	}

	public long getLastTick(){
		return lastTick;
	}

	public void setLastTick(long lastTick){
		this.lastTick = lastTick;
	}

	public abstract void drop(Location location);
}