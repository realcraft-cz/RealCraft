package realcraft.bukkit.falling.arena.drops;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.falling.FallManager;
import realcraft.bukkit.falling.arena.FallArena;
import realcraft.bukkit.falling.arena.FallArenaRegion;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.share.utils.RandomUtil;

import java.util.ArrayList;
import java.util.Collection;

public class FallArenaDrops {

	public static final int MAX_SAME_ENTITIES = 4;
	public static final EntityType[] MONSTERS = new EntityType[]{
			EntityType.ZOMBIE,
			EntityType.SKELETON,
			EntityType.CREEPER,
			EntityType.SPIDER,
	};

	private FallArena arena;
	private ArrayList<FallArenaDrop> drops = new ArrayList<>();

	public FallArenaDrops(FallArena arena){
		this.arena = arena;
		for(FallArenaDropBlockType type : FallArenaDropBlockType.values()){
			drops.add(new FallArenaDropBlock(type));
		}
		for(FallArenaDropEntityType type : FallArenaDropEntityType.values()){
			drops.add(new FallArenaDropEntity(type));
		}
	}

	public FallArena getArena(){
		return arena;
	}

	public void resetTicks(){
		for(FallArenaDrop drop : drops){
			drop.setLastTick(this.getArena().getTicks());
		}
	}

	public void drop(){
		for(FallArenaDrop drop : this.getNextDrops()){
			Location location = this.getRandomDropLocation();
			Block block = this.getHighestBlock(location);
			if(block != null){
				location.setY(block.getY()+64);
				Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable() {
					@Override
					public void run(){
						drop.drop(location);
					}
				},RandomUtil.getRandomInteger(1,20));
			}
		}
		this.dropMonsters();
	}

	private ArrayList<FallArenaDrop> getNextDrops(){
		ArrayList<FallArenaDrop> nextDrops = new ArrayList<>();
		for(FallArenaDrop drop : drops){
			if(drop.getLastTick()+drop.getTicks() <= this.getArena().getTicks()){
				drop.setLastTick(this.getArena().getTicks());
				nextDrops.add(drop);
			}
		}
		return nextDrops;
	}

	int ticks5 = 0;
	private void dropMonsters(){
		ticks5 ++;
		if(ticks5%40 == 0){
			if(FallManager.getWorld().getTime() >= 13000 && FallManager.getWorld().getTime() <= 23500){
				for(EntityType type : MONSTERS){
					int count = 0;
					Collection<Entity> entities = FallManager.getWorld().getNearbyEntities(this.getArena().getRegion().getCenterLocation(),FallArenaRegion.ARENA_SIZE,FallArenaRegion.ARENA_SIZE,FallArenaRegion.ARENA_SIZE);
					for(Entity entity : entities){
						if(entity.getType() == type){
							count ++;
						}
					}
					if(count < MAX_SAME_ENTITIES){
						for(int i=count;i<MAX_SAME_ENTITIES;i++){
							FallManager.getWorld().spawnEntity(LocationUtil.getSafeDestination(this.getRandomDropLocation()),type);
						}
					}
				}
			}
		}
	}

	private Location getRandomDropLocation(){
		Location location = this.getArena().getRegion().getMinLocation().clone();
		location.setX(RandomUtil.getRandomInteger(this.getArena().getRegion().getMinLocation().getBlockX(),this.getArena().getRegion().getMaxLocation().getBlockX())+0.5);
		location.setZ(RandomUtil.getRandomInteger(this.getArena().getRegion().getMinLocation().getBlockZ(),this.getArena().getRegion().getMaxLocation().getBlockZ())+0.5);
		return location;
	}

	private Block getHighestBlock(Location location){
		int y = 255;
		World world = location.getWorld();
		while(y >= 0){
			if(world.getBlockAt(location.getBlockX(),y,location.getBlockZ()).getType() != Material.AIR){
				return world.getBlockAt(location.getBlockX(),y,location.getBlockZ());
			}
			y --;
		}
		return null;
	}
}