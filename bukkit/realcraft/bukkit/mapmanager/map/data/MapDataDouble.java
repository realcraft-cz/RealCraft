package realcraft.bukkit.mapmanager.map.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class MapDataDouble extends MapDataEntry {

	private double value;

	public MapDataDouble(double value){
		this(null,value);
	}

	public MapDataDouble(String name){
		this(name,0);
	}

	public MapDataDouble(String name,double value){
		super(name);
		this.value = value;
	}

	public MapDataDouble(JsonElement element){
		value = element.getAsDouble();
	}

	public void setValue(double value){
		this.value = value;
	}

	public double getValue(){
		return value;
	}

	@Override
	public JsonPrimitive getData(){
		return new JsonPrimitive(value);
	}

	@Override
	public void loadData(MapData data){
		if(data.containsKey(this.getName())){
			MapDataDouble tmp = new MapDataDouble(data.getElement(this.getName()));
			value = tmp.getValue();
		}
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof MapDataDouble){
			MapDataDouble toCompare = (MapDataDouble) object;
			return (toCompare.getValue() == this.getValue());
		}
		return false;
	}
}