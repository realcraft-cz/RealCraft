package realcraft.bukkit.mapmanager.map;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.mapmanager.map.data.MapDataLocation;
import realcraft.bukkit.mapmanager.map.data.MapDataLocationArea;
import realcraft.bukkit.utils.Particles;

import java.util.ArrayList;

public class MapRenderer implements Runnable {

	private static final int EFFECT_RENDER_DISTANCE = 16*16;

	private Map map;
	private ArrayList<MapRendererEntry> entries = new ArrayList<>();

	public MapRenderer(Map map){
		this.map = map;
		Bukkit.getScheduler().runTaskTimerAsynchronously(RealCraft.getInstance(),this,3*20,3*20);
	}

	public void update(){
		for(MapRendererEntry entry : entries){
			entry.remove();
		}
		entries.clear();
		map.updateRenderer(this);
	}

	public void addEntry(MapRendererEntry entry){
		entries.add(entry);
	}

	@Override
	public void run(){
		for(Player player : Bukkit.getOnlinePlayers()){
			if(map.equals(MapManager.getMapPlayer(player).getMap())){
				for(MapRendererEntry entry : entries){
					if(entry instanceof MapRendererLocationDirection){
						MapRendererLocation location = (MapRendererLocation)entry;
						if(location.getLocation().getLocation().distanceSquared(player.getLocation()) <= EFFECT_RENDER_DISTANCE){
							Particles.BARRIER.display(location.getLocation().getLocation(),1,player);
						}
					}
					else if(entry instanceof MapRendererLocationArea){

					}
					else if(entry instanceof MapRendererLocation){
						MapRendererLocation location = (MapRendererLocation)entry;
						if(location.getLocation().getLocation().distanceSquared(player.getLocation()) <= EFFECT_RENDER_DISTANCE){
							Particles.BARRIER.display(location.getLocation().getLocation(),1,player);
						}
					}
				}
			}
		}
	}

	public static abstract class MapRendererEntry {

		private Hologram hologram;

		public MapRendererEntry(Location location,String... lines){
			this.hologram = HologramsAPI.createHologram(RealCraft.getInstance(),location);
			for(int i=0;i<lines.length;i++) hologram.insertTextLine(i,lines[i]);
		}

		public void remove(){
			hologram.delete();
		}
	}

	public static class MapRendererLocation extends MapRendererEntry {

		private MapDataLocation location;

		public MapRendererLocation(MapDataLocation location,String... lines){
			super(location.getLocation(),lines);
			this.location = location;
		}

		public MapDataLocation getLocation(){
			return location;
		}
	}

	public static class MapRendererLocationDirection extends MapRendererLocation {

		public MapRendererLocationDirection(MapDataLocation location,String... lines){
			super(location,lines);
		}
	}

	public static class MapRendererLocationArea extends MapRendererEntry {

		private MapDataLocationArea area;

		public MapRendererLocationArea(MapDataLocationArea area,String... lines){
			super(area.getMinLocation().getLocation().clone().add(area.getMaxLocation().getLocation().getX()/2f,area.getMaxLocation().getLocation().getY()/2f,area.getMaxLocation().getLocation().getZ()/2f),lines);
			this.area = area;
		}

		public MapDataLocationArea getLocation(){
			return area;
		}
	}
}