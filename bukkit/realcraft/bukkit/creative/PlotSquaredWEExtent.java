package realcraft.bukkit.creative;

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
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BaseBiome;

public class PlotSquaredWEExtent extends AbstractDelegateExtent {

	private final HashSet<RegionWrapper> mask;
	private World world;

	private static final int BLOCKS_LIMIT = 500000;
	private static final int TILES_LIMIT = 4096;
	private static final int ENTITIES_LIMIT = 512;
	private static final int CRITICALS_LIMIT = 128;

	private int blocksCount = 0;
	private int tilesCount = 0;
	private int entitiesCount = 0;
	private int criticalsCount = 0;

	public PlotSquaredWEExtent(HashSet<RegionWrapper> mask,World world,Extent extent){
		super(extent);
		this.mask = mask;
		this.world = world;
	}

	@Override
	public boolean setBlock(Vector location,BaseBlock block) throws WorldEditException {
		if(this.isBlockEqual(world.getBlock(location),block)) return false;
		if(this.isBlockForbidden(block)) return false;
		if(this.blocksCount >= BLOCKS_LIMIT) return false;
		if(this.isBlockTileEntity(block)){
			if(this.tilesCount < TILES_LIMIT && this.maskContains(this.mask,location.getBlockX(),location.getBlockY(),location.getBlockZ())){
				this.tilesCount ++;
				this.blocksCount ++;
				return super.setBlock(location,block);
			}
		}
		else if(this.isBlockCritical(block)){
			if(this.criticalsCount < CRITICALS_LIMIT && this.maskContains(this.mask,location.getBlockX(),location.getBlockY(),location.getBlockZ())){
				this.criticalsCount ++;
				this.blocksCount ++;
				return super.setBlock(location,block);
			}
		} else {
			if(this.maskContains(this.mask,location.getBlockX(),location.getBlockY(),location.getBlockZ())){
				this.blocksCount ++;
				return super.setBlock(location,block);
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

	private boolean maskContains(HashSet<RegionWrapper> mask,int x,int y,int z){
		for(RegionWrapper region : mask){
			if(region.isIn(x,y,z)){
				return true;
			}
		}
		return false;
	}

	private boolean maskContains(HashSet<RegionWrapper> mask,int x,int z){
		for(RegionWrapper region : mask){
			if(region.isIn(x,z)){
				return true;
			}
		}
		return false;
	}

	private boolean isBlockEqual(BaseBlock current,BaseBlock block){
		if(current.getType() == block.getType() && current.getData() == block.getData()) return true;
		return false;
	}

	private boolean isBlockForbidden(BaseBlock block){
		switch(block.getType()){
			case 52:
			case 46:
			case 90:
			case 116:
			case 119:
			case 130:
			case 219:
			case 220:
			case 221:
			case 222:
			case 223:
			case 224:
			case 225:
			case 226:
			case 227:
			case 228:
			case 229:
			case 230:
			case 231:
			case 232:
			case 233:
			case 234:
			case 137:
			case 210:
			case 211:
			case 422:
			case 255:
			case 217:
			return true;
		}
		return false;
	}

	private boolean isBlockTileEntity(BaseBlock block){
		switch(block.getType()){
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
			case 178:
			return true;
		}
		return false;
	}

	private boolean isBlockCritical(BaseBlock block){
		switch(block.getType()){
			case 8:
			case 9:
			case 10:
			case 11:
			case 209:
			return true;
		}
		return false;
	}
}