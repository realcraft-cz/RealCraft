package realcraft.bukkit.falling.arena.drops;

import realcraft.bukkit.falling.FallManager;
import realcraft.bukkit.falling.arena.FallArenaRegion;

import java.util.ArrayList;

public class FallArenaDrops {

	private FallArenaRegion region;
	private ArrayList<FallArenaDrop> drops = new ArrayList<>();

	public FallArenaDrops(FallArenaRegion region){
		this.region = region;
		for(FallArenaDropBlockType type : FallArenaDropBlockType.values()){
			drops.add(new FallArenaDropBlock(type));
		}
		for(FallArenaDropEntityType type : FallArenaDropEntityType.values()){
			drops.add(new FallArenaDropEntity(type));
		}
	}

	public FallArenaRegion getRegion(){
		return region;
	}

	public ArrayList<FallArenaDrop> getNextDrops(){
		ArrayList<FallArenaDrop> nextDrops = new ArrayList<>();
		for(FallArenaDrop drop : drops){
			if(drop.getLastTick()+drop.getTicks() <= FallManager.getWorld().getFullTime()){
				drop.setLastTick(FallManager.getWorld().getFullTime());
				nextDrops.add(drop);
			}
		}
		return nextDrops;
	}
}