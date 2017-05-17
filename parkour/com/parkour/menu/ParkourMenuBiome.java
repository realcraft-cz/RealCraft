package com.parkour.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.parkour.Parkour;
import com.parkour.ParkourPlayer;

public class ParkourMenuBiome {
	public static void openMenu(ParkourPlayer player){
		Inventory menu = Bukkit.createInventory(null,1*9,ParkourMenuType.BIOME.getInventoryName());
		for(int i=0;i<BiomeType.values().length;i++) menu.setItem(i,BiomeType.values()[i].getItemStack());
		menu.setItem(8,ParkourMenuType.BACK.getItemStack());
		player.getPlayer().openInventory(menu);
	}

	public static void InventoryClickEvent(InventoryClickEvent event){
		ParkourPlayer player = Parkour.getPlayer((Player)event.getWhoClicked());
		ItemStack item = event.getCurrentItem();
		if(item != null){
			if(item.getType() == ParkourMenuType.BACK.getMaterial()){
				ParkourMenuSettings.openMenu(player);
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1,1);
			}
			else if(item.getType() != Material.AIR){
				for(BiomeType biome : BiomeType.values()){
					if(item.isSimilar(biome.getItemStack())){
						player.getArena().setBiome(biome.getBiome());
						player.getPlayer().closeInventory();
						player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
						break;
					}
				}
			}
		}
		event.setCancelled(true);
	}

	public enum BiomeType {
		PLAINS, FOREST, ROOFED_FOREST, TAIGA, SWAMPLAND, DESERT, MESA;

		public Biome getBiome(){
			switch(this){
				case PLAINS: return Biome.PLAINS;
				case FOREST: return Biome.FOREST;
				case ROOFED_FOREST: return Biome.ROOFED_FOREST;
				case TAIGA: return Biome.TAIGA;
				case SWAMPLAND: return Biome.SWAMPLAND;
				case DESERT: return Biome.DESERT;
				case MESA: return Biome.MESA;
				default:break;
			}
			return null;
		}

		public String getItemName(){
			switch(this){
				case PLAINS: return "§a"+this.toString();
				case FOREST: return "§a"+this.toString();
				case ROOFED_FOREST: return "§2"+this.toString();
				case TAIGA: return "§3"+this.toString();
				case SWAMPLAND: return "§2"+this.toString();
				case DESERT: return "§e"+this.toString();
				case MESA: return "§6"+this.toString();
				default:break;
			}
			return null;
		}

		public Material getMaterial(){
			switch(this){
				case PLAINS: return Material.GRASS;
				case FOREST: return Material.SAPLING;
				case ROOFED_FOREST: return Material.SAPLING;
				case TAIGA: return Material.LONG_GRASS;
				case SWAMPLAND: return Material.WATER_LILY;
				case DESERT: return Material.CACTUS;
				case MESA: return Material.DEAD_BUSH;
				default:break;
			}
			return null;
		}

		public byte getData(){
			switch(this){
				case FOREST: return (byte)1;
				case ROOFED_FOREST: return (byte)5;
				case TAIGA: return (byte)2;
				default:break;
			}
			return 0;
		}

		@SuppressWarnings("deprecation")
		public ItemStack getItemStack(){
			ItemStack item = new ItemStack(this.getMaterial(),1,(short)0,this.getData());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(this.getItemName());
			item.setItemMeta(meta);
			return item;
		}
	}
}