package realcraft.bukkit.mapmanager.map;

import org.bukkit.ChatColor;

public enum MapState {

	BUILD, READY;

	public String toString(){
		return this.name().toLowerCase();
	}

	public int getId(){
		switch(this){
			case BUILD: return 0;
			case READY: return 1;
		}
		return 0;
	}

	public String getName(){
		switch(this){
			case BUILD: return "BUILD";
			case READY: return "READY";
		}
		return "unknown";
	}

	public String getColor(){
		switch(this){
			case BUILD: return ChatColor.DARK_PURPLE.toString();
			case READY: return ChatColor.GREEN.toString();
		}
		return ChatColor.WHITE.toString();
	}

	public static MapState getById(int id){
		for(MapState type : MapState.values()){
			if(type.getId() == id) return type;
		}
		return null;
	}
}