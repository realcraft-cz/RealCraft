package realcraft.bukkit.chat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class LastPlayerPrivateMessages {
	static Map<String, String[]> lastPlayerMessages = new HashMap<String, String[]>();
	static int spamHistory;
	
	public static void onReload(){
		lastPlayerMessages = new HashMap<String, String[]>();
	}
	
	public static void initSettings(int spamHistory){
		LastPlayerPrivateMessages.spamHistory = spamHistory;
	}
	
	public static String[] initMessages(Player player,Player recipient){
		lastPlayerMessages.put(player.getName()+";"+recipient.getName(),new String[spamHistory]);
		return new String[spamHistory];
	}
	
	public static String[] getMessages(Player player,Player recipient){
		String[] messages = lastPlayerMessages.get(player.getName()+";"+recipient.getName());
		if(messages == null || messages.length == 0) messages = initMessages(player,recipient);
		return messages;
	}
	
	public static void addMessage(Player player,Player recipient,String message){
		String [] messages = getMessages(player,recipient);
		System.arraycopy(messages,0,messages,1,messages.length-1);
		messages[0] = message;
		lastPlayerMessages.put(player.getName()+";"+recipient.getName(),messages);
	}
}