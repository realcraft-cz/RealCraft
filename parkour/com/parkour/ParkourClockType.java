package com.parkour;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.realcraft.utils.Glow;

public enum ParkourClockType {
	DAY, DAWN, NIGHT;

	public int getId(){
		switch(this){
			case DAY: return 0;
			case DAWN: return 1;
			case NIGHT: return 2;
		}
		return 0;
	}

	public int getTicks(){
		switch(this){
			case DAY: return 6000;
			case DAWN: return 23000;
			case NIGHT: return 18000;
		}
		return 0;
	}

	public static ParkourClockType fromId(int id){
		if(ParkourClockType.DAY.getId() == id) return ParkourClockType.DAY;
		else if(ParkourClockType.DAWN.getId() == id) return ParkourClockType.DAWN;
		else if(ParkourClockType.NIGHT.getId() == id) return ParkourClockType.NIGHT;
		return ParkourClockType.DAY;
	}

	public String getItemName(){
		switch(this){
			case DAY: return "§b§lDen";
			case DAWN: return "§6§lSvitani";
			case NIGHT: return "§7§lNoc";
		}
		return null;
	}

	public ArrayList<String> getLore(){
		ArrayList<String> lore = new ArrayList<String>();
		switch(this){
			case DAY:
				lore.add("§712:00");
				break;
			case DAWN:
				lore.add("§705:00");
				break;
			case NIGHT:
				lore.add("§720:00");
				break;
			default:break;
		}
		return lore;
	}

	public Material getMaterial(){
		return Material.WATCH;
	}

	public ItemStack getItemStack(boolean glow){
		ItemStack item = new ItemStack(this.getMaterial());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(this.getItemName());
		if(glow) meta.addEnchant(new Glow(255),1,true);
		meta.setLore(this.getLore());
		item.setItemMeta(meta);
		return item;
	}
}