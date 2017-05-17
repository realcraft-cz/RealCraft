package com.gadgets;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.realcraft.RealCraft;
import com.realcraft.utils.Particles;
import com.utils.MathUtils;
import com.utils.UtilParticles;

public class GadgetTsunami extends Gadget {

	public GadgetTsunami(GadgetType type){
		super(type);
	}

	@Override
	public void onClick(final Player player){
		final List<Entity> cooldownJump = new ArrayList<>();
		final Vector v = player.getLocation().getDirection().normalize().multiply(0.3);
        v.setY(0);
        final Location loc = player.getLocation().subtract(0, 1, 0).add(v);
        final int i = Bukkit.getScheduler().runTaskTimerAsynchronously(RealCraft.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (loc.getBlock().getType() != Material.AIR
                        && loc.getBlock().getType().isSolid())
                    loc.add(0, 1, 0);
                if (loc.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR)
                    loc.add(0, -1, 0);
                Location loc1 = loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(0, .5) - 0.75, MathUtils.randomDouble(-1.5, 1.5));
                Location loc2 = loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(1.3, 1.8) - 0.75, MathUtils.randomDouble(-1.5, 1.5));
                for (int i = 0; i < 5; i++) {
                    UtilParticles.display(Particles.EXPLOSION_NORMAL, 0.2d, 0.2d, 0.2d, loc1, 1);
                    UtilParticles.display(Particles.DRIP_WATER, 0.4d, 0.4d, 0.4d, loc2, 2);
                }
                for (int a = 0; a < 100; a++) UtilParticles.display(0, 0, 255, loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(1, 1.6) - 0.75, MathUtils.randomDouble(-1.5, 1.5)));

                for (final Entity ent : player.getWorld().getEntities()) {
                    if (ent.getLocation().distance(loc) < 0.6 &&
                            !cooldownJump.contains(ent) &&
                            ent != player && !(ent instanceof ArmorStand)) {
                        MathUtils.applyVelocity(ent, new Vector(0, 1, 0).add(v.clone().multiply(2)));
                        cooldownJump.add(ent);
                        Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                cooldownJump.remove(ent);
                            }
                        }, 20);
                    }
                }

                loc.add(v);
            }
        }, 0, 1).getTaskId();

        Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
            @Override
            public void run() {
            	setRunning(player,false);
                Bukkit.getScheduler().cancelTask(i);
            }
        }, 40);
	}
}