package realcraft.bukkit.cosmetics.gadgets;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.util.Vector;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.anticheat.AntiCheat;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.cosmetics.utils.UtilParticles;
import realcraft.bukkit.utils.Particles;

import java.util.ArrayList;
import java.util.List;

public class GadgetTNT extends Gadget {

	private List<Entity> entities = new ArrayList<>();

	public GadgetTNT(CosmeticType type){
		super(type);
	}

	@Override
	public void trigger(Player player){
		TNTPrimed tnt = player.getWorld().spawn(player.getLocation().add(0, 2, 0), TNTPrimed.class);
		tnt.setFuseTicks(20);
		tnt.setVelocity(player.getLocation().getDirection().multiply(0.854321));
		entities.add(tnt);

		Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
			@Override
			public void run(){
				setGadgetRunning(player,false);
			}
		},20);
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
	public void onEntityExplode(EntityExplodeEvent event) {
		if (entities.contains(event.getEntity())){
			event.setCancelled(true);
			event.blockList().clear();
			UtilParticles.display(Particles.EXPLOSION_HUGE, event.getEntity().getLocation());
			Particles.FIREWORKS_SPARK.display(0.5f,0.5f,0.5f,0.35f,64,event.getEntity().getLocation(),64);

			for(Entity ent : event.getEntity().getNearbyEntities(3, 3, 3)){
				if(ent instanceof Player){
					if(ent instanceof Player) ((Player)ent).playSound(ent.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.4f, 1.5f);
					double dX = event.getEntity().getLocation().getX() - ent.getLocation().getX();
					double dY = event.getEntity().getLocation().getY() - ent.getLocation().getY();
					double dZ = event.getEntity().getLocation().getZ() - ent.getLocation().getZ();
					double yaw = Math.atan2(dZ, dX);
					double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
					double X = Math.sin(pitch) * Math.cos(yaw);
					double Y = Math.sin(pitch) * Math.sin(yaw);
					double Z = Math.cos(pitch);

					Vector vector = new Vector(X, Z, Y);
					ent.setVelocity(vector.multiply(1.3D).add(new Vector(0, 1.4D, 0)));
					if(ent instanceof Player) AntiCheat.exempt((Player)ent,2000);
				}
			}
			entities.remove(event.getEntity());
		}
	}
}