package realcraft.bukkit.mapmanager.events;

import realcraft.bukkit.mapmanager.map.Map;

public class MapRegionLoadEvent extends MapEvent {

	private Map map;

	public MapRegionLoadEvent(Map map){
		this.map = map;
	}

	public Map getMap(){
		return map;
	}
}