package com.parkour.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.parkour.ParkourPlayer;

public class ParkourMenuCollaborator {
	@SuppressWarnings("deprecation")
	public static void openMenu(ParkourPlayer player){
		Inventory menu = Bukkit.createInventory(player.getPlayer(),InventoryType.ANVIL,ParkourMenuType.COLLABORATORS.getInventoryName());
		ItemStack item = new ItemStack(Material.SKULL_ITEM,1,(short)0,(byte)3);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("<jmeno hrace>");
		item.setItemMeta(meta);
		menu.setItem(0,item);
		player.getPlayer().openInventory(menu);
	}

	public static void InventoryClickEvent(InventoryClickEvent event){
		event.setCancelled(true);
	}
}