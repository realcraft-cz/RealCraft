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
import com.gadgets.Gadget;
import com.realcraft.RealCraft;
import com.realcraft.lobby.LobbyCosmeticsMain.LobbyCosmeticsType;

public class LobbyCosmeticsGadgets {
	static LobbyCosmetics lobbycosmetics;
	static RealCraft plugin;

	public static void init(LobbyCosmetics lobbycosmetics){
		LobbyCosmeticsGadgets.lobbycosmetics = lobbycosmetics;
		LobbyCosmeticsGadgets.plugin = RealCraft.getInstance();
	}

	public static String getName(){
		return LobbyCosmeticsMain.LobbyCosmeticsType.GADGETS.toString();
	}

	public static String getInventoryName(){
		return LobbyCosmeticsMain.LobbyCosmeticsType.GADGETS.toInventoryName();
	}

	public static void openMenu(Player player){
		Inventory menu = Bukkit.createInventory(player,6*9,LobbyCosmeticsMain.LobbyCosmeticsType.GADGETS.toInventoryName());

		setItem(menu,getIndex(1,2),Cosmetics.getGadget(Gadget.GadgetType.Chickenator));
		setItem(menu,getIndex(1,3),Cosmetics.getGadget(Gadget.GadgetType.MelonThrower));
		setItem(menu,getIndex(1,4),Cosmetics.getGadget(Gadget.GadgetType.ColorBomb));
		setItem(menu,getIndex(1,5),Cosmetics.getGadget(Gadget.GadgetType.ExplosiveSheep));
		setItem(menu,getIndex(1,6),Cosmetics.getGadget(Gadget.GadgetType.TNT));
		setItem(menu,getIndex(2,2),Cosmetics.getGadget(Gadget.GadgetType.Tsunami));
		setItem(menu,getIndex(2,3),Cosmetics.getGadget(Gadget.GadgetType.Firework));
		setItem(menu,getIndex(2,4),Cosmetics.getGadget(Gadget.GadgetType.GhostParty));
		setItem(menu,getIndex(2,5),Cosmetics.getGadget(Gadget.GadgetType.FreezeCannon));
		setItem(menu,getIndex(2,6),Cosmetics.getGadget(Gadget.GadgetType.PartyPopper));
		setItem(menu,getIndex(3,2),Cosmetics.getGadget(Gadget.GadgetType.PaintballGun));
		setItem(menu,getIndex(3,3),Cosmetics.getGadget(Gadget.GadgetType.DiamondShower));
		setItem(menu,getIndex(3,4),Cosmetics.getGadget(Gadget.GadgetType.GoldShower));
		setItem(menu,getIndex(3,5),Cosmetics.getGadget(Gadget.GadgetType.FoodShower));

		ArrayList<String> lore;

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro navrat");
		lore.add("§7do hlavniho menu.");
		menu.setItem(getIndex(5,3),getItem("§e§lDoplnky",Material.CHEST,(byte)0,1,lore));

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro zruseni");
		lore.add("§7vsech aktivnich gadgetu.");
		menu.setItem(getIndex(5,5),getItem("§c§lOdebrat gadgety",Material.BARRIER,(byte)0,1,lore));

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

	public static void setItem(Inventory menu,int index,Gadget gadget){
		Player player = (Player) menu.getHolder();
		int amount = gadget.getAmount(player);
		if(amount > 0) menu.setItem(index,getItem(gadget.getType().toString(),gadget.getType().toMaterial(),gadget.getType().toData(),amount,gadget.getType().toLore()));
		else menu.setItem(index,getItem(gadget.getType().toString(),Material.INK_SACK,(byte)8,1,gadget.getType().toLore()));
	}

	public static void InventoryClickEvent(InventoryClickEvent event){
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		if(item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
			player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
			if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Gadget.GadgetType.Chickenator.toString())){
				Cosmetics.getGadget(Gadget.GadgetType.Chickenator).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Gadget.GadgetType.MelonThrower.toString())){
				Cosmetics.getGadget(Gadget.GadgetType.MelonThrower).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Gadget.GadgetType.ColorBomb.toString())){
				Cosmetics.getGadget(Gadget.GadgetType.ColorBomb).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Gadget.GadgetType.ExplosiveSheep.toString())){
				Cosmetics.getGadget(Gadget.GadgetType.ExplosiveSheep).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Gadget.GadgetType.TNT.toString())){
				Cosmetics.getGadget(Gadget.GadgetType.TNT).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Gadget.GadgetType.Tsunami.toString())){
				Cosmetics.getGadget(Gadget.GadgetType.Tsunami).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Gadget.GadgetType.Firework.toString())){
				Cosmetics.getGadget(Gadget.GadgetType.Firework).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Gadget.GadgetType.GhostParty.toString())){
				Cosmetics.getGadget(Gadget.GadgetType.GhostParty).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Gadget.GadgetType.FreezeCannon.toString())){
				Cosmetics.getGadget(Gadget.GadgetType.FreezeCannon).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Gadget.GadgetType.PartyPopper.toString())){
				Cosmetics.getGadget(Gadget.GadgetType.PartyPopper).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Gadget.GadgetType.PaintballGun.toString())){
				Cosmetics.getGadget(Gadget.GadgetType.PaintballGun).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Gadget.GadgetType.DiamondShower.toString())){
				Cosmetics.getGadget(Gadget.GadgetType.DiamondShower).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Gadget.GadgetType.GoldShower.toString())){
				Cosmetics.getGadget(Gadget.GadgetType.GoldShower).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Gadget.GadgetType.FoodShower.toString())){
				Cosmetics.getGadget(Gadget.GadgetType.FoodShower).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(LobbyCosmeticsType.MAIN.toItemName())){
				LobbyCosmeticsMain.openMenu(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§c§lOdebrat gadgety")){
				Cosmetics.clearGadgets(player);
				LobbyCosmeticsMain.openMenu(player);
			}
		}
	}
}