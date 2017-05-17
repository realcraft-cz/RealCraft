package com.parkour.menu;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.parkour.Parkour;
import com.parkour.ParkourArena;
import com.parkour.ParkourPlayer;
import com.parkour.exceptions.ParkourNotReadyException;
import com.realcraft.utils.Glow;

public class ParkourMenuArenas {
	public static void openMenu(ParkourPlayer player,ParkourMenuType sortType){
		openMenu(player,sortType,1);
	}

	@SuppressWarnings("deprecation")
	public static void openMenu(ParkourPlayer player,ParkourMenuType sortType,int page){
		player.setMenuPage(page);
		Inventory menu = Bukkit.createInventory(null,6*9,sortType.getInventoryName());
		ItemStack item;
		ItemMeta meta;

		ArrayList<ParkourArena> arenas = new ArrayList<ParkourArena>();
		if(sortType == ParkourMenuType.NEWEST || sortType == ParkourMenuType.BEST) arenas = Parkour.getSortedArenas(sortType);
		else if(sortType == ParkourMenuType.OWN) arenas = player.getOwnArenas();
		else if(sortType == ParkourMenuType.LIKED) arenas = player.getLikedArenas();
		for(int i=0;i<5*9;i++){
			int index = i+((page-1)*(5*9));
			if(arenas.size() > index){
				ParkourArena arena = arenas.get(index);
				if(arena != null){
					int players = arena.getPlayers().size();
					if(players == 0) players = 1;
					item = new ItemStack(Material.INK_SACK,players,(short)0,arena.getColor());
					meta = item.getItemMeta();
					meta.setDisplayName("§r"+arena.getName());
					meta.setLore(arena.getInfo(player));
					if(player.getArenaRecord(arena) == null) meta.addEnchant(new Glow(255),1,true);
					item.setItemMeta(meta);
					menu.setItem(i,item);
				}
			}
		}

		int maxPage = (int)Math.ceil(arenas.size()/(5*9.0));
		item = new ItemStack(Material.MAP,page);
		meta = item.getItemMeta();
		meta.setDisplayName("§f§lStrana "+page+"/"+maxPage);
		item.setItemMeta(meta);
		menu.setItem(49,item);

		if(page > 1){
			item = new ItemStack(ParkourMenuType.PREVIOUS.getMaterial());
			meta = item.getItemMeta();
			meta.setDisplayName(ParkourMenuType.PREVIOUS.getItemName());
			item.setItemMeta(meta);
			menu.setItem(45,item);
		}
		if(page < maxPage){
			item = new ItemStack(ParkourMenuType.NEXT.getMaterial());
			meta = item.getItemMeta();
			meta.setDisplayName(ParkourMenuType.NEXT.getItemName());
			item.setItemMeta(meta);
			menu.setItem(53,item);
		}

		player.getPlayer().openInventory(menu);
	}

	public static void nextPage(ParkourPlayer player,ParkourMenuType sortType){
		player.setMenuPage(player.getMenuPage()+1);
		ParkourMenuArenas.openMenu(player,sortType,player.getMenuPage());
	}

	public static void previousPage(ParkourPlayer player,ParkourMenuType sortType){
		player.setMenuPage(player.getMenuPage()-1);
		ParkourMenuArenas.openMenu(player,sortType,player.getMenuPage());
	}

	public static void InventoryClickEvent(InventoryClickEvent event){
		ParkourPlayer player = Parkour.getPlayer((Player)event.getWhoClicked());
		ItemStack item = event.getCurrentItem();
		if(item != null){
			if(item.getType() == Material.INK_SACK){
				ArrayList<ParkourArena> arenas = new ArrayList<ParkourArena>();
				if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.NEWEST.getInventoryName())) arenas = Parkour.getSortedArenas(ParkourMenuType.NEWEST);
				else if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.BEST.getInventoryName())) arenas = Parkour.getSortedArenas(ParkourMenuType.BEST);
				else if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.OWN.getInventoryName())) arenas = player.getOwnArenas();
				else if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.LIKED.getInventoryName())) arenas = player.getLikedArenas();
				int index = event.getRawSlot()+((player.getMenuPage()-1)*(5*9));
				if(arenas.size() > index){
					ParkourArena arena = arenas.get(index);
					if(arena != null){
						try {
							arena.joinPlayer(player);
						} catch (ParkourNotReadyException e){
							Parkour.sendMessage(player,"§cTento parkour neni dokonceny.");
							player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
						}
					}
				}
			}
			else if(item.getType() == ParkourMenuType.PREVIOUS.getMaterial() && item.getItemMeta().getDisplayName().equalsIgnoreCase(ParkourMenuType.PREVIOUS.getItemName())){
				ParkourMenuType type = null;
				if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.NEWEST.getInventoryName())) type = ParkourMenuType.NEWEST;
				else if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.BEST.getInventoryName())) type = ParkourMenuType.BEST;
				else if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.OWN.getInventoryName())) type = ParkourMenuType.OWN;
				else if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.LIKED.getInventoryName())) type = ParkourMenuType.LIKED;
				ParkourMenuArenas.previousPage(player,type);
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1,1);
			}
			else if(item.getType() == ParkourMenuType.NEXT.getMaterial() && item.getItemMeta().getDisplayName().equalsIgnoreCase(ParkourMenuType.NEXT.getItemName())){
				ParkourMenuType type = null;
				if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.NEWEST.getInventoryName())) type = ParkourMenuType.NEWEST;
				else if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.BEST.getInventoryName())) type = ParkourMenuType.BEST;
				else if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.OWN.getInventoryName())) type = ParkourMenuType.OWN;
				else if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.LIKED.getInventoryName())) type = ParkourMenuType.LIKED;
				ParkourMenuArenas.nextPage(player,type);
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1,1);
			}
		}
		event.setCancelled(true);
	}
}