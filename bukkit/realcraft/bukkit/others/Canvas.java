package realcraft.bukkit.others;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.bukkit.utils.MapUtil;

import java.io.File;
import java.util.ArrayList;

public class Canvas {

	private ArrayList<CanvasImage> images = new ArrayList<>();
	private FileConfiguration config;

	public Canvas(){
		new AbstractCommand("updatecanvas"){
			@Override
			public void perform(Player player,String[] args){
				if(player.hasPermission("group.Manazer")){
					Canvas.this.update();
				}
			}
		};
		Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				Canvas.this.update();
			}
		},20);
		this.loadImages();
	}

	private FileConfiguration getConfig(){
		if(config == null){
			File file = new File(RealCraft.getInstance().getDataFolder()+"/canvas/"+RealCraft.getServerType().toString()+".yml");
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

	private void update(){
		for(CanvasImage image : images){
			MapUtil.pasteMap(image.getCanvasFile(),image.getMinLoc(),image.getMaxLoc());
		}
	}

	private void loadImages(){
		if(this.getConfig() != null){
			for(String key : this.getConfig().getKeys(false)){
				ConfigurationSection section = this.getConfig().getConfigurationSection(key);
				Location minLoc = LocationUtil.getConfigLocation(section,"minLoc");
				Location maxLoc = LocationUtil.getConfigLocation(section,"maxLoc");
				images.add(new CanvasImage(section.getString("image"),minLoc,maxLoc));
			}
		}
	}

	private class CanvasImage {

		private String name;
		private Location minLoc;
		private Location maxLoc;

		public CanvasImage(String name,Location minLoc,Location maxLoc){
			this.name = name;
			this.minLoc = minLoc;
			this.maxLoc = maxLoc;
		}

		public Location getMinLoc(){
			return minLoc;
		}

		public Location getMaxLoc(){
			return maxLoc;
		}

		private File getCanvasFile(){
			return new File(RealCraft.getInstance().getDataFolder()+"/canvas/"+name);
		}
	}
}