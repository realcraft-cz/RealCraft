package realcraft.bukkit.vip;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import realcraft.bukkit.others.AbstractCommand;
import realcraft.bukkit.utils.DateUtil;

public class VipCommand extends AbstractCommand {

    public VipCommand() {
        super("vip", "store");
    }

    @Override
    public void perform(Player player, String[] args) {
        player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.STRIKETHROUGH + " ".repeat(60));

        Vip vip = VipManager.getVip(player);

        if (vip.isActive()) {
            player.sendMessage("");
            player.sendMessage("    §b§lVIP clenstvi");
            player.sendMessage("    §aaktivni do " + DateUtil.getDate(vip.getActiveTo(), "d. M. yyyy"));

            if (vip.isAboutToExpire()) {
                player.sendMessage("");

                TextComponent message = new TextComponent("    ");
                TextComponent website = new TextComponent(ChatColor.GOLD + "" + ChatColor.BOLD + ">> Prodlouzit VIP <<");
                website.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.realcraft.cz/vip"));
                website.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Klikni pro otevreni").create()));
                message.addExtra(website);
                player.spigot().sendMessage(message);

                TextComponent message3 = new TextComponent("    ");
                website = new TextComponent("§a§lwww.realcraft.cz/vip");
                website.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.realcraft.cz/vip"));
                website.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Klikni pro otevreni").create()));
                message3.addExtra(website);
                player.spigot().sendMessage(message3);
            } else {
                player.sendMessage("");
                player.sendMessage("    §7Dekujeme za tvou podporu §c\u2764");
            }

            player.sendMessage("");
        } else {
            player.sendMessage("");
            player.sendMessage("      " + ChatColor.BOLD + player.getName() + ", stale nemas VIP ucet?");
            player.sendMessage("    " + ChatColor.GRAY + "Ziskej zdarma " + ChatColor.LIGHT_PURPLE + "doplnky" + ChatColor.GRAY + " a vyuzivej " + ChatColor.YELLOW + "vyhody,");
            player.sendMessage("  " + ChatColor.GRAY + "o kterych se ostatnim hracum muze jen zdat!");
            player.sendMessage("");
            player.sendMessage("          Podpor nas a kup si " + ChatColor.AQUA + ChatColor.BOLD + "VIP ucet");
            TextComponent message = new TextComponent("            ");
            TextComponent website = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + ">> www.realcraft.cz <<");
            website.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.realcraft.cz/vip"));
            website.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Klikni pro otevreni").create()));
            message.addExtra(website);
            player.spigot().sendMessage(message);
            player.sendMessage("");
        }

        player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.STRIKETHROUGH + " ".repeat(60));
    }
}
