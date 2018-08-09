package realcraft.bukkit.lobby;

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
import realcraft.bukkit.cosmetics2.mounts.Mount;
import realcraft.bukkit.lobby.LobbyCosmeticsMain.LobbyCosmeticsType;

import java.util.ArrayList;

public class LobbyCosmeticsMounts {
	static LobbyCosmetics lobbycosmetics;
	static RealCraft plugin;

	public static void init(LobbyCosmetics lobbycosmetics){
		LobbyCosmeticsGadgets.lobbycosmetics = lobbycosmetics;
		LobbyCosmeticsGadgets.plugin = RealCraft.getInstance();
	}

	public static String getName(){
		return LobbyCosmeticsMain.LobbyCosmeticsType.MOUNTS.toString();
	}

	public static String getInventoryName(){
		return LobbyCosmeticsMain.LobbyCosmeticsType.MOUNTS.toInventoryName();
	}

	public static void openMenu(Player player){
		Inventory menu = Bukkit.createInventory(player,6*9,LobbyCosmeticsMain.LobbyCosmeticsType.MOUNTS.toInventoryName());

		setItem(menu,getIndex(1,2),Cosmetics.getMount(Mount.MountType.INFERNALHORROR));
		setItem(menu,getIndex(1,3),Cosmetics.getMount(Mount.MountType.WALKINGDEAD));
		setItem(menu,getIndex(1,4),Cosmetics.getMount(Mount.MountType.GLACIALSTEED));
		setItem(menu,getIndex(1,5),Cosmetics.getMount(Mount.MountType.SNAKE));
		setItem(menu,getIndex(1,6),Cosmetics.getMount(Mount.MountType.NYANSHEEP));
		setItem(menu,getIndex(2,2),Cosmetics.getMount(Mount.MountType.PIG));
		setItem(menu,getIndex(2,3),Cosmetics.getMount(Mount.MountType.SPIDER));
		setItem(menu,getIndex(2,4),Cosmetics.getMount(Mount.MountType.SLIME));

		ArrayList<String> lore;

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro navrat");
		lore.add("§7do hlavniho menu.");
		menu.setItem(getIndex(5,3),getItem("§e§lDoplnky",Material.CHEST,1,lore));

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro zruseni");
		lore.add("§7vsech aktivnich efektu.");
		menu.setItem(getIndex(5,5),getItem("§c§lOdebrat jezdecka zvirata",Material.BARRIER,1,lore));

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

	public static void setItem(Inventory menu,int index,Mount effect){
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
			if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Mount.MountType.INFERNALHORROR.toString())){
				Cosmetics.getMount(Mount.MountType.INFERNALHORROR).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Mount.MountType.WALKINGDEAD.toString())){
				Cosmetics.getMount(Mount.MountType.WALKINGDEAD).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Mount.MountType.GLACIALSTEED.toString())){
				Cosmetics.getMount(Mount.MountType.GLACIALSTEED).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Mount.MountType.SNAKE.toString())){
				Cosmetics.getMount(Mount.MountType.SNAKE).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Mount.MountType.NYANSHEEP.toString())){
				Cosmetics.getMount(Mount.MountType.NYANSHEEP).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Mount.MountType.PIG.toString())){
				Cosmetics.getMount(Mount.MountType.PIG).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Mount.MountType.SPIDER.toString())){
				Cosmetics.getMount(Mount.MountType.SPIDER).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Mount.MountType.SLIME.toString())){
				Cosmetics.getMount(Mount.MountType.SLIME).equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(LobbyCosmeticsType.MAIN.toItemName())){
				LobbyCosmeticsMain.openMenu(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§c§lOdebrat jezdecka zvirata")){
				Cosmetics.clearMounts(player);
				LobbyCosmeticsMain.openMenu(player);
			}
		}
	}
}