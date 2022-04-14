package realcraft.bukkit.mapmanager.map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.mapmanager.MapPlayer;
import realcraft.bukkit.mapmanager.exceptions.MapInvalidNameException;
import realcraft.bukkit.mapmanager.exceptions.MapNameExistsException;
import realcraft.bukkit.mapmanager.map.data.*;
import realcraft.share.users.User;
import realcraft.share.users.UserRank;
import realcraft.share.users.Users;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static realcraft.bukkit.mapmanager.MapManager.MAPS;

public abstract class Map {

	private static final Pattern ALLOWED_NAME_CHARS = Pattern.compile("[a-zA-Z0-9]*");
	private static final Pattern FORBIDDEN_NAME = Pattern.compile("map([0-9]*)");

	private int id;
	private User user;
	private String name;
	private MapType type;
	private MapState state = MapState.BUILD;
	private int created;
	private int updated;
	private byte[] regionData;

	private MapDataInteger time = new MapDataInteger("time",6000);
	private MapDataBiome biome = new MapDataBiome("biome",Biome.FOREST);
	private MapDataEnvironment environment = new MapDataEnvironment("environment",Environment.NORMAL);
	private MapDataLocationSpawn spectator = new MapDataLocationSpawn("spectator");
	private MapDataList<MapDataInteger> trusted = new MapDataList<>("trusted",MapDataInteger.class);

	private MapRegion region = new MapRegion(this);
	private MapScoreboard scoreboard = new MapScoreboard(this);
	private MapRenderer renderer = new MapRenderer(this);

	public Map(int id){
		this.id = id;
	}

	public Map(User user,MapType type){
		this.user = user;
		this.type = type;
		this.name = "null";
	}

	public int getId(){
		return id;
	}

	public User getUser(){
		return user;
	}

	public String getName(){
		return name;
	}

	public void setName(String name) throws MapInvalidNameException, MapNameExistsException	{
		Matcher matcher = ALLOWED_NAME_CHARS.matcher(name);
		if(!matcher.matches() || name.length() > 32) throw new MapInvalidNameException();
		matcher = FORBIDDEN_NAME.matcher(name.toLowerCase());
		if(matcher.matches() && !name.equalsIgnoreCase("map"+this.getId())) throw new MapNameExistsException();
		if(MapManager.getMap(name,this.getType()) != null) throw new MapNameExistsException();
		this.name = name;
		this.save();
	}

	public MapType getType(){
		return type;
	}

	public MapState getState(){
		return state;
	}

	public void setState(MapState state){
		this.state = state;
	}

	public int getCreated(){
		return created;
	}

	public int getUpdated(){
		return updated;
	}

	public byte[] getRegionData(){
		return regionData;
	}

	public MapDataInteger getTime(){
		return time;
	}

	public MapDataBiome getBiome(){
		return biome;
	}

	public MapDataEnvironment getEnvironment(){
		return environment;
	}

	public MapDataLocationSpawn getSpectator(){
		return spectator;
	}

	public MapDataList<MapDataInteger> getTrusted(){
		return trusted;
	}

	public MapRegion getRegion(){
		return region;
	}

	public MapScoreboard getScoreboard(){
		return scoreboard;
	}

	public MapRenderer getRenderer(){
		return renderer;
	}

	public MapPermission getPermission(MapPlayer mPlayer){
		if(mPlayer.getUser().equals(this.getUser()) || mPlayer.getUser().getRank() == UserRank.MANAZER) return MapPermission.OWNER;
		else if(trusted.getValues().contains(new MapDataInteger(mPlayer.getUser().getId()))) return MapPermission.BUILD;
		return MapPermission.NONE;
	}

