package realcraft.bukkit.develop;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import realcraft.bukkit.others.AbstractCommand;

public class WorldLoader extends AbstractCommand {

    public WorldLoader() {
        super("wload");
    }

    @Override
    public void perform(Player player, String[] args) {
        if (! player.hasPermission("group.Manazer")) {
            return;
        }

        if (args.length == 0) {
            player.sendMessage("Load world");
            player.sendMessage("/wload <name>");
            return;
        }

        WorldCreator creator = new WorldCreator(args[0]);
        creator.type(WorldType.FLAT);
        creator.environment(World.Environment.NORMAL);
        creator.generator("VoidGenerator");

        World world = Bukkit.getServer().createWorld(creator);
        if (world == null) {
			throw new RuntimeException("World " + args[0] + " failed to load");
        }

		player.sendMessage("§dWorld "+world.getName()+" loaded");
    }
}
