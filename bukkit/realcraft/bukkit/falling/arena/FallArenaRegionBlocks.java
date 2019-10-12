package realcraft.bukkit.falling.arena;

import org.bukkit.Material;
import realcraft.bukkit.falling.FallManager;

import java.util.ArrayList;
import java.util.Random;

public class FallArenaRegionBlocks {

	private FallArenaRegion region;
	private ArrayList<FallArenaRegionBlock> blocks = new ArrayList<>();

	public FallArenaRegionBlocks(FallArenaRegion region){
		this.region = region;
		for(FallArenaRegionBlockType type : FallArenaRegionBlockType.values()){
			blocks.add(new FallArenaRegionBlock(type));
		}
	}

	public FallArenaRegion getRegion(){
		return region;
	}

	public ArrayList<FallArenaRegionBlock> getNextBlocks(){
		ArrayList<FallArenaRegionBlock> nextBlocks = new ArrayList<>();
		for(FallArenaRegionBlock block : blocks){
			if(block.getLastTick() <= FallManager.getWorld().getFullTime()){
				block.setLastTick(FallManager.getWorld().getFullTime());
				nextBlocks.add(block);
			}
		}
		return nextBlocks;
	}

	public class FallArenaRegionBlock {

		private FallArenaRegionBlockType type;
		private long lastTick;

		public FallArenaRegionBlock(FallArenaRegionBlockType type){
			this.type = type;
			this.lastTick = FallManager.getWorld().getFullTime();
		}

		public FallArenaRegionBlockType getType(){
			return type;
		}

		public Material getRandomType(){
			return type.getTypes()[new Random().nextInt(type.getTypes().length)];
		}

		public long getLastTick(){
			return lastTick;
		}

		public void setLastTick(long lastTick){
			this.lastTick = lastTick;
		}
	}
}