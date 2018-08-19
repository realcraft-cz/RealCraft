package realcraft.bukkit.mapmanager.maps;

import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.MapData;
import realcraft.bukkit.mapmanager.map.data.MapDataList;
import realcraft.bukkit.mapmanager.map.data.MapDataLocation;
import realcraft.bukkit.mapmanager.map.data.MapDataLocationArea;

public class MapRaces extends Map {

	private MapRaceType type;
	private int rounds;
	private MapDataList<MapDataLocation> spawns = new MapDataList<>("spawns");
	private MapDataList<MapDataLocationArea> checkpoints = new MapDataList<>("checkpoints");

	public MapRaces(int id){
		super(id,MapType.RACES);
	}

	public int getRounds(){
		return rounds;
	}

	public void setRounds(int rounds){
		this.rounds = rounds;
	}

	public MapDataList<MapDataLocation> getSpawns(){
		return spawns;
	}

	public MapDataList<MapDataLocationArea> getCheckpoints(){
		return checkpoints;
	}

	@Override
	public MapData getData(){
		MapData data = new MapData();
		data.addProperty(spawns);
		data.addProperty(checkpoints);
		return data;
	}

	@Override
	public void loadData(MapData data){
		spawns.loadData(data);
		checkpoints.loadData(data);
	}

	private enum MapRaceType {
		RUN, HORSE, BOAT;

		public static MapRaceType getByName(String name){
			return MapRaceType.valueOf(name.toUpperCase());
		}

		public String toString(){
			return this.name().toLowerCase();
		}
	}
}