package realcraft.bukkit.mapmanager.maps;

import org.bukkit.ChatColor;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapRenderer;
import realcraft.bukkit.mapmanager.map.MapRenderer.MapRendererLocationArea;
import realcraft.bukkit.mapmanager.map.MapRenderer.MapRendererLocationDirection;
import realcraft.bukkit.mapmanager.map.MapScoreboard;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.MapData;
import realcraft.bukkit.mapmanager.map.data.MapDataList;
import realcraft.bukkit.mapmanager.map.data.MapDataLocation;
import realcraft.bukkit.mapmanager.map.data.MapDataLocationArea;
import realcraft.share.users.User;

public class MapRaces extends Map {

	private MapRaceType type = MapRaceType.RUN;
	private int rounds = 3;
	private MapDataList<MapDataLocation> spawns = new MapDataList<>("spawns",MapDataLocation.class,20,20);
	private MapDataList<MapDataLocationArea> checkpoints = new MapDataList<>("checkpoints",MapDataLocationArea.class,2,Integer.MAX_VALUE);

	public MapRaces(int id){
		super(id);
	}

	public MapRaces(User user){
		super(user,MapType.RACES);
	}

	public int getRounds(){
		return rounds;
	}

	public void setRounds(int rounds){
		this.rounds = rounds;
	}

	public MapDataList<MapDataLocation> getSpawns(){
		return spawns;
	}

	public MapDataList<MapDataLocationArea> getCheckpoints(){
		return checkpoints;
	}

	@Override
	public MapData getData(){
		MapData data = new MapData();
		data.addProperty(spawns);
		data.addProperty(checkpoints);
		return data;
	}

	@Override
	public void loadData(MapData data){
		spawns.loadData(data);
		checkpoints.loadData(data);
	}

	@Override
	public void updateScoreboard(MapScoreboard scoreboard){
		scoreboard.addLine("§fRounds: §f"+rounds);
		scoreboard.addLine("§fType: §f"+type.toString());
		scoreboard.addLine("");
		scoreboard.addLine("§fSpawns: "+spawns.getValidColor()+spawns.size());
		scoreboard.addLine("§fCheckpoints: "+checkpoints.getValidColor()+checkpoints.size());
	}

	@Override
	public void updateRenderer(MapRenderer renderer){
		for(MapDataLocation location : spawns.getValues()){
			renderer.addEntry(new MapRendererLocationDirection(location,ChatColor.YELLOW+spawns.getName().toUpperCase()));
		}
		for(MapDataLocationArea area : checkpoints.getValues()){
			renderer.addEntry(new MapRendererLocationArea(area,ChatColor.AQUA+checkpoints.getName().toUpperCase()));
		}
	}

	@Override
	public boolean isValid(){
		return (spawns.isValid() && checkpoints.isValid());
	}

	private enum MapRaceType {
		RUN, HORSE, BOAT;

		public static MapRaceType getByName(String name){
			return MapRaceType.valueOf(name.toUpperCase());
		}

		public String toString(){
			return this.name().toLowerCase();
		}
	}
}