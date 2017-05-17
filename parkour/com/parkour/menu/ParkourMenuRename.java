package com.parkour.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.parkour.ParkourPlayer;

public class ParkourMenuRename {
	public static void openMenu(ParkourPlayer player){
		Inventory menu = Bukkit.createInventory(player.getPlayer(),InventoryType.ANVIL,ParkourMenuType.RENAME.getInventoryName());
		ItemStack item = new ItemStack(Material.EMPTY_MAP);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(player.getArena().getName());
		item.setItemMeta(meta);
		menu.setItem(0,item);
		player.getPlayer().openInventory(menu);
	}

	public static void InventoryClickEvent(InventoryClickEvent event){
		event.setCancelled(true);
	}
}