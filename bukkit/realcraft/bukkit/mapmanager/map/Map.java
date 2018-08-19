package realcraft.bukkit.mapmanager.map;

import realcraft.bukkit.mapmanager.map.data.MapData;

public abstract class Map {

	private int id;
	private MapType type;
	private MapData data;

	public Map(int id,MapType type){
		this.id = id;
		this.type = type;
	}

	public int getId(){
		return id;
	}

	public MapType getType(){
		return type;
	}

	public String getJsonData(){
		return this.getData().toString();
	}

	public abstract MapData getData();
	public abstract void loadData(MapData data);
}