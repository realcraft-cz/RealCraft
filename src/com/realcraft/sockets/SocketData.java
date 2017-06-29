package com.realcraft.sockets;

import org.bukkit.Bukkit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SocketData {

	private JsonObject objects;

	public SocketData(String channel){
		objects = new JsonObject();
		objects.addProperty("channel",channel);
		objects.addProperty("server",Bukkit.getServer().getServerName().toUpperCase());
	}

	public SocketData(String channel,String data){
		JsonElement element = new JsonParser().parse(data);
		if(element.isJsonObject()) objects = element.getAsJsonObject();
	}

	public void setInt(String key,int data){
		objects.addProperty(key,data);
	}

	public void setLong(String key,long data){
		objects.addProperty(key,data);
	}

	public void setDouble(String key,double data){
		objects.addProperty(key,data);
	}

	public void setBoolean(String key,boolean data){
		objects.addProperty(key,data);
	}

	public void setString(String key,String data){
		objects.addProperty(key,data);
	}

	public String getServer(){
		return (objects.has("server") ? objects.get("server").getAsString() : null);
	}

	public String getChannel(){
		return (objects.has("channel") ? objects.get("channel").getAsString() : null);
	}

	public int getInt(String key){
		return (objects.has(key) ? objects.get(key).getAsInt() : 0);
	}

	public long getLong(String key){
		return (objects.has(key) ? objects.get(key).getAsLong() : 0);
	}

	public double getDouble(String key){
		return (objects.has(key) ? objects.get(key).getAsDouble() : 0);
	}

	public boolean getBoolean(String key){
		return (objects.has(key) ? objects.get(key).getAsBoolean() : false);
	}

	public String getString(String key){
		return (objects.has(key) ? objects.get(key).getAsString() : null);
	}

	@Override
	public String toString(){
		return objects.toString();
	}
}