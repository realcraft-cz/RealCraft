package realcraft.bukkit.fights.arenas;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.fights.FightType;

public abstract class FightArena {

	private String name;
	private FightType type;
	private FileConfiguration config;

	private World world;
	private int time = -1;
	private ArrayList<Location> spawns = new ArrayList<Location>();

	public FightArena(String name,FightType type){
		this.name = name;
		this.loadSpawns();
	}

	public String getName(){
		return name;
	}

	public FightType getType(){
		return type;
	}

	public World getWorld(){
		if(world == null) world = Bukkit.getWorld(this.getConfig().getString("world"));
		return world;
	}

	public int getTime(){
		if(time == -1) time = this.getConfig().getInt("time",6000);
		return time;
	}

	public FileConfiguration getConfig(){
		if(config == null){
			File file = new File(RealCraft.getInstance().getDataFolder()+"/fights/"+this.getType().toString()+"/"+this.getName()+"/"+"config.yml");
			if(file.exists()){
				config = new YamlConfiguration();
				try {
					config.load(file);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		return config;
	}

	public ArrayList<Location> getSpawns(){
		return spawns;
	}

	@SuppressWarnings("unchecked")
	private void loadSpawns(){
		List<Map<String, Object>> temps = (List<Map<String, Object>>) this.getConfig().get("spawns");
		if(temps != null && !temps.isEmpty()){
			for(Map<String, Object> trader : temps){
				double x = Double.valueOf(trader.get("x").toString());
				double y = Double.valueOf(trader.get("y").toString());
				double z = Double.valueOf(trader.get("z").toString());
				float yaw = Float.valueOf(trader.get("yaw").toString());
				float pitch = Float.valueOf(trader.get("pitch").toString());
				World world = Bukkit.getWorld(trader.get("world").toString());
				if(world == null){
					world = Bukkit.createWorld(new WorldCreator(trader.get("world").toString()));
					if(world == null){
						continue;
					}
				}
				spawns.add(new Location(world,x,y,z,yaw,pitch));
			}
		}
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof FightArena){
			FightArena toCompare = (FightArena) object;
			return (toCompare.getName().equals(this.getName()) && toCompare.getType() == this.getType());
		}
		return false;
	}
}