package realcraft.bukkit.mapmanager.maps;

import com.google.gson.JsonElement;
import org.bukkit.Location;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.*;

public class MapDominate extends Map {

	private MapDataMap<MapDataLocation> spawns = new MapDataMap<>("spawns");
	private MapDataList<MapDataLocation> emeralds = new MapDataList<>("emeralds");
	private MapDataList<MapDataLocation> kits = new MapDataList<>("kits");
	private MapDataList<DominatePoint> points = new MapDataList<>("points");

	public MapDominate(int id){
		super(id,MapType.DOMINATE);
	}

	public MapDataMap<MapDataLocation> getSpawns(){
		return spawns;
	}

	public MapDataList<MapDataLocation> getEmeralds(){
		return emeralds;
	}

	public MapDataList<MapDataLocation> getKits(){
		return kits;
	}

	public MapDataList<DominatePoint> getPoints(){
		return points;
	}

	@Override
	public MapData getData(){
		MapData data = new MapData();
		data.addProperty(spawns);
		data.addProperty(emeralds);
		data.addProperty(kits);
		data.addProperty(points);
		return data;
	}

	@Override
	public void loadData(MapData data){
		spawns.loadData(data);
		emeralds.loadData(data);
		kits.loadData(data);
		points.loadData(data);
	}

	private enum MapTeam {
		RED, BLUE;

		public static MapTeam getByName(String name){
			return MapTeam.valueOf(name.toUpperCase());
		}

		public String toString(){
			return this.name().toLowerCase();
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
			this.name = element.getAsJsonObject().get("force").getAsString();
		}

		public String getName(){
			return name;
		}
	}
}