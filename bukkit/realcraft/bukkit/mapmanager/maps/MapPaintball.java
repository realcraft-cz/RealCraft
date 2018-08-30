package realcraft.bukkit.mapmanager.maps;

import com.google.gson.JsonElement;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapRenderer;
import realcraft.bukkit.mapmanager.map.MapRenderer.MapRendererLocation;
import realcraft.bukkit.mapmanager.map.MapRenderer.MapRendererLocationArea;
import realcraft.bukkit.mapmanager.map.MapRenderer.MapRendererLocationDirection;
import realcraft.bukkit.mapmanager.map.MapScoreboard;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.*;
import realcraft.share.users.User;

public class MapPaintball extends Map {

	private MapDataMap<MapDataLocation> spawns = new MapDataMap<>("spawns",MapDataLocation.class,2,2);
	private MapDataList<MapDataLocation> drops = new MapDataList<>("drops",MapDataLocation.class);
	private MapDataList<PaintballSpeed> speeds = new MapDataList<>("speeds",PaintballSpeed.class);
	private MapDataList<PaintballJumpArea> jumps = new MapDataList<>("jumps",PaintballJumpArea.class);
	private MapDataList<MapDataLocation> machineguns = new MapDataList<>("machineguns",MapDataLocation.class);

	public MapPaintball(int id){
		super(id);
	}

	public MapPaintball(User user){
		super(user,MapType.PAINTBALL);
	}

	public MapDataMap<MapDataLocation> getSpawns(){
		return spawns;
	}

	public MapDataList<MapDataLocation> getDrops(){
		return drops;
	}

	public MapDataList<PaintballSpeed> getSpeeds(){
		return speeds;
	}

	public MapDataList<PaintballJumpArea> getJumps(){
		return jumps;
	}

	public MapDataList<MapDataLocation> getMachineGuns(){
		return machineguns;
	}

	@Override
	public MapData getData(){
		MapData data = new MapData();
		data.addProperty(spawns);
		data.addProperty(drops);
		data.addProperty(speeds);
		data.addProperty(jumps);
		data.addProperty(machineguns);
		return data;
	}

	@Override
	public void loadData(MapData data){
		spawns.loadData(data);
		drops.loadData(data);
		jumps.loadData(data);
		speeds.loadData(data);
		machineguns.loadData(data);
	}

	@Override
	public void updateScoreboard(MapScoreboard scoreboard){
		scoreboard.addLine("§fSpawns: "+spawns.getValidColor()+spawns.size());
		scoreboard.addLine("§fDrops: "+drops.getValidColor()+drops.size());
		scoreboard.addLine("§fSpeeds: "+speeds.getValidColor()+speeds.size());
		scoreboard.addLine("§fJumps: "+jumps.getValidColor()+jumps.size());
		scoreboard.addLine("§fMachineGuns: "+machineguns.getValidColor()+machineguns.size());
	}

	@Override
	public void updateRenderer(MapRenderer renderer){
		for(java.util.Map.Entry<String,MapDataLocation> entry : spawns.getValues().entrySet()){
			renderer.addEntry(new MapRendererLocationDirection(entry.getValue(),ChatColor.YELLOW+spawns.getName().toUpperCase(),MapTeam.getByName(entry.getKey()).getColor()+entry.getKey().toUpperCase()));
		}
		for(MapDataLocation location : drops.getValues()){
			renderer.addEntry(new MapRendererLocation(location,ChatColor.DARK_AQUA+drops.getName().toUpperCase()));
		}
		for(MapDataLocation location : speeds.getValues()){
			renderer.addEntry(new MapRendererLocation(location,ChatColor.GOLD+speeds.getName().toUpperCase()));
		}
		for(MapDataLocationArea area : jumps.getValues()){
			renderer.addEntry(new MapRendererLocationArea(area,ChatColor.GREEN+jumps.getName().toUpperCase()));
		}
		for(MapDataLocation location : machineguns.getValues()){
			renderer.addEntry(new MapRendererLocation(location,ChatColor.DARK_GREEN+machineguns.getName().toUpperCase()));
		}
	}

	@Override
	public boolean isValid(){
		return (spawns.isValid() && drops.isValid() && speeds.isValid() && jumps.isValid() && machineguns.isValid());
	}

	private enum MapTeam {
		RED, BLUE;

		public static MapTeam getByName(String name){
			return MapTeam.valueOf(name.toUpperCase());
		}

		public String toString(){
			return this.name().toLowerCase();
		}

		public ChatColor getColor(){
			switch(this){
				case RED: return ChatColor.RED;
				case BLUE: return ChatColor.BLUE;
			}
			return ChatColor.WHITE;
		}
	}

	private class PaintballSpeed extends MapDataLocation {

		private int duration;

		public PaintballSpeed(int duration,Location location){
			super(location);
			this.duration = duration;
		}

		public PaintballSpeed(JsonElement element){
			super(element);
			this.duration = element.getAsJsonObject().get("duration").getAsInt();
		}

		public int getDuration(){
			return duration;
		}
	}

	private class PaintballJumpArea extends MapDataLocationArea {

		private double force;

		public PaintballJumpArea(double force,Location locFrom,Location locTo){
			super(locFrom,locTo);
			this.force = force;
		}

		public PaintballJumpArea(JsonElement element){
			super(element);
			this.force = element.getAsJsonObject().get("force").getAsDouble();
		}

		public double getForce(){
			return force;
		}
	}
}