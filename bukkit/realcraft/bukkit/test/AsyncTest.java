package realcraft.bukkit.test;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.others.AbstractCommand;

public class AsyncTest extends AbstractCommand implements Listener {

    public AsyncTest() {
        super("async");
        Bukkit.getPluginManager().registerEvents(this, RealCraft.getInstance());
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.hasPermission("group.Manazer")) {
            return;
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(RealCraft.getInstance(), new Runnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getServer().getOnlinePlayers()) {
                    System.out.println(player.getLocation());
                }
            }
        }, 20, 20);
    }
}
