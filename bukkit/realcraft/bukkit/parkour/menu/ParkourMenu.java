package realcraft.bukkit.parkour.menu;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import realcraft.bukkit.parkour.Parkour;
import realcraft.bukkit.parkour.ParkourPlayer;
import realcraft.bukkit.parkour.exceptions.ParkourCreateLimitException;
import realcraft.bukkit.parkour.exceptions.ParkourInProgressException;
import realcraft.bukkit.utils.DateUtil;
import realcraft.bukkit.utils.StringUtil;

public class ParkourMenu {

	public static void openMenu(ParkourPlayer player){
		Inventory menu = Bukkit.createInventory(null,1*9,ParkourMenuType.MAIN.getInventoryName());

		ItemStack item;
		ItemMeta meta;
		ArrayList<String> lore;
		int size = 0;

		size = Parkour.getAvailableArenas().size();
		item = ParkourMenuType.NEWEST.getItemStack();
		meta = item.getItemMeta();
		lore = new ArrayList<String>();
		lore.add("§7Celkem "+size+" "+StringUtil.inflect(size,new String[]{"parkour","parkoury","parkouru"}));
		meta.setLore(lore);
		item.setItemMeta(meta);
		menu.setItem(0,item);

		item = ParkourMenuType.BEST.getItemStack();
		meta = item.getItemMeta();
		lore = new ArrayList<String>();
		lore.add("§7Celkem "+size+" "+StringUtil.inflect(size,new String[]{"parkour","parkoury","parkouru"}));
		meta.setLore(lore);
		item.setItemMeta(meta);
		menu.setItem(1,item);

		size = player.getLikedArenas().size();
		item = ParkourMenuType.LIKED.getItemStack();
		meta = item.getItemMeta();
		lore = new ArrayList<String>();
		lore.add("§7Celkem "+size+" "+StringUtil.inflect(size,new String[]{"parkour","parkoury","parkouru"}));
		meta.setLore(lore);
		item.setItemMeta(meta);
		menu.setItem(2,item);

		size = player.getOwnArenas().size();
		item = ParkourMenuType.OWN.getItemStack();
		meta = item.getItemMeta();
		lore = new ArrayList<String>();
		lore.add("§7Celkem "+size+" "+StringUtil.inflect(size,new String[]{"parkour","parkoury","parkouru"}));
		meta.setLore(lore);
		item.setItemMeta(meta);
		menu.setItem(7,item);

		menu.setItem(8,ParkourMenuType.CREATE.getItemStack());

		player.getPlayer().openInventory(menu);
	}

	public static void InventoryClickEvent(InventoryClickEvent event){
		ParkourPlayer player = Parkour.getPlayer((Player)event.getWhoClicked());
		ItemStack item = event.getCurrentItem();
		if(item != null && item.getType() != Material.AIR){
			if(item.getType() == ParkourMenuType.NEWEST.getMaterial()){
				ParkourMenuArenas.openMenu(player,ParkourMenuType.NEWEST);
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1,1);
			}
			else if(item.getType() == ParkourMenuType.BEST.getMaterial()){
				ParkourMenuArenas.openMenu(player,ParkourMenuType.BEST);
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1,1);
			}
			else if(item.getType() == ParkourMenuType.LIKED.getMaterial()){
				ParkourMenuArenas.openMenu(player,ParkourMenuType.LIKED);
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1,1);
			}
			else if(item.getType() == ParkourMenuType.OWN.getMaterial()){
				ParkourMenuArenas.openMenu(player,ParkourMenuType.OWN);
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1,1);
			}
			else if(item.getType() == ParkourMenuType.CREATE.getMaterial()){
				try {
					player.createArena();
				} catch (ParkourInProgressException e){
					Parkour.sendMessage(player,"§cJeden parkour jiz mas rozpracovany.");
					player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
				} catch (ParkourCreateLimitException e){
					Parkour.sendMessage(player,"§cDalsi parkour muzes vytvorit "+DateUtil.lastTime((int)(e.getLastCreated()+Parkour.PARKOUR_CREATE_LIMIT),true)+".");
					player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
				}
			}
			event.setCancelled(true);
		}
	}
}