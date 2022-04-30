package realcraft.bukkit.sitting;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.spigotmc.event.entity.EntityDismountEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.utils.LocationUtil;

import java.util.HashMap;

public class Sitting implements Listener {

	private HashMap<Player,ArmorStand> stands = new HashMap<>();

	public Sitting(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	public void setSitting(Player player,Location location,boolean sitting){
		if(sitting){
			Stairs stairs = (Stairs) location.getBlock().getBlockData();
			this.setSitting(player,location,false);
			location.setYaw(LocationUtil.faceToYaw(stairs.getFacing()));
			player.teleport(location);
			ArmorStand stand = (ArmorStand)location.getWorld().spawnEntity(location.clone().add(0.5,0.2,0.5),EntityType.ARMOR_STAND);
			stand.setSmall(true);
			stand.setBasePlate(false);
			stand.setArms(false);
			stand.setInvisible(true);
			stand.setGravity(false);
			stand.setInvulnerable(true);
			stand.setMarker(true);
			stands.put(player,stand);
			stand.addPassenger(player);
		} else {
			if(stands.containsKey(player)){
				player.leaveVehicle();
				if(stands.get(player) != null){
					Location exitLocation = stands.get(player).getLocation().getBlock().getRelative(LocationUtil.yawToFace(stands.get(player).getLocation().getYaw()).getOppositeFace()).getLocation().add(0.5,0.0,0.5);
					exitLocation.setYaw(player.getLocation().getYaw());
					exitLocation.setPitch(player.getLocation().getPitch());
					Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable() {
						public void run(){
							player.teleport(exitLocation);
						}
					});
					stands.get(player).remove();
				}
				stands.remove(player);
			}
		}
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		if(event.getClickedBlock() == null || !(event.getClickedBlock().getBlockData() instanceof Stairs)) return;
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND || event.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) return;
		if(event.getClickedBlock().getRelative(BlockFace.UP).getType() != Material.AIR) return;
		if(event.getPlayer().isInsideVehicle()) return;
		Stairs stairs = (Stairs) event.getClickedBlock().getBlockData();
		if(stairs.getShape() != Stairs.Shape.STRAIGHT || stairs.getHalf() != Bisected.Half.BOTTOM) return;
		//if(event.getClickedBlock().getRelative(stairs.getFacing().getOppositeFace()).getType() != Material.AIR) return;
		event.setCancelled(true);
		this.setSitting(event.getPlayer(),event.getClickedBlock().getLocation(),true);
	}

	@EventHandler
	public void EntityDismountEvent(EntityDismountEvent event){
		if(event.getEntity().getType() != EntityType.PLAYER || event.getDismounted().getType() != EntityType.ARMOR_STAND) return;
		this.setSitting((Player)event.getEntity(),event.getDismounted().getLocation(),false);
	}

	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent event){
		this.setSitting(event.getPlayer(),event.getPlayer().getLocation(),false);
	}
}