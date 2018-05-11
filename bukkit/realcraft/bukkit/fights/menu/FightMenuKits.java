package realcraft.bukkit.fights.menu;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.fights.FightKit;
import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.fights.FightPlayer.FightPlayerState;
import realcraft.bukkit.fights.Fights;

public class FightMenuKits implements Listener {

	private static final String INVENTORY_NAME = "Fights > Vyber kitu";
	private static Inventory inventory;

	private HashMap<Integer,FightMenuKitItem> items = new HashMap<Integer,FightMenuKitItem>();

	public FightMenuKits(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		this.update();
	}

	private void update(){
		if(inventory == null) inventory = Bukkit.createInventory(null,3*9,INVENTORY_NAME);
		int index = 11;
		items.clear();
		for(FightKit kit : FightKit.values()){
			items.put(index,new FightMenuKitItem(index,kit));
			index ++;
		}
		inventory.clear();
		for(FightMenuKitItem item : items.values()){
			inventory.setItem(item.getIndex(),item.getKit().getMenuItemStack());
		}
	}

	public static void openMenu(FightPlayer fPlayer){
		fPlayer.getPlayer().openInventory(inventory);
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			FightPlayer fPlayer = Fights.getFightPlayer((Player)event.getWhoClicked());
			if(fPlayer.getState() == FightPlayerState.NONE){
				if(event.getInventory().getName().equalsIgnoreCase(INVENTORY_NAME)){
					event.setCancelled(true);
					if(event.getCurrentItem() != null){
						if(items.containsKey(event.getRawSlot())){
							FightKit kit = items.get(event.getRawSlot()).getKit();
							fPlayer.setKit(kit);
							Fights.getPublics().joinPlayer(fPlayer);
						}
					}
				}
			}
		}
	}

	private class FightMenuKitItem {

		private int index;
		private FightKit kit;

		public FightMenuKitItem(int index,FightKit kit){
			this.index = index;
			this.kit = kit;
		}

		public int getIndex(){
			return index;
		}

		public FightKit getKit(){
			return kit;
		}
	}
}