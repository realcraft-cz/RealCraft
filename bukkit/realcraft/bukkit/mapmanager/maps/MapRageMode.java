package realcraft.bukkit.mapmanager.maps;

import org.bukkit.ChatColor;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapRenderer;
import realcraft.bukkit.mapmanager.map.MapRenderer.MapRendererLocationDirection;
import realcraft.bukkit.mapmanager.map.MapScoreboard;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.MapData;
import realcraft.bukkit.mapmanager.map.data.MapDataList;
import realcraft.bukkit.mapmanager.map.data.MapDataLocation;
import realcraft.share.users.User;

public class MapRageMode extends Map {

	private MapDataList<MapDataLocation> spawns = new MapDataList<>("spawns",MapDataLocation.class,20,Integer.MAX_VALUE);

	public MapRageMode(int id){
		super(id);
	}

	public MapRageMode(User user){
		super(user,MapType.RAGEMODE);
	}

	public MapDataList<MapDataLocation> getSpawns(){
		return spawns;
	}

	@Override
	public MapData getData(){
		MapData data = new MapData();
		data.addProperty(spawns);
		return data;
	}

	@Override
	public void loadData(MapData data){
		spawns.loadData(data);
	}

	@Override
	public void updateScoreboard(MapScoreboard scoreboard){
		scoreboard.addLine("§fSpawns: "+spawns.getValidColor()+spawns.size());
	}

	@Override
	public void updateRenderer(MapRenderer renderer){
		for(MapDataLocation location : spawns.getValues()){
			renderer.addEntry(new MapRendererLocationDirection(location,ChatColor.YELLOW+spawns.getName().toUpperCase()));
		}
	}

	@Override
	public boolean isValid(){
		return (spawns.isValid());
	}
}