package realcraft.bukkit.mapmanager.map.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class MapDataInteger extends MapDataEntry {

	private int value;

	public MapDataInteger(int value){
		this(null,value);
	}

	public MapDataInteger(String name){
		this(name,0);
	}

	public MapDataInteger(String name,int value){
		super(name);
		this.value = value;
	}

	public MapDataInteger(JsonElement element){
		value = element.getAsInt();
	}

	public void setValue(int value){
		this.value = value;
	}

	public int getValue(){
		return value;
	}

	@Override
	public JsonPrimitive getData(){
		return new JsonPrimitive(value);
	}

	@Override
	public void loadData(MapData data){
		if(data.containsKey(this.getName())){
			MapDataInteger tmp = new MapDataInteger(data.getElement(this.getName()));
			value = tmp.getValue();
		}
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof MapDataInteger){
			MapDataInteger toCompare = (MapDataInteger) object;
			return (toCompare.getValue() == this.getValue());
		}
		return false;
	}
}