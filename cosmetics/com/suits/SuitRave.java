package com.suits;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class SuitRave extends Suit {

    public SuitRave(SuitType type){
        super(type);
    }

    @Override
    public void onCreate(Player player){
    	ItemStack itemStack;
    	LeatherArmorMeta itemMeta;

    	itemStack = new ItemStack(Material.LEATHER_HELMET);
    	itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
    	itemMeta.setColor(Color.fromBGR(255,180,0));
    	itemStack.setItemMeta(itemMeta);
    	player.getInventory().setHelmet(itemStack);

    	itemStack = new ItemStack(Material.LEATHER_CHESTPLATE);
    	itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
    	itemMeta.setColor(Color.fromBGR(255,120,0));
    	itemStack.setItemMeta(itemMeta);
		player.getInventory().setChestplate(itemStack);

		itemStack = new ItemStack(Material.LEATHER_LEGGINGS);
    	itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
    	itemMeta.setColor(Color.fromBGR(255,60,0));
    	itemStack.setItemMeta(itemMeta);
		player.getInventory().setLeggings(itemStack);

		itemStack = new ItemStack(Material.LEATHER_BOOTS);
    	itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
    	itemMeta.setColor(Color.fromBGR(255,0,0));
    	itemStack.setItemMeta(itemMeta);
		player.getInventory().setBoots(itemStack);
    }

    @Override
    public void onUpdate(Player player){
    	int [] colors = new int[3];
    	ItemStack itemStack;

    	itemStack = player.getInventory().getHelmet();
    	if(itemStack != null && itemStack.getType() == Material.LEATHER_HELMET){
    		LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
    		colors[0] = itemMeta.getColor().getRed();
    		colors[1] = itemMeta.getColor().getGreen();
    		colors[2] = itemMeta.getColor().getBlue();
    		colors = this.nextColors(colors);
    		itemMeta.setColor(Color.fromRGB(colors[0],colors[1],colors[2]));
    		itemStack.setItemMeta(itemMeta);
    		player.getInventory().setHelmet(itemStack);
    	}

    	itemStack = player.getInventory().getChestplate();
    	if(itemStack != null && itemStack.getType() == Material.LEATHER_CHESTPLATE){
    		LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
    		colors[0] = itemMeta.getColor().getRed();
    		colors[1] = itemMeta.getColor().getGreen();
    		colors[2] = itemMeta.getColor().getBlue();
    		colors = this.nextColors(colors);
    		itemMeta.setColor(Color.fromRGB(colors[0],colors[1],colors[2]));
    		itemStack.setItemMeta(itemMeta);
    		player.getInventory().setChestplate(itemStack);
    	}

    	itemStack = player.getInventory().getLeggings();
    	if(itemStack != null && itemStack.getType() == Material.LEATHER_LEGGINGS){
    		LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
    		colors[0] = itemMeta.getColor().getRed();
    		colors[1] = itemMeta.getColor().getGreen();
    		colors[2] = itemMeta.getColor().getBlue();
    		colors = this.nextColors(colors);
    		itemMeta.setColor(Color.fromRGB(colors[0],colors[1],colors[2]));
    		itemStack.setItemMeta(itemMeta);
    		player.getInventory().setLeggings(itemStack);
    	}

    	itemStack = player.getInventory().getBoots();
    	if(itemStack != null && itemStack.getType() == Material.LEATHER_BOOTS){
    		LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
    		colors[0] = itemMeta.getColor().getRed();
    		colors[1] = itemMeta.getColor().getGreen();
    		colors[2] = itemMeta.getColor().getBlue();
    		colors = this.nextColors(colors);
    		itemMeta.setColor(Color.fromRGB(colors[0],colors[1],colors[2]));
    		itemStack.setItemMeta(itemMeta);
    		player.getInventory().setBoots(itemStack);
    	}
    }

    public int[] nextColors(int [] colors){
    	if (colors[0] == 255 && colors[1] < 255 && colors[2] == 0)
            colors[1] += 15;
        if (colors[1] == 255 && colors[0] > 0 && colors[2] == 0)
            colors[0] -= 15;
        if (colors[1] == 255 && colors[2] < 255 && colors[0] == 0)
            colors[2] += 15;
        if (colors[2] == 255 && colors[1] > 0 && colors[0] == 0)
            colors[1] -= 15;
        if (colors[2] == 255 && colors[0] < 255 && colors[1] == 0)
            colors[0] += 15;
        if (colors[0] == 255 && colors[2] > 0 && colors[1] == 0)
            colors[2] -= 15;
        return colors;
    }
}