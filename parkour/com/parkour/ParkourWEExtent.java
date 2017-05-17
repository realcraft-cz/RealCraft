package com.parkour;

import org.bukkit.block.Block;

import com.parkour.menu.ParkourMenuType;
import com.parkour.utils.RegionWrapper;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.biome.BaseBiome;

public class ParkourWEExtent extends AbstractDelegateExtent {
	private ParkourArena arena;
	private RegionWrapper region;

    public ParkourWEExtent(ParkourArena arena,Extent extent){
        super(extent);
        this.arena = arena;
        this.region = arena.getRegionWrapper();
    }

    @SuppressWarnings("deprecation")
	@Override
    public boolean setBlock(Vector location,BaseBlock baseblock) throws WorldEditException {
        if(this.maskContains(this.region,location.getBlockX(),location.getBlockY(),location.getBlockZ())){
        	if(baseblock.getId() == ParkourMenuType.START.getMaterial().getId() || baseblock.getId() == ParkourMenuType.FINISH.getMaterial().getId() || baseblock.getId() == ParkourMenuType.CHECKPOINT.getMaterial().getId()){
        		return false;
        	}
        	Block block = this.arena.getWorld().getBlockAt(location.getBlockX(),location.getBlockY(),location.getBlockZ());
        	if(block.getType() == ParkourMenuType.START.getMaterial()){
				if(arena.getStartLocation() != null){
					arena.setStartLocation(null);
				}
			}
			else if(block.getType() == ParkourMenuType.FINISH.getMaterial()){
				if(arena.getFinishLocation() != null){
					arena.setFinishLocation(null);
				}
			}
			else if(block.getType() == ParkourMenuType.CHECKPOINT.getMaterial()){
				arena.removeCheckPoint(block.getLocation());
			}
			arena.setTested(false);
        	return super.setBlock(location,baseblock);
        }
        return false;
    }

    @Override
    public Entity createEntity(Location location,BaseEntity entity){
       return null;
    }

    @Override
    public boolean setBiome(Vector2D position,BaseBiome biome){
        return false;
    }

    @Override
    public BaseBlock getBlock(Vector location){
        if(this.maskContains(this.region,location.getBlockX(),location.getBlockY(),location.getBlockZ())) return super.getBlock(location);
        return new BaseBlock(0,0);
    }

    public boolean maskContains(RegionWrapper region,int x,int y,int z){
        if(region.isIn(x,y,z)) return true;
    	return false;
    }
}