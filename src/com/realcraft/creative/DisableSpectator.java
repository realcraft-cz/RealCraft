package com.realcraft.creative;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.realcraft.RealCraft;

public class DisableSpectator implements Listener {
	RealCraft plugin;

	public DisableSpectator(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	public void onReload(){
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		player.setGameMode(GameMode.CREATIVE);
	}

	@EventHandler
	public void PlayerTeleportEvent(PlayerTeleportEvent event){
		if(event.getCause() == TeleportCause.SPECTATE){
			Player player = event.getPlayer();
			if(!player.hasPermission("group.Admin") && !player.hasPermission("group.Moderator") && !player.hasPermission("group.Builder")){
				event.setCancelled(true);
			}
		}
	}
}