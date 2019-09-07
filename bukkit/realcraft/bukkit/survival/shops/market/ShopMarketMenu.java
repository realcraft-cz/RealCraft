package realcraft.bukkit.survival.shops.market;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.others.AbstractCommand;

import java.util.ArrayList;

public class ShopMarketMenu extends AbstractCommand implements Listener {

	private static final String INV_NAME = "Hracske obchody";

	public ShopMarketMenu(){
		super("market","markets","shops","trh");
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@Override
	public void perform(Player player,String[] args){
		ShopMarketMenu.openMenu(player);
	}

	public static void openMenu(Player player){
		Inventory menu = Bukkit.createInventory(player,6*9,INV_NAME);
		ArrayList<ShopMarketResidence> markets = ShopMarket.getSortedMarkets();
		for(int i=0;i<ShopMarket.MAX_MARKETS;i++){
			if(markets.size() > i){
				menu.setItem(10+i+((i/7)*2),markets.get(i).getItemStack());
			}
		}
		player.openInventory(menu);
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player && event.getView().getTitle().equalsIgnoreCase(INV_NAME)){
			event.setCancelled(true);
			Player player = (Player)event.getWhoClicked();
			if(event.getRawSlot() >= 10 && event.getRawSlot() <= 43 && event.getRawSlot()%9 >= 1 && event.getRawSlot()%9 <= 7){
				int idx = ((event.getRawSlot()/9)-1)*7+(event.getRawSlot()%9)-1;
				ArrayList<ShopMarketResidence> markets = ShopMarket.getSortedMarkets();
				if(markets.size() > idx){
					ShopMarketResidence market = markets.get(idx);
					if(market != null){
						ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByName(market.getResidence());
						if(residence != null){
							player.teleport(residence.getTeleportLocation(),PlayerTeleportEvent.TeleportCause.COMMAND);
							player.getWorld().playSound(residence.getTeleportLocation(),Sound.ENTITY_ENDERMAN_TELEPORT,1f,1f);
						}
					}
				}
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