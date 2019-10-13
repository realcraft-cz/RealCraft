package realcraft.bukkit.falling.arena.drops;

import org.bukkit.Material;

public enum FallArenaDropBlockType {

	ANDESITE(60,			Material.ANDESITE),
	BAMBOO(320,				Material.BAMBOO),
	BONE_BLOCK(800,			Material.BONE_BLOCK),
	CLAY(400,				Material.CLAY),
	COAL_ORE(300,			Material.COAL_ORE),
	DEAD_BUSH(320,			Material.DEAD_BUSH),
	DIAMOND_ORE(12000,		Material.DIAMOND_ORE),
	DIORITE(60,				Material.DIORITE),
	DIRT(10,				Material.DIRT),
	EMERALD_ORE(16000,		Material.EMERALD_ORE),
	FERN(160,				Material.FERN),
	GOLD_ORE(800,			Material.GOLD_ORE),
	GRANITE(60,				Material.GRANITE),
	GRASS(80,				Material.GRASS),
	GRASS_BLOCK(40,			Material.GRASS_BLOCK),
	GRAVEL(40,				Material.GRAVEL),
	ICE(1200,				Material.ICE),
	IRON_ORE(500,			Material.IRON_ORE),
	LAPIS_ORE(240,			Material.LAPIS_ORE),
	LEAVE(320,				Material.OAK_LEAVES,Material.SPRUCE_LEAVES,Material.BIRCH_LEAVES,Material.JUNGLE_LEAVES,Material.ACACIA_LEAVES,Material.DARK_OAK_LEAVES),
	LOG(400,				Material.OAK_LOG,Material.SPRUCE_LOG,Material.BIRCH_LOG,Material.JUNGLE_LOG,Material.ACACIA_LOG,Material.DARK_OAK_LOG),
	MELON(4800,				Material.MELON),
	MUSHROOM_BLOCK(1800,	Material.BROWN_MUSHROOM_BLOCK,Material.RED_MUSHROOM_BLOCK),
	MYCELIUM(6000,			Material.MYCELIUM),
	OBSIDIAN(2500,			Material.OBSIDIAN),
	PACKED_ICE(2400,		Material.PACKED_ICE),
	PUMPKIN(4000,			Material.PUMPKIN),
	RED_SAND(400,			Material.RED_SAND),
	RED_SANDSTONE(800,		Material.RED_SANDSTONE),
	REDSTONE_ORE(240,		Material.REDSTONE_ORE),
	SAND(80,				Material.SAND),
	SANDSTONE(400,			Material.SANDSTONE),
	SNOW(600,				Material.SNOW),
	STONE(7,				Material.STONE),
	SWEET_BERRY_BUSH(5600,	Material.SWEET_BERRY_BUSH),
	;

	private Material[] types;
	private int ticks;

	private FallArenaDropBlockType(int ticks,Material... types){
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