package realcraft.bungee.config;

import java.util.List;

import net.md_5.bungee.config.Configuration;
import realcraft.bungee.RealCraftBungee;

public class Config {
	private RealCraftBungee plugin;
	private Configuration fileconfig = null;
	
	public Config(RealCraftBungee realcraft){
		plugin = realcraft;
		fileconfig = new YamlConfig("config.yml", plugin).getConfig();
	}
	
	public void onReload(){
		fileconfig = new YamlConfig("config.yml", plugin).getConfig();
	}
	
	public void onDisable(){
		
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
}