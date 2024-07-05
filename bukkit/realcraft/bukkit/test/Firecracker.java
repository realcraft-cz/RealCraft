package realcraft.bukkit.test;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.others.AbstractCommand;

public class Firecracker extends AbstractCommand implements Listener {

	public Firecracker() {
		super("firecracker");
	}

	@Override
	public void perform(Player player, String[] args) {
		Item firecracker = player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.RED_CANDLE));
		firecracker.setPickupDelay(Integer.MAX_VALUE);
		firecracker.setVelocity(player.getLocation().getDirection().multiply(0.4));

		new BukkitRunnable() {
			private int count;

			@Override
			public void run() {
				if (firecracker.isOnGround()) {
					count ++;

					if (count < 6) {
						firecracker.getWorld().spawnParticle(Particle.SMOKE, firecracker.getLocation().add(0, 0.8, 0), 4, 0, 0.1, 0, 0);
						return;
					}

					if (count == 6) {
						firecracker.getWorld().playSound(firecracker.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 3f, 1f);
						firecracker.getWorld().spawnParticle(Particle.EXPLOSION, firecracker.getLocation().add(0, 0.2, 0), 1, 0, 0, 0, 0);
					}

					if (count == 6 || count == 7) {
						firecracker.getWorld().playSound(firecracker.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 3f, 1f);
						firecracker.getWorld().spawnParticle(Particle.FIREWORK, firecracker.getLocation().add(0, 0.2, 0), 8, 0.2, 0.2, 0.2, 0.1f);
						firecracker.getWorld().spawnParticle(Particle.WAX_ON, firecracker.getLocation().add(0, 0.2, 0), 8, 0.2, 0.2, 0.2, 10f);
						firecracker.getWorld().spawnParticle(Particle.LAVA, firecracker.getLocation().add(0, 0.2, 0), 4, 0.2, 0.2, 0.2, 1f);
					}

					if (!firecracker.isDead()) {
						firecracker.remove();
					}

					if (count >= 7) {
						this.cancel();
					}
				}
			}
		}.runTaskTimer(RealCraft.getInstance(), 6, 6);
	}
}
