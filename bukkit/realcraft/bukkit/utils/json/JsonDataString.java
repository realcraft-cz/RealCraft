package realcraft.bukkit.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class JsonDataString extends JsonDataEntry {

	private String value;

	public JsonDataString(String name){
		this(name,"");
	}

	public JsonDataString(String name,String value){
		super(name);
		this.value = value;
	}

	public JsonDataString(JsonElement element){
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
	public void loadData(JsonData data){
		if(data.containsKey(this.getName())){
			JsonDataString tmp = new JsonDataString(data.getElement(this.getName()));
			this.setValue(tmp.getValue());
		}
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof JsonDataString){
			JsonDataString toCompare = (JsonDataString) object;
			return (toCompare.getValue().equals(this.getValue()));
		}
		return false;
	}

	@Override
	public String toString() {
		return this.getValue();
	}
}