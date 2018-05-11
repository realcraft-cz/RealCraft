package realcraft.bukkit.fights.events;

import realcraft.bukkit.fights.FightPlayer;

public class FightPlayerJoinLobbyEvent extends FightEvent {

	private FightPlayer fPlayer;

	public FightPlayerJoinLobbyEvent(FightPlayer fPlayer){
		this.fPlayer = fPlayer;
	}

	public FightPlayer getPlayer(){
		return fPlayer;
	}
}