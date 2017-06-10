package com.gadgets;

import java.lang.reflect.Field;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.collect.Sets;
import com.realcraft.RealCraft;
import com.realcraft.utils.Particles;
import com.utils.CustomPathFinderGoalPanic;
import com.utils.MathUtils;
import com.utils.UtilParticles;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityCreature;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.PathfinderGoalSelector;

public class GadgetExplosiveSheep extends Gadget {

	public GadgetExplosiveSheep(GadgetType type){
		super(type);
	}

	@Override
	public void onClick(Player player){
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
                for (int i = 0; i < 40; i++) {
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
                    clearPathfinders(sheep);
                    makePanic(sheep);
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
                        setRunning(player,false);
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

	public void clearPathfinders(org.bukkit.entity.Entity entity) {
        Entity nmsEntity = ((CraftEntity) entity).getHandle();
        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(((EntityInsentient) nmsEntity).goalSelector, Sets.newLinkedHashSet());
            bField.set(((EntityInsentient) nmsEntity).targetSelector, Sets.newLinkedHashSet());
            cField.set(((EntityInsentient) nmsEntity).goalSelector, Sets.newLinkedHashSet());
            cField.set(((EntityInsentient) nmsEntity).targetSelector, Sets.newLinkedHashSet());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

	public void makePanic(org.bukkit.entity.Entity entity) {
        EntityInsentient insentient = (EntityInsentient) ((CraftEntity) entity).getHandle();
        insentient.goalSelector.a(3, new CustomPathFinderGoalPanic((EntityCreature) insentient, 0.4d));
    }
}