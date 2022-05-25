package realcraft.bukkit.vip;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.DateUtil;
import realcraft.share.users.User;

import java.util.HashMap;

public class VipReminder implements Runnable {

    private final HashMap<User, Integer> lastReminds = new HashMap<>();

    public VipReminder() {
        Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(), this, 300 * 20, 300 * 20);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = Users.getUser(player);

            if (lastReminds.containsKey(user)) {
                continue;
            }

            VipManager.getVip(user).load().whenComplete((vip, throwable) -> {
                if (!vip.isActive()) {
                    return;
                }

                if (!vip.isAboutToExpire()) {
                    return;
                }

                lastReminds.put(user, DateUtil.getTimestamp());
                this._remindExpiration(player);
            });
        }
    }

    private void _remindExpiration(Player player) {
        Vip vip = VipManager.getVip(player);

        player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.STRIKETHROUGH + " ".repeat(60));

        player.sendMessage("");
        player.sendMessage("    §fTve §b§lVIP clenstvi§r konci §l" + DateUtil.getDate(vip.getActiveTo(), "d. M. yyyy")+",");
        player.sendMessage("    prodluz si ho a vyuzivej vyhody i nadale.");
        player.sendMessage("");
        TextComponent message2 = new TextComponent("    ");
        TextComponent website2 = new TextComponent(ChatColor.GOLD + "" + ChatColor.BOLD + ">> Prodlouzit VIP <<");
        website2.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.realcraft.cz/vip"));
        website2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Klikni pro otevreni").create()));
        message2.addExtra(website2);
        player.spigot().sendMessage(message2);

        TextComponent message3 = new TextComponent("    ");
        website2 = new TextComponent("§a§lwww.realcraft.cz/vip");
        website2.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.realcraft.cz/vip"));
        website2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Klikni pro otevreni").create()));
        message3.addExtra(website2);
        player.spigot().sendMessage(message3);
        player.sendMessage("");

        player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.STRIKETHROUGH + " ".repeat(60));
    }
}
