package realcraft.bukkit.spawn;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.utils.AbstractCommand;
import realcraft.bukkit.utils.LocationUtil;

import java.io.File;
import java.util.List;

public class ServerSpawn extends AbstractCommand {

	private static Location spawnLocation = null;

	public ServerSpawn(){
		super("spawn");
	}

	@Override
	public void perform(Player player,String[] args){
		if(getLocation() != null) player.teleport(getLocation(),PlayerTeleportEvent.TeleportCause.PLUGIN);
	}

	@Override
	public List<String> onTabComplete(Player player,String[] args){
		return null;
	}

	public static Location getLocation(){
		if(spawnLocation == null){
			File file = new File(RealCraft.getInstance().getDataFolder()+"/spawns/"+RealCraft.getServerType().toString()+".yml");
			if(file.exists()){
				FileConfiguration config = new YamlConfiguration();
				try {
					config.load(file);
				} catch (Exception e){
					e.printStackTrace();
				}
				spawnLocation = LocationUtil.getConfigLocation(config,"spawn");
			}
		}
		return spawnLocation;
	}
}