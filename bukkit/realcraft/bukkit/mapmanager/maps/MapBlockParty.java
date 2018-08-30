package realcraft.bukkit.mapmanager.maps;

import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapRenderer;
import realcraft.bukkit.mapmanager.map.MapScoreboard;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.MapData;
import realcraft.share.users.User;

public class MapBlockParty extends Map {

	public MapBlockParty(int id){
		super(id);
	}

	public MapBlockParty(User user){
		super(user,MapType.BLOCKPARTY);
	}

	@Override
	public MapData getData(){
		return new MapData();
	}

	@Override
	public void loadData(MapData data){
	}

	@Override
	public void updateScoreboard(MapScoreboard scoreboard){
	}

	@Override
	public void updateRenderer(MapRenderer renderer){
	}

	@Override
	public boolean isValid(){
		return true;
	}
}