package realcraft.bukkit.others;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.AbstractCommand;
import realcraft.share.users.UserRank;

public class VipComamnd extends AbstractCommand {

	public VipComamnd(){
		super("vip","premium","store");
	}

	@Override
	public void perform(Player player,String[] args){
		player.sendMessage(ChatColor.LIGHT_PURPLE+""+ChatColor.STRIKETHROUGH+StringUtils.repeat(" ",60));
		if(Users.getUser(player).getRank() != UserRank.VIP){
			player.sendMessage("");
			player.sendMessage("      "+ChatColor.BOLD+player.getName()+", stale nemas VIP ucet?");
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
		} else {
			player.sendMessage("");
			player.sendMessage("    §aVIP ucet mas aktivni");
		}
		player.sendMessage(ChatColor.LIGHT_PURPLE+""+ChatColor.STRIKETHROUGH+StringUtils.repeat(" ",60));
	}
}