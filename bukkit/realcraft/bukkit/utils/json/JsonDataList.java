package realcraft.bukkit.utils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class JsonDataList<E extends JsonDataEntry> extends JsonDataEntry {

	private Class<E> clazz;
	private ArrayList<E> values = new ArrayList<>();

	public JsonDataList(String name,Class<E> clazz){
		super(name);
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
	public void loadData(JsonData data){
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
		if(object instanceof JsonDataList){
			JsonDataList toCompare = (JsonDataList) object;
			return toCompare.getName().equalsIgnoreCase(this.getName());
		}
		return false;
	}
}