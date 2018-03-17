package realcraft.bungee.skins;

public class Skin {

	private String name;
	private String uuid;
	private String value;
	private String signature;

	public Skin(String name,String uuid,String value,String signature){
		this.name = name;
		this.uuid = uuid;
		this.value = value;
		this.signature = signature;
	}

	public String getName(){
		return name;
	}

	public String getUuid(){
		return uuid;
	}

	public String getValue(){
		return value;
	}

	public String getSignature(){
		return signature;
	}
}