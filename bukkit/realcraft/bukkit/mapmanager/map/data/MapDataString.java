package realcraft.bukkit.mapmanager.map.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class MapDataString extends MapDataEntry {

	private String value;

	public MapDataString(String name){
		this(name,"");
	}

	public MapDataString(String name,String value){
		super(name);
		this.value = value;
	}

	public MapDataString(JsonElement element){
		value = element.getAsString();
	}

	public void setValue(String value){
		this.value = value;
	}

	public String getValue(){
		return value;
	}

	@Override
	public JsonPrimitive getData(){
		return new JsonPrimitive(value);
	}

	@Override
	public void loadData(MapData data){
		if(data.containsKey(this.getName())){
			MapDataString tmp = new MapDataString(data.getElement(this.getName()));
			value = tmp.getValue();
		}
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof MapDataString){
			MapDataString toCompare = (MapDataString) object;
			return (toCompare.getValue().equals(this.getValue()));
		}
		return false;
	}
}