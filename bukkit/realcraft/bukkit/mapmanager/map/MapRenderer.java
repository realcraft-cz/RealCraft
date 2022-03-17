package realcraft.bukkit.mapmanager.map;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.mapmanager.map.data.MapDataLocation;
import realcraft.bukkit.mapmanager.map.data.MapDataLocationArea;
import realcraft.bukkit.mapmanager.map.data.MapDataLocationBlock;
import realcraft.bukkit.mapmanager.map.data.MapDataLocationSpawn;
import realcraft.bukkit.wrappers.HologramsApi;

import java.util.ArrayList;

public class MapRenderer implements Runnable {

	private static final int EFFECT_RENDER_DISTANCE = 16*16;

	private Map map;
	private ArrayList<MapRendererEntry> entries = new ArrayList<>();

	public MapRenderer(Map map){
		this.map = map;
		Bukkit.getScheduler().runTaskTimerAsynchronously(RealCraft.getInstance(),this,2,2);
	}

	public void update(){
		for(MapRendererEntry entry : entries){
			entry.remove();
		}
		entries.clear();
		map.updateRenderer(this);
		if(map.getSpectator().getLocation() != null) this.addEntry(new MapRendererLocation(map.getSpectator(),ChatColor.GRAY+"Spectator"));
	}

	public void addEntry(MapRendererEntry entry){
		entries.add(entry);
	}

	@Override
	public void run(){
		for(Player player : Bukkit.getOnlinePlayers()){
			if(player.isSneaking() && map.equals(MapManager.getMapPlayer(player).getMap())){
				for(MapRendererEntry entry : entries){
					if(entry instanceof MapRendererLocation){
						MapRendererLocation location = (MapRendererLocation)entry;
						if(location.getLocation().getLocation().distanceSquared(player.getLocation()) <= EFFECT_RENDER_DISTANCE){
							Location tmpLocation = location.getLocation().getLocation().clone();
							tmpLocation.setX(tmpLocation.getBlockX());
							tmpLocation.setY(tmpLocation.getBlockY());
							tmpLocation.setZ(tmpLocation.getBlockZ());
							for(int y=0;y<=1;y++){
								player.spawnParticle(Particle.REDSTONE,tmpLocation.clone().add(0,y,0),1,0f,0f,0f,0f,new Particle.DustOptions(Color.YELLOW,0.8f));
								player.spawnParticle(Particle.REDSTONE,tmpLocation.clone().add(0,y,1),1,0f,0f,0f,0f,new Particle.DustOptions(Color.YELLOW,0.8f));
								player.spawnParticle(Particle.REDSTONE,tmpLocation.clone().add(1,y,0),1,0f,0f,0f,0f,new Particle.DustOptions(Color.YELLOW,0.8f));
								player.spawnParticle(Particle.REDSTONE,tmpLocation.clone().add(1,y,1),1,0f,0f,0f,0f,new Particle.DustOptions(Color.YELLOW,0.8f));
							}
							if(((MapRendererLocation)entry).getLocation() instanceof MapDataLocationSpawn){
								tmpLocation = location.getLocation().getLocation().clone();
								tmpLocation.setY(tmpLocation.getBlockY()+0.5);
								player.spawnParticle(Particle.REDSTONE,tmpLocation,1,0f,0f,0f,0f,new Particle.DustOptions(Color.BLUE,0.6f));
								for(int i=0;i<10;i++){
									player.spawnParticle(Particle.REDSTONE,tmpLocation.add(tmpLocation.getDirection().normalize().multiply(0.1)),1,0f,0f,0f,0f,new Particle.DustOptions(Color.BLUE,0.6f));
								}
							}
						}
					}
					else if(entry instanceof MapRendererLocationArea){
						MapRendererLocationArea area = (MapRendererLocationArea)entry;
						if(area.getArea().getMinLocation().getLocation().distanceSquared(player.getLocation()) <= EFFECT_RENDER_DISTANCE || area.getArea().getMaxLocation().getLocation().distanceSquared(player.getLocation()) <= EFFECT_RENDER_DISTANCE || area.getCenterLocation().distanceSquared(player.getLocation()) <= EFFECT_RENDER_DISTANCE){
							Location locMin = area.getArea().getMinLocation().getLocation().clone();
							Location locMax = area.getArea().getMaxLocation().getLocation().clone();
							locMin.setX(locMin.getBlockX());
							locMin.setY(locMin.getBlockY());
							locMin.setZ(locMin.getBlockZ());
							locMax.setX(locMax.getBlockX());
							locMax.setY(locMax.getBlockY());
							locMax.setZ(locMax.getBlockZ());
							for(int x=locMin.getBlockX();x<=locMax.getBlockX()+1;x++){
								for(int y=locMin.getBlockY();y<=locMax.getBlockY()+1;y++){
									for(int z=locMin.getBlockZ();z<=locMax.getBlockZ()+1;z++){
										boolean edge = false;
										if((x == locMin.getBlockX() || x == locMax.getBlockX()+1) && (y == locMin.getBlockY() || y == locMax.getBlockY()+1)) edge = true;
										if((z == locMin.getBlockZ() || z == locMax.getBlockZ()+1) && (y == locMin.getBlockY() || y == locMax.getBlockY()+1)) edge = true;
										if((x == locMin.getBlockX() || x == locMax.getBlockX()+1) && (z == locMin.getBlockZ() || z == locMax.getBlockZ()+1)) edge = true;
										if(edge){
											player.spawnParticle(Particle.REDSTONE,new Location(locMin.getWorld(),x,y,z),1,0f,0f,0f,0f,new Particle.DustOptions(Color.RED,0.8f));
										}
									}
								}
							}
							/*Location tmpLocation = area.getArea().getMinLocation().getLocation().clone();
							tmpLocation.setX(tmpLocation.getBlockX());
							tmpLocation.setY(tmpLocation.getBlockY());
							tmpLocation.setZ(tmpLocation.getBlockZ());
							for(int y=0;y<=1;y++){
								player.spawnParticle(Particle.REDSTONE,tmpLocation.clone().add(0,y,0),1,0f,0f,0f,0f,new Particle.DustOptions(Color.RED,0.8f));
								player.spawnParticle(Particle.REDSTONE,tmpLocation.clone().add(0,y,1),1,0f,0f,0f,0f,new Particle.DustOptions(Color.RED,0.8f));
								player.spawnParticle(Particle.REDSTONE,tmpLocation.clone().add(1,y,0),1,0f,0f,0f,0f,new Particle.DustOptions(Color.RED,0.8f));
								player.spawnParticle(Particle.REDSTONE,tmpLocation.clone().add(1,y,1),1,0f,0f,0f,0f,new Particle.DustOptions(Color.RED,0.8f));
							}
							tmpLocation = area.getArea().getMaxLocation().getLocation().clone();
							tmpLocation.setX(tmpLocation.getBlockX());
							tmpLocation.setY(tmpLocation.getBlockY());
							tmpLocation.setZ(tmpLocation.getBlockZ());
							for(int y=0;y<=1;y++){
								player.spawnParticle(Particle.REDSTONE,tmpLocation.clone().add(0,y,0),1,0f,0f,0f,0f,new Particle.DustOptions(Color.RED,0.8f));
								player.spawnParticle(Particle.REDSTONE,tmpLocation.clone().add(0,y,1),1,0f,0f,0f,0f,new Particle.DustOptions(Color.RED,0.8f));
								player.spawnParticle(Particle.REDSTONE,tmpLocation.clone().add(1,y,0),1,0f,0f,0f,0f,new Particle.DustOptions(Color.RED,0.8f));
								player.spawnParticle(Particle.REDSTONE,tmpLocation.clone().add(1,y,1),1,0f,0f,0f,0f,new Particle.DustOptions(Color.RED,0.8f));
							}*/
						}
					}
				}
			}
		}
	}

