package com.parkour.menu;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.parkour.Parkour;
import com.parkour.ParkourPlayer;

public class ParkourMenuFloor {
	@SuppressWarnings("deprecation")
	private static final ItemStack[] floors = new ItemStack[]{
		new ItemStack(Material.BARRIER),
		new ItemStack(Material.STONE),
		new ItemStack(Material.GRASS),
		new ItemStack(Material.DIRT),
		new ItemStack(Material.DIRT,1,(short)0,(byte)2),
		new ItemStack(Material.COBBLESTONE),
		new ItemStack(Material.WOOD),
		new ItemStack(Material.WOOD,1,(short)0,(byte)1),
		new ItemStack(Material.WOOD,1,(short)0,(byte)2),
		new ItemStack(Material.WOOD,1,(short)0,(byte)3),
		new ItemStack(Material.WOOD,1,(short)0,(byte)4),
		new ItemStack(Material.WOOD,1,(short)0,(byte)5),
		new ItemStack(Material.SANDSTONE),
		new ItemStack(Material.SNOW_BLOCK),
		new ItemStack(Material.PACKED_ICE),
		new ItemStack(Material.ICE),
		new ItemStack(Material.SMOOTH_BRICK),
		new ItemStack(Material.SMOOTH_BRICK,1,(short)0,(byte)2),
		new ItemStack(Material.ENDER_STONE),
		new ItemStack(Material.QUARTZ_BLOCK),
		new ItemStack(Material.QUARTZ_BLOCK,1,(short)0,(byte)1),
		new ItemStack(Material.NETHERRACK),
		new ItemStack(Material.STAINED_CLAY,1,(short)0,(byte)0),
		new ItemStack(Material.STAINED_CLAY,1,(short)0,(byte)1),
		new ItemStack(Material.STAINED_CLAY,1,(short)0,(byte)2),
		new ItemStack(Material.STAINED_CLAY,1,(short)0,(byte)3),
		new ItemStack(Material.STAINED_CLAY,1,(short)0,(byte)4),
		new ItemStack(Material.STAINED_CLAY,1,(short)0,(byte)5),
		new ItemStack(Material.STAINED_CLAY,1,(short)0,(byte)6),
		new ItemStack(Material.STAINED_CLAY,1,(short)0,(byte)7),
		new ItemStack(Material.STAINED_CLAY,1,(short)0,(byte)8),
		new ItemStack(Material.STAINED_CLAY,1,(short)0,(byte)9),
		new ItemStack(Material.STAINED_CLAY,1,(short)0,(byte)10),
		new ItemStack(Material.STAINED_CLAY,1,(short)0,(byte)11),
		new ItemStack(Material.STAINED_CLAY,1,(short)0,(byte)12),
		new ItemStack(Material.STAINED_CLAY,1,(short)0,(byte)13),
		new ItemStack(Material.STAINED_CLAY,1,(short)0,(byte)14),
		new ItemStack(Material.STAINED_CLAY,1,(short)0,(byte)15),
		new ItemStack(Material.WATER_BUCKET),
		new ItemStack(Material.LAVA_BUCKET),
	};

	public static void openMenu(ParkourPlayer player){
		Inventory menu = Bukkit.createInventory(null,6*9,ParkourMenuType.FLOOR.getInventoryName());
		int column = 0;
		int row = 0;
		for(ItemStack item : floors){
			menu.setItem((row*9)+column,item);
			column ++;
			if(column == 7){
				column = 0;
				row ++;
			}
		}
		menu.setItem(8,ParkourMenuType.BACK.getItemStack());
		player.getPlayer().openInventory(menu);
	}

	@SuppressWarnings("deprecation")
	public static void InventoryClickEvent(InventoryClickEvent event){
		ParkourPlayer player = Parkour.getPlayer((Player)event.getWhoClicked());
		ItemStack item = event.getCurrentItem();
		if(item != null){
			if(item.getType() == ParkourMenuType.BACK.getMaterial()){
				ParkourMenuSettings.openMenu(player);
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1,1);
			}
			else if(item.getType() != Material.AIR){
				if(Arrays.asList(floors).contains(item)){
					Material material = item.getType();
					Byte data = item.getData().getData();
					if(item.getType() == Material.BARRIER){
						material = Material.AIR;
					}
					else if(item.getType() == Material.WATER_BUCKET){
						material = Material.WATER;
						data = 0;
					}
					else if(item.getType() == Material.LAVA_BUCKET){
						material = Material.LAVA;
						data = 0;
					}
					player.getArena().setFloor(material,data);
					player.getPlayer().closeInventory();
					player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
				}
			}
		}
		event.setCancelled(true);
	}
}