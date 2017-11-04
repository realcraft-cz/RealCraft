package com.realcraft.test;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.realcraft.RealCraft;

public class PokemonTest implements Listener {

	public PokemonTest(){
		RealCraft.getInstance().getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1).toLowerCase();
		if(command.startsWith("pokemon") && player.hasPermission("group.Manazer")){
			event.setCancelled(true);
			String[] args = command.split(" ");
			if(args.length < 2){
				player.sendMessage("§f/pokemon <player>");
				return;
			}
			Player victim = Bukkit.getPlayer(args[1]);
			if(victim == null){
				player.sendMessage("§cHrac nenalezen.");
				return;
			}
			victim.getInventory().setItem(6,RealCraft.getInstance().lobby.lobbypokemons.getItem());
		}
	}
}