package realcraft.bukkit.mapmanager.map;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.world.biome.BaseBiome;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Location;
import org.bukkit.World;
import realcraft.bukkit.mapmanager.MapManager;

public class MapRegion {

	private static final int X_MARGIN = 256;
	private static final int Z_OFFSET = 1000;

	private Map map;

	private Location baseLoc;
	private Location centerLoc;
	private Location minLoc;
	private Location maxLoc;

	public MapRegion(Map map){
		this.map = map;
	}

	private int getXOffset(){
		return (map.getId()*map.getType().getDimension().getX())+X_MARGIN;
	}

	private int getZOffset(){
		return map.getType().getId()*Z_OFFSET;
	}

	public World getWorld(){
		return this.getBaseLocation().getWorld();
	}

	public Location getBaseLocation(){
		if(baseLoc == null) baseLoc = new Location(MapManager.getWorld(),this.getXOffset(),0,this.getZOffset());
		return baseLoc;
	}

	public Location getCenterLocation(){
		if(centerLoc == null) centerLoc = this.getBaseLocation().clone().add(map.getType().getDimension().getX()/2f,map.getType().getDimension().getY()/2f,map.getType().getDimension().getZ()/2f);
		return centerLoc;
	}

	public Location getMinLocation(){
		if(minLoc == null) minLoc = this.getBaseLocation().clone();
		return minLoc;
	}

	public Location getMaxLocation(){
		if(maxLoc == null) maxLoc = this.getBaseLocation().clone().add(map.getType().getDimension().getX(),map.getType().getDimension().getY(),map.getType().getDimension().getZ());
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

	public boolean isLocationInside(Vector vector){
		return (vector.getBlockX() >= this.getMinLocation().getBlockX() && vector.getBlockX() <= this.getMaxLocation().getBlockX()
				&& vector.getBlockY() >= this.getMinLocation().getBlockY() && vector.getBlockY() <= this.getMaxLocation().getBlockY()
				&& vector.getBlockZ() >= this.getMinLocation().getBlockZ() && vector.getBlockZ() <= this.getMaxLocation().getBlockZ());
	}

	public boolean isLocationInside(Vector2D vector){
		return (vector.getBlockX() >= this.getMinLocation().getBlockX() && vector.getBlockX() <= this.getMaxLocation().getBlockX()
				&& vector.getBlockZ() >= this.getMinLocation().getBlockZ() && vector.getBlockZ() <= this.getMaxLocation().getBlockZ());
	}

	public MapRegionExtent getExtent(Extent extent){
		return new MapRegionExtent(extent);
	}

	private class MapRegionExtent extends AbstractDelegateExtent {

		public MapRegionExtent(Extent extent){
			super(extent);
		}

		@Override
		public boolean setBlock(Vector location,BlockStateHolder block) throws WorldEditException{
			if(MapRegion.this.isLocationInside(location)) return super.setBlock(location,block);
			return false;
		}

		@Override
		public Entity createEntity(com.sk89q.worldedit.util.Location location,BaseEntity entity){
			if(MapRegion.this.isLocationInside(location)) return super.createEntity(location,entity);
			return null;
		}

		@Override
		public boolean setBiome(Vector2D position,BaseBiome biome){
			return MapRegion.this.isLocationInside(position);
		}

		@Override
		public BlockState getBlock(Vector location){
			if(MapRegion.this.isLocationInside(location)) return super.getBlock(location);
			return BlockTypes.AIR.getDefaultState();
		}

		@Override
		public BaseBlock getFullBlock(Vector location){
			return this.getBlock(location).toBaseBlock();
		}
	}
}