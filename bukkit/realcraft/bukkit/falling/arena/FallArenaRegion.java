package realcraft.bukkit.falling.arena;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.falling.FallManager;
import realcraft.bukkit.falling.events.FallArenaRegionGenerateEvent;
import realcraft.bukkit.falling.exceptions.FallArenaRegionGenerateTimeoutException;
import realcraft.bukkit.falling.exceptions.FallArenaRegionGeneratingException;

public class FallArenaRegion {

	public static final int ARENA_SIZE = 64;
	public static final int ARENA_MARGIN = 256;
	public static final int ARENA_FULLSIZE = ARENA_SIZE+(ARENA_MARGIN*2);

	public static final Material[] ARENA_FLOOR = new Material[]{
			Material.BEDROCK,
			Material.GRANITE,
			Material.DIORITE,
			Material.ANDESITE,
	};

	public static final int GENERATE_TIMEOUT = 600*1000;

	private FallArena arena;
	private Location minLoc;
	private Location maxLoc;
	private Location minLocFull;
	private Location maxLocFull;
	private Location centerLoc;

	private boolean generating = false;
	private long generated;

	public FallArenaRegion(FallArena arena){
		this.arena = arena;
		int[] coords = this.getIndexCoords(arena.getId());
		this.minLoc = new Location(FallManager.getWorld(),(coords[0]*ARENA_FULLSIZE)+ARENA_MARGIN,0,(coords[1]*ARENA_FULLSIZE)+ARENA_MARGIN);
		this.maxLoc = new Location(FallManager.getWorld(),(coords[0]*ARENA_FULLSIZE)+ARENA_FULLSIZE-ARENA_MARGIN-1,256,(coords[1]*ARENA_FULLSIZE)+ARENA_FULLSIZE-ARENA_MARGIN-1);
		this.minLocFull = new Location(FallManager.getWorld(),(coords[0]*ARENA_FULLSIZE),0,(coords[1]*ARENA_FULLSIZE));
		this.maxLocFull = new Location(FallManager.getWorld(),(coords[0]*ARENA_FULLSIZE)+ARENA_FULLSIZE,256,(coords[1]*ARENA_FULLSIZE)+ARENA_FULLSIZE);
		this.centerLoc = new Location(FallManager.getWorld(),this.getMinLocation().getBlockX()+(ARENA_SIZE/2)+0.5,5,this.getMinLocation().getBlockZ()+(ARENA_SIZE/2)+0.5);
	}

	public FallArena getArena(){
		return arena;
	}

	public Location getCenterLocation(){
		return centerLoc;
	}

	public Location getMinLocation(){
		return minLoc;
	}

	public Location getMaxLocation(){
		return maxLoc;
	}

	public Location getMinLocationFull(){
		return minLocFull;
	}

	public Location getMaxLocationFull(){
		return maxLocFull;
	}

	public boolean isLocationInside(Location location){
		return (location.getBlockX() >= this.getMinLocation().getBlockX() && location.getBlockX() <= this.getMaxLocation().getBlockX()
				&& location.getBlockY() >= this.getMinLocation().getBlockY() && location.getBlockY() <= this.getMaxLocation().getBlockY()
				&& location.getBlockZ() >= this.getMinLocation().getBlockZ() && location.getBlockZ() <= this.getMaxLocation().getBlockZ());
	}

	public boolean isLocationInside(com.sk89q.worldedit.util.Location location){
		return (location.getBlockX() >= this.getMinLocation().getBlockX() && location.getBlockX() <= this.getMaxLocation().getBlockX()
				&& location.getBlockY() >= this.getMinLocation().getBlockY() && location.getBlockY() <= this.getMaxLocation().getBlockY()
				&& location.getBlockZ() >= this.getMinLocation().getBlockZ() && location.getBlockZ() <= this.getMaxLocation().getBlockZ());
	}

	public boolean isLocationInside(BlockVector3 vector){
		return (vector.getBlockX() >= this.getMinLocation().getBlockX() && vector.getBlockX() <= this.getMaxLocation().getBlockX()
				&& vector.getBlockY() >= this.getMinLocation().getBlockY() && vector.getBlockY() <= this.getMaxLocation().getBlockY()
				&& vector.getBlockZ() >= this.getMinLocation().getBlockZ() && vector.getBlockZ() <= this.getMaxLocation().getBlockZ());
	}

	public boolean isLocationInside(BlockVector2 vector){
		return (vector.getBlockX() >= this.getMinLocation().getBlockX() && vector.getBlockX() <= this.getMaxLocation().getBlockX()
				&& vector.getBlockZ() >= this.getMinLocation().getBlockZ() && vector.getBlockZ() <= this.getMaxLocation().getBlockZ());
	}

