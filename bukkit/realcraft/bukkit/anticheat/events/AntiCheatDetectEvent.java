package realcraft.bukkit.anticheat.events;

import org.bukkit.entity.Player;

import realcraft.bukkit.anticheat.checks.Check.CheckType;

public class AntiCheatDetectEvent extends AntiCheatBaseEvent {

	private CheckType type;

	public AntiCheatDetectEvent(Player player,CheckType type){
		super(player);
		this.type = type;
	}

	public CheckType getType(){
		return this.type;
	}
}