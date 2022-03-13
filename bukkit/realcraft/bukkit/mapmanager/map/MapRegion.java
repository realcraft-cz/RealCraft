package realcraft.bukkit.mapmanager.map;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.*;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.mapmanager.events.MapRegionLoadEvent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public class MapRegion implements Runnable {

	private static final int X_MARGIN = 256;
	private static final int Z_OFFSET = 1000;
	private static final int REGION_SAVE_TIMEOUT = 10*1000;

	private Map map;

	private Location baseLoc;
	private Location centerLoc;
	private Location minLoc;
	private Location maxLoc;

	private boolean loaded = false;
	private boolean loading = false;

	private boolean toSave = false;
	private long lastBlockChange = 0;

	public MapRegion(Map map){
		this.map = map;
		Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(),this,20,20);
	}

	public Map getMap(){
		return map;
	}

	@Override
	public void run(){
		if(this.isToSave() && lastBlockChange+REGION_SAVE_TIMEOUT < System.currentTimeMillis()){
			map.saveRegion();
			this.setToSave(false);
		}
	}

	public boolean isLoaded(){
		return loaded;
	}

	public void setLoaded(boolean loaded){
		this.loaded = loaded;
	}

	public boolean isLoading(){
		return loading;
	}

	public void setLoading(boolean loading){
		this.loading = loading;
	}

	public boolean isToSave(){
		return toSave;
	}

	public void setToSave(boolean toSave){
		this.toSave = toSave;
		this.lastBlockChange = System.currentTimeMillis();
	}

	private int getXOffset(){
		return (map.getId()*map.getType().getDimension().getX())+(map.getId()*X_MARGIN);
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
		if(maxLoc == null) maxLoc = this.getBaseLocation().clone().add(map.getType().getDimension().getX()-1,map.getType().getDimension().getY()-1,map.getType().getDimension().getZ()-1);
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

	public MapRegionExtent getExtent(Extent extent){
		return new MapRegionExtent(extent);
	}

	public void load(){
		if(this.isLoading()) return;
		this.setLoading(true);
		try {
			BuiltInClipboardFormat format = BuiltInClipboardFormat.SPONGE_SCHEMATIC;
			ByteArrayInputStream bais = new ByteArrayInputStream(map.getRegionData());
			ClipboardReader reader = format.getReader(bais);
			Clipboard clipboard = reader.read();
			new SchemaStages(clipboard,this.getBaseLocation());
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public byte[] toByteArray(){
		int minX = Integer.MAX_VALUE,maxX = -1;
		int minY = Integer.MAX_VALUE,maxY = -1;
		int minZ = Integer.MAX_VALUE,maxZ = -1;
		for(int x=this.getMinLocation().getBlockX();x<=this.getMaxLocation().getBlockX();x++){
			for(int y=this.getMinLocation().getBlockY();y<=this.getMaxLocation().getBlockY();y++){
				for(int z=this.getMinLocation().getBlockZ();z<=this.getMaxLocation().getBlockZ();z++){
					if(MapManager.getWorld().getBlockAt(x,y,z).getType() != Material.AIR){
						if(minX > x) minX = x;
						if(maxX < x) maxX = x;
						if(minY > y) minY = y;
						if(maxY < y) maxY = y;
						if(minZ > z) minZ = z;
						if(maxZ < z) maxZ = z;
					}
				}
			}
		}
		CuboidRegion region = new CuboidRegion(BlockVector3.at(minX,minY,minZ),BlockVector3.at(maxX,maxY,maxZ));
		BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
		EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(this.getBaseLocation().getWorld()),-1);
		ForwardExtentCopy copy = new ForwardExtentCopy(session,region,clipboard,region.getMinimumPoint());
		Operations.completeBlindly(copy);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			BuiltInClipboardFormat format = BuiltInClipboardFormat.SPONGE_SCHEMATIC;
			ClipboardWriter writer = format.getWriter(baos);
			writer.write(clipboard);
			writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		return baos.toByteArray();
	}

	private class MapRegionExtent extends AbstractDelegateExtent {

		public MapRegionExtent(Extent extent){
			super(extent);
		}

		@Override
		public boolean setBlock(BlockVector3 location,BlockStateHolder block) throws WorldEditException{
			if(MapRegion.this.isLocationInside(location)) return super.setBlock(location,block);
			return false;
		}

		@Override
		public Entity createEntity(com.sk89q.worldedit.util.Location location,BaseEntity entity){
			if(MapRegion.this.isLocationInside(location)) return super.createEntity(location,entity);
			return null;
		}

		@Override
		public boolean setBiome(BlockVector2 position,BiomeType biome){
			return MapRegion.this.isLocationInside(position);
		}

		@Override
		public BlockState getBlock(BlockVector3 location){
			if(MapRegion.this.isLocationInside(location)) return super.getBlock(location);
			return BlockTypes.AIR.getDefaultState();
		}

		@Override
		public BaseBlock getFullBlock(BlockVector3 location){
			return this.getBlock(location).toBaseBlock();
		}
	}

	public class SchemaStages extends Thread {

		private static final int SLEEP_TIME = 20;

		private Clipboard clipboard;
		private Location location;
		private EditSession editSession;

		private boolean build = false;
		private HashMap<BlockVector3,BaseBlock> blocks = new HashMap<>();

		public SchemaStages(Clipboard clipboard,Location location){
			this.clipboard = clipboard;
			this.location = location;
			this.editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(location.getWorld()),-1);
			this.start();
		}

		@Override
		public void run(){
			try {
				startStage(1);
				startStage(2);
				startStage(3);
				Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
					@Override
					public void run(){
						for(Chunk chunk : location.getWorld().getLoadedChunks()) chunk.unload();
						MapRegion.this.setLoaded(true);
						Bukkit.getServer().getPluginManager().callEvent(new MapRegionLoadEvent(MapRegion.this.getMap()));
					}
				});
			} catch (InterruptedException | SecurityException | IllegalArgumentException e){
				e.printStackTrace();
			}
		}

		private void startStage(int stage) throws InterruptedException, SecurityException, IllegalArgumentException {
			int maxBlocksPerRun = 16 * 16 * clipboard.getDimensions().getBlockY();
			for(int x=clipboard.getRegion().getMinimumPoint().getBlockX();x<=clipboard.getRegion().getMaximumPoint().getBlockX();x++){
				for(int y=clipboard.getRegion().getMinimumPoint().getBlockY();y<=clipboard.getRegion().getMaximumPoint().getBlockY();y++){
					for(int z=clipboard.getRegion().getMinimumPoint().getBlockZ();z<=clipboard.getRegion().getMaximumPoint().getBlockZ();z++){
						BlockVector3 pt = BlockVector3.at(x,y,z);
						BaseBlock block = clipboard.getFullBlock(pt);
						boolean place = false;
						if(stage == 1 && !shouldPlaceLast(BukkitAdapter.adapt(block.getBlockType())) && !shouldPlaceFinal(BukkitAdapter.adapt(block.getBlockType()))) place = true;
						else if(stage == 2 && shouldPlaceLast(BukkitAdapter.adapt(block.getBlockType()))) place = true;
						else if(stage == 3 && shouldPlaceFinal(BukkitAdapter.adapt(block.getBlockType()))) place = true;
						if(place){
							BlockVector3 pos = pt.add(-clipboard.getRegion().getMinimumPoint().getBlockX(),-clipboard.getRegion().getMinimumPoint().getBlockY(),-clipboard.getRegion().getMinimumPoint().getBlockZ());
							pos = pos.add(location.getBlockX(),location.getBlockY(),location.getBlockZ());
							pos = pos.add(clipboard.getRegion().getMinimumPoint().subtract(BlockVector3.at(location.getBlockX(),location.getBlockY(),location.getBlockZ())));
							blocks.put(pos,block);
							if(blocks.size() >= maxBlocksPerRun){
								nextPaste();
								while(build){
									sleep(SLEEP_TIME);
								}
							}
						}
					}
				}
			}
			nextPaste();
			while(build){
				sleep(SLEEP_TIME);
			}
		}

		private void nextPaste(){
			build = true;
			Bukkit.getScheduler().callSyncMethod(RealCraft.getInstance(),new Callable<Void>(){
				@Override
				public Void call(){
					for(java.util.Map.Entry<BlockVector3,BaseBlock> map : blocks.entrySet()){
						if(!map.getValue().hasNbtData()){
							location.getWorld().getBlockAt(map.getKey().getBlockX(),map.getKey().getBlockY(),map.getKey().getBlockZ()).setType(BukkitAdapter.adapt(map.getValue().getBlockType()),false);
							location.getWorld().getBlockAt(map.getKey().getBlockX(),map.getKey().getBlockY(),map.getKey().getBlockZ()).setBlockData(BukkitAdapter.adapt(map.getValue()),false);
						} else {
							try {
								editSession.setBlock(map.getKey(),map.getValue());
							} catch (MaxChangedBlocksException e){
								e.printStackTrace();
							}
						}
					}
					editSession.commit();
					blocks.clear();
					build = false;
					return null;
				}
			});
		}
	}

	private static final Set<Material> shouldPlaceLast = new HashSet<Material>();
	static {
		shouldPlaceLast.add(Material.ACACIA_SAPLING);
		shouldPlaceLast.add(Material.BIRCH_SAPLING);
		shouldPlaceLast.add(Material.DARK_OAK_SAPLING);
		shouldPlaceLast.add(Material.JUNGLE_SAPLING);
		shouldPlaceLast.add(Material.OAK_SAPLING);
		shouldPlaceLast.add(Material.SPRUCE_SAPLING);
		shouldPlaceLast.add(Material.BLACK_BED);
		shouldPlaceLast.add(Material.BLUE_BED);
		shouldPlaceLast.add(Material.BROWN_BED);
		shouldPlaceLast.add(Material.CYAN_BED);
		shouldPlaceLast.add(Material.GRAY_BED);
		shouldPlaceLast.add(Material.GREEN_BED);
		shouldPlaceLast.add(Material.LIGHT_BLUE_BED);
		shouldPlaceLast.add(Material.LIGHT_GRAY_BED);
		shouldPlaceLast.add(Material.LIME_BED);
		shouldPlaceLast.add(Material.MAGENTA_BED);
		shouldPlaceLast.add(Material.ORANGE_BED);
		shouldPlaceLast.add(Material.PINK_BED);
		shouldPlaceLast.add(Material.PURPLE_BED);
		shouldPlaceLast.add(Material.RED_BED);
		shouldPlaceLast.add(Material.WHITE_BED);
		shouldPlaceLast.add(Material.YELLOW_BED);
		shouldPlaceLast.add(Material.GRASS);
		shouldPlaceLast.add(Material.TALL_GRASS);
		shouldPlaceLast.add(Material.DEAD_BUSH);
		shouldPlaceLast.add(Material.SUNFLOWER);
		shouldPlaceLast.add(Material.BROWN_MUSHROOM);
		shouldPlaceLast.add(Material.RED_MUSHROOM);
		shouldPlaceLast.add(Material.TORCH);
		shouldPlaceLast.add(Material.FIRE);
		shouldPlaceLast.add(Material.REDSTONE_WIRE);
		shouldPlaceLast.add(Material.COMPARATOR);
		shouldPlaceLast.add(Material.WHEAT);
		shouldPlaceLast.add(Material.LADDER);
		shouldPlaceLast.add(Material.RAIL);
		shouldPlaceLast.add(Material.ACTIVATOR_RAIL);
		shouldPlaceLast.add(Material.DETECTOR_RAIL);
		shouldPlaceLast.add(Material.POWERED_RAIL);
		shouldPlaceLast.add(Material.LEVER);
		shouldPlaceLast.add(Material.ACACIA_PRESSURE_PLATE);
		shouldPlaceLast.add(Material.BIRCH_PRESSURE_PLATE);
		shouldPlaceLast.add(Material.DARK_OAK_PRESSURE_PLATE);
		shouldPlaceLast.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
		shouldPlaceLast.add(Material.JUNGLE_PRESSURE_PLATE);
		shouldPlaceLast.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
		shouldPlaceLast.add(Material.OAK_PRESSURE_PLATE);
		shouldPlaceLast.add(Material.SPRUCE_PRESSURE_PLATE);
		shouldPlaceLast.add(Material.STONE_PRESSURE_PLATE);
		shouldPlaceLast.add(Material.REDSTONE_TORCH);
		shouldPlaceLast.add(Material.REDSTONE_WALL_TORCH);
		shouldPlaceLast.add(Material.SNOW);
		shouldPlaceLast.add(Material.END_PORTAL);
		shouldPlaceLast.add(Material.NETHER_PORTAL);
		shouldPlaceLast.add(Material.REPEATER);
		shouldPlaceLast.add(Material.ACACIA_TRAPDOOR);
		shouldPlaceLast.add(Material.BIRCH_TRAPDOOR);
		shouldPlaceLast.add(Material.DARK_OAK_TRAPDOOR);
		shouldPlaceLast.add(Material.IRON_TRAPDOOR);
		shouldPlaceLast.add(Material.JUNGLE_TRAPDOOR);
		shouldPlaceLast.add(Material.OAK_TRAPDOOR);
		shouldPlaceLast.add(Material.SPRUCE_TRAPDOOR);
		shouldPlaceLast.add(Material.VINE);
		shouldPlaceLast.add(Material.LILY_PAD);
		shouldPlaceLast.add(Material.NETHER_WART);
		shouldPlaceLast.add(Material.PISTON);
		shouldPlaceLast.add(Material.PISTON_HEAD);
		shouldPlaceLast.add(Material.MOVING_PISTON);
		shouldPlaceLast.add(Material.STICKY_PISTON);
		shouldPlaceLast.add(Material.COCOA);
		shouldPlaceLast.add(Material.TRIPWIRE_HOOK);
		shouldPlaceLast.add(Material.TRIPWIRE);
		shouldPlaceLast.add(Material.FLOWER_POT);
		shouldPlaceLast.add(Material.CARROT);
		shouldPlaceLast.add(Material.POTATO);
		shouldPlaceLast.add(Material.ACACIA_BUTTON);
		shouldPlaceLast.add(Material.BIRCH_BUTTON);
		shouldPlaceLast.add(Material.DARK_OAK_BUTTON);
		shouldPlaceLast.add(Material.JUNGLE_BUTTON);
		shouldPlaceLast.add(Material.OAK_BUTTON);
		shouldPlaceLast.add(Material.SPRUCE_BUTTON);
		shouldPlaceLast.add(Material.STONE_BUTTON);
		shouldPlaceLast.add(Material.ANVIL);
		shouldPlaceLast.add(Material.BLACK_CARPET);
		shouldPlaceLast.add(Material.BLUE_CARPET);
		shouldPlaceLast.add(Material.BROWN_CARPET);
		shouldPlaceLast.add(Material.CYAN_CARPET);
		shouldPlaceLast.add(Material.GRAY_CARPET);
		shouldPlaceLast.add(Material.GREEN_CARPET);
		shouldPlaceLast.add(Material.LIGHT_BLUE_CARPET);
		shouldPlaceLast.add(Material.LIGHT_GRAY_CARPET);
		shouldPlaceLast.add(Material.LIME_CARPET);
		shouldPlaceLast.add(Material.MAGENTA_CARPET);
		shouldPlaceLast.add(Material.ORANGE_CARPET);
		shouldPlaceLast.add(Material.PINK_CARPET);
		shouldPlaceLast.add(Material.PURPLE_CARPET);
		shouldPlaceLast.add(Material.RED_CARPET);
		shouldPlaceLast.add(Material.WHITE_CARPET);
		shouldPlaceLast.add(Material.YELLOW_CARPET);
		shouldPlaceLast.add(Material.CHORUS_PLANT);
		shouldPlaceLast.add(Material.KELP_PLANT);
	}

	private static final HashSet<Material> shouldPlaceFinal = new HashSet<Material>();
	static {
		shouldPlaceFinal.add(Material.ACACIA_SIGN);
		shouldPlaceFinal.add(Material.BIRCH_SIGN);
		shouldPlaceFinal.add(Material.DARK_OAK_SIGN);
		shouldPlaceFinal.add(Material.JUNGLE_SIGN);
		shouldPlaceFinal.add(Material.OAK_SIGN);
		shouldPlaceFinal.add(Material.SPRUCE_SIGN);
		shouldPlaceFinal.add(Material.ACACIA_WALL_SIGN);
		shouldPlaceFinal.add(Material.BIRCH_WALL_SIGN);
		shouldPlaceFinal.add(Material.DARK_OAK_WALL_SIGN);
		shouldPlaceFinal.add(Material.JUNGLE_WALL_SIGN);
		shouldPlaceFinal.add(Material.OAK_WALL_SIGN);
		shouldPlaceFinal.add(Material.SPRUCE_WALL_SIGN);
		shouldPlaceFinal.add(Material.PISTON_HEAD);
		shouldPlaceFinal.add(Material.MOVING_PISTON);
		shouldPlaceFinal.add(Material.ACACIA_DOOR);
		shouldPlaceFinal.add(Material.BIRCH_DOOR);
		shouldPlaceFinal.add(Material.DARK_OAK_DOOR);
		shouldPlaceFinal.add(Material.IRON_DOOR);
		shouldPlaceFinal.add(Material.JUNGLE_DOOR);
		shouldPlaceFinal.add(Material.OAK_DOOR);
		shouldPlaceFinal.add(Material.SPRUCE_DOOR);
		shouldPlaceFinal.add(Material.CACTUS);
		shouldPlaceFinal.add(Material.SUGAR_CANE);
		shouldPlaceFinal.add(Material.CAKE);
		shouldPlaceFinal.add(Material.WHITE_BANNER);
		shouldPlaceFinal.add(Material.ORANGE_BANNER);
		shouldPlaceFinal.add(Material.MAGENTA_BANNER);
		shouldPlaceFinal.add(Material.LIGHT_BLUE_BANNER);
		shouldPlaceFinal.add(Material.YELLOW_BANNER);
		shouldPlaceFinal.add(Material.LIME_BANNER);
		shouldPlaceFinal.add(Material.PINK_BANNER);
		shouldPlaceFinal.add(Material.GRAY_BANNER);
		shouldPlaceFinal.add(Material.LIGHT_GRAY_BANNER);
		shouldPlaceFinal.add(Material.CYAN_BANNER);
		shouldPlaceFinal.add(Material.PURPLE_BANNER);
		shouldPlaceFinal.add(Material.BLUE_BANNER);
		shouldPlaceFinal.add(Material.BROWN_BANNER);
		shouldPlaceFinal.add(Material.GREEN_BANNER);
		shouldPlaceFinal.add(Material.RED_BANNER);
		shouldPlaceFinal.add(Material.BLACK_BANNER);
		shouldPlaceFinal.add(Material.WHITE_WALL_BANNER);
		shouldPlaceFinal.add(Material.ORANGE_WALL_BANNER);
		shouldPlaceFinal.add(Material.MAGENTA_WALL_BANNER);
		shouldPlaceFinal.add(Material.LIGHT_BLUE_WALL_BANNER);
		shouldPlaceFinal.add(Material.YELLOW_WALL_BANNER);
		shouldPlaceFinal.add(Material.LIME_WALL_BANNER);
		shouldPlaceFinal.add(Material.PINK_WALL_BANNER);
		shouldPlaceFinal.add(Material.GRAY_WALL_BANNER);
		shouldPlaceFinal.add(Material.LIGHT_GRAY_WALL_BANNER);
		shouldPlaceFinal.add(Material.CYAN_WALL_BANNER);
		shouldPlaceFinal.add(Material.PURPLE_WALL_BANNER);
		shouldPlaceFinal.add(Material.BLUE_WALL_BANNER);
		shouldPlaceFinal.add(Material.BROWN_WALL_BANNER);
		shouldPlaceFinal.add(Material.GREEN_WALL_BANNER);
		shouldPlaceFinal.add(Material.RED_WALL_BANNER);
		shouldPlaceFinal.add(Material.BLACK_WALL_BANNER);
	}

	public static boolean shouldPlaceLast(Material type){
		return shouldPlaceLast.contains(type);
	}

	public static boolean shouldPlaceFinal(Material type){
		return shouldPlaceFinal.contains(type);
	}
}