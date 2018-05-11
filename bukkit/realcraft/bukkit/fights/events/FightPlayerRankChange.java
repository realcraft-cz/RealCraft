package realcraft.bukkit.fights.events;

import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.fights.FightRank;

public class FightPlayerRankChange extends FightEvent {

	private FightPlayer fPlayer;
	private FightRank oldRank;

	public FightPlayerRankChange(FightPlayer fPlayer,FightRank oldRank){
		this.fPlayer = fPlayer;
		this.oldRank = oldRank;
	}

	public FightPlayer getPlayer(){
		return fPlayer;
	}

	public FightRank getOldRank(){
		return oldRank;
	}
}