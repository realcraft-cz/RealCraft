package realcraft.bukkit.mapmanager.maps;

import org.bukkit.ChatColor;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapRenderer;
import realcraft.bukkit.mapmanager.map.MapRenderer.MapRendererLocationDirection;
import realcraft.bukkit.mapmanager.map.MapScoreboard;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.MapData;
import realcraft.bukkit.mapmanager.map.data.MapDataLocation;
import realcraft.bukkit.mapmanager.map.data.MapDataMap;
import realcraft.share.users.User;

public class MapHidenSeek extends Map {

	private MapDataMap<MapDataLocation> spawns = new MapDataMap<>("spawns",MapDataLocation.class,2,2);

	public MapHidenSeek(int id){
		super(id);
	}

	public MapHidenSeek(User user){
		super(user,MapType.HIDENSEEK);
	}

	public MapDataMap<MapDataLocation> getSpawns(){
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
		for(java.util.Map.Entry<String,MapDataLocation> entry : spawns.getValues().entrySet()){
			renderer.addEntry(new MapRendererLocationDirection(entry.getValue(),ChatColor.YELLOW+spawns.getName().toUpperCase(),MapTeam.getByName(entry.getKey()).getColor()+entry.getKey().toUpperCase()));
		}
	}

	@Override
	public boolean isValid(){
		return (spawns.isValid());
	}

	private enum MapTeam {
		HIDERS, SEEKERS;

		public static MapTeam getByName(String name){
			return MapTeam.valueOf(name.toUpperCase());
		}

		public String toString(){
			return this.name().toLowerCase();
		}

		public ChatColor getColor(){
			switch(this){
				case HIDERS: return ChatColor.AQUA;
				case SEEKERS: return ChatColor.RED;
			}
			return ChatColor.WHITE;
		}
	}
}