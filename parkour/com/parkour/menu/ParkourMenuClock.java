package com.parkour.menu;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.parkour.Parkour;
import com.parkour.ParkourClockType;
import com.parkour.ParkourPlayer;

public class ParkourMenuClock {
	public static void openMenu(ParkourPlayer player){
		Inventory menu = Bukkit.createInventory(null,1*9,ParkourMenuType.CLOCK.getInventoryName());
		menu.setItem(0,ParkourClockType.DAY.getItemStack(player.getArena().getClock() == ParkourClockType.DAY));
		menu.setItem(1,ParkourClockType.DAWN.getItemStack(player.getArena().getClock() == ParkourClockType.DAWN));
		menu.setItem(2,ParkourClockType.NIGHT.getItemStack(player.getArena().getClock() == ParkourClockType.NIGHT));
		menu.setItem(8,ParkourMenuType.BACK.getItemStack());
		player.getPlayer().openInventory(menu);
	}

	public static void InventoryClickEvent(InventoryClickEvent event){
		ParkourPlayer player = Parkour.getPlayer((Player)event.getWhoClicked());
		ItemStack item = event.getCurrentItem();
		if(item != null){
			if(event.getSlot() == 0){
				player.getArena().setClock(ParkourClockType.DAY);
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
				ParkourMenuClock.openMenu(player);
			}
			else if(event.getSlot() == 1){
				player.getArena().setClock(ParkourClockType.DAWN);
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
				ParkourMenuClock.openMenu(player);
			}
			else if(event.getSlot() == 2){
				player.getArena().setClock(ParkourClockType.NIGHT);
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
				ParkourMenuClock.openMenu(player);
			}
			else if(item.getType() == ParkourMenuType.BACK.getMaterial()){
				ParkourMenuSettings.openMenu(player);
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1,1);
			}
		}
		event.setCancelled(true);
	}
}