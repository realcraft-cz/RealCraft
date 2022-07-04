package realcraft.bukkit.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class JsonDataMap<E extends JsonDataEntry> extends JsonDataEntry {

	private Class<E> clazz;
	private HashMap<String,E> values = new HashMap<>();

	public JsonDataMap(String name,Class<E> clazz){
		super(name);
		this.clazz = clazz;
	}

	public HashMap<String,E> getValues(){
		return values;
	}

	public void add(String key,E object){
		values.put(key,object);
	}

	public void remove(String key){
		values.remove(key);
	}

	public void clear(){
		values.clear();
	}

	public int size(){
		return values.size();
	}

	public void putAll(HashMap<String,E> hashMap){
		values.putAll(hashMap);
	}

	@Override
	public JsonObject getData(){
		JsonObject object = new JsonObject();
		for(java.util.Map.Entry<String,E> entry : values.entrySet()){
			object.add(entry.getKey(),entry.getValue().getData());
		}
		return object;
	}

	@Override
	public void loadData(JsonData data){
		if(data.containsKey(this.getName())){
			JsonObject object = data.getElement(this.getName()).getAsJsonObject();
			for(java.util.Map.Entry<String,JsonElement> entry : object.entrySet()){
				try {
					values.put(entry.getKey(),clazz.getConstructor(JsonElement.class).newInstance(entry.getValue()));
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e){
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof JsonDataMap){
			JsonDataMap toCompare = (JsonDataMap) object;
			return toCompare.getName().equalsIgnoreCase(this.getName());
		}
		return false;
	}
}