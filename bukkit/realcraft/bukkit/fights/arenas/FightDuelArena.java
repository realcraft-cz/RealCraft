package realcraft.bukkit.fights.arenas;

import org.bukkit.Location;

import realcraft.bukkit.fights.FightType;
import realcraft.bukkit.utils.LocationUtil;

public class FightDuelArena extends FightArena {

	private Location spectatorLocation;

	public FightDuelArena(String name){
		super(name,FightType.DUEL);
	}

	public Location getSpectatorLocation(){
		if(spectatorLocation == null) spectatorLocation = LocationUtil.getConfigLocation(this.getConfig(),"spectator");
		return spectatorLocation;
	}
}