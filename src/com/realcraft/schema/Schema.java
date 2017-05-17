package com.realcraft.schema;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.realcraft.RealCraft;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.world.registry.WorldData;

public class Schema implements Listener, CommandExecutor {
	RealCraft plugin;

	WorldEditPlugin we;

	public Schema(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		plugin.getCommand("schema").setExecutor(this);
		we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
	}

	public void onReload(){
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("schema")){
			if(player.hasPermission("group.Manazer")){
				if(args.length == 0){
					player.sendMessage("Paste a schematic");
					player.sendMessage("/schema paste <filename>");
					return true;
				}
				else if(args.length == 1){
					String name = args[0];
					File file = new File(we.getDataFolder().getPath()+"/schematics/"+name+".schematic");
					if(!file.exists()){
						player.sendMessage("§cSchematic file doesn't exists.");
						return true;
					}
					new SchemaPaste(file,player.getLocation(),player);
					player.sendMessage("§dSchematic paste started.");
				}
			}
		}
		return true;
	}


	public class SchemaPaste extends Thread {
		private Clipboard schema = null;
		private Location location = null;
		private com.sk89q.worldedit.world.World world = null;
		private Player player = null;

		private boolean build = false;
		private HashMap<Vector,BaseBlock> blocks = new HashMap<>();
		private EditSession editSession = null;

		private static final int SLEEP_TIME = 100;

		public SchemaPaste(File file,Location location,Player player){
			this.location = location;
			this.player = player;
			for(com.sk89q.worldedit.world.World lws : we.getWorldEdit().getServer().getWorlds()){
				if(lws.getName().equals(location.getWorld().getName())){
					this.world = lws;
					break;
				}
			}
			try {
				ClipboardFormat format = ClipboardFormat.SCHEMATIC;
				FileInputStream fis = new FileInputStream(file);
	            BufferedInputStream bis = new BufferedInputStream(fis);
	            ClipboardReader reader = format.getReader(bis);
	            WorldData worldData = world.getWorldData();
	            this.schema = reader.read(worldData);
	            fis.close();
	            bis.close();
			} catch (IOException e){
				e.printStackTrace();
			}
			this.start();
		}

		@Override
		public void run(){
			try {
				editSession = we.getWorldEdit().getEditSessionFactory().getEditSession(world,-1);
				startStage(1);
				startStage(2);
				startStage(3);
				pasteEntities();
				editSession.commit();
			} catch (InterruptedException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e){
				e.printStackTrace();
			}
			player.sendMessage("§dSchematic paste finished.");
		}

		private void startStage(int stage) throws InterruptedException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
			Vector size = schema.getDimensions();
			player.sendMessage("§dSchematic stage "+stage+".");
			int maxBlocksPerRun = 16 * 16 * size.getBlockY();
			for(int x=this.schema.getRegion().getMinimumPoint().getBlockX();x<=this.schema.getRegion().getMaximumPoint().getBlockX();x++){
				for(int y=this.schema.getRegion().getMinimumPoint().getBlockY();y<=this.schema.getRegion().getMaximumPoint().getBlockY();y++){
					for(int z=this.schema.getRegion().getMinimumPoint().getBlockZ();z<=this.schema.getRegion().getMaximumPoint().getBlockZ();z++){
						BlockVector pt = new BlockVector(x,y,z);
						BaseBlock block = schema.getBlock(pt);
						boolean place = false;
						if(stage == 1 && !Schema.shouldPlaceLast(block.getType()) && !Schema.shouldPlaceFinal(block.getType())) place = true;
						else if(stage == 2 && Schema.shouldPlaceLast(block.getType())) place = true;
						else if(stage == 3 && Schema.shouldPlaceFinal(block.getType())) place = true;
						if(place){
							Vector pos = pt.add(-this.schema.getRegion().getMinimumPoint().getBlockX(),-this.schema.getRegion().getMinimumPoint().getBlockY(),-this.schema.getRegion().getMinimumPoint().getBlockZ());
							pos = pos.add(location.getBlockX(),location.getBlockY(),location.getBlockZ());
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
			player.sendMessage("§dSchematic progress.");
			Bukkit.getScheduler().callSyncMethod(RealCraft.getInstance(),new Callable<Void>(){
				@Override
				public Void call() {
					for(Entry<Vector,BaseBlock> map : blocks.entrySet()){
						try {
							editSession.setBlock(map.getKey(),map.getValue());
						} catch (MaxChangedBlocksException e){
							e.printStackTrace();
						}
					}
					blocks.clear();
					build = false;
					return null;
				}
			});
		}

		private void pasteEntities(){
			Bukkit.getScheduler().callSyncMethod(RealCraft.getInstance(),new Callable<Void>(){
				@Override
				public Void call() {
					for(Entity entity : schema.getEntities()){
						Vector pos = entity.getLocation().toVector().add(-schema.getRegion().getMinimumPoint().getBlockX(),-schema.getRegion().getMinimumPoint().getBlockY(),-schema.getRegion().getMinimumPoint().getBlockZ());
						pos = pos.add(location.getBlockX(),location.getBlockY(),location.getBlockZ());
						com.sk89q.worldedit.util.Location location = new com.sk89q.worldedit.util.Location(entity.getLocation().getExtent(),pos);
						editSession.createEntity(location,entity.getState());
					}
					return null;
				}
			});
		}
	}

	private static final Set<Material> shouldPlaceLast = new HashSet<Material>();
    static {
        shouldPlaceLast.add(Material.SAPLING);
        shouldPlaceLast.add(Material.BED);
        shouldPlaceLast.add(Material.POWERED_RAIL);
        shouldPlaceLast.add(Material.DETECTOR_RAIL);
        shouldPlaceLast.add(Material.LONG_GRASS);
        shouldPlaceLast.add(Material.DEAD_BUSH);
        shouldPlaceLast.add(Material.PISTON_EXTENSION);
        shouldPlaceLast.add(Material.YELLOW_FLOWER);
        shouldPlaceLast.add(Material.RED_ROSE);
        shouldPlaceLast.add(Material.BROWN_MUSHROOM);
        shouldPlaceLast.add(Material.RED_MUSHROOM);
        shouldPlaceLast.add(Material.TORCH);
        shouldPlaceLast.add(Material.FIRE);
        shouldPlaceLast.add(Material.REDSTONE_WIRE);
        shouldPlaceLast.add(Material.CROPS);
        shouldPlaceLast.add(Material.LADDER);
        shouldPlaceLast.add(Material.RAILS);
        shouldPlaceLast.add(Material.LEVER);
        shouldPlaceLast.add(Material.STONE_PLATE);
        shouldPlaceLast.add(Material.WOOD_PLATE);
        shouldPlaceLast.add(Material.REDSTONE_TORCH_OFF);
        shouldPlaceLast.add(Material.REDSTONE_TORCH_ON);
        shouldPlaceLast.add(Material.STONE_BUTTON);
        shouldPlaceLast.add(Material.SNOW);
        shouldPlaceLast.add(Material.PORTAL);
        shouldPlaceLast.add(Material.DIODE_BLOCK_OFF);
        shouldPlaceLast.add(Material.DIODE_BLOCK_ON);
        shouldPlaceLast.add(Material.TRAP_DOOR);
        shouldPlaceLast.add(Material.VINE);
        shouldPlaceLast.add(Material.WATER_LILY);
        shouldPlaceLast.add(Material.NETHER_STALK);
        shouldPlaceLast.add(Material.PISTON_BASE);
        shouldPlaceLast.add(Material.PISTON_STICKY_BASE);
        shouldPlaceLast.add(Material.PISTON_EXTENSION);
        shouldPlaceLast.add(Material.PISTON_MOVING_PIECE);
        shouldPlaceLast.add(Material.COCOA);
        shouldPlaceLast.add(Material.TRIPWIRE_HOOK);
        shouldPlaceLast.add(Material.TRIPWIRE);
        shouldPlaceLast.add(Material.FLOWER_POT);
        shouldPlaceLast.add(Material.CARROT);
        shouldPlaceLast.add(Material.POTATO);
        shouldPlaceLast.add(Material.WOOD_BUTTON);
        shouldPlaceLast.add(Material.ANVIL);
        shouldPlaceLast.add(Material.IRON_PLATE);
        shouldPlaceLast.add(Material.GOLD_PLATE);
        shouldPlaceLast.add(Material.REDSTONE_COMPARATOR_OFF);
        shouldPlaceLast.add(Material.REDSTONE_COMPARATOR_ON);
        shouldPlaceLast.add(Material.ACTIVATOR_RAIL);
        shouldPlaceLast.add(Material.IRON_TRAPDOOR);
        shouldPlaceLast.add(Material.CARPET);
        shouldPlaceLast.add(Material.DOUBLE_PLANT);
        shouldPlaceLast.add(Material.DAYLIGHT_DETECTOR_INVERTED);
    }

    private static final HashSet<Material> shouldPlaceFinal = new HashSet<Material>();
    static {
        shouldPlaceFinal.add(Material.SIGN_POST);
        shouldPlaceFinal.add(Material.WOODEN_DOOR);
        shouldPlaceFinal.add(Material.ACACIA_DOOR);
        shouldPlaceFinal.add(Material.BIRCH_DOOR);
        shouldPlaceFinal.add(Material.JUNGLE_DOOR);
        shouldPlaceFinal.add(Material.DARK_OAK_DOOR);
        shouldPlaceFinal.add(Material.SPRUCE_DOOR);
        shouldPlaceFinal.add(Material.WALL_SIGN);
        shouldPlaceFinal.add(Material.IRON_DOOR);
        shouldPlaceFinal.add(Material.CACTUS);
        shouldPlaceFinal.add(Material.SUGAR_CANE);
        shouldPlaceFinal.add(Material.CAKE_BLOCK);
        shouldPlaceFinal.add(Material.PISTON_EXTENSION);
        shouldPlaceFinal.add(Material.PISTON_MOVING_PIECE);
        shouldPlaceFinal.add(Material.STANDING_BANNER);
        shouldPlaceFinal.add(Material.WALL_BANNER);
    }

    //https://github.com/sk89q/WorldEdit/blob/master/worldedit-core/src/main/java/com/sk89q/worldedit/extent/reorder/MultiStageReorder.java

    @SuppressWarnings("deprecation")
	public static boolean shouldPlaceLast(int id){
    	Material material = Material.getMaterial(id);
		return shouldPlaceLast.contains(material);
    }

	@SuppressWarnings("deprecation")
	public static boolean shouldPlaceFinal(int id){
		Material material = Material.getMaterial(id);
		return shouldPlaceFinal.contains(material);
    }
}