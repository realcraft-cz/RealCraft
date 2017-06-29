package com.realcraft.antispam;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import com.realcraft.RealCraft;

public class AntiSpam implements Listener {
	RealCraft plugin;
	Map<String, Long> lastPlayerMessagesTime = new HashMap<String, Long>();

	int timeLimit;
	int maxSameChars;
	public int spamHistory;
	double spamProbability;
	String spamMessage = null;

	public AntiSpam(RealCraft realcraft){
		plugin = realcraft;
		loadConfig();
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	public void onReload(){
		LastPlayerMessages.onReload();
		loadConfig();
	}

	public void loadConfig(){
		if(plugin.config.getBoolean("antispam.enabled")){
			timeLimit = plugin.config.getInt("antispam.timeLimit",1000);
			maxSameChars = plugin.config.getInt("antispam.maxSameChars",5);
			spamHistory = plugin.config.getInt("antispam.spamHistory",5);
			spamProbability = plugin.config.getDouble("antispam.spamProbability",0.6);
			spamMessage = plugin.config.getString("antispam.spamMessage",null);
			if(spamMessage.length() == 0 || spamMessage.equals("false")) spamMessage = null;
			LastPlayerMessages.initSettings(spamHistory);
		}
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerLogin(PlayerLoginEvent e){
		LastPlayerMessages.initMessages(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		String message = event.getMessage();
		if(player.hasPermission("group.Admin") || player.hasPermission("group.Moderator")) return;

		/** Timelimit messages */
		long lastMessageTime = (lastPlayerMessagesTime.get(player.getName()) != null ? lastPlayerMessagesTime.get(player.getName()) : 0);
		if(lastMessageTime+timeLimit >= System.currentTimeMillis()){
			if(spamMessage != null) player.sendMessage(RealCraft.parseColors(spamMessage));
			event.setCancelled(true);
			return;
		}

		/** Clear CapsLock */
		if(message.length() >= 5) message = this.clearCapsLock(message);

		/** Similar messages */
		String[] messages = LastPlayerMessages.getMessages(player);
		for(String oldMessage : messages){
			if(oldMessage != null && StringSimilarity.similarity(message,oldMessage) > spamProbability){
				if(spamMessage != null) player.sendMessage(RealCraft.parseColors(spamMessage));
				event.setCancelled(true);
				return;
			}
		}

		LastPlayerMessages.addMessage(player,message);
		lastPlayerMessagesTime.put(player.getName(),System.currentTimeMillis());

		/** Repeated chars */
		message = message.replaceAll("(.)\\1{"+maxSameChars+",}",StringUtils.repeat("$1",maxSameChars));
		message = message.replaceAll("(..)\\1{"+maxSameChars+",}",StringUtils.repeat("$1",maxSameChars));
		event.setMessage(message);
	}

	private String clearCapsLock(String message){
		if(message.matches("[^\\p{Ll}]+")) message = message.toLowerCase();
		return message;
	}
}