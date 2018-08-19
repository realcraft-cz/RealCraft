package realcraft.bukkit.mapmanager.map.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class MapDataLocationArea extends MapDataEntry {

	private MapDataLocation minLoc;
	private MapDataLocation maxLoc;

	public MapDataLocationArea(Location locFrom,Location locTo){
		this(null,locFrom,locTo);
	}

	public MapDataLocationArea(String name,Location locFrom,Location locTo){
		super(name);
		this.minLoc = new MapDataLocation(Vector.getMinimum(locFrom.toVector(),locTo.toVector()).toLocation(locFrom.getWorld()));
		this.maxLoc = new MapDataLocation(Vector.getMaximum(locFrom.toVector(),locTo.toVector()).toLocation(locFrom.getWorld()));
	}

	public MapDataLocationArea(JsonElement element){
		JsonObject json = element.getAsJsonObject();
		this.minLoc = new MapDataLocation(json.get("from").getAsJsonObject());
		this.maxLoc = new MapDataLocation(json.get("to").getAsJsonObject());
	}

	public MapDataLocation getMinLocation(){
		return minLoc;
	}

	public MapDataLocation getMaxLocation(){
		return maxLoc;
	}

	public JsonObject getData(){
		JsonObject object = new JsonObject();
		object.add("from",minLoc.getData());
		object.add("to",maxLoc.getData());
		return object;
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof MapDataLocationArea){
			MapDataLocationArea toCompare = (MapDataLocationArea) object;
			return (toCompare.getMinLocation().equals(this.getMinLocation()) && toCompare.getMaxLocation().equals(this.getMaxLocation()));
		}
		return false;
	}
}