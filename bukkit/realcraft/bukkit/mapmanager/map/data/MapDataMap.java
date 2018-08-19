package realcraft.bukkit.mapmanager.map.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;

public class MapDataMap<E extends MapDataEntry> extends MapDataEntry {

	private Class<E> clazz;
	private HashMap<String,E> values = new HashMap<>();

	public MapDataMap(String name){
		super(name);
		this.clazz = (Class<E>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
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

	public JsonElement getData(){
		JsonObject object  = new JsonObject();
		for(java.util.Map.Entry<String,E> entry : values.entrySet()){
			object.add(entry.getKey(),entry.getValue().getData());
		}
		return object;
	}

	public void loadData(MapData data){
		JsonObject object = data.getElement(this.getName()).getAsJsonObject();
		for(java.util.Map.Entry<String,JsonElement> entry : object.entrySet()){
			try {
				values.put(entry.getKey(),clazz.getConstructor(JsonElement.class).newInstance(entry.getValue()));
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e){
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof MapDataMap){
			MapDataMap toCompare = (MapDataMap) object;
			return toCompare.getName().equalsIgnoreCase(this.getName());
		}
		return false;
	}
}