package com.suits;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SuitDiamond extends Suit {

	public SuitDiamond(SuitType type){
	    super(type);
	}

	@Override
	public void onCreate(Player player){
		ItemStack itemStack;
		ItemMeta itemMeta;

		itemStack = new ItemStack(Material.DIAMOND_HELMET);
		itemMeta = itemStack.getItemMeta();
		itemMeta.addEnchant(Enchantment.DURABILITY,10,true);
		itemStack.setItemMeta(itemMeta);
		player.getInventory().setHelmet(itemStack);

		itemStack = new ItemStack(Material.DIAMOND_CHESTPLATE);
		itemMeta = itemStack.getItemMeta();
		itemMeta.addEnchant(Enchantment.DURABILITY,10,true);
		itemStack.setItemMeta(itemMeta);
		player.getInventory().setChestplate(itemStack);

		itemStack = new ItemStack(Material.DIAMOND_LEGGINGS);
		itemMeta = itemStack.getItemMeta();
		itemMeta.addEnchant(Enchantment.DURABILITY,10,true);
		itemStack.setItemMeta(itemMeta);
		player.getInventory().setLeggings(itemStack);

		itemStack = new ItemStack(Material.DIAMOND_BOOTS);
		itemMeta = itemStack.getItemMeta();
		itemMeta.addEnchant(Enchantment.DURABILITY,10,true);
		itemStack.setItemMeta(itemMeta);
		player.getInventory().setBoots(itemStack);
	}

	@Override
	public void onUpdate(Player player){
	}
}