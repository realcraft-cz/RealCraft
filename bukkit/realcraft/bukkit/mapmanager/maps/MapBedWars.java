package realcraft.bukkit.mapmanager.maps;

import org.bukkit.ChatColor;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapRenderer;
import realcraft.bukkit.mapmanager.map.MapRenderer.MapRendererLocation;
import realcraft.bukkit.mapmanager.map.MapRenderer.MapRendererLocationDirection;
import realcraft.bukkit.mapmanager.map.MapScoreboard;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.*;
import realcraft.share.users.User;

public class MapBedWars extends Map {

	private MapDataMap<MapDataLocation> spawns = new MapDataMap<>("spawns",MapDataLocation.class,4,4);
	private MapDataMap<MapDataLocation> beds = new MapDataMap<>("beds",MapDataLocation.class,4,4);
	private MapDataList<MapDataLocation> traders = new MapDataList<>("traders",MapDataLocation.class,4,4);
	private MapDataList<MapDataLocation> bronze = new MapDataList<>("bronze",MapDataLocation.class,4,8);
	private MapDataList<MapDataLocation> iron = new MapDataList<>("iron",MapDataLocation.class,4,8);
	private MapDataList<MapDataLocation> gold = new MapDataList<>("gold",MapDataLocation.class,4,5);

	public MapBedWars(int id){
		super(id);
	}

	public MapBedWars(User user){
		super(user,MapType.BEDWARS);
	}

	public MapDataMap<MapDataLocation> getSpawns(){
		return spawns;
	}

	public MapDataMap<MapDataLocation> getBeds(){
		return beds;
	}

	public MapDataList<MapDataLocation> getTraders(){
		return traders;
	}

	public MapDataList<MapDataLocation> getBronze(){
		return bronze;
	}

	public MapDataList<MapDataLocation> getSilver(){
		return iron;
	}

	public MapDataList<MapDataLocation> getGold(){
		return gold;
	}

	@Override
	public MapData getData(){
		MapData data = new MapData();
		data.addProperty(spawns);
		data.addProperty(beds);
		data.addProperty(traders);
		data.addProperty(bronze);
		data.addProperty(iron);
		data.addProperty(gold);
		return data;
	}

	@Override
	public void loadData(MapData data){
		spawns.loadData(data);
		beds.loadData(data);
		traders.loadData(data);
		bronze.loadData(data);
		iron.loadData(data);
		gold.loadData(data);
	}

	@Override
	public void updateScoreboard(MapScoreboard scoreboard){
		scoreboard.addLine("§fSpawns: "+spawns.getValidColor()+spawns.size());
		scoreboard.addLine("§fBeds: "+beds.getValidColor()+beds.size());
		scoreboard.addLine("§fTraders: "+traders.getValidColor()+traders.size());
		scoreboard.addLine("");
		scoreboard.addLine("§fBronze: "+bronze.getValidColor()+bronze.size());
		scoreboard.addLine("§fSilver: "+iron.getValidColor()+iron.size());
		scoreboard.addLine("§fGold: "+gold.getValidColor()+gold.size());
	}

	@Override
	public void updateRenderer(MapRenderer renderer){
		for(java.util.Map.Entry<String,MapDataLocation> entry : spawns.getValues().entrySet()){
			renderer.addEntry(new MapRendererLocationDirection(entry.getValue(),ChatColor.YELLOW+spawns.getName().toUpperCase(),MapTeam.getByName(entry.getKey()).getColor()+entry.getKey().toUpperCase()));
		}
		for(java.util.Map.Entry<String,MapDataLocation> entry : beds.getValues().entrySet()){
			renderer.addEntry(new MapRendererLocation(entry.getValue(),ChatColor.LIGHT_PURPLE+beds.getName().toUpperCase(),MapTeam.getByName(entry.getKey()).getColor()+entry.getKey().toUpperCase()));
		}
		for(MapDataLocation location : traders.getValues()){
			renderer.addEntry(new MapRendererLocationDirection(location,ChatColor.WHITE+traders.getName().toUpperCase()));
		}
		for(MapDataLocation location : bronze.getValues()){
			renderer.addEntry(new MapRendererLocation(location,ChatColor.DARK_RED+bronze.getName().toUpperCase()));
		}
		for(MapDataLocation location : iron.getValues()){
			renderer.addEntry(new MapRendererLocation(location,ChatColor.GRAY+iron.getName().toUpperCase()));
		}
		for(MapDataLocation location : gold.getValues()){
			renderer.addEntry(new MapRendererLocation(location,ChatColor.GOLD+gold.getName().toUpperCase()));
		}
	}

	@Override
	public boolean isValid(){
		return (spawns.isValid() && beds.isValid() && traders.isValid() && bronze.isValid() && iron.isValid() && gold.isValid());
	}

	private enum MapTeam {
		RED, BLUE, YELLOW, GREEN;

		public static MapTeam getByName(String name){
			return MapTeam.valueOf(name.toUpperCase());
		}

		public String toString(){
			return this.name().toLowerCase();
		}

		public ChatColor getColor(){
			switch(this){
				case RED: return ChatColor.RED;
				case BLUE: return ChatColor.BLUE;
				case YELLOW: return ChatColor.YELLOW;
				case GREEN: return ChatColor.GREEN;
			}
			return ChatColor.WHITE;
		}
	}
}