package realcraft.bukkit.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class JsonDataBoolean extends JsonDataEntry {

	private boolean value;

	public JsonDataBoolean(boolean value){
		this(null,value);
	}

	public JsonDataBoolean(String name){
		this(name,false);
	}

	public JsonDataBoolean(String name,boolean value){
		super(name);
		this.value = value;
	}

	public JsonDataBoolean(JsonElement element){
		value = element.getAsBoolean();
	}

	public void setValue(boolean value){
		this.value = value;
	}

	public boolean getValue(){
		return value;
	}

	@Override
	public JsonPrimitive getData(){
		return new JsonPrimitive(value);
	}

	@Override
	public void loadData(JsonData data){
		if(data.containsKey(this.getName())){
			JsonDataBoolean tmp = new JsonDataBoolean(data.getElement(this.getName()));
			value = tmp.getValue();
		}
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof JsonDataBoolean){
			JsonDataBoolean toCompare = (JsonDataBoolean) object;
			return (toCompare.getValue() == this.getValue());
		}
		return false;
	}
}