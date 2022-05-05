package realcraft.bukkit.test;

import org.bukkit.*;
import org.bukkit.entity.Player;
import realcraft.bukkit.others.AbstractCommand;

import java.util.ArrayList;
import java.util.List;

public class ParticlesTest extends AbstractCommand {

    public ParticlesTest() {
        super("particle");
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.hasPermission("group.Manazer")) {
            return;
        }

        if (args.length == 0) {
            player.sendMessage("/particle <particle> [count] [speed]");
            return;
        }

        try {
            Particle particle = Particle.valueOf(args[0].toUpperCase());

            int count = 10;
            float speed = 0.1f;
            if (args.length > 1) count = Integer.valueOf(args[1]);
            if (args.length > 2) speed = Float.valueOf(args[2]);
            Location location = player.getLocation();
            location.setPitch(0f);
            location.add(0, 1, 0);
            location.add(location.getDirection().setY(0).normalize().multiply(3));
            player.spawnParticle(particle, location, count, 0, 0, 0, speed);
            player.sendMessage("§7Effect:§r " + particle.toString());
        } catch (IllegalArgumentException ignored) {
            player.sendMessage("§c" + ignored.getMessage());
        }
    }

    @Override
    public List<String> tabCompleter(Player player, String[] args) {
        if (args.length <= 1) {
            ArrayList<String> completions = new ArrayList<>();

            if (args.length == 0) {
                for (Particle particle : Particle.values()) {
                    completions.add(particle.toString());
                }
            } else {
                String search = args[0].toUpperCase();
                for (Particle particle : Particle.values()) {
                    if (particle.toString().startsWith(search)) {
                        completions.add(particle.toString());
                    }
                }
            }

            return completions;
        }

        return null;
    }
}