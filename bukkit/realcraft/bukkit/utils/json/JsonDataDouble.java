package realcraft.bukkit.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class JsonDataDouble extends JsonDataEntry {

	private double value;

	public JsonDataDouble(double value){
		this(null,value);
	}

	public JsonDataDouble(String name){
		this(name,0);
	}

	public JsonDataDouble(String name,double value){
		super(name);
		this.value = value;
	}

	public JsonDataDouble(JsonElement element){
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
	public void loadData(JsonData data){
		if(data.containsKey(this.getName())){
			JsonDataDouble tmp = new JsonDataDouble(data.getElement(this.getName()));
			value = tmp.getValue();
		}
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof JsonDataDouble){
			JsonDataDouble toCompare = (JsonDataDouble) object;
			return (toCompare.getValue() == this.getValue());
		}
		return false;
	}

	@Override
	public String toString() {
		return String.valueOf(this.getValue());
	}
}