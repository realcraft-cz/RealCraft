package realcraft.bukkit.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.utils.StringUtil;

public class ChatAdvert implements Listener {
	RealCraft plugin;

	boolean enabled = false;
	String warnMessage;
	private Pattern [] allowedURLs;
	private Pattern [] blockedWords;

	public ChatAdvert(RealCraft realcraft){
		plugin = realcraft;
		if(plugin.config.getBoolean("chatadvert.enabled")){
			enabled = true;
			warnMessage = plugin.config.getString("chatadvert.warnMessage","&cREKLAMA");
			this.loadAllowedURLs();
			this.loadBlockedWords();
			plugin.getServer().getPluginManager().registerEvents(this,plugin);
		}
	}

	public void onReload(){
		this.loadAllowedURLs();
	}

	private void loadAllowedURLs(){
		List<String> tmpurls = plugin.config.getStringList("chatadvert.allowedURLs");
		int index = 0;
		allowedURLs = new Pattern[tmpurls.size()];
		for(String url : tmpurls){
			allowedURLs[index++] = Pattern.compile(url);
		}
	}

	private void loadBlockedWords(){
		List<String> tmpurls = plugin.config.getStringList("chatadvert.blockedWords");
		int index = 0;
		blockedWords = new Pattern[tmpurls.size()];
		for(String url : tmpurls){
			blockedWords[index++] = Pattern.compile(url);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		if(!enabled || event.isCancelled()) return;
		Player sender = event.getPlayer();
		String message = event.getMessage();
		message = this.checkAdvert(sender,message);
		event.setMessage(message);
	}

	public String checkAdvert(Player sender,String message){
		ArrayList<String> adverts = new ArrayList<String>();
		message = StringUtil.blockURL(message,allowedURLs,blockedWords,adverts);
		if(adverts.size() > 0) this.sendWarning(sender,adverts);
		return message;
	}

	public void sendWarning(Player sender,ArrayList<String> adverts){
		final String warnMessage = RealCraft.parseColors(this.warnMessage+" "+sender.getDisplayName()+"&c: &r"+adverts.toString());
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			@Override
			public void run(){
				for(Player player : plugin.getServer().getOnlinePlayers()){
					if(player.hasPermission("group.Admin") || player.hasPermission("group.Moderator")){
						player.sendMessage(warnMessage);
					}
				}
			}
		},2);
	}
}