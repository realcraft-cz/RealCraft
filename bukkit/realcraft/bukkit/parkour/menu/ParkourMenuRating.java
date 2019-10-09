package realcraft.bukkit.parkour.menu;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import realcraft.bukkit.parkour.Parkour;
import realcraft.bukkit.parkour.ParkourPlayer;
import realcraft.bukkit.parkour.exceptions.ParkourAlreadyRatedException;
import realcraft.bukkit.parkour.exceptions.ParkourOwnRatingException;

public class ParkourMenuRating {

	public static void openMenu(ParkourPlayer player) throws ParkourAlreadyRatedException, ParkourOwnRatingException {
		if(player.isRatedArena(player.getRatingArena())) throw new ParkourAlreadyRatedException();
		if(player.getRatingArena().getAuthor() == player.getId()) throw new ParkourOwnRatingException();
		Inventory menu = Bukkit.createInventory(null,1*9,ParkourMenuType.RATING.getInventoryName());
		menu.setItem(3,ParkourMenuType.RATING_YES.getItemStack());
		menu.setItem(5,ParkourMenuType.RATING_NO.getItemStack());
		player.getPlayer().openInventory(menu);
	}

	public static void InventoryClickEvent(InventoryClickEvent event){
		ParkourPlayer player = Parkour.getPlayer((Player)event.getWhoClicked());
		ItemStack item = event.getCurrentItem();
		if(item != null){
			if(item.getType() == ParkourMenuType.RATING_YES.getMaterial()){
				try {
					player.getRatingArena().addRating(player,1);
					player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
				} catch (ParkourAlreadyRatedException e){
				}
				player.getPlayer().getPlayer().closeInventory();
			}
			else if(item.getType() == ParkourMenuType.RATING_NO.getMaterial()){
				try {
					player.getRatingArena().addRating(player,-1);
					player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
				} catch (ParkourAlreadyRatedException e){
				}
				player.getPlayer().getPlayer().closeInventory();
			}
		}
		event.setCancelled(true);
	}
}
