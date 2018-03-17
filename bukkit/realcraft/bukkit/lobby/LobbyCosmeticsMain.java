package realcraft.bukkit.lobby;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import realcraft.bukkit.RealCraft;

public class LobbyCosmeticsMain {
	static LobbyCosmetics lobbycosmetics;
	static RealCraft plugin;

	public static void init(LobbyCosmetics lobbycosmetics){
		LobbyCosmeticsMain.lobbycosmetics = lobbycosmetics;
		LobbyCosmeticsMain.plugin = RealCraft.getInstance();
	}

	public static String getName(){
		return LobbyCosmeticsType.MAIN.toString();
	}

	public static String getInventoryName(){
		return LobbyCosmeticsType.MAIN.toInventoryName();
	}

	public static void openMenu(Player player){
		Inventory menu = Bukkit.createInventory(player,6*9,LobbyCosmeticsType.MAIN.toInventoryName());
		ArrayList<String> lore;

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro otevreni.");
		menu.setItem(getIndex(1,2),getItem(LobbyCosmeticsType.PETS.toItemName(),Material.NAME_TAG,1,lore));

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro otevreni.");
		menu.setItem(getIndex(1,4),getItem(LobbyCosmeticsType.GADGETS.toItemName(),Material.ENDER_PEARL,1,lore));

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro otevreni.");
		menu.setItem(getIndex(1,6),getItem(LobbyCosmeticsType.EFFECTS.toItemName(),Material.NETHER_STAR,1,lore));

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro otevreni.");
		menu.setItem(getIndex(3,2),getItem(LobbyCosmeticsType.HATS.toItemName(),Material.DIAMOND_HELMET,1,lore));

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro otevreni.");
		menu.setItem(getIndex(3,4),getItem(LobbyCosmeticsType.SUITS.toItemName(),Material.LEATHER_CHESTPLATE,1,lore));

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro otevreni.");
		menu.setItem(getIndex(3,6),getItem(LobbyCosmeticsType.MOUNTS.toItemName(),Material.SADDLE,1,lore));

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro zruseni");
		lore.add("§7vsech aktivnich doplnku.");
		menu.setItem(getIndex(5,4),getItem(LobbyCosmeticsType.CLEAR.toItemName(),Material.BARRIER,1,lore));

		player.openInventory(menu);
	}

	public static int getIndex(int row,int column){
		return (row*9)+column;
	}

	public static ItemStack getItem(String name,Material material,int amount,ArrayList<String> lore){
		ItemStack itemstack = new ItemStack(material,amount);
		ItemMeta meta = itemstack.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		itemstack.setItemMeta(meta);
		return itemstack;
	}

	public static void InventoryClickEvent(InventoryClickEvent event){
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		if(item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
			player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
			if(item.getItemMeta().getDisplayName().equalsIgnoreCase(LobbyCosmeticsType.HATS.toItemName())){
				LobbyCosmeticsHats.openMenu(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(LobbyCosmeticsType.SUITS.toItemName())){
				LobbyCosmeticsSuits.openMenu(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(LobbyCosmeticsType.GADGETS.toItemName())){
				LobbyCosmeticsGadgets.openMenu(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(LobbyCosmeticsType.EFFECTS.toItemName())){
				LobbyCosmeticsEffects.openMenu(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(LobbyCosmeticsType.PETS.toItemName())){
				LobbyCosmeticsPets.openMenu(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(LobbyCosmeticsType.MOUNTS.toItemName())){
				LobbyCosmeticsMounts.openMenu(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(LobbyCosmeticsType.CLEAR.toItemName())){
				player.closeInventory();
				lobbycosmetics.clearCosmetics(player);
			}
		}
	}

	public enum LobbyCosmeticsType {
		MAIN, HATS, SUITS, GADGETS, EFFECTS, PETS, MOUNTS, CLEAR;

		public String toString(){
			switch(this){
				case MAIN: return "Doplnky";
				case HATS: return "Helmy";
				case SUITS: return "Brneni";
				case GADGETS: return "Gadgety";
				case EFFECTS: return "Efekty";
				case PETS: return "Mazlici";
				case MOUNTS: return "Jezdecka zvirata";
				case CLEAR: return "Odebrat doplnky";
			}
			return null;
		}

		public String toInventoryName(){
			switch(this){
				case MAIN: return "Doplnky";
				case HATS: return LobbyCosmeticsType.MAIN.toString()+" > Helmy";
				case SUITS: return LobbyCosmeticsType.MAIN.toString()+" > Brneni";
				case GADGETS: return LobbyCosmeticsType.MAIN.toString()+" > Gadgety";
				case EFFECTS: return LobbyCosmeticsType.MAIN.toString()+" > Efekty";
				case PETS: return LobbyCosmeticsType.MAIN.toString()+" > Mazlici";
				case MOUNTS: return LobbyCosmeticsType.MAIN.toString()+" > Jezdecka zvirata";
				case CLEAR: return LobbyCosmeticsType.MAIN.toString()+" > Odebrat doplnky";
			}
			return null;
		}

		public String toItemName(){
			switch(this){
				case MAIN: return "§e§l"+this.toString();
				case HATS: return "§b§l"+this.toString();
				case SUITS: return "§b§l"+this.toString();
				case GADGETS: return "§b§l"+this.toString();
				case EFFECTS: return "§b§l"+this.toString();
				case PETS: return "§b§l"+this.toString();
				case MOUNTS: return "§b§l"+this.toString();
				case CLEAR: return "§c§l"+this.toString();
			}
			return null;
		}
	}
}