	public boolean isLocationInsideFull(Location location){
		return (location.getBlockX() >= this.getMinLocationFull().getBlockX() && location.getBlockX() <= this.getMaxLocationFull().getBlockX()
				&& location.getBlockY() >= this.getMinLocationFull().getBlockY() && location.getBlockY() <= this.getMaxLocationFull().getBlockY()
				&& location.getBlockZ() >= this.getMinLocationFull().getBlockZ() && location.getBlockZ() <= this.getMaxLocationFull().getBlockZ());
	}

	public FallArenaRegionExtent getExtent(Extent extent){
		return new FallArenaRegionExtent(extent);
	}

	public boolean isGenerating(){
		return generating;
	}

	public void setGenerating(boolean generating){
		this.generating = generating;
	}

	public long getGenerated(){
		return generated;
	}

	public void setGenerated(long generated){
		this.generated = generated;
	}

	public void regenerate() throws FallArenaRegionGeneratingException, FallArenaRegionGenerateTimeoutException {
		if(this.isGenerating()){
			throw new FallArenaRegionGeneratingException(this.getArena());
		}
		if(this.getGenerated()+GENERATE_TIMEOUT > System.currentTimeMillis()){
			throw new FallArenaRegionGenerateTimeoutException(this.getArena(),(int)((this.getGenerated()+GENERATE_TIMEOUT-System.currentTimeMillis())/1000));
		}
		arena.getDrops().resetTicks();
		arena.getRegion().setGenerating(true);
		arena.getRegion().generate();
	}

	public void generate(){
		this.setGenerated(System.currentTimeMillis());
		int delay = 0;
		for(int x=0;x<ARENA_SIZE/16;x++){
			for(int z=0;z<ARENA_SIZE/16;z++){
				delay += 5;
				final int chunkX = x;
				final int chunkZ = z;
				Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable() {
					@Override
					public void run(){
						FallArenaRegion.this.generateChunk(chunkX,chunkZ);
					}
				},delay);
			}
		}
		Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable() {
			@Override
			public void run(){
				Bukkit.getPluginManager().callEvent(new FallArenaRegionGenerateEvent(FallArenaRegion.this.getArena()));
			}
		},delay+5);
	}

	private void generateChunk(int chunkX,int chunkZ){
		Location location = this.getMinLocation().clone().add(chunkX*16,0,chunkZ*16);
		for(int x=0;x<16;x++){
			for(int y=0;y<256;y++){
				for(int z=0;z<16;z++){
					if(y < ARENA_FLOOR.length){
						location.clone().add(x,y,z).getBlock().setType(ARENA_FLOOR[y]);
					} else {
						location.clone().add(x,y,z).getBlock().setType(Material.AIR);
					}
				}
			}
		}
	}

	private int[] getIndexCoords(int index){
		int dir = 1;
		int step = 1;
		int round = 1;
		int x = 0,y = 1;
		for(int i=1;i<=index;i++){
			int sideSize = ((round*8)/4)+1;
			if(step == 1){
				x -= 1;
				y -= 2;
			} else {
				if(dir == 1) x += 1;
				else if(dir == 2) y += 1;
				else if(dir == 3) x -= 1;
				else if(dir == 4) y -= 1;
			}
			if(step == sideSize) dir = 2;
			else if(step == (sideSize+sideSize)-1) dir = 3;
			else if(step == (sideSize+sideSize+sideSize)-2) dir = 4;
			else if(step == (sideSize+sideSize+sideSize+sideSize)-4){
				dir = 1;
				round ++;
				step = 0;
			}
			step ++;
		}
		return new int[]{x,y};
	}

	private class FallArenaRegionExtent extends AbstractDelegateExtent {

		public FallArenaRegionExtent(Extent extent){
			super(extent);
		}

		@Override
		public boolean setBlock(BlockVector3 location,BlockStateHolder block) throws WorldEditException{
			if(FallArenaRegion.this.isLocationInside(location)) return super.setBlock(location,block);
			return false;
		}

		@Override
		public Entity createEntity(com.sk89q.worldedit.util.Location location,BaseEntity entity){
			if(FallArenaRegion.this.isLocationInside(location)) return super.createEntity(location,entity);
			return null;
		}

		@Override
		public boolean setBiome(BlockVector2 position,BiomeType biome){
			return FallArenaRegion.this.isLocationInside(position);
		}

		@Override
		public BlockState getBlock(BlockVector3 location){
			if(FallArenaRegion.this.isLocationInside(location)) return super.getBlock(location);
			return BlockTypes.AIR.getDefaultState();
		}

		@Override
		public BaseBlock getFullBlock(BlockVector3 location){
			return this.getBlock(location).toBaseBlock();
		}
	}
}