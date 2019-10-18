package realcraft.bukkit.utils.json;

import com.google.gson.JsonElement;

public abstract class JsonDataEntry {

	private String name;

	public JsonDataEntry(){
		this(null);
	}

	public JsonDataEntry(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public abstract JsonElement getData();
	public abstract void loadData(JsonData data);
	public abstract boolean equals(Object object);
}