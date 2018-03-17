package realcraft.bukkit.config;

import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

import realcraft.bukkit.RealCraft;

public class Config {
	private RealCraft plugin;
	private FileConfiguration fileconfig;

	public Config(RealCraft realcraft){
		plugin = realcraft;
		plugin.saveDefaultConfig();
		fileconfig = plugin.getConfig();
	}

	public FileConfiguration getConfig(){
		return fileconfig;
	}

	public void onReload(){
		fileconfig = plugin.getConfig();
	}

	public void onDisable(){

	}

	public Object get(String name){
		return fileconfig.get(name);
	}

	public boolean getBoolean(String name){
		return getBoolean(name,false);
	}

	public boolean getBoolean(String name,boolean def){
		return fileconfig.getBoolean(name,def);
	}

	public int getInt(String name){
		return getInt(name,0);
	}

	public int getInt(String name,int def){
		return fileconfig.getInt(name,def);
	}

	public double getDouble(String name){
		return getDouble(name,0.0);
	}

	public double getDouble(String name,double def){
		return fileconfig.getDouble(name,def);
	}

	public String getString(String name){
		return getString(name,null);
	}

	public String getString(String name,String def){
		return fileconfig.getString(name,def);
	}

	public List<String> getStringList(String name){
		return fileconfig.getStringList(name);
	}

	public Set<String> getKeys(String name){
		return fileconfig.getConfigurationSection(name).getKeys(false);
	}
}