package realcraft.bukkit.falling.arena.drops;

import org.bukkit.entity.EntityType;

public enum FallArenaDropEntityType {

	ANIMAL(18000,			EntityType.PIG,EntityType.COW,EntityType.SHEEP,EntityType.CHICKEN,EntityType.RABBIT),
	PET(40000,				EntityType.CAT,EntityType.DONKEY,EntityType.FOX,EntityType.HORSE,EntityType.LLAMA,EntityType.OCELOT,EntityType.PANDA,EntityType.PARROT,EntityType.POLAR_BEAR,EntityType.WOLF),
	TRADER(24000,			EntityType.WANDERING_TRADER),
	VILLAGER(36000,			EntityType.VILLAGER),
	RAID_PILLAGER1(96000,	EntityType.PILLAGER),
	RAID_PILLAGER2(96000,	EntityType.PILLAGER),
	RAID_PILLAGER3(96000,	EntityType.PILLAGER),
	RAID_ILLUSIONER(96000,	EntityType.ILLUSIONER),
	RAID_RAVAGER(96000,		EntityType.RAVAGER),
	;

	private EntityType[] types;
	private int ticks;

	private FallArenaDropEntityType(int ticks,EntityType... types){
		this.types = types;
		this.ticks = ticks;
	}

	public EntityType[] getTypes(){
		return types;
	}

	public int getTicks(){
		return ticks;
	}
}