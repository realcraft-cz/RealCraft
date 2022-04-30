package realcraft.bukkit.mapmanager.map.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MapData {

	private JsonObject objects;

	public MapData(){
		objects = new JsonObject();
	}

	public MapData(String data){
		JsonElement element = JsonParser.parseString(data);
		if(element.isJsonObject()) objects = element.getAsJsonObject();
		else objects = new JsonObject();
	}

	public boolean containsKey(String key){
		return objects.has(key);
	}

	public void addProperty(String key,MapDataEntry entry){
		objects.add(key,entry.getData());
	}

	public void addProperty(MapDataEntry entry){
		objects.add(entry.getName(),entry.getData());
	}

	public JsonElement getElement(String key){
		return objects.get(key);
	}

	@Override
	public String toString(){
		return objects.toString();
	}
}