package realcraft.bukkit.test;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class RandomTest implements Listener {

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1).toLowerCase();
		if(command.startsWith("test") && player.hasPermission("group.Manazer")){
			event.setCancelled(true);
			player.sendMessage(ChatColor.LIGHT_PURPLE+""+ChatColor.STRIKETHROUGH+StringUtils.repeat(" ",60));
			player.sendMessage("");
			player.sendMessage("      "+ChatColor.BOLD+"FreeWall, stale nemas VIP ucet?");
			player.sendMessage("    "+ChatColor.GRAY+"Ziskej zdarma "+ChatColor.LIGHT_PURPLE+"doplnky"+ChatColor.GRAY+" a vyuzivej "+ChatColor.YELLOW+"vyhody,");
			player.sendMessage("  "+ChatColor.GRAY+"o kterych se ostatnim hracum muze jen zdat!");
			player.sendMessage("");
			player.sendMessage("          Podpor nas a kup si "+ChatColor.AQUA+"VIP ucet");
			TextComponent message = new TextComponent("            ");
			TextComponent website = new TextComponent(ChatColor.GREEN+""+ChatColor.BOLD+">> www.realcraft.cz <<");
			website.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://www.realcraft.cz/shop/vip"));
			website.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§7Klikni pro otevreni").create()));
			message.addExtra(website);
			player.spigot().sendMessage(message);
			player.sendMessage(ChatColor.LIGHT_PURPLE+""+ChatColor.STRIKETHROUGH+StringUtils.repeat(" ",60));
		}
	}
}