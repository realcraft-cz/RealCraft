package realcraft.bukkit.mapmanager.map.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

public class MapDataList<E extends MapDataEntry> extends MapDataEntry {

	private Class<E> clazz;
	private ArrayList<E> values = new ArrayList<>();

	public MapDataList(String name){
		super(name);
		this.clazz = (Class<E>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public ArrayList<E> getValues(){
		return values;
	}

	public void add(E object){
		if(values.contains(object)) values.remove(object);
		values.add(object);
	}

	public void remove(E object){
		values.remove(object);
	}

	public void clear(){
		values.clear();
	}

	public JsonArray getData(){
		JsonArray array  = new JsonArray();
		for(E object : values) array.add(object.getData());
		return array;
	}

	public void loadData(MapData data){
		JsonArray array = data.getElement(this.getName()).getAsJsonArray();
		for(JsonElement element : array){
			try {
				values.add(clazz.getConstructor(JsonElement.class).newInstance(element));
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e){
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof MapDataList){
			MapDataList toCompare = (MapDataList) object;
			return toCompare.getName().equalsIgnoreCase(this.getName());
		}
		return false;
	}
}