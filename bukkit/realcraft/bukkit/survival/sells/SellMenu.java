package realcraft.bukkit.survival.sells;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.others.AbstractCommand;
import realcraft.bukkit.survival.economy.Economy;
import realcraft.bukkit.survival.sells.SellBasket.BasketItem;
import realcraft.bukkit.utils.ItemUtil;

import java.util.HashMap;
import java.util.Map;

public class SellMenu implements Listener {

	public static final String INV_NAME = "Vykupna surovin";
	private static HashMap<Player,SellBasket> baskets = new HashMap<>();

	public SellMenu(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		new AbstractCommand("sell","sells","vykupna"){
			@Override
			public void perform(Player player,String[] args){
				player.performCommand("warp vykupna");
			}
		};
	}

	public static SellBasket getNewBasket(Player player){
		baskets.put(player,new SellBasket(player));
		return baskets.get(player);
	}

	public static SellBasket getBasket(Player player){
		return baskets.get(player);
	}

	public static void openMenu(Player player){
		SellMenu.getNewBasket(player);
		SellMenu.updateMenu(player);
	}

	public static void updateMenu(Player player){
		SellBasket basket = SellMenu.getBasket(player);
		Inventory menu = Bukkit.createInventory(player,6*9,INV_NAME);

		ItemStack item;
		ItemMeta meta;
		for(Map.Entry<Integer,SellItem> entry : Sells.getItems().entrySet()){
			item = new ItemStack(entry.getValue().getType(),entry.getValue().getAmount());
			meta = item.getItemMeta();
			if(basket.hasNextEnough(item.getType(),1)) meta.addEnchant(Enchantment.LURE,1,true);
			meta.setLore(ItemUtil.getLores(
					"§fCena: §a"+Economy.format(entry.getValue().getPrice())+" §7("+entry.getValue().getAmount()+" ks)",
					"§fCena za kus: §a"+Economy.format(entry.getValue().getPrice()/(entry.getValue().getAmount()*1.0)),
					"§7Klikni pro pridani"
			));
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
			menu.setItem(entry.getKey(),item);
		}

		item = new ItemStack(Material.EMERALD_BLOCK);
		meta = item.getItemMeta();
		meta.setDisplayName("§a§lProdat");
		meta.setLore(ItemUtil.getLores(
				"§fCena: §a"+Economy.format(basket.getPrice()),
				"§7Klikni pro prodani"
		));
		item.setItemMeta(meta);
		menu.setItem(52,item);

		item = new ItemStack(Material.REDSTONE_BLOCK);
		meta = item.getItemMeta();
		meta.setDisplayName("§c§lZrusit");
		meta.setLore(ItemUtil.getLores("§7Klikni pro zruseni"));
		item.setItemMeta(meta);
		menu.setItem(53,item);

		item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		meta = item.getItemMeta();
		meta.setDisplayName("§r");
		item.setItemMeta(meta);
		for(int i=0;i<9;i++){
			menu.setItem(36+i,item);
		}
		menu.setItem(51,item);

		int idx = 0;
		for(BasketItem bItem : basket.getItems()){
			item = new ItemStack(bItem.getItem().getType(),bItem.getAmount());
			menu.setItem(45+idx++,item);
		}

		player.openInventory(menu);
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player && event.getView().getTitle().equalsIgnoreCase(INV_NAME)){
			event.setCancelled(true);
			Player player = (Player)event.getWhoClicked();
			SellBasket basket = SellMenu.getBasket(player);
			SellItem item = Sells.getItem(event.getRawSlot());
			if(item != null){
				try {
					basket.addItem(item,(event.isShiftClick() ? item.getType().getMaxStackSize() : item.getAmount()));
					player.playSound(player.getLocation(),Sound.ENTITY_CHICKEN_EGG,1f,1f);
				} catch (SellBasket.FullBasketException e){
					Sells.sendMessage(player,"§cKosik je plny.");
					player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
					return;
				} catch (SellBasket.NotEnoughtItemsException e){
					return;
				}
				SellMenu.updateMenu(player);
			}
			else if(event.getRawSlot() >= 45 && event.getRawSlot() <= 50){
				try {
					basket.removeItem(event.getRawSlot()-45,event.isShiftClick());
					player.playSound(player.getLocation(),Sound.ENTITY_CHICKEN_EGG,1f,0.5f);
				} catch (SellBasket.EmptySlotException e){
					return;
				}
				SellMenu.updateMenu(player);
			}
			else if(event.getRawSlot() == 52){
				try {
					basket.checkout();
					player.closeInventory();
					Sells.sendMessage(player,"Ziskal jsi §a"+Economy.format(basket.getPrice())+"§r prodejem surovin");
					player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
					player.closeInventory();
				} catch (SellBasket.EmptyBasketException e){
					Sells.sendMessage(player,"§cKosik je prazdny.");
					player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
				}
			}
			else if(event.getRawSlot() == 53){
				player.closeInventory();
			}
		}
	}

	@EventHandler
	public void InventoryDragEvent(InventoryDragEvent event){
		if(event.getWhoClicked() instanceof Player && event.getView().getTitle().equalsIgnoreCase(INV_NAME)){
			event.setCancelled(true);
		}
	}
}