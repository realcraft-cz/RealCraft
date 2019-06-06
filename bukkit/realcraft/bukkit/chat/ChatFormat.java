package realcraft.bukkit.chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import realcraft.bukkit.RealCraft;

public class ChatFormat implements Listener {
	RealCraft plugin;

	String chatFormat;

	public ChatFormat(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	public void onReload(){
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChatLowest(AsyncPlayerChatEvent event){
		if(event.isCancelled()) return;
		chatFormat = event.getFormat();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChatMonitor(AsyncPlayerChatEvent event){
		if(event.isCancelled()) return;
		if(chatFormat.equalsIgnoreCase(event.getFormat()) == false) return;
		Player player = event.getPlayer();
		String message = event.getMessage();
		if(player.hasPermission("essentials.chat.magic")) message = RealCraft.parseColors(message);
		String chatMessage = "# "+player.getDisplayName()+": "+message;
		event.setCancelled(true);
		for(Player user : plugin.getServer().getOnlinePlayers()){
			if(RealCraft.getInstance().essentials.getUser(user).isIgnoredPlayer(RealCraft.getInstance().essentials.getUser(player))){
				user.sendMessage("§7# "+ChatColor.stripColor(player.getDisplayName())+":");
				continue;
			}
			user.sendMessage(chatMessage);
		}
	}
}