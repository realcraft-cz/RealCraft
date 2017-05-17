package com.realcraft.lobby;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.cosmetics.Cosmetics;
import com.particleeffects.ParticleEffect;
import com.realcraft.RealCraft;
import com.realcraft.lobby.LobbyCosmeticsMain.LobbyCosmeticsType;

public class LobbyCosmeticsEffects {
	static LobbyCosmetics lobbycosmetics;
	static RealCraft plugin;

	public static void init(LobbyCosmetics lobbycosmetics){
		LobbyCosmeticsGadgets.lobbycosmetics = lobbycosmetics;
		LobbyCosmeticsGadgets.plugin = RealCraft.getInstance();
	}

	public static String getName(){
		return LobbyCosmeticsMain.LobbyCosmeticsType.EFFECTS.toString();
	}

	public static String getInventoryName(){
		return LobbyCosmeticsMain.LobbyCosmeticsType.EFFECTS.toInventoryName();
	}

	public static void openMenu(Player player){
		Inventory menu = Bukkit.createInventory(player,6*9,LobbyCosmeticsMain.LobbyCosmeticsType.EFFECTS.toInventoryName());

		setItem(menu,getIndex(1,1),Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.RAINCLOUD));
		setItem(menu,getIndex(1,2),Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.SNOWCLOUD));
		setItem(menu,getIndex(1,3),Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.BLOODHELIX));
		setItem(menu,getIndex(1,4),Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.FROSTLORD));
		setItem(menu,getIndex(1,5),Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.FLAMERINGS));
		setItem(menu,getIndex(1,6),Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.INLOVE));
		setItem(menu,getIndex(1,7),Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.GREENSPARKS));
		setItem(menu,getIndex(2,1),Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.FROZENWALK));
		setItem(menu,getIndex(2,2),Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.MUSIC));
		setItem(menu,getIndex(2,3),Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.ENCHANTED));
		setItem(menu,getIndex(2,4),Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.INFERNO));
		setItem(menu,getIndex(2,5),Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.ANGELWINGS));
		setItem(menu,getIndex(2,6),Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.SUPERHERO));
		setItem(menu,getIndex(2,7),Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.SANTAHAT));
		setItem(menu,getIndex(3,1),Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.CRUSHEDCANDYCANE));
		setItem(menu,getIndex(3,2),Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.ENDERAURA));
		setItem(menu,getIndex(3,3),Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.FLAMEFAIRY));

		ArrayList<String> lore;

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro navrat");
		lore.add("§7do hlavniho menu.");
		menu.setItem(getIndex(5,3),getItem("§e§lDoplnky",Material.CHEST,(byte)0,1,lore));

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro zruseni");
		lore.add("§7vsech aktivnich efektu.");
		menu.setItem(getIndex(5,5),getItem("§c§lOdebrat efekty",Material.BARRIER,(byte)0,1,lore));

		player.openInventory(menu);
	}

	public static int getIndex(int row,int column){
		return (row*9)+column;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getItem(String name,Material material,Byte data,int amount,ArrayList<String> lore){
		ItemStack itemstack = new ItemStack(material,amount,(short)0,data);
		ItemMeta meta = itemstack.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		itemstack.setItemMeta(meta);
		return itemstack;
	}

	public static void setItem(Inventory menu,int index,ParticleEffect effect){
		Player player = (Player) menu.getHolder();
		boolean enabled = effect.isEnabled(player);
		if(enabled) menu.setItem(index,getItem(effect.getType().toString(),effect.getType().toMaterial(),effect.getType().toData(),1,effect.getType().toLore()));
		else menu.setItem(index,getItem(effect.getType().toString(),Material.INK_SACK,(byte)8,1,effect.getType().toLore()));
	}

	public static void InventoryClickEvent(InventoryClickEvent event){
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		if(item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
			player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
			if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ParticleEffect.ParticleEffectType.RAINCLOUD.toString())){
				Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.RAINCLOUD).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ParticleEffect.ParticleEffectType.SNOWCLOUD.toString())){
				Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.SNOWCLOUD).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ParticleEffect.ParticleEffectType.BLOODHELIX.toString())){
				Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.BLOODHELIX).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ParticleEffect.ParticleEffectType.FROSTLORD.toString())){
				Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.FROSTLORD).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ParticleEffect.ParticleEffectType.FLAMERINGS.toString())){
				Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.FLAMERINGS).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ParticleEffect.ParticleEffectType.INLOVE.toString())){
				Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.INLOVE).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ParticleEffect.ParticleEffectType.GREENSPARKS.toString())){
				Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.GREENSPARKS).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ParticleEffect.ParticleEffectType.FROZENWALK.toString())){
				Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.FROZENWALK).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ParticleEffect.ParticleEffectType.MUSIC.toString())){
				Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.MUSIC).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ParticleEffect.ParticleEffectType.ENCHANTED.toString())){
				Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.ENCHANTED).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ParticleEffect.ParticleEffectType.INFERNO.toString())){
				Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.INFERNO).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ParticleEffect.ParticleEffectType.ANGELWINGS.toString())){
				Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.ANGELWINGS).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ParticleEffect.ParticleEffectType.SUPERHERO.toString())){
				Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.SUPERHERO).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ParticleEffect.ParticleEffectType.SANTAHAT.toString())){
				Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.SANTAHAT).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ParticleEffect.ParticleEffectType.CRUSHEDCANDYCANE.toString())){
				Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.CRUSHEDCANDYCANE).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ParticleEffect.ParticleEffectType.ENDERAURA.toString())){
				Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.ENDERAURA).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ParticleEffect.ParticleEffectType.FLAMEFAIRY.toString())){
				Cosmetics.getParticleEffect(ParticleEffect.ParticleEffectType.FLAMEFAIRY).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(LobbyCosmeticsType.MAIN.toItemName())){
				LobbyCosmeticsMain.openMenu(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§c§lOdebrat efekty")){
				Cosmetics.clearParticleEffects(player);
				LobbyCosmeticsMain.openMenu(player);
			}
		}
	}
}