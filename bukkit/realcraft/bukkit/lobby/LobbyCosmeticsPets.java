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
import realcraft.bukkit.cosmetics2.pets.Pet;
import realcraft.bukkit.lobby.LobbyCosmeticsMain.LobbyCosmeticsType;

public class LobbyCosmeticsPets {
	static LobbyCosmetics lobbycosmetics;
	static RealCraft plugin;

	public static void init(LobbyCosmetics lobbycosmetics){
		LobbyCosmeticsGadgets.lobbycosmetics = lobbycosmetics;
		LobbyCosmeticsGadgets.plugin = RealCraft.getInstance();
	}

	public static String getName(){
		return LobbyCosmeticsMain.LobbyCosmeticsType.PETS.toString();
	}

	public static String getInventoryName(){
		return LobbyCosmeticsMain.LobbyCosmeticsType.PETS.toInventoryName();
	}

	public static void openMenu(Player player){
		Inventory menu = Bukkit.createInventory(player,6*9,LobbyCosmeticsMain.LobbyCosmeticsType.PETS.toInventoryName());

		setItem(menu,getIndex(1,1),Cosmetics.getPet(Pet.PetType.PIGGY));
		setItem(menu,getIndex(1,2),Cosmetics.getPet(Pet.PetType.SHEEP));
		setItem(menu,getIndex(1,3),Cosmetics.getPet(Pet.PetType.EASTERBUNNY));
		setItem(menu,getIndex(1,4),Cosmetics.getPet(Pet.PetType.COW));
		setItem(menu,getIndex(1,5),Cosmetics.getPet(Pet.PetType.KITTY));
		setItem(menu,getIndex(1,6),Cosmetics.getPet(Pet.PetType.DOG));
		setItem(menu,getIndex(1,7),Cosmetics.getPet(Pet.PetType.CHICK));

		ArrayList<String> lore;

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro navrat");
		lore.add("§7do hlavniho menu.");
		menu.setItem(getIndex(5,3),getItem("§e§lDoplnky",Material.CHEST,1,lore));

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro zruseni");
		lore.add("§7vsech aktivnich efektu.");
		menu.setItem(getIndex(5,5),getItem("§c§lOdebrat mazlicky",Material.BARRIER,1,lore));

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

	public static void setItem(Inventory menu,int index,Pet effect){
		Player player = (Player) menu.getHolder();
		boolean enabled = effect.isEnabled(player);
		if(enabled) menu.setItem(index,getItem(effect.getType().toString(),effect.getType().toMaterial(),1,effect.getType().toLore()));
		else menu.setItem(index,getItem(effect.getType().toString(),Material.GRAY_DYE,1,effect.getType().toLore()));
	}

	public static void InventoryClickEvent(InventoryClickEvent event){
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		if(item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
			player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
			if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Pet.PetType.PIGGY.toString())){
				Cosmetics.getPet(Pet.PetType.PIGGY).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Pet.PetType.SHEEP.toString())){
				Cosmetics.getPet(Pet.PetType.SHEEP).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Pet.PetType.EASTERBUNNY.toString())){
				Cosmetics.getPet(Pet.PetType.EASTERBUNNY).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Pet.PetType.COW.toString())){
				Cosmetics.getPet(Pet.PetType.COW).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Pet.PetType.KITTY.toString())){
				Cosmetics.getPet(Pet.PetType.KITTY).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Pet.PetType.DOG.toString())){
				Cosmetics.getPet(Pet.PetType.DOG).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Pet.PetType.CHICK.toString())){
				Cosmetics.getPet(Pet.PetType.CHICK).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(LobbyCosmeticsType.MAIN.toItemName())){
				LobbyCosmeticsMain.openMenu(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§c§lOdebrat mazlicky")){
				Cosmetics.clearPets(player);
				LobbyCosmeticsMain.openMenu(player);
			}
		}
	}
}