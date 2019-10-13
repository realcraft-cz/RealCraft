package realcraft.bukkit.falling.arena.drops;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;

import java.util.Random;

public class FallArenaDropBlock extends FallArenaDrop {

	private FallArenaDropBlockType type;

	public FallArenaDropBlock(FallArenaDropBlockType type){
		super(type.getTicks());
		this.type = type;
	}

	public FallArenaDropBlockType getType(){
		return type;
	}

	public Material getRandomType(){
		return type.getTypes()[new Random().nextInt(type.getTypes().length)];
	}

	@Override
	public void drop(Location location){
		FallingBlock fallblock = location.getWorld().spawnFallingBlock(location,this.getRandomType(),(byte)0);
		fallblock.setDropItem(false);
	}
}
