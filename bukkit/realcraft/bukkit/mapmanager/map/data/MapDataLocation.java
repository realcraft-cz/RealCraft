package realcraft.bukkit.mapmanager.map.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import realcraft.bukkit.mapmanager.MapManager;

public class MapDataLocation extends MapDataEntry {

	private Location location;

	public MapDataLocation(Location location){
		this(null,location);
	}

	public MapDataLocation(String name,Location location){
		super(name);
		this.location = location;
	}

	public MapDataLocation(JsonElement element){
		JsonObject json = element.getAsJsonObject();
		double x = json.get("x").getAsDouble();
		double y = json.get("y").getAsDouble();
		double z = json.get("z").getAsDouble();
		float yaw = json.get("yaw").getAsFloat();
		float pitch = json.get("pitch").getAsFloat();
		this.location = new Location(MapManager.getWorld(),x,y,z,yaw,pitch);
	}

	public Location getLocation(){
		return location;
	}

	public JsonObject getData(){
		JsonObject json = new JsonObject();
		json.addProperty("x",location.getX());
		json.addProperty("y",location.getY());
		json.addProperty("z",location.getZ());
		json.addProperty("yaw",location.getYaw());
		json.addProperty("pitch",location.getPitch());
		return json;
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof MapDataLocation){
			MapDataLocation toCompare = (MapDataLocation) object;
			return (toCompare.getLocation().getBlockX() == this.getLocation().getBlockX() &&
					toCompare.getLocation().getBlockY() == this.getLocation().getBlockY() &&
					toCompare.getLocation().getBlockZ() == this.getLocation().getBlockZ()
			);
		}
		return false;
	}
}