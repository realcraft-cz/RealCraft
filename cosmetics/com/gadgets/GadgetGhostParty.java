package com.gadgets;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.realcraft.RealCraft;
import com.realcraft.utils.Particles;
import com.utils.ItemFactory;
import com.utils.UtilParticles;

public class GadgetGhostParty extends Gadget {

	public GadgetGhostParty(GadgetType type){
		super(type);
	}

	@Override
	public void onClick(final Player player){
		final Map<Bat, ArmorStand> bats = new HashMap<>();
		for (int i = 0; i < 20; i++) {
            Bat bat = player.getWorld().spawn(player.getLocation().add(0, 1, 0), Bat.class);
            ArmorStand ghost = bat.getWorld().spawn(bat.getLocation(), ArmorStand.class);
            ghost.setSmall(true);
            ghost.setGravity(false);
            ghost.setVisible(false);
            ghost.setHelmet(ItemFactory.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhkMjE4MzY0MDIxOGFiMzMwYWM1NmQyYWFiN2UyOWE5NzkwYTU0NWY2OTE2MTllMzg1NzhlYTRhNjlhZTBiNiJ9fX0"," "));
            ghost.setChestplate(ItemFactory.createColouredLeather(Material.LEATHER_CHESTPLATE, 255, 255, 255));
            ghost.setItemInHand(new ItemStack(Material.DIAMOND_HOE));
            bat.setPassenger(ghost);
            bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 160, 1));
            bats.put(bat,ghost);
        }
		final BukkitRunnable runnable = new BukkitRunnable(){
			@Override
			public void run(){
				if (!bats.isEmpty()) {
	                for (Bat bat : bats.keySet())
	                    UtilParticles.display(Particles.CLOUD, 0.05f, 0.05f, 0.05f, bat.getLocation().add(0, 1.5, 0), 1);
				}
			}
		};
		runnable.runTaskTimerAsynchronously(RealCraft.getInstance(),0,2);
        Bukkit.getScheduler().runTaskLaterAsynchronously(RealCraft.getInstance(), new Runnable() {
            @Override
            public void run() {
            	runnable.cancel();
            	for (Bat bat : bats.keySet()) {
                    bats.get(bat).remove();
                    bat.remove();
                }
                bats.clear();
                setRunning(player,false);
            }
        }, 160);
	}

	@EventHandler
    public void onPlayerInteractGhost(PlayerInteractAtEntityEvent event) {
        if(event.getRightClicked() != null && event.getRightClicked().getVehicle() != null && event.getRightClicked().getType() == EntityType.ARMOR_STAND) event.setCancelled(true);
    }
}