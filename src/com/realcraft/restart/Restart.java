package com.realcraft.restart;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.realcraft.RealCraft;
import com.realcraft.utils.Glow;
import com.realcraft.utils.LocationUtil;

public class Restart implements Runnable, CommandExecutor {
	RealCraft plugin;

	boolean enabled = false;
	int restartHour;
	int restartTimeout;
	int restartTimeoutEx = 0;
	String restartMessageCountdown;
	String restartMessage;
	int taskId;

	FileConfiguration locationsConfig;
	ArrayList<Location> locations = new ArrayList<Location>();

	public Restart(RealCraft realcraft){
		plugin = realcraft;
		if(plugin.config.getBoolean("restart.enabled")){
			enabled = true;
			restartHour = plugin.config.getInt("restart.restartHour",0);
			restartTimeout = plugin.config.getInt("restart.restartTimeout",3);
			restartMessageCountdown = plugin.config.getString("restart.restartMessageCountdown",null);
			restartMessage = plugin.config.getString("restart.restartMessage",null);
			plugin.getCommand("restart").setExecutor(this);
			plugin.getCommand("rpos").setExecutor(this);
			plugin.getCommand("chunk").setExecutor(this);
			plugin.getCommand("glow").setExecutor(this);
			plugin.getCommand("saveloc").setExecutor(this);
			taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,this,3600*20,60*20);
		}
	}

	public void onReload(){
		enabled = false;
		if(plugin.config.getBoolean("restart.enabled")){
			enabled = true;
			restartHour = plugin.config.getInt("restart.restartHour",0);
			restartTimeout = plugin.config.getInt("restart.restartTimeout",3);
			restartMessageCountdown = plugin.config.getString("restart.restartMessageCountdown",null);
			restartMessage = plugin.config.getString("restart.restartMessage",null);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!enabled) return false;
		final Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("restart")){
			if(player.hasPermission("group.Manazer") || player.hasPermission("group.HlAdmin")){
				if(restartTimeoutEx != 0){
					player.sendMessage("§cRestart serveru prave probiha!");
					return true;
				}
				else if(args.length == 0){
					player.sendMessage("Potvrdte restart prikazem /restart confirm");
					return true;
				}
				else if(args.length == 1 && args[0].equalsIgnoreCase("confirm")){
					executeRestart();
				}
			}
		}
		else if(command.getName().equalsIgnoreCase("rpos")){
			if(player.hasPermission("group.Admin")){
				if(args.length < 6){
					player.sendMessage("/rpos <period> <distance> <minX> <minY> <maxX> <maxY>");
					return true;
				}
				int period = Integer.valueOf(args[0]);
				final int distance = Integer.valueOf(args[1]);
				final int minX = Integer.valueOf(args[2]);
				final int minZ = Integer.valueOf(args[3]);
				final int maxX = Integer.valueOf(args[4]);
				final int maxZ = Integer.valueOf(args[5]);
				currentX = minX;
				currentZ = minZ;
				nextRPos(player,distance,minX,minZ,maxX,maxZ,period);
			}
		}
		else if(command.getName().equalsIgnoreCase("chunk")){
			if(player.hasPermission("group.Admin")){
				if(args.length < 2){
					player.sendMessage("/chunk <x> <y>");
					return true;
				}
				final int x = Integer.valueOf(args[0]);
				final int z = Integer.valueOf(args[1]);
				final Location location = new Location(player.getWorld(),x,65,z);
				player.getWorld().loadChunk(location.getChunk().getX(),location.getChunk().getZ());

				final Entity entity = player.getWorld().spawnEntity(location,EntityType.CHICKEN);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RealCraft.getInstance(),new Runnable(){
					@Override
					public void run(){
						entity.remove();
						player.getWorld().unloadChunk(location.getChunk().getX(),location.getChunk().getZ(),true);
						player.sendMessage("Chunk "+location.getChunk().getX()+","+location.getChunk().getZ()+" unloaded.");
					}
				},20);
				player.sendMessage("Chunk "+location.getChunk().getX()+","+location.getChunk().getZ()+" loaded.");
			}
		}
		else if(command.getName().equalsIgnoreCase("glow")){
			if(player.hasPermission("group.Admin")){
				ItemStack item = player.getInventory().getItemInMainHand();
				if(item != null && item.getType() != Material.AIR){
					ItemMeta meta = item.getItemMeta();
					meta.addEnchant(new Glow(255),1,true);
					item.setItemMeta(meta);
					player.getInventory().setItemInMainHand(item);
				}
			}
		}
		else if(command.getName().equalsIgnoreCase("saveloc")){
			if(player.hasPermission("group.Manazer")){
				if(args.length == 1 && args[0].equalsIgnoreCase("reset")){
					player.sendMessage("Locations reset.");
					locations = new ArrayList<Location>();
					return true;
				}
				Location location = player.getLocation();
				locations.add(location);
				player.sendMessage("Location saved ["+location.getBlockX()+","+location.getBlockY()+","+location.getBlockZ()+"]");
				this.saveLocations();
			}
		}
		return true;
	}

	public void saveLocations(){
		ArrayList<HashMap<String, Object>> locationList = new ArrayList<HashMap<String, Object>>();
		for(Location location : locations){
			HashMap<String, Object> section = new HashMap<String, Object>();
			section.put("world", location.getWorld().getName());
			section.put("x", location.getBlockX()+0.5);
			section.put("y", Math.round(location.getY()));
			section.put("z", location.getBlockZ()+0.5);
			section.put("pitch", (double) LocationUtil.faceToYaw(LocationUtil.yawToFace(location.getPitch())));
			section.put("yaw", (double) LocationUtil.faceToYaw(LocationUtil.yawToFace(location.getYaw())));
			locationList.add(section);
		}
		locationsConfig = new YamlConfiguration();
		locationsConfig.set("spawns",locationList);
		try {
			File file = new File(RealCraft.getInstance().getDataFolder()+"/locations.yml");
			locationsConfig.save(file);
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	int currentX = 0;
	int currentZ = 0;
	public void nextRPos(final Player player,final int distance,final int minX,final int minZ,final int maxX,final int maxZ,final int period){
		if(currentX+distance > maxX){
			currentX = minX;
			currentZ += distance;
		}
		if(currentZ+distance <= maxZ){
			currentX += distance;
			Location location = player.getLocation();
			location.setX(currentX);
			location.setY(100);
			location.setZ(currentZ);
			player.sendMessage("nextRPos: "+currentX+";100;"+currentZ);
			player.teleport(location);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable(){
				@Override
				public void run(){
					nextRPos(player,distance,minX,minZ,maxX,maxZ,period);
				}
			},period);
		}
	}

	@Override
	public void run(){
		LocalDateTime now = LocalDateTime.now();
		if(now.getHour() == restartHour){
			if(restartTimeout > 0){
				executeRestart();
			} else {
				plugin.getServer().broadcastMessage(restartMessage);
				plugin.getServer().shutdown();
			}
		}
	}

	public void executeRestart(){
		plugin.getServer().getScheduler().cancelTask(taskId);
		restartTimeoutEx = restartTimeout;
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,new Runnable(){
			@Override
			public void run(){
				if(restartTimeoutEx > 0){
					plugin.getServer().broadcastMessage(getRestartMessage(restartTimeoutEx));
					restartTimeoutEx --;
				} else {
					plugin.getServer().broadcastMessage(RealCraft.parseColors(restartMessage));
					plugin.getServer().shutdown();
				}
			}
		},0,60*20);
	}

	public String getRestartMessage(int timeout){
		String result = restartMessageCountdown;
		result = result.replaceAll("%timeout%",""+timeout+" "+(timeout == 1 ? "minutu" : "minuty"));
		return RealCraft.parseColors(result);
	}
}