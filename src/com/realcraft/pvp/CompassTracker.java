package com.realcraft.pvp;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.earth2me.essentials.Essentials;
import com.mccraftaholics.warpportals.api.WarpPortalsEvent;
import com.realcraft.RealCraft;
import com.realcraft.playermanazer.PlayerManazer.PlayerInfo;

public class CompassTracker implements Runnable,Listener {
	RealCraft plugin;
	Essentials essentials;

	boolean enabled = false;
	String [] worlds;
	int slotId;

	public CompassTracker(RealCraft realcraft){
		plugin = realcraft;
		if(plugin.config.getBoolean("compasstracker.enabled",false)){
			enabled = true;
			essentials = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
			slotId = plugin.config.getInt("compasstracker.slot",0);
			List<String> tmpworlds = plugin.config.getStringList("compasstracker.worlds");
			worlds = tmpworlds.toArray(new String[tmpworlds.size()]);
			plugin.getServer().getPluginManager().registerEvents(this,plugin);
			plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,this,10*20,2*20);
		}
	}

	public void onReload(){
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		essentials.getUser(player).setNickname("§7"+essentials.getUser(player).getName()+"§r");
		essentials.getUser(player).setDisplayNick();
	}

	@EventHandler
	public void WarpPortalsEvent(WarpPortalsEvent event){
		Player player = event.getPlayer();
		event.setCancelled(true);
		plugin.getServer().dispatchCommand(player,"msp random default");
	}

	@EventHandler
	public void onPlayerWorldChange(PlayerChangedWorldEvent event){
		Player player = event.getPlayer();
		boolean spawn = true;
		player.getInventory().remove(Material.COMPASS);
		for(String world : worlds){
			if(player.getWorld().getName().equalsIgnoreCase(world)){
				ItemStack compass = new ItemStack(Material.COMPASS,1);
				if(player.getInventory().getItem(slotId) != null) player.getInventory().addItem(compass);
				else player.getInventory().setItem(slotId,compass);
				essentials.getUser(player).setNickname(essentials.getUser(player).getName());
				essentials.getUser(player).setDisplayNick();
				spawn = false;
				break;
			}
		}
		if(spawn){
			essentials.getUser(player).setNickname("§7"+essentials.getUser(player).getName()+"§r");
			essentials.getUser(player).setDisplayNick();
		}
	}

	@EventHandler
	public void onPlayerInventoryClick(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(player.getInventory().getItemInMainHand().getType() == Material.COMPASS){
			if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
				plugin.playermanazer.getPlayerInfo(player).setCompassMode(0);
			}
			else if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				Player target = this.findNextPlayer(player);
				if(target != null){
					plugin.playermanazer.getPlayerInfo(player).setNearestPlayer(target);
				}
				else plugin.playermanazer.getPlayerInfo(player).setCompassMode(0);
			}
			this.setCompassItem(player);
			player.updateInventory();
			event.setCancelled(true);
		}
	}

	public void setCompassItem(Player player){
		PlayerInfo playerinfo = plugin.playermanazer.getPlayerInfo(player);
		if(playerinfo != null){
			if(player.getInventory().getItemInMainHand().getType() == Material.COMPASS){
				ItemStack compass = player.getInventory().getItemInMainHand();
				if(player.getWorld().getName().equalsIgnoreCase("world")){
					ItemMeta meta = compass.getItemMeta();
					meta.setDisplayName("§r§7Kompas");
					compass.setItemMeta(meta);
				} else {
					if(playerinfo.getCompassMode() == 0){
						Player target = this.getNearestPlayer(player);
						if(target != null){
							playerinfo.setNearestPlayer(target);
							player.setCompassTarget(target.getLocation());

							ItemMeta meta = compass.getItemMeta();
							meta.setDisplayName("§r§7Nejblizsi hrac: §6"+playerinfo.getNearestPlayer().getDisplayName()+"§r ("+Math.round(playerinfo.getNearestPlayer().getLocation().distance(player.getLocation()))+"m)");
							compass.setItemMeta(meta);
						} else {
							player.setCompassTarget(player.getLocation());

							ItemMeta meta = compass.getItemMeta();
							meta.setDisplayName("§r§7Zadny hrac nebojuje");
							compass.setItemMeta(meta);
						}
					} else {
						if(playerinfo.getNearestPlayer() != null){
							player.setCompassTarget(playerinfo.getNearestPlayer().getLocation());
							ItemMeta meta = compass.getItemMeta();
							meta.setDisplayName("§r§6"+playerinfo.getNearestPlayer().getDisplayName()+"§r ("+Math.round(playerinfo.getNearestPlayer().getLocation().distance(player.getLocation()))+"m)");
							compass.setItemMeta(meta);
						} else {
							plugin.playermanazer.getPlayerInfo(player).setCompassMode(0);
							this.setCompassItem(player);
						}
					}
				}
			}
		}
	}

	@Override
	public void run(){
		for(Player player : plugin.getServer().getOnlinePlayers()){
			this.setCompassItem(player);
		}
	}

	public Player getNearestPlayer(Player player){
		double distance = 0;
		Player dest = null;
		for(Player target : plugin.getServer().getOnlinePlayers()){
			if(target != player && target.getWorld() == player.getWorld() && !target.isDead()){
				double distanceTmp = player.getLocation().distance(target.getLocation());
				if(dest == null || distanceTmp < distance){
					dest = target;
					distance = distanceTmp;
				}
			}
		}
		return dest;
	}

	public Player findNextPlayer(Player player){
		Player dest = null;
		int index = 0;
		for(Player target : plugin.getServer().getOnlinePlayers()){
			if(target != player && target.getWorld() == player.getWorld() && !target.isDead()){
				index ++;
				if(plugin.playermanazer.getPlayerInfo(player).getCompassMode() < index){
					dest = target;
					plugin.playermanazer.getPlayerInfo(player).setCompassMode(index);
					return dest;
				}
			}
		}
		if(index == 0) return null;
		plugin.playermanazer.getPlayerInfo(player).setCompassMode(0);
		return findNextPlayer(player);
	}
}