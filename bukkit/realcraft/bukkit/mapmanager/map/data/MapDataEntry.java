package realcraft.bukkit.mapmanager.map.data;

import com.google.gson.JsonElement;

public abstract class MapDataEntry {

	private String name;

	public MapDataEntry(){
		this(null);
	}

	public MapDataEntry(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public abstract JsonElement getData();
	public abstract void loadData(MapData data);
	public abstract boolean equals(Object object);
}