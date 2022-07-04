package realcraft.bukkit.develop;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import realcraft.bukkit.others.AbstractCommand;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            player.sendMessage("/wload <name> [void=false]");
            return;
        }

        WorldCreator creator = new WorldCreator(args[0]);
        creator.environment(World.Environment.NORMAL);

        if (args.length == 2 && Boolean.parseBoolean(args[1])) {
            creator.type(WorldType.FLAT);
            creator.generator("VoidGenerator");
        }

        World world = Bukkit.getServer().createWorld(creator);
        if (world == null) {
			throw new RuntimeException("World " + args[0] + " failed to load");
        }

		player.sendMessage("§dWorld "+world.getName()+" loaded");
    }

    @Override
    public List<String> tabCompleter(Player player, String[] args) {
        if (args.length <= 1) {
            ArrayList<String> completions = new ArrayList<>();

            if (args.length == 0) {
                for (String world : this._getUnloadedWorldNames()) {
                    completions.add(world);
                }
            } else {
                String search = args[0];
                for (String world : this._getUnloadedWorldNames()) {
                    if (world.contains(search)) {
                        completions.add(world);
                    }
                }
            }

            return completions;
        }

        return null;
    }

    private ArrayList<String> _getUnloadedWorldNames() {
        ArrayList<String> list = new ArrayList<>();

        File[] folders = Bukkit.getWorldContainer().listFiles();
        if (folders != null) {
            for (File file : folders) {
                if (file.isDirectory() && Bukkit.getWorld(file.getName()) == null) {
                    String[] files = file.list();
                    if (files != null && Arrays.asList(files).contains("level.dat")) {
                        list.add(file.getName());
                    }
                }
            }
        }

        return list;
    }
}
