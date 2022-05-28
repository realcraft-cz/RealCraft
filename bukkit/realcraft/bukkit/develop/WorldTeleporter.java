package realcraft.bukkit.develop;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import realcraft.bukkit.others.AbstractCommand;

import java.util.ArrayList;
import java.util.List;

public class WorldTeleporter extends AbstractCommand {

	public WorldTeleporter(){
		super("wtp");
	}

	@Override
	public void perform(Player player, String[] args) {
		if (! player.hasPermission("group.Manazer")) {
			return;
		}

		if (args.length == 0) {
			player.sendMessage("Teleport to world");
			player.sendMessage("/wtp <name>");
			return;
		}

		World world = Bukkit.getWorld(args[0]);
		if (world == null) {
			player.sendMessage("§cWorld does not exists");
		}

		player.teleport(world.getSpawnLocation());
	}

	@Override
	public List<String> tabCompleter(Player player, String[] args) {
		if (args.length <= 1) {
			ArrayList<String> completions = new ArrayList<>();

			if (args.length == 0) {
				for (World world : Bukkit.getWorlds()) {
					completions.add(world.getName());
				}
			} else {
				String search = args[0];
				for (World world : Bukkit.getWorlds()) {
					if (world.getName().contains(search)) {
						completions.add(world.getName());
					}
				}
			}

			return completions;
		}

		return null;
	}
}