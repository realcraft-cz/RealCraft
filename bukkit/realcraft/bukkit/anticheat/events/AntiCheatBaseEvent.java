package realcraft.bukkit.anticheat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class AntiCheatBaseEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private Player player = null;
	private boolean cancelled = false;

	public AntiCheatBaseEvent(Player player){
		this.player = player;
	}

	public Player getPlayer(){
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