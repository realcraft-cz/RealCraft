package com.realcraft.pvp;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.realcraft.RealCraft;

public class DisableFly implements Listener {
	RealCraft plugin;
	
	public DisableFly(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}
	
	public void onReload(){
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode() == GameMode.SURVIVAL){
			player.setAllowFlight(false);
			player.setFlying(false);
		}
	}
	
	@EventHandler
	public void onPlayerWorldChange(PlayerChangedWorldEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode() == GameMode.SURVIVAL){
			player.setAllowFlight(false);
			player.setFlying(false);
		}
	}
	
	@EventHandler
	public void onPlayerFlyToggle(PlayerToggleFlightEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode() == GameMode.SURVIVAL){
			player.setAllowFlight(false);
			player.setFlying(false);
		}
	}
}