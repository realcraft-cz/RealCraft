package realcraft.bukkit.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class JsonDataInteger extends JsonDataEntry {

	private int value;

	public JsonDataInteger(int value){
		this(null,value);
	}

	public JsonDataInteger(String name){
		this(name,0);
	}

	public JsonDataInteger(String name,int value){
		super(name);
		this.value = value;
	}

	public JsonDataInteger(JsonElement element){
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
	public void loadData(JsonData data){
		if(data.containsKey(this.getName())){
			JsonDataInteger tmp = new JsonDataInteger(data.getElement(this.getName()));
			value = tmp.getValue();
		}
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof JsonDataInteger){
			JsonDataInteger toCompare = (JsonDataInteger) object;
			return (toCompare.getValue() == this.getValue());
		}
		return false;
	}

	@Override
	public String toString() {
		return String.valueOf(this.getValue());
	}
}