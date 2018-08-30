package realcraft.bukkit.mapmanager.map.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class MapDataList<E extends MapDataEntry> extends MapDataBounds {

	private Class<E> clazz;
	private ArrayList<E> values = new ArrayList<>();

	public MapDataList(String name,Class<E> clazz){
		this(name,clazz,0,Integer.MAX_VALUE);
	}

	public MapDataList(String name,Class<E> clazz,int min,int max){
		super(name,min,max);
		this.clazz = clazz;
	}

	public ArrayList<E> getValues(){
		return values;
	}

	public void add(E object){
		values.remove(object);
		values.add(object);
	}

	public void remove(E object){
		values.remove(object);
	}

	public void clear(){
		values.clear();
	}

	@Override
	public int size(){
		return values.size();
	}

	@Override
	public JsonArray getData(){
		JsonArray array = new JsonArray();
		for(E object : values) array.add(object.getData());
		return array;
	}

	@Override
	public void loadData(MapData data){
		if(data.containsKey(this.getName())){
			JsonArray array = data.getElement(this.getName()).getAsJsonArray();
			for(JsonElement element : array){
				try {
					values.add(clazz.getConstructor(JsonElement.class).newInstance(element));
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e){
					e.printStackTrace();
				}
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