package realcraft.bukkit.fights.arenas;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import realcraft.bukkit.RealCraft;

public abstract class FightArena {

	private String name;
	private FightArenaType type;

	private World world;
	private int time = -1;

	private FileConfiguration config;

	public FightArena(String name,FightArenaType type){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public FightArenaType getType(){
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

	public enum FightArenaType {
		PUBLIC, DUEL;

		@Override
		public String toString(){
			return this.name().toLowerCase();
		}
	}
}