package com.realcraft.creative;

import java.util.HashSet;

import com.intellectualcrafters.plot.object.RegionWrapper;
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

public class PlotSquaredWEExtent extends AbstractDelegateExtent {

	private final HashSet<RegionWrapper> mask;

	private static final int BLOCKS_LIMIT = 500000;
	private static final int TILES_LIMIT = 4096;
	private static final int ENTITIES_LIMIT = 512;

	private int blocksCount = 0;
	private int tilesCount = 0;
	private int entitiesCount = 0;

	public PlotSquaredWEExtent(HashSet<RegionWrapper> mask,Extent extent){
		super(extent);
		this.mask = mask;
	}

	@Override
	public boolean setBlock(Vector location,BaseBlock block) throws WorldEditException {
		int id = block.getType();
		switch(id){
			case 54:
			case 130:
			case 142:
			case 27:
			case 137:
			case 52:
			case 154:
			case 84:
			case 25:
			case 144:
			case 138:
			case 176:
			case 177:
			case 63:
			case 68:
			case 323:
			case 117:
			case 116:
			case 28:
			case 66:
			case 157:
			case 61:
			case 62:
			case 140:
			case 146:
			case 149:
			case 150:
			case 158:
			case 23:
			case 123:
			case 124:
			case 29:
			case 33:
			case 151:
			case 178: {
				if(this.tilesCount < TILES_LIMIT && this.blocksCount < BLOCKS_LIMIT && this.maskContains(this.mask,location.getBlockX(),location.getBlockY(),location.getBlockZ())){
					this.tilesCount ++;
					this.blocksCount ++;
					return super.setBlock(location,block);
				}
				break;
			}
			default: {
				if(this.blocksCount < BLOCKS_LIMIT && this.maskContains(this.mask,location.getBlockX(),location.getBlockY(),location.getBlockZ())){
					this.blocksCount ++;
					return super.setBlock(location,block);
				}
				break;
			}
		}
		return false;
	}

	@Override
	public Entity createEntity(Location location,BaseEntity entity){
		if(this.entitiesCount < ENTITIES_LIMIT && this.maskContains(this.mask,location.getBlockX(),location.getBlockY(),location.getBlockZ())){
			this.entitiesCount ++;
			return super.createEntity(location,entity);
		}
		return null;
	}

	@Override
	public boolean setBiome(Vector2D position,BaseBiome biome){
		return this.maskContains(this.mask,position.getBlockX(),position.getBlockZ());
	}

	@Override
	public BaseBlock getBlock(Vector location){
		if(this.maskContains(this.mask,location.getBlockX(),location.getBlockY(),location.getBlockZ())) return super.getBlock(location);
		return new BaseBlock(0,0);
	}

	public boolean maskContains(HashSet<RegionWrapper> mask,int x,int y,int z){
		for(RegionWrapper region : mask){
			if(region.isIn(x,y,z)){
				return true;
			}
		}
		return false;
	}

	public boolean maskContains(HashSet<RegionWrapper> mask,int x,int z){
		for(RegionWrapper region : mask){
			if(region.isIn(x,z)){
				return true;
			}
		}
		return false;
	}
}