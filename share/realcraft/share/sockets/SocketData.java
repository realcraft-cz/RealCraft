package realcraft.share.sockets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public abstract class SocketData {

	private JsonObject objects;

	public SocketData(String channel,String server){
		objects = new JsonObject();
		objects.addProperty("_channel",channel);
		objects.addProperty("_server",server);
	}

	public SocketData(String channel,String server,String data){
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

	public void setIntList(String key,List<Integer> list){
		JsonArray array = new JsonArray();
		for(Integer element : list) array.add(element);
		objects.add(key,array);
	}

	public void setStringList(String key,List<String> list){
		JsonArray array = new JsonArray();
		for(String element : list) array.add(element);
		objects.add(key,array);
	}

	public String getServer(){
		return (objects.has("_server") ? objects.get("_server").getAsString() : null);
	}

	public String getChannel(){
		return (objects.has("_channel") ? objects.get("_channel").getAsString() : null);
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

	public ArrayList<Integer> getIntList(String key){
		if(objects.has(key)){
			ArrayList<Integer> list = new ArrayList<Integer>();
			JsonArray array = objects.get(key).getAsJsonArray();
			for(int i=0;i<array.size();i++){
				list.add(array.get(i).getAsInt());
			}
			return list;
		}
		return null;
	}

	public ArrayList<String> getStringList(String key){
		if(objects.has(key)){
			ArrayList<String> list = new ArrayList<String>();
			JsonArray array = objects.get(key).getAsJsonArray();
			for(int i=0;i<array.size();i++){
				list.add(array.get(i).getAsString());
			}
			return list;
		}
		return null;
	}

	@Override
	public String toString(){
		return objects.toString();
	}
}