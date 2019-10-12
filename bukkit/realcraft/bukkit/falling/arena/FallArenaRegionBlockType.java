package realcraft.bukkit.falling.arena;

import org.bukkit.Material;

public enum FallArenaRegionBlockType {

	STONE(1,Material.STONE),
	LOGS(20*20,Material.OAK_LOG,Material.SPRUCE_LOG,Material.BIRCH_LOG,Material.JUNGLE_LOG,Material.ACACIA_LOG,Material.DARK_OAK_LOG),
	;

	private Material[] types;
	private int ticks;

	private FallArenaRegionBlockType(int ticks,Material... types){
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