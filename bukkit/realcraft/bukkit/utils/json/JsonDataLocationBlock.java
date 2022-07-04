package realcraft.bukkit.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Location;

public class JsonDataLocationBlock extends JsonDataLocation {

	public JsonDataLocationBlock(Location location){
		this(null,location);
	}

	public JsonDataLocationBlock(String name){
		this(name,null);
	}

	public JsonDataLocationBlock(String name,Location location){
		super(name,location);
		if(location != null){
			this.getLocation().setX(location.getBlockX());
			this.getLocation().setY(location.getBlockY());
			this.getLocation().setZ(location.getBlockZ());
		}
	}

	public JsonDataLocationBlock(JsonElement element){
		super(element);
	}

	@Override
	public JsonObject getData(){
		JsonObject json = new JsonObject();
		if(this.getLocation() != null){
			json.addProperty("world",this.getLocation().getWorld().getName());
			json.addProperty("x",this.round(this.getLocation().getX(),0));
			json.addProperty("y",this.round(this.getLocation().getY(),0));
			json.addProperty("z",this.round(this.getLocation().getZ(),0));
			json.addProperty("yaw",0);
			json.addProperty("pitch",0);
		}
		return json;
	}
}