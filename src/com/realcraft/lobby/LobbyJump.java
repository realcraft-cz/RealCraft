package com.realcraft.lobby;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.anticheat.AntiCheat;
import com.realcraft.RealCraft;
import com.realcraft.auth.AuthLoginEvent;
import com.realcraft.utils.Particles;

public class LobbyJump implements Listener {
	RealCraft plugin;

	double velocityMultiply;
	double velocityHeight;

	public LobbyJump(RealCraft realcraft){
		plugin = realcraft;
		velocityMultiply = plugin.config.getDouble("lobby.jump.velocityMultiply",1.6);
		velocityHeight = plugin.config.getDouble("lobby.jump.velocityHeight",1.0);
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	public void onReload(){
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		player.setAllowFlight(false);
		player.setFlying(false);
	}

	@EventHandler
	public void AuthLoginEvent(AuthLoginEvent event){
		Player player = event.getPlayer();
		if(player.getWorld().getName().equalsIgnoreCase("world")){
			if(plugin.playermanazer.getPlayerInfo(player).getRank() >= 20){
				player.setAllowFlight(true);
				player.setFlying(false);

				/*ItemStack elytra = new ItemStack(Material.ELYTRA,1);
				elytra.addEnchantment(Enchantment.DURABILITY,3);
				player.getInventory().setChestplate(elytra);

				ItemStack starboost = new ItemStack(Material.NETHER_STAR,1);
				ItemMeta meta = starboost.getItemMeta();
				meta.setDisplayName("§b§lElytra Boost");
				starboost.setItemMeta(meta);
				player.getInventory().setItem(2,starboost);*/
			}
		}
	}

	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(player.getWorld().getName().equalsIgnoreCase("world") && plugin.playermanazer.getPlayerInfo(player).isLogged()){
			if(plugin.playermanazer.getPlayerInfo(player).getRank() >= 20){
				player.setAllowFlight(true);
				player.setFlying(false);

				/*ItemStack elytra = new ItemStack(Material.ELYTRA,1);
				elytra.addEnchantment(Enchantment.DURABILITY,3);
				player.getInventory().setChestplate(elytra);

				ItemStack starboost = new ItemStack(Material.NETHER_STAR,1);
				ItemMeta meta = starboost.getItemMeta();
				meta.setDisplayName("§b§lElytra Boost");
				starboost.setItemMeta(meta);
				player.getInventory().setItem(2,starboost);*/
			}
		}
	}

	@EventHandler(priority=EventPriority.LOW,ignoreCancelled=true)
	public void PlayerToggleFlightEvent(PlayerToggleFlightEvent event){
		final Player player = event.getPlayer();
		if((player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) && event.isFlying() && !player.isGliding()){
			if(player.getWorld().getName().equalsIgnoreCase("world") && plugin.playermanazer.getPlayerInfo(player).getRank() >= 20){
				event.setCancelled(true);

				plugin.playermanazer.getPlayerInfo(player).setLastLobbyJump(System.currentTimeMillis());
				player.setFlying(false);
				player.setVelocity(player.getLocation().getDirection().multiply(velocityMultiply).setY(velocityHeight));
				player.setAllowFlight(false);
				player.playSound(player.getLocation(),Sound.ENTITY_BAT_TAKEOFF,1,1);

				for(int i=0;i<10;i++){
					plugin.getServer().getScheduler().runTaskLater(plugin,new Runnable(){
						@Override
						public void run(){
							Particles.SPELL_WITCH.display(0.4f,0.4f,0.4f,0f,2,player.getLocation(),128);
						}
					},i);
				}

				AntiCheat.exempt(player,2000);
			} else {
				event.setCancelled(true);
				player.setFlying(false);
				player.setAllowFlight(false);
			}
		}
	}

	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode() == GameMode.ADVENTURE && player.isFlying()){
			player.setFlying(false);
		}
		else if(player.getGameMode() == GameMode.ADVENTURE && player.getWorld().getName().equalsIgnoreCase("world") && player.getLocation().getBlock().getRelative(0,-1,0).getType() != Material.AIR
				&& !player.isFlying() && plugin.playermanazer.getPlayerInfo(player).getLastLobbyJump()+200 < System.currentTimeMillis() && plugin.playermanazer.getPlayerInfo(player).getRank() >= 20){
			player.setAllowFlight(true);
		}
	}

	/*@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player && event.getSlotType() == SlotType.QUICKBAR){
			event.setCancelled(true);
		}
	}*/

	@EventHandler(ignoreCancelled = false)
	public void PlayerDropItemEvent(PlayerDropItemEvent event){
		/*if(event.getItemDrop().getItemStack().getType() == Material.NETHER_STAR){
			event.setCancelled(true);
		}
		else if(event.getItemDrop().getItemStack().getType() == Material.ELYTRA){
			event.getItemDrop().remove();
			event.getPlayer().getInventory().remove(Material.NETHER_STAR);
		}*/
	}

	@EventHandler
	public void EntityDamageEvent(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			Player player = (Player) event.getEntity();
			if(event.getEntity().getWorld().getName().equalsIgnoreCase("world")){
				if(event.getCause() == EntityDamageEvent.DamageCause.FALL || event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL){
					event.setCancelled(true);
				}
				else if(event.getCause() == EntityDamageEvent.DamageCause.VOID){
					event.setCancelled(true);
					player.teleport(plugin.auth.getServerSpawn());
				}
			}
		}
	}
}