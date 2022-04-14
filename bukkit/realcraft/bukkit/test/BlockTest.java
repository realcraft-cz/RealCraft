package realcraft.bukkit.test;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.others.AbstractCommand;
import realcraft.bukkit.utils.BlockUtil;

public class BlockTest extends AbstractCommand implements Listener {

    public BlockTest() {
        super("block");
        Bukkit.getPluginManager().registerEvents(this, RealCraft.getInstance());
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.hasPermission("group.Manazer")) {
            return;
        }

        if (args.length == 0) {
            return;
        }

        int value = Integer.parseInt(args[0]);
        Location location = player.getLocation().add(0, -1, 0);
        BlockUtil.sendBlockDamage(player, location, value);
    }
}
