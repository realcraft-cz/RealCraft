package realcraft.bukkit.cosmetics.gadgets;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.cosmetics.utils.MathUtils;
import realcraft.bukkit.cosmetics.utils.UtilParticles;
import realcraft.bukkit.utils.EntityUtil;
import realcraft.bukkit.utils.Particles;

import java.util.Random;

public class GadgetExplosiveSheep extends Gadget {

	public GadgetExplosiveSheep(CosmeticType type){
		super(type);
	}

	@Override
	public void trigger(Player player){
		Location loc = player.getLocation().add(player.getEyeLocation().getDirection().multiply(0.5));
		loc.setY(player.getLocation().getBlockY() + 1);
		Sheep s = player.getWorld().spawn(loc, Sheep.class);
		s.setNoDamageTicks(100000);
		new SheepColorRunnable(player, 7, true, s, this);
	}

	class SheepColorRunnable extends BukkitRunnable {
		private Player player;
		private boolean red;
		private double time;
		private Sheep s;
		private GadgetExplosiveSheep gadgetExplosiveSheep;

		public SheepColorRunnable(Player player,double time, boolean red, Sheep s, GadgetExplosiveSheep gadgetExplosiveSheep) {
			this.player = player;
			this.red = red;
			this.time = time;
			this.s = s;
			this.runTaskLater(RealCraft.getInstance(), (int) time);
			this.gadgetExplosiveSheep = gadgetExplosiveSheep;
		}


		@Override
		public void run() {
			if (red) s.setColor(DyeColor.RED);
			else s.setColor(DyeColor.WHITE);
			s.getWorld().playSound(s.getLocation(), Sound.UI_BUTTON_CLICK, 1.4f, 1.5f);
			red = !red;
			time -= 0.2;

			if (time < 0.5){
				s.getWorld().playSound(s.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.4f, 1.5f);
				UtilParticles.display(Particles.EXPLOSION_HUGE, s.getLocation());
				for (int i = 0; i < 20; i++) {
					final Sheep sheep = s.getWorld().spawn(s.getLocation(), Sheep.class);
					try {
						sheep.setColor(DyeColor.values()[MathUtils.randomRangeInt(0, 15)]);
					} catch (Exception exc) {
					}
					Random r = new Random();
					MathUtils.applyVelocity(sheep, new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2, r.nextDouble() - 0.5).multiply(2).add(new Vector(0, 0.8, 0)));
					sheep.setBaby();
					sheep.setAgeLock(true);
					sheep.setNoDamageTicks(120);
					EntityUtil.clearPathfinders(sheep);
					Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
						@Override
						public void run() {
							UtilParticles.display(Particles.LAVA, sheep.getLocation(), 5);
							sheep.remove();
						}
					},160);
				}
				Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
					@Override
					public void run() {
						setGadgetRunning(player,false);
					}
				},160);
				s.remove();
				cancel();
			} else {
				Bukkit.getScheduler().cancelTask(getTaskId());
				new SheepColorRunnable(player, time, red, s, gadgetExplosiveSheep);
			}
		}
	}
}