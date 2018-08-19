package realcraft.bukkit.mapmanager.maps;

import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.MapData;
import realcraft.bukkit.mapmanager.map.data.MapDataList;
import realcraft.bukkit.mapmanager.map.data.MapDataLocation;

public class MapRageMode extends Map {

	private MapDataList<MapDataLocation> spawns = new MapDataList<>("spawns");

	public MapRageMode(int id){
		super(id,MapType.RAGEMODE);
	}

	public MapDataList<MapDataLocation> getSpawns(){
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
}