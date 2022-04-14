package realcraft.bukkit.test;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.others.AbstractCommand;

public class ViewTest extends AbstractCommand implements Listener {

    public ViewTest() {
        super("view");
        Bukkit.getPluginManager().registerEvents(this, RealCraft.getInstance());
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.hasPermission("group.Manazer")) {
            return;
        }

        if (args.length == 0) {
            player.sendMessage("Current: " + player.getWorld().getViewDistance() + ", " + player.getWorld().getSendViewDistance() + ", " + player.getWorld().getSimulationDistance());
            player.sendMessage("/view <view distance> [simulation distance]");
            return;
        }

        if (args.length > 0) {
            int distance = Integer.parseInt(args[0]);
            player.getWorld().setViewDistance(distance);
            player.getWorld().setSendViewDistance(distance + 1);

            if (player.getWorld().getSimulationDistance() > distance) {
                player.getWorld().setSimulationDistance(distance);
            }
        }

        if (args.length > 1) {
            int distance = Integer.parseInt(args[1]);
            player.getWorld().setSimulationDistance(distance);
        }
    }
}
