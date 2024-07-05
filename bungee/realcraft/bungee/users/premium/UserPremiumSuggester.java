package realcraft.bungee.users.premium;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import realcraft.bungee.RealCraftBungee;
import realcraft.bungee.users.Users;
import realcraft.share.users.User;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class UserPremiumSuggester implements Listener {

    private final HashMap<User, Integer> lastReminds = new HashMap<>();

    public UserPremiumSuggester() {
        ProxyServer.getInstance().getPluginManager().registerListener(RealCraftBungee.getInstance(), this);
    }

    @EventHandler
    public void PostLoginEvent(PostLoginEvent event) {
        User user = Users.getUser(event.getPlayer());

        if (user.isPremium()) {
            return;
        }

        RealCraftBungee.getInstance().getProxy().getScheduler().schedule(RealCraftBungee.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (!event.getPlayer().isConnected()) {
                    return;
                }

                if (lastReminds.containsKey(user)) {
                    return;
                }

                UserPremiumSuggester.this._suggestPremium(user);
            }
        }, 180, TimeUnit.SECONDS);
    }

    private void _suggestPremium(User user) {
        lastReminds.put(user, (int) (System.currentTimeMillis() / 1000));

        UserPremiumManager.checkPossiblePremium(user).whenComplete((isPremium, throwable) -> {
            if (!isPremium) {
                return;
            }

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(user.getName());

            if (!player.isConnected()) {
                return;
            }

            player.sendMessage("");
            player.sendMessage("§eMas originalni Minecraft ucet?");
            player.sendMessage("§fAktivuj si prihlaseni bez hesla - §6§l/premium");
        });
    }
}
