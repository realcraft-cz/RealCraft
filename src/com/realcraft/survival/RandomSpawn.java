package com.realcraft.survival;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.realcraft.RealCraft;
import com.realcraft.utils.LocationUtil;
import com.realcraft.utils.RandomUtil;

public class RandomSpawn implements Listener {

	private static final int RANDOM_LIMIT = 500*1000;
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
			player.teleport(this.getRandomLocation(Bukkit.getWorld("world")));
		}
	}

	public Location getRandomLocation(World world){
		return LocationUtil.getSafeDestination(new Location(world,RandomUtil.getRandomInteger(-RANDOM_SIZE,RANDOM_SIZE),world.getMaxHeight(),RandomUtil.getRandomInteger(-RANDOM_SIZE,RANDOM_SIZE)));
	}
}