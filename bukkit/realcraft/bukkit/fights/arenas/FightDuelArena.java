package realcraft.bukkit.fights.arenas;

import java.util.ArrayList;

import org.bukkit.Location;

import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.fights.FightType;
import realcraft.bukkit.fights.Fights;
import realcraft.share.utils.RandomUtil;

public class FightDuelArena extends FightArena {

	private static final int Z_OFFSET = 300;

	private int order;
	private Location basicLocation;
	private Location spectatorLocation;
	private ArrayList<Location> spawns = new ArrayList<Location>();

	public FightDuelArena(int id,int order,String name){
		super(id,name,FightType.DUEL);
		this.order = order;
		this.loadSpawns();
	}

	public int getOrder(){
		return order;
	}

	private int getOffset(){
		return order*Z_OFFSET;
	}

	@Override
	public ArrayList<Location> getSpawns(){
		return spawns;
	}

	@Override
	public Location getRandomSpawn(){
		return spawns.get(RandomUtil.getRandomInteger(0,spawns.size()-1));
	}

	@Override
	public void loadSpawns(){
		super.loadSpawns();
		for(Location location : super.getSpawns()){
			spawns.add(location.clone().add(0,0,this.getOffset()));
		}
	}

	@Override
	public Location getBasicLocation(){
		if(basicLocation == null) basicLocation = super.getBasicLocation().clone().add(0,0,this.getOffset());
		return basicLocation;
	}

	@Override
	public Location getSpectatorLocation(){
		if(spectatorLocation == null) spectatorLocation = super.getSpectatorLocation().clone().add(0,0,this.getOffset());
		return spectatorLocation;
	}

	public boolean isUsed(){
		for(FightPlayer fPlayer : Fights.getFightPlayers(FightType.DUEL)){
			if(fPlayer.getArena().equals(this)) return true;
		}
		return false;
	}
}