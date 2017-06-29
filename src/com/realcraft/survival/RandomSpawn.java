package com.realcraft.survival;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.realcraft.RealCraft;
import com.realcraft.chat.ChatCommandSpy;
import com.realcraft.utils.LocationUtil;
import com.realcraft.utils.RandomUtil;

public class RandomSpawn implements Listener {

	private static final int RANDOM_LIMIT = 300*1000;
	private static final int RANDOM_SIZE = 5000;
	private HashMap<Player,Long> lastRandomSpawn = new HashMap<Player,Long>();

	public RandomSpawn(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1).toLowerCase();
		if(command.equalsIgnoreCase("priroda") || command.equalsIgnoreCase("warp rs")){
			event.setCancelled(true);
			if(lastRandomSpawn.containsKey(player) && lastRandomSpawn.get(player)+RANDOM_LIMIT >= System.currentTimeMillis()){
				player.sendMessage("§cNahodny spawn do prirody muzete znovu pouzit za "+(((lastRandomSpawn.get(player)+RANDOM_LIMIT)-System.currentTimeMillis())/1000)+" sekund.");
				return;
			}
			lastRandomSpawn.put(player,System.currentTimeMillis());
			player.teleport(this.getRandomLocation(Bukkit.getWorld("world")),TeleportCause.PLUGIN);
			ChatCommandSpy.sendCommandMessage(player,command);
		}
	}

	public Location getRandomLocation(World world){
		Location location = LocationUtil.getSafeDestination(new Location(world,RandomUtil.getRandomInteger(-RANDOM_SIZE,RANDOM_SIZE),world.getMaxHeight(),RandomUtil.getRandomInteger(-RANDOM_SIZE,RANDOM_SIZE)));
		if(this.isLocationInOcean(location)) location = this.getRandomLocation(world);
		return location;
	}

	public boolean isLocationInOcean(Location location){
		return (
			location.getWorld().getBiome(location.getBlockX(),location.getBlockZ()) == Biome.OCEAN || location.getWorld().getBiome(location.getBlockX(),location.getBlockZ()) == Biome.DEEP_OCEAN ||
			location.getWorld().getBiome(location.getBlockX()+1,location.getBlockZ()) == Biome.OCEAN || location.getWorld().getBiome(location.getBlockX()+1,location.getBlockZ()) == Biome.DEEP_OCEAN ||
			location.getWorld().getBiome(location.getBlockX()-1,location.getBlockZ()) == Biome.OCEAN || location.getWorld().getBiome(location.getBlockX()-1,location.getBlockZ()) == Biome.DEEP_OCEAN ||
			location.getWorld().getBiome(location.getBlockX(),location.getBlockZ()+1) == Biome.OCEAN || location.getWorld().getBiome(location.getBlockX(),location.getBlockZ()+1) == Biome.DEEP_OCEAN ||
			location.getWorld().getBiome(location.getBlockX(),location.getBlockZ()-1) == Biome.OCEAN || location.getWorld().getBiome(location.getBlockX(),location.getBlockZ()-1) == Biome.DEEP_OCEAN
		);
	}
}