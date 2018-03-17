package realcraft.bukkit.creative;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.object.RegionWrapper;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.NullExtent;
import com.sk89q.worldedit.util.eventbus.EventHandler.Priority;
import com.sk89q.worldedit.util.eventbus.Subscribe;

import realcraft.bukkit.RealCraft;

public class PlotSquaredWorldEdit implements Listener, Runnable {

	private static final int ITEMS_LIMIT = 256;
	private static final int FALLINGS_LIMIT = 2048;

	private HashMap<String,Boolean> WEByPass = new HashMap<String,Boolean>();

	private HashMap<Chunk,Long> chunkLastItem = new HashMap<Chunk,Long>();
	private HashMap<Chunk,Integer> chunkItemCount = new HashMap<Chunk,Integer>();

	private HashMap<Chunk,Long> chunkLastFall = new HashMap<Chunk,Long>();
	private HashMap<Chunk,Integer> chunkFallCount = new HashMap<Chunk,Integer>();

	public PlotSquaredWorldEdit(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,20,20);
		WorldEdit.getInstance().getEventBus().register(this);
	}

	@Override
	public void run(){
		this.resetItems();
		this.resetFalls();
	}

	private void resetItems(){
		ArrayList<Chunk> toRemove = new ArrayList<Chunk>();
		for(Chunk chunk : chunkLastItem.keySet()){
			if(chunkLastItem.get(chunk)+1000 < System.currentTimeMillis()) toRemove.add(chunk);
		}
		for(Chunk chunk : toRemove){
			chunkLastItem.remove(chunk);
			chunkItemCount.remove(chunk);
		}
	}

	private void resetFalls(){
		ArrayList<Chunk> toRemove = new ArrayList<Chunk>();
		for(Chunk chunk : chunkLastFall.keySet()){
			if(chunkLastFall.get(chunk)+1000 < System.currentTimeMillis()) toRemove.add(chunk);
		}
		for(Chunk chunk : toRemove){
			chunkLastFall.remove(chunk);
			chunkFallCount.remove(chunk);
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1).toLowerCase();
		if((command.equalsIgnoreCase("p wea") || command.equalsIgnoreCase("wea")) && (player.hasPermission("group.Admin") || player.hasPermission("group.Moderator") || player.hasPermission("group.Builder"))){
			WEByPass.put(player.getName(),!WEByPass.get(player.getName()));
			player.sendMessage("§8[§6P2§8] §6WorldEdit bypass "+(WEByPass.get(player.getName()) ? "§aenabled" : "§cdisabled"));
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		WEByPass.put(event.getPlayer().getName(),false);
	}

	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent event){
		WEByPass.put(event.getPlayer().getName(),false);
	}

	@EventHandler
	public void EntitySpawnEvent(EntitySpawnEvent event){
		if(this.isEntityForbidden(event.getEntity())){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void CreatureSpawnEvent(CreatureSpawnEvent event){
	}

	@EventHandler
	public void ItemSpawnEvent(ItemSpawnEvent event){
		Chunk chunk = event.getLocation().getChunk();
		chunkLastItem.put(chunk,System.currentTimeMillis());
		chunkItemCount.put(chunk,(chunkItemCount.containsKey(chunk) ? chunkItemCount.get(chunk)+1 : 1));
		if(chunkItemCount.get(chunk) > ITEMS_LIMIT){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void EntityChangeBlockEvent(EntityChangeBlockEvent event){
		if(event.getEntityType() == EntityType.FALLING_BLOCK){
			Chunk chunk = event.getBlock().getChunk();
			chunkLastFall.put(chunk,System.currentTimeMillis());
			chunkFallCount.put(chunk,(chunkFallCount.containsKey(chunk) ? chunkFallCount.get(chunk)+1 : 1));
			if(chunkFallCount.get(chunk) > FALLINGS_LIMIT){
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void EntityExplodeEvent(EntityExplodeEvent event){
		if(event.getEntityType() == EntityType.PRIMED_TNT){
			event.setCancelled(true);
			event.blockList().clear();
		}
	}

	@EventHandler
	public void ExplosionPrimeEvent(ExplosionPrimeEvent event){
		if(event.getEntityType() == EntityType.PRIMED_TNT){
			event.setCancelled(true);
		}
	}

	@Subscribe(priority = Priority.NORMAL)
	public void EditSessionEvent(EditSessionEvent event){
		if(event.getActor() != null && event.getActor().isPlayer()){
			Player player = Bukkit.getServer().getPlayer(event.getActor().getName());
			if(player != null && (!WEByPass.containsKey(player.getName()) || WEByPass.get(player.getName()) == false)){
				PlotPlayer plotPlayer = PlotPlayer.wrap(player.getName());
				if(plotPlayer == null){
					event.setExtent(new NullExtent());
					return;
				}
				HashSet<RegionWrapper> mask = this.getMask(plotPlayer);
				if(mask.isEmpty()){
					event.setExtent(new NullExtent());
					return;
				}
				event.setExtent(new PlotSquaredWEExtent(mask,event.getWorld(),event.getExtent()));
			}
		}
	}

	private HashSet<RegionWrapper> getMask(PlotPlayer player){
		HashSet<RegionWrapper> regions = new HashSet<>();
		UUID uuid = player.getUUID();
		Location location = player.getLocation();
		String world = location.getWorld();
		if(!PS.get().hasPlotArea(world)){
			regions.add(new RegionWrapper(Integer.MIN_VALUE,Integer.MAX_VALUE,Integer.MIN_VALUE,Integer.MAX_VALUE));
			return regions;
		}
		PlotArea area = player.getApplicablePlotArea();
		if(area == null){
			return regions;
		}
		Plot plot = player.getCurrentPlot();
		if(plot == null) plot = player.getMeta("WorldEditRegionPlot");
		if(plot != null && plot.isAdded(uuid)){
			for(RegionWrapper region : plot.getRegions()){
				RegionWrapper copy = new RegionWrapper(region.minX,region.maxX,area.MIN_BUILD_HEIGHT,area.MAX_BUILD_HEIGHT,region.minZ,region.maxZ);
				regions.add(copy);
			}
			player.setMeta("WorldEditRegionPlot",plot);
		}
		return regions;
	}

	private boolean isEntityForbidden(Entity entity){
		switch(entity.getType()){
			case FIREBALL: return true;
			case DRAGON_FIREBALL: return true;
			case SMALL_FIREBALL: return true;
			case WITHER: return true;
			default:break;
		}
		return false;
	}
}