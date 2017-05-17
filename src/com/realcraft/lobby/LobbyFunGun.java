package com.realcraft.lobby;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.realcraft.RealCraft;
import com.realcraft.auth.AuthLoginEvent;
import com.realcraft.utils.Particles;
import com.realcraft.utils.RandomUtil;

public class LobbyFunGun implements Listener {
	RealCraft plugin;

	HashMap<String,Long> lastShotByFunGun = new HashMap<String,Long>();

	public LobbyFunGun(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	public void onReload(){
	}

	@EventHandler
	public void AuthLoginEvent(AuthLoginEvent event){
		Player player = event.getPlayer();
		ItemStack fungun = new ItemStack(Material.BLAZE_ROD,1);
		ItemMeta meta = fungun.getItemMeta();
		meta.setDisplayName("§6§lFun Gun");
		fungun.setItemMeta(meta);
		player.getInventory().setItem(1,fungun);
	}

	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(plugin.playermanazer.getPlayerInfo(player).isLogged() && player.getWorld().getName().equalsIgnoreCase("world")){
			ItemStack fungun = new ItemStack(Material.BLAZE_ROD,1);
			ItemMeta meta = fungun.getItemMeta();
			meta.setDisplayName("§6§lFun Gun");
			fungun.setItemMeta(meta);
			player.getInventory().setItem(1,fungun);
		}
	}

	@EventHandler
	public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event){
		ItemStack fungun = new ItemStack(Material.BLAZE_ROD,1);
		ItemMeta meta = fungun.getItemMeta();
		meta.setDisplayName("§6§lFun Gun");
		fungun.setItemMeta(meta);
		if(event.getFrom().getName().equalsIgnoreCase("world")){
			event.getPlayer().getInventory().remove(fungun);
		}
		else if(event.getPlayer().getWorld().getName().equalsIgnoreCase("world")){
			event.getPlayer().getInventory().setItem(1,fungun);
		}
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(player.getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD && (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
			event.setCancelled(true);
			if(plugin.playermanazer.getPlayerInfo(player).isLogged()){
				this.fireFunGun(player);
			}
		}
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player && event.getSlotType() == SlotType.QUICKBAR && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BLAZE_ROD){
			event.setCancelled(true);
		}
	}

	private void fireFunGun(Player player){
		if(!lastShotByFunGun.containsKey(player.getName()) || lastShotByFunGun.get(player.getName())+2000 < System.currentTimeMillis()){
			for(int i=0;i<3;i++){
				Snowball snowball = (Snowball) player.getWorld().spawnEntity(player.getEyeLocation(),EntityType.SNOWBALL);
				snowball.setShooter(player);
				snowball.setCustomName("fungun");
				snowball.setCustomNameVisible(false);
				Vector vector = player.getLocation().getDirection().clone();
	            vector.setX(vector.getX() + RandomUtil.getRandomDouble(-0.01,0.01));
	            vector.setY(vector.getY() + RandomUtil.getRandomDouble(-0.01,0.01));
	            vector.setZ(vector.getZ() + RandomUtil.getRandomDouble(-0.01,0.01));
	            snowball.setVelocity(vector.multiply(2));
			}
			lastShotByFunGun.put(player.getName(),System.currentTimeMillis());
		}
	}

	@EventHandler
	public void ProjectileHitEvent(ProjectileHitEvent event){
		Projectile projectile = event.getEntity();
		if(projectile.getType() == EntityType.SNOWBALL && projectile.getWorld().getName().equalsIgnoreCase("world") && projectile.getCustomName() != null && projectile.getCustomName().equalsIgnoreCase("fungun")){
			Location location = projectile.getLocation();
			event.getEntity().remove();
			Particles.LAVA.display(1.3f,1f,1.3f,0f,5,location,128);
			Particles.HEART.display(0.8f,0.8f,0.8f,0f,6,location,128);
			location.getWorld().playSound(location,Sound.ENTITY_CAT_PURREOW,1.0f,1.5f);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Snowball){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void PlayerDropItemEvent(PlayerDropItemEvent event){
		if(event.getItemDrop().getItemStack().getType() == Material.BLAZE_ROD){
			event.setCancelled(true);
		}
	}
}