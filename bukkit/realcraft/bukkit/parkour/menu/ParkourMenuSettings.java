package realcraft.bukkit.parkour.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.parkour.Parkour;
import realcraft.bukkit.parkour.ParkourOfflinePlayer;
import realcraft.bukkit.parkour.ParkourPlayer;
import realcraft.bukkit.parkour.exceptions.ParkourCheckPointsException;
import realcraft.bukkit.parkour.exceptions.ParkourFinishLocationException;
import realcraft.bukkit.parkour.exceptions.ParkourNotTestedException;
import realcraft.bukkit.parkour.exceptions.ParkourStartLocationException;
import realcraft.bukkit.utils.Glow;

public class ParkourMenuSettings {
	public static void openMenu(ParkourPlayer player){
		Inventory menu = Bukkit.createInventory(null,3*9,ParkourMenuType.SETTINGS.getInventoryName());
		menu.setItem(0,ParkourMenuType.START.getItemStack());
		menu.setItem(1,ParkourMenuType.CHECKPOINT.getItemStack());
		menu.setItem(2,ParkourMenuType.FINISH.getItemStack());
		ItemStack item = ParkourMenuType.TEST.getItemStack();
		if(player.getArena().isTested()){
			ItemMeta meta = item.getItemMeta();
			meta.addEnchant(new Glow(255),1,true);
			item.setItemMeta(meta);
		}
		menu.setItem(6,item);
		menu.setItem(7,ParkourMenuType.RENAME.getItemStack());
		menu.setItem(8,ParkourMenuType.CONFIRM.getItemStack());
		menu.setItem(18,ParkourMenuType.CLOCK.getItemStack());
		menu.setItem(19,ParkourMenuType.FLOOR.getItemStack());
		menu.setItem(20,ParkourMenuType.BIOME.getItemStack());
		menu.setItem(21,ParkourMenuType.WORLDEDIT.getItemStack());
		if(player.isAuthor(player.getArena())) menu.setItem(26,ParkourMenuType.COLLABORATORS.getItemStack());
		else {
			ParkourOfflinePlayer collaborator = new ParkourOfflinePlayer(player.getArena().getAuthor());
			menu.setItem(26,collaborator.getItemStack(true));
		}
		int i = 25;
		for(ParkourOfflinePlayer collaborator : player.getArena().getCollaborators().values()){
			menu.setItem(i--,collaborator.getItemStack());
		}
		player.getPlayer().openInventory(menu);
		Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				player.getPlayer().updateInventory();
			}
		},10);
	}

	@SuppressWarnings("deprecation")
	public static void InventoryClickEvent(InventoryClickEvent event){
		ParkourPlayer player = Parkour.getPlayer((Player)event.getWhoClicked());
		ItemStack item = event.getCurrentItem();
		if(item != null){
			if(item.getType() == ParkourMenuType.START.getMaterial()){
				if(player.getPlayer().getInventory().contains(ParkourMenuType.START.getMaterial())) player.getPlayer().getInventory().remove(ParkourMenuType.START.getMaterial());
				player.getPlayer().getInventory().addItem(ParkourMenuType.START.getItemStack());
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_PICKUP,1,1);
			}
			else if(item.getType() == ParkourMenuType.CHECKPOINT.getMaterial()){
				if(player.getPlayer().getInventory().contains(ParkourMenuType.CHECKPOINT.getMaterial())) player.getPlayer().getInventory().remove(ParkourMenuType.CHECKPOINT.getMaterial());
				player.getPlayer().getInventory().addItem(ParkourMenuType.CHECKPOINT.getItemStack());
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_PICKUP,1,1);
			}
			else if(item.getType() == ParkourMenuType.FINISH.getMaterial()){
				if(player.getPlayer().getInventory().contains(ParkourMenuType.FINISH.getMaterial())) player.getPlayer().getInventory().remove(ParkourMenuType.FINISH.getMaterial());
				player.getPlayer().getInventory().addItem(ParkourMenuType.FINISH.getItemStack());
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_PICKUP,1,1);
			}
			else if(item.getType() == ParkourMenuType.TEST.getMaterial()){
				try {
					player.getArena().testPlayer(player);
				} catch (ParkourStartLocationException e){
					Parkour.sendMessage(player,"?cNastavte startovni pozici parkouru.");
					player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
				} catch (ParkourFinishLocationException e){
					Parkour.sendMessage(player,"?cNastavte cilovou pozici parkouru.");
					player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
				} catch (ParkourCheckPointsException e){
					Parkour.sendMessage(player,"?cNastavte alespon 1 zachytny bod (checkpoint).");
					player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
				}
			}
			else if(item.getType() == ParkourMenuType.RENAME.getMaterial()){
				if(player.isAuthor(player.getArena())){
					ParkourMenuRename.openMenu(player);
					player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1,1);
				}
				else player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
			}
			else if(item.getType() == ParkourMenuType.CONFIRM.getMaterial()){
				if(player.isAuthor(player.getArena())){
					try {
						player.getArena().setReady();
						player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,1);
						Parkour.sendMessage(player,"");
						Parkour.sendMessage(player,"?aParkour byl dokoncen a nyni je dostupny vsem.");
						Parkour.sendMessageToAll("");
						TextComponent message = new TextComponent(Parkour.PARKOUR_PREFIX+"?b"+player.getPlayer().getName()+" ?dvytvoril novy parkour.");
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/join "+player.getArena().getName()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("?7Klikni pro pripojeni").create()));
						Parkour.sendMessageToAll(message);
						message = new TextComponent(Parkour.PARKOUR_PREFIX+"?dPripojte se prikazem ?e/join "+player.getArena().getName());
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/join "+player.getArena().getName()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("?7Klikni pro pripojeni").create()));
						Parkour.sendMessageToAll(message);
						player.getPlayer().getPlayer().closeInventory();
						player.getPlayer().getPlayer().getInventory().clear();
						for(ParkourOfflinePlayer collaborator : player.getArena().getCollaborators().values()){
							Player bukkitPlayer = Bukkit.getServer().getPlayer(collaborator.getName());
							if(bukkitPlayer != null && bukkitPlayer.isOnline()){
								ParkourPlayer player2 = Parkour.getPlayer(bukkitPlayer);
								if(player2.getArena() == player.getArena()) player2.getArena().leavePlayer(player2);
							}
						}
						Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
							@Override
							public void run(){
								player.getArena().leavePlayer(player);
							}
						},2*20);
					} catch (ParkourStartLocationException e){
						Parkour.sendMessage(player,"?cNastavte startovni pozici parkouru.");
						player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
					} catch (ParkourFinishLocationException e){
						Parkour.sendMessage(player,"?cNastavte cilovou pozici parkouru.");
						player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
					} catch (ParkourCheckPointsException e){
						Parkour.sendMessage(player,"?cNastavte alespon 1 zachytny bod (checkpoint).");
						player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
					} catch (ParkourNotTestedException e){
						Parkour.sendMessage(player,"?cParkour neni dosud otestovan.");
						player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
					}
				}
				else player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
			}
			else if(item.getType() == ParkourMenuType.CLOCK.getMaterial()){
				ParkourMenuClock.openMenu(player);
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1,1);
			}
			else if(item.getType() == ParkourMenuType.FLOOR.getMaterial()){
				ParkourMenuFloor.openMenu(player);
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1,1);
			}
			else if(item.getType() == ParkourMenuType.BIOME.getMaterial()){
				ParkourMenuBiome.openMenu(player);
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1,1);
			}
			else if(item.getType() == ParkourMenuType.WORLDEDIT.getMaterial()){
				if(player.getPlayer().getInventory().contains(ParkourMenuType.WORLDEDIT.getMaterial())) player.getPlayer().getInventory().remove(ParkourMenuType.WORLDEDIT.getMaterial());
				player.getPlayer().getInventory().addItem(ParkourMenuType.WORLDEDIT.getItemStack());
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_PICKUP,1,1);
			}
			else if(player.isAuthor(player.getArena()) && item.getType() == ParkourMenuType.COLLABORATORS.getMaterial() && item.getData().getData() == (byte)0){
				ParkourMenuCollaborator.openMenu(player);
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1,1);
			}
			else if(player.isAuthor(player.getArena()) && item.getType() == ParkourMenuType.COLLABORATORS.getMaterial() && item.getData().getData() == (byte)3 && item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
				for(ParkourOfflinePlayer collaborator : player.getArena().getCollaborators().values()){
					if(collaborator.getName().equalsIgnoreCase(ChatColor.stripColor(item.getItemMeta().getDisplayName()))){
						player.getArena().removeCollaborator(collaborator.getId());
						player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_PICKUP,1,1);
						ParkourMenuSettings.openMenu(player);
					}
				}
			}
		}
		event.setCancelled(true);
	}
}
