package realcraft.bukkit.cosmetics.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.auth.AuthLoginEvent;
import realcraft.bukkit.cosmetics.Cosmetics;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.AbstractCommand;

public class CosmeticMenu implements Listener {

	private ItemStack hotbarItem;

	public CosmeticMenu(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		new CosmeticMenuMain();
		new CosmeticMenuCategory();
		new AbstractCommand("cosmetics","cosmetic","doplnky"){
			@Override
			public void perform(Player player,String[] args){
				if(Cosmetics.isAvailable(player.getWorld())){
					CosmeticMenuMain.openMenu(player);
				}
			}
		};
	}

	private ItemStack getHotbarItem(){
		if(hotbarItem == null){
			hotbarItem = new ItemStack(Material.CHEST);
			ItemMeta meta = hotbarItem.getItemMeta();
			meta.setDisplayName("§e§lDoplnky");
			hotbarItem.setItemMeta(meta);
		}
		return hotbarItem;
	}

	private void giveHotbarItem(Player player){
		player.getInventory().setItem(4,this.getHotbarItem());
	}

	private void removeHotbarItem(Player player){
		player.getInventory().remove(this.getHotbarItem());
	}

	@EventHandler
	public void AuthLoginEvent(AuthLoginEvent event){
		Player player = event.getPlayer();
		if(Cosmetics.isAvailable(player.getWorld())){
			this.giveHotbarItem(player);
			Cosmetics.loadCosmetics(player);
		}
	}

	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(Users.getUser(player).isLogged() && Cosmetics.isAvailable(player.getWorld())){
			this.giveHotbarItem(player);
		}
	}

	@EventHandler
	public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event){
		Player player = event.getPlayer();
		if(Cosmetics.isAvailable(player.getWorld())){
			this.giveHotbarItem(player);
			Cosmetics.loadCosmetics(player);
		} else {
			this.removeHotbarItem(player);
			Cosmetics.clearCosmetics(player);
		}
	}

	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent event){
		Player player = event.getPlayer();
		Cosmetics.clearCosmetics(player);
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(Cosmetics.isAvailable(player.getWorld()) && player.getInventory().getItemInMainHand().isSimilar(this.getHotbarItem()) && event.getAction() != Action.PHYSICAL){
			event.setCancelled(true);
			CosmeticMenuMain.openMenu(player);
		}
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			Player player = (Player)event.getWhoClicked();
			if(Cosmetics.isAvailable(player.getWorld())){
				ItemStack item = event.getCurrentItem();
				if(event.getClick() == ClickType.NUMBER_KEY) item = player.getInventory().getItem(event.getHotbarButton());
				if(item != null && item.isSimilar(this.getHotbarItem())){
					event.setCancelled(true);
					if(event.getSlotType() == InventoryType.SlotType.QUICKBAR) CosmeticMenuMain.openMenu(player);
				}
			}
		}
	}

	@EventHandler
	public void InventoryDragEvent(InventoryDragEvent event){
		if(event.getOldCursor().isSimilar(this.getHotbarItem())){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void PlayerDropItemEvent(PlayerDropItemEvent event){
		Player player = event.getPlayer();
		if(Cosmetics.isAvailable(player.getWorld()) && event.getItemDrop().getItemStack().isSimilar(this.getHotbarItem())){
			event.setCancelled(true);
		}
	}
}