package realcraft.bukkit.falling.arena;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;
import realcraft.bukkit.falling.FallManager;

public class FallArenaRegion {

	public static final int ARENA_SIZE = 64;
	public static final int ARENA_MARGIN = 128;
	public static final int ARENA_FULLSIZE = ARENA_SIZE+(ARENA_MARGIN*2);

	private FallArena arena;
	private Location minLoc;
	private Location maxLoc;
	private Location centerLoc;

	public FallArenaRegion(FallArena arena){
		this.arena = arena;
		int [] coords = this.getIndexCoords(arena.getId());
		this.minLoc = new Location(FallManager.getWorld(),(coords[0]*ARENA_FULLSIZE)+ARENA_MARGIN,0,(coords[1]*ARENA_FULLSIZE)+ARENA_MARGIN);
		this.maxLoc = new Location(FallManager.getWorld(),(coords[0]*ARENA_FULLSIZE)+ARENA_FULLSIZE-ARENA_MARGIN,128,(coords[1]*ARENA_FULLSIZE)+ARENA_FULLSIZE-ARENA_MARGIN);
		this.centerLoc = new Location(FallManager.getWorld(),this.getMinLocation().getBlockX()+(ARENA_SIZE/2)+0.5,this.getMinLocation().getBlockY(),this.getMinLocation().getBlockZ()+(ARENA_SIZE/2)+0.5);
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

	private int[] getIndexCoords(int index){
		int dir = 1;
		int step = 1;
		int round = 1;
		int x = 0,y = 1;
		for(int i=1;i<=index;i++){
			int sideSize = ((round*8)/4)+1;
			if(step == 1){
				x -= 1;
				y -= 1*2;
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
}