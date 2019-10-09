package realcraft.bukkit.parkour.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import realcraft.bukkit.parkour.ParkourArena;
import realcraft.bukkit.parkour.ParkourPlayer;

public abstract class ParkourEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private Event event;
	private ParkourArena arena;
	private ParkourPlayer player = null;
	private boolean cancelled = false;

	public ParkourEvent(ParkourArena arena){
		this(arena,null,null);
	}

	public ParkourEvent(ParkourArena arena,Event event){
		this(arena,null,event);
	}

	public ParkourEvent(ParkourArena arena,ParkourPlayer player){
		this(arena,player,null);
	}

	public ParkourEvent(ParkourArena arena,ParkourPlayer player,Event event){
		this.arena = arena;
		this.player = player;
		this.event = event;
	}

	public Event getEventBase(){
		return event;
	}

	public ParkourArena getArena(){
		return arena;
	}

	public ParkourPlayer getPlayer(){
		return player;
	}

	@Override
	public boolean isCancelled(){
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel){
		cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers(){
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}
}