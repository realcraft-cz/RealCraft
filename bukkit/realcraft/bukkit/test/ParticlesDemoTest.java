package realcraft.bukkit.test;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.others.AbstractCommand;

public class ParticlesDemoTest extends AbstractCommand implements Runnable {

    private BukkitTask task;

    private int count;
    private float speed;
    private Location location;

    public ParticlesDemoTest() {
        super("particledemo");
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.hasPermission("group.Manazer")) {
            return;
        }

        if (task != null) {
            task.cancel();
            task = null;
            return;
        }

        if (args.length == 0) {
            player.sendMessage("/particledemo [count] [speed] [ticks]");
            return;
        }

        count = 10;
        speed = 0.1f;
        int ticks = 20;
        if (args.length > 0) count = Integer.parseInt(args[0]);
        if (args.length > 1) speed = Float.parseFloat(args[1]);
        if (args.length > 2) ticks = Integer.parseInt(args[2]);

        location = player.getLocation();
        location.setPitch(0f);
        location.add(0, 2, 0);

        task = Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(), this, ticks, ticks);
    }

    @Override
    public void run() {
        Location tmpLocation = location.clone();

        for (Particle particle : Particle.values()) {
            if (particle == Particle.MOB_APPEARANCE || particle == Particle. EXPLOSION_HUGE) {
                continue;
            }

            tmpLocation.add(location.getDirection().setY(0).normalize().multiply(2));
            try {
                location.getWorld().spawnParticle(particle, tmpLocation, count, 0, 0, 0, speed);
            } catch (Exception ignored) {
            }
        }
    }
}
