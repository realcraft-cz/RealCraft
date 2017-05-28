package com.anticheat.checks;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class CheckEnchant extends Check {

	public CheckEnchant(){
		super(CheckType.ENCHANT);
	}

	@Override
	public void run(){
	}

	@EventHandler
	public void PlayerItemHeldEvent(PlayerItemHeldEvent event){
		this.check(event.getPlayer(),event.getPlayer().getInventory().getItem(event.getNewSlot()));
	}

	@EventHandler
	public void PlayerPickupItemEvent(PlayerPickupItemEvent event){
		this.check(event.getPlayer(),event.getItem().getItemStack());
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			ItemStack item = event.getCurrentItem();
			if(this.check((Player)event.getWhoClicked(),item)){
				event.setCancelled(true);
				event.setCurrentItem(item);
			}
		}
	}

	public boolean check(Player player,ItemStack item){
		boolean detected = false;
		if(item != null){
			for(Enchantment enchantment : item.getEnchantments().keySet()){
				if(enchantment.getName() != null && (!enchantment.canEnchantItem(item) || item.getEnchantmentLevel(enchantment) > enchantment.getMaxLevel())){
					item.removeEnchantment(enchantment);
					detected = true;
				}
			}
		}
		//if(detected) this.detect(player);
		return detected;
	}
}