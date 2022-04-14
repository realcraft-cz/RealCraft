package realcraft.bukkit.mapmanager;

import org.bukkit.*;
import org.bukkit.entity.Player;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.mapmanager.commands.MapCommands;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.maps.*;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class MapManager {

	public static final String MAPS = "minigames_maps";
	private static final String PREFIX = "§6[Maps]§r ";

	private static World world;
	private static HashMap<User,MapPlayer> players = new HashMap<>();
	private static ArrayList<Map> maps = new ArrayList<>();

	public MapManager(){
		MapManager._createWorld();
		new MapListeners();
		new MapCommands();
		MapManager.loadMaps();
	}

	protected static void _createWorld() {
		WorldCreator creator = new WorldCreator("world_maps");
		creator.type(WorldType.FLAT);
		creator.environment(World.Environment.NORMAL);
		creator.generator("VoidGenerator");

		world = Bukkit.getServer().createWorld(creator);
		if (world == null) {
			throw new RuntimeException("World world_maps failed to create");
		}

		world.setKeepSpawnInMemory(false);
		world.setDifficulty(Difficulty.HARD);
		world.setPVP(true);
		world.setAutoSave(true);
		world.setFullTime(1000);
		world.setMonsterSpawnLimit(0);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false);
		world.setGameRule(GameRule.DO_WEATHER_CYCLE,false);
		world.setGameRule(GameRule.DO_MOB_SPAWNING,false);
		world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,false);
	}

	public static World getWorld(){
		return world;
	}

	public static MapPlayer getMapPlayer(User user){
		if(!players.containsKey(user)) players.put(user,new MapPlayer(user));
		return players.get(user);
	}

	public static MapPlayer getMapPlayer(Player player){
		return MapManager.getMapPlayer(Users.getUser(player));
	}

	public static ArrayList<Map> getMaps(){
		return maps;
	}

	public static ArrayList<Map> getMaps(MapType type){
		ArrayList<Map> maps = new ArrayList<>();
		for(Map map : MapManager.getMaps()){
			if(map.getType() == type) maps.add(map);
		}
		return maps;
	}

	public static ArrayList<Map> getSortedMaps(MapType type){
		ArrayList<Map> maps = MapManager.getMaps(type);
		maps.sort(new Comparator<Map>(){
			@Override
			public int compare(Map map1,Map map2){
				return Long.compare(map1.getCreated(),map2.getCreated());
			}
		});
		return maps;
	}

	public static Map getMap(int id){
		for(Map map : maps){
			if(map.getId() == id) return map;
		}
		return null;
	}

	public static Map getMap(String name,MapType type){
		for(Map map : maps){
			if(map.getType() == type && map.getName().equalsIgnoreCase(name)) return map;
		}
		return null;
	}

	public static Map getMap(Location location){
		for(Map map : maps){
			if(map.getRegion().isLocationInside(location)) return map;
		}
		return null;
	}

	public static Map createMap(MapPlayer mPlayer,MapType type){
		Map map = MapManager.createMapInstance(mPlayer,type);
		if(map != null){
			map.create();
			maps.add(map);
		}
		return map;
	}

	private static Map createMapInstance(MapPlayer mPlayer,MapType type){
		switch(type){
			case BEDWARS: return new MapBedWars(mPlayer.getUser());
			case BLOCKPARTY: return new MapBlockParty(mPlayer.getUser());
			case DOMINATE: return new MapDominate(mPlayer.getUser());
			case HIDENSEEK: return new MapHidenSeek(mPlayer.getUser());
			case PAINTBALL: return new MapPaintball(mPlayer.getUser());
			case RACES: return new MapRaces(mPlayer.getUser());
			case RAGEMODE: return new MapRageMode(mPlayer.getUser());
		}
		return null;
	}

	private static Map getMapInstance(int id,MapType type){
		switch(type){
			case BEDWARS: return new MapBedWars(id);
			case BLOCKPARTY: return new MapBlockParty(id);
			case DOMINATE: return new MapDominate(id);
			case HIDENSEEK: return new MapHidenSeek(id);
			case PAINTBALL: return new MapPaintball(id);
			case RACES: return new MapRaces(id);
			case RAGEMODE: return new MapRageMode(id);
		}
		return null;
	}

	private static void loadMaps(){
		ResultSet rs = DB.query("SELECT * FROM "+MAPS+" ORDER BY map_id ASC");
		try {
			while(rs.next()){
				int id = rs.getInt("map_id");
				MapType type = MapType.getById(rs.getInt("map_type"));
				if(type != null){
					Map map = MapManager.getMapInstance(id,type);
					if(map != null){
						map.load();
						maps.add(map);
					}
				}
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	public static void sendMessage(String message){
		Bukkit.broadcastMessage(PREFIX+message);
	}

	public static void sendMessage(Player player,String message){
		player.sendMessage(PREFIX+message);
	}

	public static void sendMessage(MapPlayer mPlayer,String message){
		MapManager.sendMessage(mPlayer.getPlayer(),message);
	}
}