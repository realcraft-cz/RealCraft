package realcraft.bukkit.mapmanager.map.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Location;

public class MapDataLocationBlock extends MapDataLocation {

	public MapDataLocationBlock(Location location){
		this(null,location);
	}

	public MapDataLocationBlock(String name){
		this(name,null);
	}

	public MapDataLocationBlock(String name,Location location){
		super(name,location);
		if(location != null){
			this.getLocation().setX(location.getBlockX());
			this.getLocation().setY(location.getBlockY());
			this.getLocation().setZ(location.getBlockZ());
		}
	}

	public MapDataLocationBlock(JsonElement element){
		super(element);
	}

	@Override
	public JsonObject getData(){
		JsonObject json = new JsonObject();
		if(this.getLocation() != null){
			json.addProperty("x",this.round(this.getLocation().getX(),0));
			json.addProperty("y",this.round(this.getLocation().getY(),0));
			json.addProperty("z",this.round(this.getLocation().getZ(),0));
			json.addProperty("yaw",0);
			json.addProperty("pitch",0);
		}
		return json;
	}
}