	public void create(){
		created = (int)(System.currentTimeMillis()/1000);
		try {
			ResultSet rs = DB.insert("INSERT INTO "+MAPS+" (user_id,map_name,map_type,map_data,map_created) VALUES(?,?,?,?,?)",
					this.getUser().getId(),
					this.getName(),
					this.getType().getId(),
					"",
					this.getCreated()
			);
			if(rs.next()){
				id = rs.getInt(1);
				name = "Map"+id;
				rs.close();
				this.getBiome().setBiome(this.getBiome().getBiome());
				this.save();
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		this.getRegion().getCenterLocation().getBlock().setType(Material.BEDROCK);
	}

	public void load(){
		ResultSet rs = DB.query("SELECT * FROM "+MAPS+" WHERE map_id = '"+this.getId()+"'");
		try {
			if(rs.next()){
				user = Users.getUser(rs.getInt("user_id"));
				name = rs.getString("map_name");
				type = MapType.getById(rs.getInt("map_type"));
				created = rs.getInt("map_created");
				updated = rs.getInt("map_updated");
				state = MapState.getById(rs.getInt("map_state"));
				this._loadData(new MapData(rs.getString("map_data")));
				Blob blob = rs.getBlob("map_region");
				if(blob != null){
					regionData = blob.getBytes(1,(int)blob.length());
					blob.free();
				} else {
					this.getRegion().setLoaded(true);
				}
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		this.getRenderer().update();
	}

	public void save(){
		updated = (int)(System.currentTimeMillis()/1000);
		DB.update("UPDATE "+MAPS+" SET user_id = ?,map_name = ?,map_type = ?,map_data = ?,map_created = ?,map_updated = ?,map_state = ? WHERE map_id = '"+this.getId()+"'",
				this.getUser().getId(),
				this.getName(),
				this.getType().getId(),
				this.getJsonData(),
				this.getCreated(),
				this.getUpdated(),
				this.getState().getId()
		);
		this.getRenderer().update();
		this.saveRegion();
	}

	public void saveRegion(){
		Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable() {
			@Override
			public void run(){
				regionData = Map.this.getRegion().toByteArray();
				Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(),new Runnable(){
					@Override
					public void run(){
						Map.this.getRegion().setToSave(false);
						DB.update("UPDATE "+MAPS+" SET map_updated = ?,map_region = ? WHERE map_id = '"+Map.this.getId()+"'",
								Map.this.getUpdated(),
								new ByteArrayInputStream(regionData)
						);
						updated = (int)(System.currentTimeMillis()/1000);
					}
				});
			}
		},10);
	}

	private String getJsonData(){
		MapData data = this.getData();
		data.addProperty(time);
		data.addProperty(biome);
		data.addProperty(environment);
		data.addProperty(spectator);
		data.addProperty(trusted);
		return data.toString();
	}

	private void _loadData(MapData data){
		time.loadData(data);
		biome.loadData(data);
		environment.loadData(data);
		spectator.loadData(data);
		trusted.loadData(data);
		this.loadData(data);
	}

	public abstract MapData getData();
	public abstract void loadData(MapData data);
	public abstract boolean isValid();

	public abstract void updateScoreboard(MapScoreboard scoreboard);
	public abstract void updateRenderer(MapRenderer renderer);

	public abstract void performCommand(Player player,String[] args);
	public abstract List<String> tabCompleter(Player player,String[] args);

	@Override
	public int hashCode(){
		return id;
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof Map){
			Map toCompare = (Map) object;
			return (toCompare.getId() == this.getId());
		}
		return false;
	}

	public class MapDataBiome extends MapDataString {

		private Biome biome;

		public MapDataBiome(String name,Biome biome){
			super(name,biome.toString());
			this.biome = biome;
		}

		public Biome getBiome(){
			return biome;
		}

		public void setBiome(Biome biome){
			this.biome = biome;
			this.setValue(biome.toString());
			Map.this.save();
			HashMap<String,Chunk> chunks = new HashMap<>();
			for(int x=Map.this.getRegion().getMinLocation().getBlockX()-1;x<Map.this.getRegion().getMaxLocation().getBlockX()+1;x++){
				for(int y=Map.this.getRegion().getMinLocation().getBlockY()-1;y<Map.this.getRegion().getMaxLocation().getBlockY()+1;y++) {
					for (int z = Map.this.getRegion().getMinLocation().getBlockZ() - 1;z < Map.this.getRegion().getMaxLocation().getBlockZ() + 1;z++) {
						//Map.this.getRegion().getWorld().setBiome(x, z, biome);
						Location location = new Location(Map.this.getRegion().getWorld(), x, y, z);
						location.getBlock().setBiome(biome);
						String key = location.getWorld().getChunkAt(location).getX() + ";" + location.getWorld().getChunkAt(location).getZ();
						if (!chunks.containsKey(key))
							chunks.put(key, location.getWorld().getChunkAt(location));
					}
				}
			}
			for(Chunk chunk : chunks.values()){
				Map.this.getRegion().getWorld().refreshChunk(chunk.getX(),chunk.getZ());
			}
		}

		@Override
		public void loadData(MapData data){
			super.loadData(data);
			if(this.getValue() != null) this.biome = Biome.valueOf(this.getValue());
			else this.setBiome(Biome.FOREST);
		}
	}

	public class MapDataEnvironment extends MapDataString {

		private Environment environment;

		public MapDataEnvironment(String name,Environment environment){
			super(name,environment.toString());
			this.environment = environment;
		}

		public Environment getEnvironment(){
			return environment;
		}

		public void setEnvironment(Environment environment){
			this.environment = environment;
			this.setValue(environment.toString());
			Map.this.save();
		}

		@Override
		public void loadData(MapData data){
			super.loadData(data);
			if(this.getValue() != null) this.environment = Environment.valueOf(this.getValue());
			else this.setEnvironment(Environment.NORMAL);
		}
	}
}