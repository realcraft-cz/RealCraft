package com.realcraft.creative;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.object.RegionWrapper;
import com.realcraft.RealCraft;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.NullExtent;
import com.sk89q.worldedit.util.eventbus.EventHandler.Priority;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.biome.BaseBiome;

public class PlotSquaredWorldEdit implements Listener {

	private HashMap<String,Boolean> WEByPass = new HashMap<String,Boolean>();

	public PlotSquaredWorldEdit(){
		Bukkit.getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
		WorldEdit.getInstance().getEventBus().register(this);
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1).toLowerCase();
		if((command.startsWith("p wea") || command.startsWith("wea")) && (player.hasPermission("group.Admin") || player.hasPermission("group.Moderator") || player.hasPermission("group.Builder"))){
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
				event.setExtent(new PlotSquaredWEExtent(mask,event.getExtent()));
			}
		}
	}

	private HashSet<RegionWrapper> getMask(PlotPlayer player) {
        HashSet<RegionWrapper> regions = new HashSet<>();
        UUID uuid = player.getUUID();
        Location location = player.getLocation();
        String world = location.getWorld();
        if(!PS.get().hasPlotArea(world)){
            regions.add(new RegionWrapper(Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE));
            return regions;
        }
        PlotArea area = player.getApplicablePlotArea();
        if(area == null){
            return regions;
        }
        Plot plot = player.getCurrentPlot();
        if(plot == null) plot = player.getMeta("WorldEditRegionPlot");
        if(plot != null && plot.isAdded(uuid)){
            for (RegionWrapper region : plot.getRegions()) {
                RegionWrapper copy = new RegionWrapper(region.minX, region.maxX, area.MIN_BUILD_HEIGHT, area.MAX_BUILD_HEIGHT, region.minZ, region.maxZ);
                regions.add(copy);
            }
            player.setMeta("WorldEditRegionPlot",plot);
        }
        return regions;
    }

	private class PlotSquaredWEExtent extends AbstractDelegateExtent {

		private final HashSet<RegionWrapper> mask;

	    public PlotSquaredWEExtent(HashSet<RegionWrapper> mask,Extent extent){
	        super(extent);
	        this.mask = mask;
	    }

		@Override
	    public boolean setBlock(Vector location,BaseBlock block) throws WorldEditException {
	        return this.maskContains(this.mask,location.getBlockX(),location.getBlockY(),location.getBlockZ()) && super.setBlock(location,block);
	    }

	    @Override
	    public Entity createEntity(com.sk89q.worldedit.util.Location location,BaseEntity entity){
	    	if(this.maskContains(this.mask, location.getBlockX(), location.getBlockY(), location.getBlockZ())){
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

	    public boolean maskContains(HashSet<RegionWrapper> mask, int x, int y, int z) {
	        for (RegionWrapper region : mask) {
	            if (region.isIn(x, y, z)) {
	                return true;
	            }
	        }
	        return false;
	    }

	    public boolean maskContains(HashSet<RegionWrapper> mask, int x, int z) {
	        for (RegionWrapper region : mask) {
	            if (region.isIn(x, z)) {
	                return true;
	            }
	        }
	        return false;
	    }
	}
}