	public static abstract class MapRendererEntry {

		private HologramsApi.Hologram hologram;

		public void spawn(Location location,Material material,String... lines){
			location.setY(location.getBlockY()+0.6+(0.4*(lines.length-1))+(material != null ? 0.4 : 0));
			this.hologram = HologramsApi.createHologram(location);
			for(int i=0;i<lines.length;i++) hologram.insertTextLine(i,lines[i]);
			if(material != null) hologram.appendItemLine(new ItemStack(material));
		}

		public void remove(){
			hologram.delete();
		}
	}

	public static class MapRendererLocation extends MapRendererEntry {

		private MapDataLocation location;

		public MapRendererLocation(MapDataLocation location,String... lines){
			this(location,null,lines);
		}

		public MapRendererLocation(MapDataLocation location,Material material,String... lines){
			this.location = location;
			if(location instanceof MapDataLocationBlock){
				this.spawn(location.getLocation().clone().add(0.5,0.0,0.5),material,lines);
			}
			else if(location instanceof MapDataLocationSpawn){
				this.spawn(location.getLocation().clone(),material,lines);
			}
			else {
				this.spawn(location.getLocation().clone(),material,lines);
			}
		}

		public MapDataLocation getLocation(){
			return location;
		}
	}

	public static class MapRendererLocationArea extends MapRendererEntry {

		private MapDataLocationArea area;

		public MapRendererLocationArea(MapDataLocationArea area,String... lines){
			this.area = area;
			this.spawn(this.getCenterLocation(),null,lines);
		}

		public MapDataLocationArea getArea(){
			return area;
		}

		public Location getCenterLocation(){
			return area.getMinLocation().getLocation().clone().add(
					(area.getMaxLocation().getLocation().getX()-area.getMinLocation().getLocation().getX())/2f,
					(area.getMaxLocation().getLocation().getY()-area.getMinLocation().getLocation().getY())/2f,
					(area.getMaxLocation().getLocation().getZ()-area.getMinLocation().getLocation().getZ())/2f
			);
		}
	}
}