package realcraft.bukkit.lobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.utils.AbstractCommand;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.bukkit.utils.MapUtil;

import java.io.File;

public class LobbyCanvas {

	private Location minLoc;
	private Location maxLoc;

	private FileConfiguration config;

	public LobbyCanvas(){
		minLoc = LocationUtil.getConfigLocation(this.getConfig(),"minLoc");
		maxLoc = LocationUtil.getConfigLocation(this.getConfig(),"maxLoc");
		new AbstractCommand("updatecanvas"){
			@Override
			public void perform(Player player,String[] args){
				if(player.hasPermission("group.Manazer")){
					LobbyCanvas.this.update();
				}
			}
		};
		Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				LobbyCanvas.this.update();
			}
		},20);
	}

	private FileConfiguration getConfig(){
		if(config == null){
			File file = new File(RealCraft.getInstance().getDataFolder() + "/canvas/canvas.yml");
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

	private File getCanvasFile(){
		return new File(RealCraft.getInstance().getDataFolder() + "/canvas/image.png");
	}

	private void update(){
		MapUtil.pasteMap(this.getCanvasFile(),minLoc,maxLoc);
	}
}