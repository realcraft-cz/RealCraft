package realcraft.bukkit.cosmetics.gadgets;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.util.Vector;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.cosmetics.utils.BlockUtils;
import realcraft.bukkit.utils.MaterialUtil;
import realcraft.bukkit.utils.RandomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GadgetPaintballGun extends Gadget {

	private List<Entity> entities = new ArrayList<>();

	public GadgetPaintballGun(CosmeticType type){
		super(type);
	}

	@Override
	public void trigger(Player player){
		Snowball snowball = (Snowball) player.getWorld().spawnEntity(player.getEyeLocation(),EntityType.SNOWBALL);
		snowball.setShooter(player);
		snowball.setCustomName("paintballgun");
		snowball.setCustomNameVisible(false);
		Vector vector = player.getLocation().getDirection().clone();
		vector.setX(vector.getX() + RandomUtil.getRandomDouble(-0.01,0.01));
		vector.setY(vector.getY() + RandomUtil.getRandomDouble(-0.01,0.01));
		vector.setZ(vector.getZ() + RandomUtil.getRandomDouble(-0.01,0.01));
		snowball.setVelocity(vector.multiply(2));
		entities.add(snowball);
		player.playSound(player.getLocation(),Sound.ENTITY_CHICKEN_EGG,1.0f,1.5f);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (entities.contains(event.getDamager())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemFrameBreak(HangingBreakEvent event) {
		for (Entity ent : entities) {
			if (ent.getLocation().distance(event.getEntity().getLocation()) < 15)
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onVehicleDestroy(VehicleDestroyEvent event) {
		for (Entity tnt : entities) {
			if (tnt.getLocation().distance(event.getVehicle().getLocation()) < 10) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void ProjectileHitEvent(ProjectileHitEvent event){
		Projectile projectile = event.getEntity();
		if(entities.contains(projectile)){
			Random r = new Random();
			Location center = event.getEntity().getLocation().add(event.getEntity().getVelocity());
			for (Block block : BlockUtils.getBlocksInRadius(center.getBlock().getLocation(), 3, false)) {
				BlockUtils.setToRestore(block, MaterialUtil.getTerracotta(DyeColor.values()[r.nextInt(DyeColor.values().length)]), 20 * 3);
			}
			entities.remove(projectile);
			this.setGadgetRunning((Player)projectile.getShooter(),false);
		}
	}
}