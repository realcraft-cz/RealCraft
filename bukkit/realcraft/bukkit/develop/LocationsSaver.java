package realcraft.bukkit.develop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.utils.LocationUtil;

public class LocationsSaver implements Listener {

	private HashMap<String,LocationGroup> groups = new HashMap<String,LocationGroup>();

	public LocationsSaver(){
		RealCraft.getInstance().getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1).toLowerCase();
		if(command.startsWith("loc") && player.hasPermission("group.Manazer")){
			event.setCancelled(true);
			String[] args = command.split(" ");
			if(args.length < 2){
				player.sendMessage("§fLocations saver");
				player.sendMessage("§f/loc add <group>");
				player.sendMessage("§f/loc clear <group>");
				player.sendMessage("§f/loc save <filename>");
				return;
			}
			else if(args[1].equalsIgnoreCase("add")){
				if(args.length < 3){
					player.sendMessage("§fAdd location to group");
					player.sendMessage("§f/loc add <group>");
					return;
				}
				args[2] = args[2].toLowerCase();
				if(!groups.containsKey(args[2])) groups.put(args[2],new LocationGroup(args[2]));
				BaseLocation baseLocation = groups.get(args[2]).add(player.getLocation());
				player.sendMessage("§aLocation §6"+baseLocation.getName()+" added to §6"+args[2]);
			}
			else if(args[1].equalsIgnoreCase("clear")){
				if(args.length < 3){
					player.sendMessage("§fClear location group");
					player.sendMessage("§f/loc clear <group>");
					return;
				}
				args[2] = args[2].toLowerCase();
				if(groups.containsKey(args[2])) groups.get(args[2]).clear();
				player.sendMessage("§aLocation group §6"+args[2]+"§a cleared");
			}
			else if(args[1].equalsIgnoreCase("save")){
				if(args.length < 3){
					player.sendMessage("§fSave locations to file");
					player.sendMessage("§f/loc save <filename>");
					return;
				}
				this.saveGroups(args[2]);
				player.sendMessage("§aLocations saved to §6"+args[2]+".yml");
			}
		}
	}

	private void saveGroups(String filename){
		YamlConfiguration config = new YamlConfiguration();
		for(LocationGroup group : groups.values()){
			ArrayList<HashMap<String, Object>> locations = new ArrayList<HashMap<String, Object>>();
			for(BaseLocation location : group.getLocations()){
				HashMap<String, Object> section = new HashMap<String, Object>();
				section.put("world", location.getLocation().getWorld().getName());
				section.put("x", location.getLocation().getBlockX()+0.5);
				section.put("y", Math.floor(location.getLocation().getY()));
				section.put("z", location.getLocation().getBlockZ()+0.5);
				section.put("pitch", (double) LocationUtil.faceToYaw(LocationUtil.yawToFace(location.getLocation().getPitch(),true),true));
				section.put("yaw", (double) LocationUtil.faceToYaw(LocationUtil.yawToFace(location.getLocation().getYaw(),true),true));
				locations.add(section);
			}
			config.set(group.getName(),locations);
		}
		try {
			File file = new File(RealCraft.getInstance().getDataFolder()+"/"+filename+".yml");
			config.save(file);
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	private class LocationGroup {

		private String name;
		private ArrayList<BaseLocation> locations = new ArrayList<BaseLocation>();

		public LocationGroup(String name){
			this.name = name;
		}

		public String getName(){
			return name;
		}

		public ArrayList<BaseLocation> getLocations(){
			return locations;
		}

		public BaseLocation add(Location location){
			BaseLocation baseLocation = new BaseLocation(location);
			if(locations.contains(baseLocation)) locations.remove(baseLocation);
			locations.add(baseLocation);
			return baseLocation;
		}

		public void clear(){
			locations.clear();
		}
	}

	private class BaseLocation {

		private int x;
		private int y;
		private int z;
		private Location location;

		public BaseLocation(Location location){
			this.x = location.getBlockX();
			this.y = location.getBlockY();
			this.z = location.getBlockZ();
			this.location = location;
		}

		public Location getLocation(){
			return location;
		}

		public String getName(){
			return "§7[§e"+x+"§7;§6"+y+"§7;§e"+z+"§7]";
		}

		@Override
		public String toString(){
			return x+";"+y+";"+z;
		}

		@Override
		public boolean equals(Object object){
			if(object instanceof BaseLocation){
				BaseLocation toCompare = (BaseLocation) object;
				return toCompare.toString().equals(this.toString());
			}
			return false;
		}
	}
}