package realcraft.bukkit.fights.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.fights.FightPlayer.FightPlayerState;
import realcraft.bukkit.fights.FightState;
import realcraft.bukkit.fights.Fights;
import realcraft.bukkit.fights.duels.FightDuel;

public class FightMenuDuels implements Listener, Runnable {

	private static final String INVENTORY_NAME = "Fights > Duely";
	private static Inventory inventory;

	private HashMap<Integer,FightMenuDuelItem> items = new HashMap<Integer,FightMenuDuelItem>();

	public FightMenuDuels(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,20,20);
		this.update();
	}

	@Override
	public void run(){
		this.update();
	}

	private void update(){
		int rows = (Fights.getDuels().getDuels().size()/7)+1;
		if(rows >= 5) rows = 4;
		int size = (rows+2)*9;
		int index = 10;
		items.clear();
		for(FightDuel duel : Fights.getDuels().getDuels()){
			items.put(index,new FightMenuDuelItem(index,duel));
			index ++;
			if(index == 17) index = 19;
			else if(index == 26) index = 28;
			else if(index == 35) index = 37;
		}
		if(inventory == null) inventory = Bukkit.createInventory(null,size,INVENTORY_NAME);
		else if(inventory.getSize() != size){
			List<HumanEntity> viewers = inventory.getViewers();
			inventory = Bukkit.createInventory(null,size,INVENTORY_NAME);
			for(HumanEntity human : viewers){
				human.openInventory(inventory);
			}
		}
		inventory.clear();
		for(FightMenuDuelItem item : items.values()){
			inventory.setItem(item.getIndex(),this.getDuelItemStack(item.getDuel()));
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
					if(event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.DIAMOND_BLOCK){
						if(items.containsKey(event.getRawSlot())){
							FightDuel duel = items.get(event.getRawSlot()).getDuel();
							if(duel.getWinner() != null){
								fPlayer.getPlayer().sendMessage("§cTento duel jiz skoncil.");
								return;
							}
							fPlayer.getPlayer().playSound(fPlayer.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
							fPlayer.getPlayer().closeInventory();
							duel.joinPlayer(fPlayer);
						}
					}
				}
			}
		}
	}

	public ItemStack getDuelItemStack(FightDuel duel){
		ItemStack item = new ItemStack(Material.DIAMOND_BLOCK);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		meta.setDisplayName("§b§lDuel");
		lore.add("§f"+duel.getHealths()[0]+" §c"+FightDuel.CHAR_HEART+" §f"+duel.getPlayers()[0].getUser().getName());
		lore.add("§f"+duel.getHealths()[1]+" §c"+FightDuel.CHAR_HEART+" §f"+duel.getPlayers()[1].getUser().getName());
		if(duel.getState() == FightState.ENDING){
			lore.add("");
			lore.add("§a§lVitez");
			lore.add("§f"+(duel.getWinner() == null ? "§7Nikdo nevyhral" : duel.getWinner().getUser().getName()));
		} else {
			lore.add("");
			lore.add("§7Klikni pro sledovani");
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	private class FightMenuDuelItem {

		private int index;
		private FightDuel duel;

		public FightMenuDuelItem(int index,FightDuel duel){
			this.index = index;
			this.duel = duel;
		}

		public int getIndex(){
			return index;
		}

		public FightDuel getDuel(){
			return duel;
		}
	}
}