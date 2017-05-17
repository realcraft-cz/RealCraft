package com.parkour.utils;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class JsonUtil {

	public static Location getJSONLocation(String value,World world){
		JsonElement element = new JsonParser().parse(value);
		if(!element.isJsonObject()) return null;
		JsonObject json = element.getAsJsonObject();
		double x = json.get("x").getAsDouble();
		double y = json.get("y").getAsDouble();
		double z = json.get("z").getAsDouble();
		float yaw = json.get("yaw").getAsFloat();
		float pitch = json.get("pitch").getAsFloat();
		return new Location(world,x,y,z,yaw,pitch);
	}

	public static ArrayList<Location> getJSONLocationList(String value,World world){
		ArrayList<Location> locations = new ArrayList<Location>();
		JsonElement element = new JsonParser().parse(value);
		if(element.isJsonArray()){
			JsonArray array = element.getAsJsonArray();
			for(int i=0;i<array.size();i++){
				JsonObject json = array.get(i).getAsJsonObject();
				double x = json.get("x").getAsDouble();
				double y = json.get("y").getAsDouble();
				double z = json.get("z").getAsDouble();
				float yaw = json.get("yaw").getAsFloat();
				float pitch = json.get("pitch").getAsFloat();
				locations.add(new Location(world,x,y,z,yaw,pitch));
			}
		}
		return locations;
	}

	public static String toJSONLocation(Location location){
		if(location == null) return "";
		JsonObject json = new JsonObject();
		json.addProperty("x",location.getX());
		json.addProperty("y",location.getY());
		json.addProperty("z",location.getZ());
		json.addProperty("yaw",location.getYaw());
		json.addProperty("pitch",location.getPitch());
		return json.toString();
	}

	public static String toJSONLocationList(ArrayList<Location> locations){
		JsonArray array = new JsonArray();
		for(Location location : locations){
			JsonObject json = new JsonObject();
			json.addProperty("x",location.getX());
			json.addProperty("y",location.getY());
			json.addProperty("z",location.getZ());
			json.addProperty("yaw",location.getYaw());
			json.addProperty("pitch",location.getPitch());
			array.add(json);
		}
		return array.toString();
	}

	public static ArrayList<Integer> getJSONIntegerList(String value){
		ArrayList<Integer> integers = new ArrayList<Integer>();
		JsonElement element = new JsonParser().parse(value);
		if(element.isJsonArray()){
			JsonArray array = element.getAsJsonArray();
			for(int i=0;i<array.size();i++){
				integers.add(array.get(i).getAsInt());
			}
		}
		return integers;
	}

	public static String toJSONIntegerList(ArrayList<Integer> integers){
		JsonArray array = new JsonArray();
		for(Integer integer : integers){
			JsonPrimitive json = new JsonPrimitive(integer);
			array.add(json);
		}
		return array.toString();
	}
}