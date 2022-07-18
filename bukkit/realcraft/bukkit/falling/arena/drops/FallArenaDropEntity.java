package realcraft.bukkit.falling.arena.drops;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import realcraft.bukkit.utils.LocationUtil;

import java.util.Random;

public class FallArenaDropEntity extends FallArenaDrop {

	private static final Random random = new Random();

	private FallArenaDropEntityType type;

	public FallArenaDropEntity(FallArenaDropEntityType type){
		super(type.getTicks());
		this.type = type;
	}

	public FallArenaDropEntityType getType(){
		return type;
	}

	public EntityType getRandomType(){
		return type.getTypes()[random.nextInt(type.getTypes().length)];
	}

	@Override
	public void drop(Location location){
		location.getWorld().spawnEntity(LocationUtil.getSafeDestination(location),this.getRandomType());
	}
}