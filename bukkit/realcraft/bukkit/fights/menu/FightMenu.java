package realcraft.bukkit.fights.menu;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.fights.FightPlayer.FightPlayerState;
import realcraft.bukkit.fights.FightType;
import realcraft.bukkit.fights.Fights;
import realcraft.bukkit.fights.events.FightPlayerJoinLobbyEvent;
import realcraft.bukkit.utils.BungeeMessages;
import realcraft.share.ServerType;
import realcraft.share.utils.StringUtil;

public class FightMenu implements Listener, Runnable {

	private static final String INVENTORY_NAME = "Fights";
	private static Inventory inventory;

	private ItemStack menuItem;
	private ItemStack leaveItem;

	private static int publics = -1;
	private static int duels = -1;

	public FightMenu(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,20,20);
		new FightMenuKits();
		new FightMenuDuels();
		this.update();
	}

	@Override
	public void run(){
		this.update();
	}

	private ItemStack getMenuItem(){
		if(menuItem == null){
			menuItem = new ItemStack(Material.WATCH);
			ItemMeta meta = menuItem.getItemMeta();
			meta.setDisplayName("§b§l"+INVENTORY_NAME);
			menuItem.setItemMeta(meta);
		}
		return menuItem;
	}

	private ItemStack getLeaveItem(){
		if(leaveItem == null){
			leaveItem = new ItemStack(Material.SLIME_BALL);
			ItemMeta meta = leaveItem.getItemMeta();
			meta.setDisplayName("§e§lHlavni lobby");
			leaveItem.setItemMeta(meta);
		}
		return leaveItem;
	}

	private void update(){
		if(inventory == null) inventory = Bukkit.createInventory(null,3*9,INVENTORY_NAME);
		int tmpPublics = Fights.getFightPlayers(FightType.PUBLIC).size();
		if(publics != tmpPublics){
			publics = tmpPublics;
			inventory.setItem(11,FightMenuType.PUBLICS.getItemStack());
		}
		int tmpDuels = Fights.getDuels().getDuels().size();
		if(duels != tmpDuels){
			duels = tmpDuels;
			inventory.setItem(15,FightMenuType.DUELS.getItemStack());
		}
	}

	private void openMenu(FightPlayer fPlayer){
		fPlayer.getPlayer().openInventory(inventory);
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void FightPlayerJoinLobbyEvent(FightPlayerJoinLobbyEvent event){
		Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
			public void run(){
				if(event.getPlayer().getPlayer() != null && event.getPlayer().getState() == FightPlayerState.NONE){
					event.getPlayer().getPlayer().getInventory().setItem(0,FightMenu.this.getMenuItem());
					event.getPlayer().getPlayer().getInventory().setItem(8,FightMenu.this.getLeaveItem());
				}
			}
		},10);
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			FightPlayer fPlayer = Fights.getFightPlayer(event.getPlayer());
			if(fPlayer.getState() == FightPlayerState.NONE){
				if(event.getPlayer().getInventory().getItemInMainHand().getType() == this.getMenuItem().getType()){
					event.setCancelled(true);
					this.openMenu(fPlayer);
				}
				else if(event.getPlayer().getInventory().getItemInMainHand().getType() == this.getLeaveItem().getType()){
					event.setCancelled(true);
					BungeeMessages.connectPlayerToServer(event.getPlayer(),ServerType.LOBBY);
				}
			}
		}
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			FightPlayer fPlayer = Fights.getFightPlayer((Player)event.getWhoClicked());
			if(fPlayer.getState() == FightPlayerState.NONE){
				if(event.getInventory().getName().equalsIgnoreCase(INVENTORY_NAME)){
					event.setCancelled(true);
					if(event.getCurrentItem() != null){
						if(event.getCurrentItem().getType() == FightMenuType.PUBLICS.getMaterial()){
							fPlayer.getPlayer().playSound(fPlayer.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
							fPlayer.getPlayer().closeInventory();
							if(!event.isRightClick()){
								FightMenuKits.openMenu(fPlayer);
							} else {
								Fights.getPublics().joinSpectator(fPlayer);
							}
						}
						else if(event.getCurrentItem().getType() == FightMenuType.DUELS.getMaterial()){
							if(!event.isRightClick()){
								fPlayer.joinQueue();
								fPlayer.getPlayer().closeInventory();
							} else {
								fPlayer.getPlayer().playSound(fPlayer.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
								FightMenuDuels.openMenu(fPlayer);
							}
						}
					}
				}
				if(event.getCurrentItem() != null && (event.getCurrentItem().getType() == this.getMenuItem().getType() || event.getCurrentItem().getType() == this.getLeaveItem().getType())){
					event.setCancelled(true);
				}
				else if(event.getClick() == ClickType.NUMBER_KEY){
					event.setCancelled(true);
				}
			}
		}
	}

	private enum FightMenuType {
		PUBLICS, DUELS;

		public ItemStack getItemStack(){
			ItemStack item = new ItemStack(this.getMaterial(),(this == PUBLICS ? (publics < 1 ? 1 : publics) : (duels < 1 ? 1 : duels)));
			ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			ArrayList<String> lore = new ArrayList<String>();
			if(this == PUBLICS){
				meta.setDisplayName("§e§lFFA");
				lore.add("§fHraje §l"+publics+"§f "+StringUtil.inflect(publics,new String[]{"hrac","hraci","hracu"}));
				lore.add("");
				lore.add("§7Klikni pravym");
				lore.add("§7pro sledovani");
			}
			else if(this == DUELS){
				meta.setDisplayName("§b§lDuely");
				lore.add("§f"+StringUtil.inflect(duels,new String[]{"Probiha","Probihaji","Probiha"})+" §l"+duels+"§f "+StringUtil.inflect(duels,new String[]{"duel","duely","duelu"}));
				lore.add("");
				lore.add("§7Klikni pravym");
				lore.add("§7pro sledovani");
			}
			meta.setLore(lore);
			item.setItemMeta(meta);
			return item;
		}

		public Material getMaterial(){
			switch(this){
				case PUBLICS: return Material.GOLD_CHESTPLATE;
				case DUELS: return Material.DIAMOND_CHESTPLATE;
			}
			return Material.AIR;
		}
	}
}