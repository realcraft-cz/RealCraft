package realcraft.bukkit.fights.arenas;

import realcraft.bukkit.fights.FightType;

public class FightPublicArena extends FightArena {

	public FightPublicArena(int id,String name){
		super(id,name,FightType.PUBLIC);
		this.loadSpawns();
	}
}