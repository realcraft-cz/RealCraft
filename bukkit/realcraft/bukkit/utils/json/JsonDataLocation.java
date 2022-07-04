package realcraft.bukkit.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class JsonDataLocation extends JsonDataEntry {

	private Location location;

	public JsonDataLocation(Location location){
		this(null,location);
	}

	public JsonDataLocation(String name){
		this(name,null);
	}

	public JsonDataLocation(String name,Location location){
		super(name);
		this.location = location;
	}

	public JsonDataLocation(JsonElement element){
		JsonObject json = element.getAsJsonObject();
		if(json.has("x")){
			double x = json.get("x").getAsDouble();
			double y = json.get("y").getAsDouble();
			double z = json.get("z").getAsDouble();
			float yaw = json.get("yaw").getAsFloat();
			float pitch = json.get("pitch").getAsFloat();
			this.location = new Location(Bukkit.getWorld(json.get("world").getAsString()),x,y,z,yaw,pitch);
		}
	}

	public Location getLocation(){
		return location;
	}

	public void setLocation(Location location){
		this.location = location;
	}

	@Override
	public JsonObject getData(){
		JsonObject json = new JsonObject();
		if(location != null){
			json.addProperty("world",location.getWorld().getName());
			json.addProperty("x",this.round(location.getX(),1));
			json.addProperty("y",this.round(location.getY(),1));
			json.addProperty("z",this.round(location.getZ(),1));
			json.addProperty("yaw",this.round(location.getYaw(),1));
			json.addProperty("pitch",this.round(location.getPitch(),1));
		}
		return json;
	}

	@Override
	public void loadData(JsonData data){
		if(data.containsKey(this.getName())){
			JsonDataLocation tmp = new JsonDataLocation(data.getElement(this.getName()));
			location = tmp.getLocation();
		}
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof JsonDataLocation){
			JsonDataLocation toCompare = (JsonDataLocation) object;
			return (toCompare.getLocation().getWorld().getName().equals(this.getLocation().getWorld().getName()) &&
					toCompare.getLocation().getBlockX() == this.getLocation().getBlockX() &&
					toCompare.getLocation().getBlockY() == this.getLocation().getBlockY() &&
					toCompare.getLocation().getBlockZ() == this.getLocation().getBlockZ()
			);
		}
		return false;
	}

	protected double round(double amount,int places){
		int factor = (int)Math.pow(10,places);
		return (double)Math.round((amount*factor))/factor;
	}
}