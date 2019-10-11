package realcraft.bukkit.falling.arena;

import org.bukkit.Material;

public class FallArenaRegionBlocks {

	public static final FallArenaRegionBlock[] BLOCKS = new FallArenaRegionBlock[]{
			new FallArenaRegionBlock(20,Material.STONE,Material.ANDESITE,Material.DIORITE),
			new FallArenaRegionBlock(20*20,Material.OAK_LOG,Material.SPRUCE_LOG,Material.BIRCH_LOG,Material.JUNGLE_LOG,Material.ACACIA_LOG,Material.DARK_OAK_LOG),
	};

	public static class FallArenaRegionBlock {

		private Material[] types;
		private int ticks;

		public FallArenaRegionBlock(int ticks,Material... types){
			this.types = types;
			this.ticks = ticks;
		}

		public Material[] getTypes(){
			return types;
		}

		public int getTicks(){
			return ticks;
		}
	}
}
