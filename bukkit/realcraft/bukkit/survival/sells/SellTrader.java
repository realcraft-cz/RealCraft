package realcraft.bukkit.survival.sells;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import realcraft.bukkit.RealCraft;

public class SellTrader implements Listener {

	private Location location;
	private Villager entity;

	public SellTrader(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		location = new Location(Bukkit.getWorld("world"),112.5,72.0,-54.5,0f,0f);
		Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				SellTrader.this.spawn();
			}
		});
		Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				if(entity == null || entity.isDead()){
					SellTrader.this.spawn();
				}
			}
		},20*30,20*30);
	}

	public boolean isInChunk(Chunk chunk){
		return (location.getBlockX() >> 4 == chunk.getX() && location.getBlockZ() >> 4 == chunk.getZ());
	}

	public void spawn(){
		this.remove();
		Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER, false);
		villager.setAI(false);
		villager.setRemoveWhenFarAway(false);
		villager.setInvulnerable(false);
		villager.setCustomNameVisible(true);
		villager.setCustomName("§e§lVykupna");
		villager.teleport(location);
	}

	public void remove(){
		if(entity != null && !entity.isDead()){
			entity.remove();
			entity = null;
		}
	}

	public void click(Player player){
		SellMenu.openMenu(player);
	}

	@EventHandler
	public void ChunkLoadEvent(ChunkLoadEvent event){
		if(this.isInChunk(event.getChunk())){
			this.spawn();
		}
	}

	@EventHandler
	public void ChunkUnloadEvent(ChunkUnloadEvent event){
		if(this.isInChunk(event.getChunk())){
			this.remove();
		}
	}

	@EventHandler
	public void EntityDamageEvent(EntityDamageEvent event){
		if(event.getEntity().getType() == EntityType.VILLAGER && entity != null && entity.getEntityId() == event.getEntity().getEntityId()){
			event.setCancelled(true);
		}
	}

	@EventHandler(priority= EventPriority.LOW)
	public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event){
		if(event.getHand().equals(EquipmentSlot.HAND) && event.getRightClicked().getType() == EntityType.VILLAGER && entity != null && entity.getEntityId() == event.getRightClicked().getEntityId()){
			event.setCancelled(true);
			this.click(event.getPlayer());
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(event.getEntity().getType() == EntityType.VILLAGER && entity != null && entity.getEntityId() == event.getEntity().getEntityId()){
			event.setCancelled(true);
			if(event.getDamager() instanceof Player){
				this.click((Player)event.getDamager());
			}
		}
	}
}