package realcraft.bukkit.mapmanager.maps;

import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.*;

public class MapBedWars extends Map {

	private MapDataMap<MapDataLocation> spawns = new MapDataMap<>("spawns");
	private MapDataMap<MapDataLocation> beds = new MapDataMap<>("beds");
	private MapDataList<MapDataLocation> traders = new MapDataList<>("traders");
	private MapDataList<MapDataLocation> bronze = new MapDataList<>("bronze");
	private MapDataList<MapDataLocation> silver = new MapDataList<>("silver");
	private MapDataList<MapDataLocation> gold = new MapDataList<>("gold");

	public MapBedWars(int id){
		super(id,MapType.BEDWARS);
	}

	public MapDataMap<MapDataLocation> getSpawns(){
		return spawns;
	}

	public MapDataMap<MapDataLocation> getBeds(){
		return beds;
	}

	public MapDataList<MapDataLocation> getTraders(){
		return traders;
	}

	public MapDataList<MapDataLocation> getBronze(){
		return bronze;
	}

	public MapDataList<MapDataLocation> getSilver(){
		return silver;
	}

	public MapDataList<MapDataLocation> getGold(){
		return gold;
	}

	@Override
	public MapData getData(){
		MapData data = new MapData();
		data.addProperty(spawns);
		data.addProperty(beds);
		data.addProperty(traders);
		data.addProperty(bronze);
		data.addProperty(silver);
		data.addProperty(gold);
		return data;
	}

	@Override
	public void loadData(MapData data){
		spawns.loadData(data);
		beds.loadData(data);
		traders.loadData(data);
		bronze.loadData(data);
		silver.loadData(data);
		gold.loadData(data);
	}

	private enum MapTeam {
		RED, BLUE, YELLOW, GREEN;

		public static MapTeam getByName(String name){
			return MapTeam.valueOf(name.toUpperCase());
		}

		public String toString(){
			return this.name().toLowerCase();
		}
	}
}