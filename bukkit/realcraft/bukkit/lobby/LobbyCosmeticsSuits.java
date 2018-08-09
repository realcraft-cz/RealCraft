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
import realcraft.bukkit.cosmetics2.Cosmetics;
import realcraft.bukkit.cosmetics2.suits.Suit;
import realcraft.bukkit.lobby.LobbyCosmeticsMain.LobbyCosmeticsType;

public class LobbyCosmeticsSuits {
	static LobbyCosmetics lobbycosmetics;
	static RealCraft plugin;

	public static void init(LobbyCosmetics lobbycosmetics){
		LobbyCosmeticsGadgets.lobbycosmetics = lobbycosmetics;
		LobbyCosmeticsGadgets.plugin = RealCraft.getInstance();
	}

	public static String getName(){
		return LobbyCosmeticsMain.LobbyCosmeticsType.SUITS.toString();
	}

	public static String getInventoryName(){
		return LobbyCosmeticsMain.LobbyCosmeticsType.SUITS.toInventoryName();
	}

	public static void openMenu(Player player){
		Inventory menu = Bukkit.createInventory(player,6*9,LobbyCosmeticsMain.LobbyCosmeticsType.SUITS.toInventoryName());

		setItem(menu,getIndex(1,1),Cosmetics.getSuit(Suit.SuitType.RAVE));
		setItem(menu,getIndex(1,3),Cosmetics.getSuit(Suit.SuitType.DIAMOND));
		setItem(menu,getIndex(1,5),Cosmetics.getSuit(Suit.SuitType.GOLD));
		setItem(menu,getIndex(1,7),Cosmetics.getSuit(Suit.SuitType.IRON));

		ArrayList<String> lore;

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro navrat");
		lore.add("§7do hlavniho menu.");
		menu.setItem(getIndex(5,3),getItem("§e§lDoplnky",Material.CHEST,(byte)0,1,lore));

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro zruseni");
		lore.add("§7vsech aktivnich brneni.");
		menu.setItem(getIndex(5,5),getItem("§c§lOdebrat brneni",Material.BARRIER,(byte)0,1,lore));

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

	public static void setItem(Inventory menu,int index,Suit suit){
		Player player = (Player) menu.getHolder();
		boolean enabled = suit.isEnabled(player);
		if(enabled) menu.setItem(index,getItem(suit.getType().toString(),suit.getType().toMaterial(),suit.getType().toData(),1,suit.getType().toLore()));
		else menu.setItem(index,getItem(suit.getType().toString(),Material.INK_SAC,(byte)8,1,suit.getType().toLore()));
	}

	public static void InventoryClickEvent(InventoryClickEvent event){
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		if(item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
			player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
			if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Suit.SuitType.RAVE.toString())){
				Cosmetics.getSuit(Suit.SuitType.RAVE).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Suit.SuitType.DIAMOND.toString())){
				Cosmetics.getSuit(Suit.SuitType.DIAMOND).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Suit.SuitType.GOLD.toString())){
				Cosmetics.getSuit(Suit.SuitType.GOLD).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Suit.SuitType.IRON.toString())){
				Cosmetics.getSuit(Suit.SuitType.IRON).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(LobbyCosmeticsType.MAIN.toItemName())){
				LobbyCosmeticsMain.openMenu(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§c§lOdebrat brneni")){
				Cosmetics.clearSuits(player);
				LobbyCosmeticsMain.openMenu(player);
			}
		}
	}
}