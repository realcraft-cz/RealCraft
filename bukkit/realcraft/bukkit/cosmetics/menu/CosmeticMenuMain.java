package realcraft.bukkit.cosmetics.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.Cosmetics;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticCategory;
import realcraft.bukkit.utils.ItemUtil;

import java.util.HashMap;
import java.util.Map;

public class CosmeticMenuMain implements Listener {

	public static final String INV_NAME = "Doplnky";
	private static HashMap<Integer,CosmeticMenuItem> items = new HashMap<>();

	public CosmeticMenuMain(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		//items.put(getIndex(1,2),new CosmeticMenuItem(CosmeticMenuItem.CosmeticMenuItemType.CATEGORY,CosmeticCategory.PET));
		items.put(getIndex(1,4),new CosmeticMenuItem(CosmeticMenuItem.CosmeticMenuItemType.CATEGORY,CosmeticCategory.GADGET));
		items.put(getIndex(1,6),new CosmeticMenuItem(CosmeticMenuItem.CosmeticMenuItemType.CATEGORY,CosmeticCategory.EFFECT));
		items.put(getIndex(3,2),new CosmeticMenuItem(CosmeticMenuItem.CosmeticMenuItemType.CATEGORY,CosmeticCategory.HAT));
		//items.put(getIndex(3,4),new CosmeticMenuItem(CosmeticMenuItem.CosmeticMenuItemType.CATEGORY,CosmeticCategory.SUIT));
		//items.put(getIndex(3,6),new CosmeticMenuItem(CosmeticMenuItem.CosmeticMenuItemType.CATEGORY,CosmeticCategory.MOUNT));
		items.put(getIndex(5,4),new CosmeticMenuItem(CosmeticMenuItem.CosmeticMenuItemType.CLEAR));
	}

	public static void openMenu(Player player){
		Inventory menu = Bukkit.createInventory(player,6*9,INV_NAME);

		for(Map.Entry<Integer,CosmeticMenuItem> entry : items.entrySet()){
			ItemStack item = new ItemStack(Material.AIR);
			ItemMeta meta = item.getItemMeta();
			if(entry.getValue().getType() == CosmeticMenuItem.CosmeticMenuItemType.CATEGORY){
				item = new ItemStack(entry.getValue().getCategory().getMaterial());
				meta = item.getItemMeta();
				meta.setDisplayName("§b§l"+entry.getValue().getCategory().getName());
				if(entry.getValue().getCategory().isAvailable(player.getWorld())){
					meta.setLore(ItemUtil.getLores("§7Klikni pro otevreni."));
				} else {
					item.setType(Material.GRAY_DYE);
					meta.setLore(ItemUtil.getLores("§cNelze pouzit v tomto svete."));
				}
			}
			else if(entry.getValue().getType() == CosmeticMenuItem.CosmeticMenuItemType.CLEAR){
				item = new ItemStack(Material.BARRIER);
				meta = item.getItemMeta();
				meta.setDisplayName("§c§lOdebrat doplnky");
				meta.setLore(ItemUtil.getLores(
						"§7Klikni pro zruseni",
						"§7vsech aktivnich doplnku."
				));
			}
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			item.setItemMeta(meta);
			menu.setItem(entry.getKey(),item);
		}
		player.openInventory(menu);
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player && event.getView().getTitle().equalsIgnoreCase(INV_NAME)){
			event.setCancelled(true);
			Player player = (Player)event.getWhoClicked();
			if(Cosmetics.isAvailable(player.getWorld())){
				CosmeticMenuItem item = items.get(event.getRawSlot());
				if(item != null){
					if(item.getType() == CosmeticMenuItem.CosmeticMenuItemType.CATEGORY){
						if(item.getCategory().isAvailable(player.getWorld())){
							CosmeticMenuCategory.openMenu(player,item.getCategory());
						} else {
							player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
						}
					}
					else if(item.getType() == CosmeticMenuItem.CosmeticMenuItemType.CLEAR){
						player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
						player.closeInventory();
						Cosmetics.disableCosmetics(player);
					}
				}
			}
		}
	}

	private static int getIndex(int row,int column){
		return (row*9)+column;
	}
}