package realcraft.bukkit.lobby;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.utils.LocationUtil;

public class LobbySpawn implements Listener {
	RealCraft plugin;

	boolean enabled = false;
	Location spawnLocation = null;

	public LobbySpawn(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		this.loadSpawn();
	}

	public void onReload(){
	}

	public void loadSpawn(){
		File file = new File(RealCraft.getInstance().getDataFolder()+"/spawns/"+plugin.serverName+".yml");
		if(file.exists()){
			FileConfiguration config = new YamlConfiguration();
			try {
				config.load(file);
				enabled = true;
			} catch (Exception e){
				e.printStackTrace();
			}
			spawnLocation = LocationUtil.getConfigLocation(config,"spawn");
		}
	}

	public Location getSpawnLocation(){
		return spawnLocation;
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void PlayerJoinEvent(PlayerJoinEvent event){
		if(this.enabled) event.getPlayer().teleport(spawnLocation);
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		if(this.enabled) event.getPlayer().teleport(spawnLocation);
	}
}