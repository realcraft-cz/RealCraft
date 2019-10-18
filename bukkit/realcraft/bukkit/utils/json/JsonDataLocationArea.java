package realcraft.bukkit.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class JsonDataLocationArea extends JsonDataEntry {

	private JsonDataLocation minLoc;
	private JsonDataLocation maxLoc;

	public JsonDataLocationArea(Location locFrom,Location locTo){
		this(null,locFrom,locTo);
	}

	public JsonDataLocationArea(String name,Location locFrom,Location locTo){
		super(name);
		this.minLoc = new JsonDataLocation(Vector.getMinimum(locFrom.toVector(),locTo.toVector()).toLocation(locFrom.getWorld()));
		this.maxLoc = new JsonDataLocation(Vector.getMaximum(locFrom.toVector(),locTo.toVector()).toLocation(locFrom.getWorld()));
	}

	public JsonDataLocationArea(JsonElement element){
		JsonObject json = element.getAsJsonObject();
		this.minLoc = new JsonDataLocation(json.get("from").getAsJsonObject());
		this.maxLoc = new JsonDataLocation(json.get("to").getAsJsonObject());
	}

	public JsonDataLocationArea(String name){
		super(name);
	}

	public JsonDataLocation getMinLocation(){
		return minLoc;
	}

	public JsonDataLocation getMaxLocation(){
		return maxLoc;
	}

	@Override
	public JsonObject getData(){
		JsonObject object = new JsonObject();
		object.add("from",minLoc.getData());
		object.add("to",maxLoc.getData());
		return object;
	}

	@Override
	public void loadData(JsonData data){
		if(data.containsKey(this.getName())){
			JsonDataLocationArea tmp = new JsonDataLocationArea(data.getElement(this.getName()));
			minLoc = tmp.getMinLocation();
			maxLoc = tmp.getMaxLocation();
		}
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof JsonDataLocationArea){
			JsonDataLocationArea toCompare = (JsonDataLocationArea) object;
			return (toCompare.getMinLocation().equals(this.getMinLocation()) && toCompare.getMaxLocation().equals(this.getMaxLocation()));
		}
		return false;
	}
}