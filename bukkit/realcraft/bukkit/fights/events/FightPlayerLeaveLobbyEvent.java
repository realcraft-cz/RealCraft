package realcraft.bukkit.fights.events;

import realcraft.bukkit.fights.FightPlayer;

public class FightPlayerLeaveLobbyEvent extends FightEvent {

	private FightPlayer fPlayer;

	public FightPlayerLeaveLobbyEvent(FightPlayer fPlayer){
		this.fPlayer = fPlayer;
	}

	public FightPlayer getPlayer(){
		return fPlayer;
	}
}