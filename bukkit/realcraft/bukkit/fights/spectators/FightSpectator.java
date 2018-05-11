package realcraft.bukkit.fights.spectators;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.fights.FightPlayer.FightPlayerState;
import realcraft.bukkit.fights.Fights;

public abstract class FightSpectator implements Listener {

	protected ArrayList<FightSpectatorHotbarItem> hotbarItems;

	public FightSpectator(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	public abstract ArrayList<FightSpectatorHotbarItem> getHotbarItems();

	public void remove(){
		HandlerList.unregisterAll(this);
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			FightPlayer fPlayer = Fights.getFightPlayer((Player)event.getWhoClicked());
			if(fPlayer.getState() == FightPlayerState.SPECTATOR){
				event.setCancelled(true);
			}
		}
	}

	public class FightSpectatorHotbarItem {

		private int index;
		private ItemStack item;

		public FightSpectatorHotbarItem(int index,String name,Material type){
			this.index = index;
			item = new ItemStack(type);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(name);
			item.setItemMeta(meta);
		}

		public int getIndex(){
			return index;
		}

		public ItemStack getItemStack(){
			return item;
		}
	}
}