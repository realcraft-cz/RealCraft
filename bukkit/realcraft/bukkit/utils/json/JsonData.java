package realcraft.bukkit.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonData {

	private JsonObject objects;

	public JsonData(){
		objects = new JsonObject();
	}

	public JsonData(String data){
		JsonElement element = new JsonParser().parse(data);
		if(element.isJsonObject()) objects = element.getAsJsonObject();
		else objects = new JsonObject();
	}

	public boolean containsKey(String key){
		return objects.has(key);
	}

	public void addProperty(String key,JsonDataEntry entry){
		objects.add(key,entry.getData());
	}

	public void addProperty(JsonDataEntry entry){
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