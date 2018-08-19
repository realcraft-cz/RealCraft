package realcraft.bukkit.mapmanager.maps;

import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.MapData;
import realcraft.bukkit.mapmanager.map.data.MapDataLocation;
import realcraft.bukkit.mapmanager.map.data.MapDataMap;

public class MapHidenSeek extends Map {

	private MapDataMap<MapDataLocation> spawns = new MapDataMap<>("spawns");

	public MapHidenSeek(int id){
		super(id,MapType.HIDENSEEK);
	}

	public MapDataMap<MapDataLocation> getSpawns(){
		return spawns;
	}

	@Override
	public MapData getData(){
		MapData data = new MapData();
		data.addProperty(spawns);
		return data;
	}

	@Override
	public void loadData(MapData data){
		spawns.loadData(data);
	}

	private enum MapTeam {
		HIDERS, SEEKERS;

		public static MapTeam getByName(String name){
			return MapTeam.valueOf(name.toUpperCase());
		}

		public String toString(){
			return this.name().toLowerCase();
		}
	}
}