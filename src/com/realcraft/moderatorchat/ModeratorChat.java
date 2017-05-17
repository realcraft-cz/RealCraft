package com.realcraft.moderatorchat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.realcraft.RealCraft;

public class ModeratorChat implements Listener, CommandExecutor {
	RealCraft plugin;

	boolean enabled = false;
	String modMessage;

	public ModeratorChat(RealCraft realcraft){
		plugin = realcraft;
		if(plugin.config.getBoolean("modchat.enabled")){
			enabled = true;
			modMessage = plugin.config.getString("modchat.message",null);
			plugin.getServer().getPluginManager().registerEvents(this,plugin);
			plugin.getCommand("mod").setExecutor(this);
		}
	}

	public void onReload(){
		enabled = false;
		if(plugin.config.getBoolean("modchat.enabled")){
			enabled = true;
			modMessage = plugin.config.getString("modchat.message",null);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(!enabled) return false;
		Player player = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("mod")){
			if(player.hasPermission("group.Admin") || player.hasPermission("group.Moderator")){
				boolean modchat = plugin.playermanazer.getPlayerInfo(player).toggleModChat();
				if(modMessage != null){
					String message = modMessage.replaceAll("%status%",(modchat ? "&aZapnuto" : "&cVypnuto"));
					player.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
					return true;
				}
			}
		}
		return true;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		if(!enabled || event.isCancelled()) return;
		Player player = event.getPlayer();
		if(plugin.playermanazer.getPlayerInfo(player).getModChat()){
			String message = event.getMessage();
			event.setMessage("§6"+message);
		}
	}
}