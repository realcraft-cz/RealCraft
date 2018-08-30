package realcraft.bukkit.mapmanager.maps;

import com.google.gson.JsonElement;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapRenderer;
import realcraft.bukkit.mapmanager.map.MapRenderer.MapRendererLocation;
import realcraft.bukkit.mapmanager.map.MapRenderer.MapRendererLocationDirection;
import realcraft.bukkit.mapmanager.map.MapScoreboard;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.MapData;
import realcraft.bukkit.mapmanager.map.data.MapDataList;
import realcraft.bukkit.mapmanager.map.data.MapDataLocation;
import realcraft.bukkit.mapmanager.map.data.MapDataMap;
import realcraft.share.users.User;

public class MapDominate extends Map {

	private MapDataMap<MapDataLocation> spawns = new MapDataMap<>("spawns",MapDataLocation.class,2,2);
	private MapDataList<MapDataLocation> kits = new MapDataList<>("kits",MapDataLocation.class,10,10);
	private MapDataList<MapDataLocation> emeralds = new MapDataList<>("emeralds",MapDataLocation.class,2,4);
	private MapDataList<DominatePoint> points = new MapDataList<>("points",DominatePoint.class,5,5);

	public MapDominate(int id){
		super(id);
	}

	public MapDominate(User user){
		super(user,MapType.DOMINATE);
	}

	public MapDataMap<MapDataLocation> getSpawns(){
		return spawns;
	}

	public MapDataList<MapDataLocation> getKits(){
		return kits;
	}

	public MapDataList<MapDataLocation> getEmeralds(){
		return emeralds;
	}

	public MapDataList<DominatePoint> getPoints(){
		return points;
	}

	@Override
	public MapData getData(){
		MapData data = new MapData();
		data.addProperty(spawns);
		data.addProperty(kits);
		data.addProperty(emeralds);
		data.addProperty(points);
		return data;
	}

	@Override
	public void loadData(MapData data){
		spawns.loadData(data);
		kits.loadData(data);
		emeralds.loadData(data);
		points.loadData(data);
	}

	@Override
	public void updateScoreboard(MapScoreboard scoreboard){
		scoreboard.addLine("§fSpawns: "+spawns.getValidColor()+spawns.size());
		scoreboard.addLine("§fKits: "+kits.getValidColor()+kits.size());
		scoreboard.addLine("§fEmeralds: "+emeralds.getValidColor()+emeralds.size());
		scoreboard.addLine("§fPoints: "+points.getValidColor()+points.size());
	}

	@Override
	public void updateRenderer(MapRenderer renderer){
		for(java.util.Map.Entry<String,MapDataLocation> entry : spawns.getValues().entrySet()){
			renderer.addEntry(new MapRendererLocationDirection(entry.getValue(),ChatColor.YELLOW+spawns.getName().toUpperCase(),MapTeam.getByName(entry.getKey()).getColor()+entry.getKey().toUpperCase()));
		}
		for(MapDataLocation location : kits.getValues()){
			renderer.addEntry(new MapRendererLocationDirection(location,ChatColor.DARK_AQUA+kits.getName().toUpperCase()));
		}
		for(MapDataLocation location : emeralds.getValues()){
			renderer.addEntry(new MapRendererLocation(location,ChatColor.GREEN+emeralds.getName().toUpperCase()));
		}
		for(MapDataLocation location : points.getValues()){
			renderer.addEntry(new MapRendererLocation(location,ChatColor.AQUA+points.getName().toUpperCase()));
		}
	}

	@Override
	public boolean isValid(){
		return (spawns.isValid() && kits.isValid() && emeralds.isValid() && points.isValid());
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

	private class DominatePoint extends MapDataLocation {

		private String name;

		public DominatePoint(String name,Location location){
			super(location);
			this.name = name;
		}

		public DominatePoint(JsonElement element){
			super(element);
			this.name = element.getAsJsonObject().get("name").getAsString();
		}

		public String getName(){
			return name;
		}
	}
}