package realcraft.bukkit.test;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import realcraft.bukkit.others.AbstractCommand;

import java.util.ArrayList;
import java.util.List;

public class SoundsTest extends AbstractCommand {

    public SoundsTest() {
        super("sound");
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.hasPermission("group.Manazer")) {
            return;
        }

        if (args.length == 0) {
            player.sendMessage("/sound <sound> [pitch]");
            return;
        }

        try {
            Sound sound = Sound.valueOf(args[0].toUpperCase());

            float pitch = 1f;
            if (args.length > 1) pitch = Float.valueOf(args[1]);
            player.playSound(player.getLocation(), sound, 1f, pitch);
            player.sendMessage("§7Sound:§r " + sound.toString());
        } catch (IllegalArgumentException ignored) {
            player.sendMessage("§c" + ignored.getMessage());
        }
    }

    @Override
    public List<String> tabCompleter(Player player, String[] args) {
        if (args.length <= 1) {
            ArrayList<String> completions = new ArrayList<>();

            if (args.length == 0) {
                for (Sound sound : Sound.values()) {
                    completions.add(sound.toString());
                }
            } else {
                String search = args[0].toUpperCase();
                for (Sound sound : Sound.values()) {
                    if (sound.toString().contains(search)) {
                        completions.add(sound.toString());
                    }
                }
            }

            return completions;
        }

        return null;
    }
}