package realcraft.bukkit.cosmetics.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.CosmeticPlayer;
import realcraft.bukkit.cosmetics.Cosmetics;
import realcraft.bukkit.cosmetics.cosmetic.Cosmetic;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticCategory;
import realcraft.bukkit.utils.ItemUtil;

import java.util.HashMap;

public class CosmeticMenuCategory implements Listener {

	private static final int MAX_ITEMS_PER_PAGE = 21;
	private static HashMap<CosmeticCategory,HashMap<Integer,CosmeticMenuItem>> items = new HashMap<>();

	public CosmeticMenuCategory(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		for(CosmeticCategory category : CosmeticCategory.values()){
			HashMap<Integer,CosmeticMenuItem> tmpItems = new HashMap<>();
			int idx = 0;
			for(Cosmetic cosmetic : Cosmetics.getCosmetics(category)){
				tmpItems.put(idx++,new CosmeticMenuItem(CosmeticMenuItem.CosmeticMenuItemType.COSMETIC,cosmetic.getType()));
			}
			items.put(category,tmpItems);
		}
	}

	public static void openMenu(Player player,CosmeticCategory category){
		openMenu(player,category,1);
	}

	public static void openMenu(Player player,CosmeticCategory category,int page){
		player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
		Cosmetics.getCosmeticPlayer(player).setMenuCategory(category);
		Cosmetics.getCosmeticPlayer(player).setMenuPage(page);
		Inventory menu = Bukkit.createInventory(player,6*9,CosmeticMenuMain.INV_NAME+" > "+category.getName());

		int maxPage = (int)Math.ceil(items.get(category).size()/(MAX_ITEMS_PER_PAGE*1.0));
		if(page < 1) page = 1;
		else if(page > maxPage) page = maxPage;

		for(int i=0;i<MAX_ITEMS_PER_PAGE;i++){
			int idx = i+((page-1)*MAX_ITEMS_PER_PAGE);
			if(items.get(category).size() > idx){
				Cosmetic cosmetic = Cosmetics.getCosmetic(items.get(category).get(idx).getCosmetic());
				menu.setItem(10+i+((i/7)*2),cosmetic.getItemStack(player));
			}
		}

		ItemStack item;
		ItemMeta meta;
		if(page > 1){
			item = new ItemStack(Material.PAPER);
			meta = item.getItemMeta();
			meta.setDisplayName("§6§lPredchozi");
			item.setItemMeta(meta);
			menu.setItem(getIndex(4,1),item);
		}
		if(page < maxPage){
			item = new ItemStack(Material.PAPER);
			meta = item.getItemMeta();
			meta.setDisplayName("§6§lDalsi");
			item.setItemMeta(meta);
			menu.setItem(getIndex(4,7),item);
		}

		item = new ItemStack(Material.CHEST);
		meta = item.getItemMeta();
		meta.setDisplayName("§e§lDoplnky");
		meta.setLore(ItemUtil.getLores("§7Klikni pro navrat zpet"));
		item.setItemMeta(meta);
		menu.setItem(getIndex(5,3),item);

		item = new ItemStack(Material.BARRIER);
		meta = item.getItemMeta();
		meta.setDisplayName("§c§lOdebrat doplnky");
		meta.setLore(ItemUtil.getLores("§7Klikni pro zruseni doplnku"));
		item.setItemMeta(meta);
		menu.setItem(getIndex(5,5),item);

		player.openInventory(menu);
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			Player player = (Player)event.getWhoClicked();
			CosmeticPlayer cPlayer = Cosmetics.getCosmeticPlayer(player);
			if(cPlayer.getMenuCategory() != null && event.getView().getTitle().equalsIgnoreCase(CosmeticMenuMain.INV_NAME+" > "+cPlayer.getMenuCategory().getName())){
				event.setCancelled(true);
				if(Cosmetics.isAvailable(player.getWorld())){
					if(event.getRawSlot() >= 10 && event.getRawSlot() <= 34 && event.getRawSlot()%9 >= 1 && event.getRawSlot()%9 <= 7){
						int idx = (((event.getRawSlot()/9)-1)*7+(event.getRawSlot()%9)-1)+(MAX_ITEMS_PER_PAGE*(cPlayer.getMenuPage()-1));
						CosmeticMenuItem item = items.get(cPlayer.getMenuCategory()).get(idx);
						if(item != null){
							if(!item.getCosmetic().getCategory().isAvailable(player.getWorld())){
								player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
								return;
							}
							if(!cPlayer.hasCosmetic(item.getCosmetic())){
								player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
								return;
							}
							player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
							player.closeInventory();
							Cosmetics.getCosmetic(item.getCosmetic()).setEnabled(player,true);
						}
					}
					else if(event.getRawSlot() == getIndex(4,1) && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PAPER){
						CosmeticMenuCategory.openMenu(player,cPlayer.getMenuCategory(),cPlayer.getMenuPage()-1);
					}
					else if(event.getRawSlot() == getIndex(4,7) && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PAPER){
						CosmeticMenuCategory.openMenu(player,cPlayer.getMenuCategory(),cPlayer.getMenuPage()+1);
					}
					else if(event.getRawSlot() == getIndex(5,3) && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.CHEST){
						player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
						CosmeticMenuMain.openMenu(player);
					}
					else if(event.getRawSlot() == getIndex(5,5) && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BARRIER){
						player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
						player.closeInventory();
						Cosmetics.disableCosmetics(player,cPlayer.getMenuCategory());
					}
				}
			}
		}
	}

	private static int getIndex(int row,int column){
		return (row*9)+column;
	}
}