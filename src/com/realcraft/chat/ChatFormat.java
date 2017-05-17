package com.realcraft.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.realcraft.RealCraft;

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
		/*TextComponent chatMessage = new TextComponent("# ");
		TextComponent chatUser = new TextComponent(player.getDisplayName());
		chatUser.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(
						player.getDisplayName()+"\n"+
						"§7XP level:§r "+player.getLevel()+"\n"+
						"§7Ping:§r "+((CraftPlayer)player).getHandle().ping+" ms"
				).create()
		));
		chatUser.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/msg "+player.getName()+" "));
		chatMessage.addExtra(chatUser);
		chatMessage.addExtra(new TextComponent(": "));
		for(BaseComponent component : TextComponent.fromLegacyText(message)){
			Matcher matcher = StringUtil.URL_PATTERN.matcher(component.toPlainText());
			if(matcher.find()){
				String urlString = matcher.group();
				component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,urlString.startsWith( "http" ) ? urlString : "http://" + urlString));
				component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("Klikni pro otevreni odkazu").create()));
			}
			chatMessage.addExtra(component);
		}*/
		event.setCancelled(true);
		for(Player user : plugin.getServer().getOnlinePlayers()){
			user.sendMessage(chatMessage);
		}
	}
}