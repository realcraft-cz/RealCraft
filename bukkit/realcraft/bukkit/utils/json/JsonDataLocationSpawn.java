package realcraft.bukkit.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import realcraft.bukkit.utils.LocationUtil;

public class JsonDataLocationSpawn extends JsonDataLocation {

	public JsonDataLocationSpawn(Location location){
		this(null,location);
	}

	public JsonDataLocationSpawn(String name){
		this(name,null);
	}

	public JsonDataLocationSpawn(String name,Location location){
		super(name,location);
		if(location != null){
			this.getLocation().setX(this.roundToHalf(location.getX()));
			this.getLocation().setY(this.roundToHalf(location.getY()));
			this.getLocation().setZ(this.roundToHalf(location.getZ()));
			this.getLocation().setYaw(LocationUtil.faceToYaw(LocationUtil.yawToFace(this.getLocation().getYaw(),true),true));
			this.getLocation().setPitch(LocationUtil.faceToYaw(LocationUtil.yawToFace(this.getLocation().getPitch(),true),true));
		}
	}

	public JsonDataLocationSpawn(JsonElement element){
		super(element);
	}

	@Override
	public void setLocation(Location location){
		super.setLocation(location);
		if(location != null){
			this.getLocation().setX(this.roundToHalf(location.getX()));
			this.getLocation().setY(this.roundToHalf(location.getY()));
			this.getLocation().setZ(this.roundToHalf(location.getZ()));
			this.getLocation().setYaw(LocationUtil.faceToYaw(LocationUtil.yawToFace(this.getLocation().getYaw(),true),true));
			this.getLocation().setPitch(LocationUtil.faceToYaw(LocationUtil.yawToFace(this.getLocation().getPitch(),true),true));
		}
	}

	@Override
	public JsonObject getData(){
		JsonObject json = new JsonObject();
		if(this.getLocation() != null){
			json.addProperty("x",this.roundToHalf(this.getLocation().getX()));
			json.addProperty("y",this.roundToHalf(this.getLocation().getY()));
			json.addProperty("z",this.roundToHalf(this.getLocation().getZ()));
			json.addProperty("yaw",LocationUtil.faceToYaw(LocationUtil.yawToFace(this.getLocation().getYaw(),true),true));
			json.addProperty("pitch",LocationUtil.faceToYaw(LocationUtil.yawToFace(this.getLocation().getPitch(),true),true));
		}
		return json;
	}

	private double roundToHalf(double value){
		return Math.round(value*2)/2.0;
	}
}