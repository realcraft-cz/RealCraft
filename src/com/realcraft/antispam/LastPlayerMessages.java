package com.realcraft.antispam;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class LastPlayerMessages {
	static Map<String, String[]> lastPlayerMessages = new HashMap<String, String[]>();
	static int spamHistory;
	
	public static void onReload(){
		lastPlayerMessages = new HashMap<String, String[]>();
	}
	
	public static void initSettings(int spamHistory){
		LastPlayerMessages.spamHistory = spamHistory;
	}
	
	public static String[] initMessages(Player player){
		lastPlayerMessages.put(player.getName(),new String[spamHistory]);
		return new String[spamHistory];
	}
	
	public static String[] getMessages(Player player){
		String[] messages = lastPlayerMessages.get(player.getName());
		if(messages == null || messages.length == 0) messages = initMessages(player);
		return messages;
	}
	
	public static void addMessage(Player player,String message){
		String [] messages = getMessages(player);
		System.arraycopy(messages,0,messages,1,messages.length-1);
		messages[0] = message;
		lastPlayerMessages.put(player.getName(),messages);
	}
}