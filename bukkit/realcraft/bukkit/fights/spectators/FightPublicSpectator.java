package realcraft.bukkit.fights.spectators;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.ChatColor;
import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.fights.FightPlayer.FightPlayerState;
import realcraft.bukkit.fights.FightType;
import realcraft.bukkit.fights.Fights;
import realcraft.bukkit.utils.ItemUtil;

public class FightPublicSpectator extends FightSpectator {

	private static final String INVENTORY_NAME = "Spectator";
	private Inventory inventory;
	private HashMap<Integer,FightSpectatorMenuItem> items = new HashMap<Integer,FightSpectatorMenuItem>();

	public Inventory getInventory(){
		if(inventory == null){
			inventory = Bukkit.createInventory(null,4*9,INVENTORY_NAME);
		}
		return inventory;
	}

	public ArrayList<FightSpectatorHotbarItem> getHotbarItems(){
		if(hotbarItems == null){
			hotbarItems = new ArrayList<FightSpectatorHotbarItem>();
			hotbarItems.add(new FightSpectatorHotbarItem(0,"§f§l"+INVENTORY_NAME,Material.COMPASS));
			hotbarItems.add(new FightSpectatorHotbarItem(8,"§e§lOpustit hru",Material.SLIME_BALL));
		}
		return hotbarItems;
	}

	public void update(){
		items.clear();
		int index = 0;
		for(FightPlayer fPlayer : Fights.getFightPlayers(FightType.PUBLIC)){
			if(fPlayer.getState() == FightPlayerState.FIGHT){
				items.put(index,new FightSpectatorMenuItem(index,fPlayer));
				index ++;
			}
		}
		this.getInventory().clear();
		for(FightSpectatorMenuItem item : items.values()){
			inventory.setItem(item.getIndex(),item.getItemStack());
		}
	}

	private void open(FightPlayer fPlayer){
		this.update();
		fPlayer.getPlayer().openInventory(this.getInventory());
	}

	@EventHandler(ignoreCancelled=false)
	public void PlayerInteractEvent(PlayerInteractEvent event){
		FightPlayer fPlayer = Fights.getFightPlayer(event.getPlayer());
		if(fPlayer.getState() == FightPlayerState.SPECTATOR){
			if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.COMPASS && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
				event.setCancelled(true);
				this.open(fPlayer);
			}
			else if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.SLIME_BALL && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
				event.setCancelled(true);
				Fights.joinLobby(fPlayer);
			}
		}
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			FightPlayer fPlayer = Fights.getFightPlayer((Player)event.getWhoClicked());
			if(fPlayer.getState() == FightPlayerState.SPECTATOR){
				if(event.getInventory().getName().equalsIgnoreCase(INVENTORY_NAME)){
					event.setCancelled(true);
					if(items.containsKey(event.getRawSlot())){
						FightSpectatorMenuItem item = items.get(event.getRawSlot());
						if(item.getPlayer().getPlayer() != null){
							fPlayer.getPlayer().closeInventory();
							fPlayer.getPlayer().teleport(item.getPlayer().getPlayer().getLocation());
							fPlayer.getPlayer().playSound(fPlayer.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
						}
					}
				}
			}
		}
	}

	private class FightSpectatorMenuItem {

		private int index;
		private FightPlayer fPlayer;
		private ItemStack item;

		public FightSpectatorMenuItem(int index,FightPlayer fPlayer){
			this.index = index;
			this.fPlayer = fPlayer;
			item = ItemUtil.getHead(fPlayer.getUser().getSkin().getValue());
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setDisplayName(ChatColor.RESET+fPlayer.getUser().getName());
			item.setItemMeta(meta);
		}

		public int getIndex(){
			return index;
		}

		public FightPlayer getPlayer(){
			return fPlayer;
		}

		public ItemStack getItemStack(){
			return item;
		}
	}
}