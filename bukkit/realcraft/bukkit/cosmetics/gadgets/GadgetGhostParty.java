package realcraft.bukkit.cosmetics.gadgets;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.cosmetics2.utils.ItemFactory;
import realcraft.bukkit.cosmetics2.utils.UtilParticles;
import realcraft.bukkit.utils.ItemUtil;
import realcraft.bukkit.utils.Particles;
import realcraft.share.utils.RandomUtil;

import java.util.HashMap;
import java.util.Map;

public class GadgetGhostParty extends Gadget {

	public GadgetGhostParty(CosmeticType type){
		super(type);
	}

	@Override
	public void trigger(Player player){
		Location location = player.getLocation();
		final Map<Bat, ArmorStand> bats = new HashMap<>();
		for (int i = 0; i < 20; i++) {
			Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					Bat bat = player.getWorld().spawn(location.clone().add(RandomUtil.getRandomInteger(-2,2),2,RandomUtil.getRandomInteger(-2,2)),Bat.class);
					ArmorStand ghost = bat.getWorld().spawn(bat.getLocation(), ArmorStand.class);
					ghost.setSmall(true);
					ghost.setGravity(false);
					ghost.setVisible(false);
					ghost.setHelmet(ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhkMjE4MzY0MDIxOGFiMzMwYWM1NmQyYWFiN2UyOWE5NzkwYTU0NWY2OTE2MTllMzg1NzhlYTRhNjlhZTBiNiJ9fX0"));
					ghost.setChestplate(ItemFactory.createColouredLeather(Material.LEATHER_CHESTPLATE, 255, 255, 255));
					bat.addPassenger(ghost);
					bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 1));
					bats.put(bat,ghost);
				}
			},i*5);
		}
		final BukkitRunnable runnable = new BukkitRunnable(){
			@Override
			public void run(){
				if (!bats.isEmpty()) {
					for (Bat bat : bats.keySet())
						if(!bat.isDead()) UtilParticles.display(Particles.CLOUD, 0.05f, 0.05f, 0.05f, bat.getLocation().add(0, 1.5, 0), 1);
				}
			}
		};
		runnable.runTaskTimerAsynchronously(RealCraft.getInstance(),0,2);
		for (int i = 0; i < 20; i++){
			Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable() {
				@Override
				public void run(){
					for(Bat bat : bats.keySet()){
						if(!bat.isDead()){
							bats.get(bat).remove();
							bat.remove();
							break;
						}
					}
				}
			},100+(i*5));
		}
		Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				runnable.cancel();
				for (Bat bat : bats.keySet()) {
					bats.get(bat).remove();
					bat.remove();
				}
				bats.clear();
				setGadgetRunning(player,false);
			}
		},200);
	}

	@EventHandler
	public void onPlayerInteractGhost(PlayerInteractAtEntityEvent event) {
		if(event.getRightClicked() != null && event.getRightClicked().getVehicle() != null && event.getRightClicked().getType() == EntityType.ARMOR_STAND) event.setCancelled(true);
	}
}