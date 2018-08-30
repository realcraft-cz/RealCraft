package realcraft.bukkit.mapmanager.events;

import realcraft.bukkit.mapmanager.MapPlayer;
import realcraft.bukkit.mapmanager.map.Map;

public class MapPlayerLeaveMapEvent extends MapEvent {

	private MapPlayer mPlayer;
	private Map map;

	public MapPlayerLeaveMapEvent(MapPlayer mPlayer,Map map){
		this.mPlayer = mPlayer;
		this.map = map;
	}

	public MapPlayer getPlayer(){
		return mPlayer;
	}

	public Map getMap(){
		return map;
	}
}