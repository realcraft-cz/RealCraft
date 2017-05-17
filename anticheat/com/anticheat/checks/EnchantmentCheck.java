package com.anticheat.checks;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.anticheat.AntiCheat;
import com.realcraft.RealCraft;

//http://git.wakes.cz/craftmania-cz/craftmanager/blob/master/src/main/java/cz/wake/manager/listener/DetectOpItems.java

public class EnchantmentCheck implements Listener {
	AntiCheat anticheat;

	public EnchantmentCheck(AntiCheat anticheat){
		this.anticheat = anticheat;
		Bukkit.getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler
	public void PlayerItemHeldEvent(PlayerItemHeldEvent event){
		this.checkEnchantments(event.getPlayer(),event.getPlayer().getInventory().getItem(event.getNewSlot()));
	}

	@EventHandler
	public void PlayerPickupItemEvent(PlayerPickupItemEvent event){
		this.checkEnchantments(event.getPlayer(),event.getItem().getItemStack());
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			ItemStack item = this.checkEnchantments((Player)event.getWhoClicked(),event.getCurrentItem());
			event.setCurrentItem(item);
		}
	}

	public ItemStack checkEnchantments(Player player,ItemStack item){
		if(item != null){
			for(Enchantment enchantment : item.getEnchantments().keySet()){
				if(enchantment.getName() != null && (!enchantment.canEnchantItem(item) || item.getEnchantmentLevel(enchantment) > enchantment.getMaxLevel())){
					item.removeEnchantment(enchantment);
				}
			}
			/*if(detected){
				AntiCheatDetectEvent callevent = new AntiCheatDetectEvent(player.getPlayer(),CheckType.ENCHANT);
				Bukkit.getServer().getPluginManager().callEvent(callevent);
			}*/
		}
		return item;
	}
}