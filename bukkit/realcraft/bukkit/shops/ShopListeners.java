package realcraft.bukkit.shops;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.event.ResidenceDeleteEvent;
import com.bekvon.bukkit.residence.event.ResidenceRenameEvent;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.playermanazer.PlayerManazer;
import realcraft.bukkit.shops.ShopMarket.ShopMarketPlace;
import realcraft.bukkit.shops.ShopMarket.ShopMarketResidence;
import realcraft.bukkit.shops.ShopPlayer.ShopPlayerCommand;
import realcraft.bukkit.utils.ItemUtil;
import realcraft.bukkit.utils.Title;

public class ShopListeners implements Listener, CommandExecutor {

	public ShopListeners(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		RealCraft.getInstance().getCommand("shop").setExecutor(this);
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		ShopManager.getPlayer(event.getPlayer());
	}

	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent event){
		ShopManager.removePlayer(event.getPlayer());
	}

	@EventHandler
	public void ResidenceDeleteEvent(ResidenceDeleteEvent event){
		ShopMarketResidence market = ShopMarket.getMarketByName(event.getResidence().getName());
		if(market != null) ShopMarket.removeMarket(market);
	}

	@EventHandler
	public void ResidenceRenameEvent(ResidenceRenameEvent event){
		ShopMarketResidence market = ShopMarket.getMarketByName(event.getOldResidenceName());
		if(market != null) ShopMarket.renameMarket(market,event.getNewResidenceName());
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void PlayerInteractEvent(PlayerInteractEvent event){
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if(player.getWorld().getName().equalsIgnoreCase(ShopManager.WORLD) && player.getGameMode() != GameMode.SPECTATOR){
			if(block != null && ShopUtil.isChest(block)){
				Shop shop = ShopManager.getShop(block.getLocation());
				if(shop != null){
					if(ShopManager.getPlayer(player).hasCommand(ShopPlayerCommand.CREATE)){
						event.setCancelled(true);
						ShopManager.sendMessage(player,"§cTato truhla je jiz obchodem.");
					} else {
						if(!shop.isOwner(player)){
							if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
								event.setCancelled(true);
								shop.buy(player);
							}
							else if(event.getAction() == Action.LEFT_CLICK_BLOCK && (player.getGameMode() != GameMode.CREATIVE || !player.isSneaking())){
								event.setCancelled(true);
								shop.showInfo(player);
							}
						} else {
							if(event.getAction() == Action.LEFT_CLICK_BLOCK){
								shop.showInfo(player);
							}
						}
					}
				} else {
					if(ShopManager.getPlayer(player).hasCommand(ShopPlayerCommand.CREATE)){
						ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByLoc(block.getLocation());
						if(residence != null && !residence.isOwner(player)){
							event.setCancelled(true);
							ShopManager.sendMessage(player,"§cNejsi vlastnik teto residence.");
						}
						else if(PlayerManazer.getPlayerInfo(player).getCoins() < ShopManager.SHOP_FEE){
							event.setCancelled(true);
							ShopManager.sendMessage(player,"§cNemas dostatek coinu pro vytvoreni obchodu ("+ShopManager.SHOP_FEE+" coins).");
						} else {
							event.setCancelled(true);
							ShopManager.createShop(player,block.getLocation());
						}
					}
				}
				if(ShopManager.getPlayer(player).hasCommand(ShopPlayerCommand.CREATE)){
					ShopManager.getPlayer(player).cancelCommand();
					Title.showTitle(player," ",0,0,0);
					Title.showSubTitle(player," ",0,0,0);
				}
			}
			else if(block != null && event.getClickedBlock().getState() instanceof Sign){
				ShopMarketPlace place = ShopMarket.getPlace(event.getClickedBlock().getLocation());
				if(place != null){
					if(!player.isSneaking()) place.teleport(player);
					else place.showInfo(player);
				}
			}
		}
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player && event.getInventory().getType() == InventoryType.CHEST){
			Player player = (Player)event.getWhoClicked();
			Shop shop = ShopManager.getShop(event.getInventory().getLocation());
			if(shop != null){
				if(event.getClick().isShiftClick()){
					ItemStack item = event.getCurrentItem();
					if(item != null && !item.isSimilar(shop.getItem())){
						event.setCancelled(true);
					}
				}
				else if(event.getClickedInventory() != player.getInventory()){
					ItemStack item = event.getCursor();
					if(item != null && item.getType() != Material.AIR && !item.isSimilar(shop.getItem())){
						event.setCancelled(true);
					}
				}
				Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
					@Override
					public void run(){
						shop.update();
					}
				});
			}
		}
	}

	@EventHandler
	public void InventoryDragEvent(InventoryDragEvent event){
		if(event.getWhoClicked() instanceof Player && event.getInventory().getType() == InventoryType.CHEST){
			Shop shop = ShopManager.getShop(event.getInventory().getLocation());
			if(shop != null){
				ItemStack item = event.getOldCursor();
				if(item != null && !item.isSimilar(shop.getItem())){
					int size = event.getInventory().getSize();
					for(int i : event.getRawSlots()){
						if(i < size){
							event.setCancelled(true);
							break;
						}
					}
				} else {
					Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
						@Override
						public void run(){
							shop.update();
						}
					});
				}
			}
		}
	}

	@EventHandler
	public void InventoryMoveItemEvent(InventoryMoveItemEvent event){
		if(event.getDestination().getType() == InventoryType.CHEST){
			Shop shop = ShopManager.getShop(event.getDestination().getLocation());
			if(shop != null){
				ItemStack item = event.getItem();
				if(item != null && !item.isSimilar(shop.getItem())){
					event.setCancelled(true);
				} else {
					Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
						@Override
						public void run(){
							shop.update();
						}
					});
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void BlockBreakEvent(BlockBreakEvent event){
		if(event.isCancelled()) return;
		Block block = event.getBlock();
		if(ShopUtil.isChest(block)){
			Shop shop = ShopManager.getShop(block.getLocation());
			if(shop != null){
				Player player = event.getPlayer();
				if(shop.isOwner(player) || (player.getGameMode() == GameMode.CREATIVE && player.hasPermission("group.Manazer"))){
					if(player.getGameMode() != GameMode.CREATIVE || player.isSneaking()){
						ShopManager.removeShop(shop);
						ShopManager.sendMessage(player,"§fObchod odstranen.");
					} else {
						event.setCancelled(true);
					}
				} else {
					event.setCancelled(true);
					ShopManager.sendMessage(player,"§cNejsi vlastnik tohoto obchodu.");
				}
			}
		}
		else if(block.getState() instanceof Sign){
			ShopMarketPlace place = ShopMarket.getPlace(block.getLocation());
			if(place != null) event.setCancelled(true);
		}
	}

	@EventHandler
	public void BlockPlaceEvent(BlockPlaceEvent event){
		if(event.isCancelled()) return;
		Block block = event.getBlock();
		if(ShopUtil.isChest(block,false)){
			if(ShopManager.getShop(block.getLocation().clone().add(1,0,0)) != null) event.setCancelled(true);
			if(ShopManager.getShop(block.getLocation().clone().add(-1,0,0)) != null) event.setCancelled(true);
			if(ShopManager.getShop(block.getLocation().clone().add(0,0,1)) != null) event.setCancelled(true);
			if(ShopManager.getShop(block.getLocation().clone().add(0,0,-1)) != null) event.setCancelled(true);
		}
	}

	@EventHandler
	public void BlockExplodeEvent(BlockExplodeEvent event){
		ArrayList<Block> blocks = new ArrayList<Block>(event.blockList());
		for(Block block : blocks){
			if(ShopUtil.isChest(block)){
				if(ShopManager.getShop(block.getLocation()) != null) event.blockList().remove(block);
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender,Command command,String label,String[] args){
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("shop")){
			if(!player.getWorld().getName().equalsIgnoreCase(ShopManager.WORLD)){
				ShopManager.sendMessage(player,"§cMusis byt v normalnim svete.");
				return true;
			}
			if(args.length == 0 || (!args[0].equalsIgnoreCase("create") && !args[0].equalsIgnoreCase("crate") && !args[0].equalsIgnoreCase("crete") && !args[0].equalsIgnoreCase("stats") && !args[0].equalsIgnoreCase("market"))){
				player.sendMessage("§3----- §e§lChestShop §3----------------------------");
				player.sendMessage("§6/shop create §e<pocet> §e<cena> §f- Vytvorit obchod");
				player.sendMessage("§6/shop market §e<cena> §f- Pridat residenci na verejny trh");
				player.sendMessage("§6/shop market remove §f- Odebrat residenci z verejneho trhu");
				player.sendMessage("§6/shop stats §f- Statistiky vlastnich obchodu");
				player.sendMessage("§7Pri vytvareni obchodu musis drzet item, ktery chces pridat.");
				player.sendMessage("§7Obchod zrusis znicenim truhly.");
				return true;
			}
			else if(args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("crate") || args[0].equalsIgnoreCase("crete")){
				if(PlayerManazer.getPlayerInfo(player).getCoins() < ShopManager.SHOP_FEE){
					ShopManager.sendMessage(player,"§cNemas dostatek coinu pro vytvoreni obchodu ("+ShopManager.SHOP_FEE+" coins).");
					return true;
				}
				if(player.getInventory().getItemInMainHand().getType() == Material.AIR){
					ShopManager.sendMessage(player,"§cV ruce musis drzet item, ktery chces pridat.");
					return true;
				}
				if(ItemUtil.getItemName(player.getInventory().getItemInMainHand()) == null){
					ShopManager.sendMessage(player,"§cTento item nemuzes pridat do obchodu.");
					return true;
				}
				if(args.length < 3){
					player.sendMessage("");
					player.sendMessage("Vytvorit obchod:");
					player.sendMessage("§6/shop create §e<pocet> §e<cena>");
					player.sendMessage("§7Pri vytvareni obchodu musis drzet item, ktery chces pridat.");
					return true;
				}
				try {
					int amount = Integer.valueOf(args[1]);
					int price = Integer.valueOf(args[2]);
					ShopManager.getPlayer(player).toCreate(amount,price);
				} catch (NumberFormatException exception){
					player.sendMessage("");
					player.sendMessage("Vytvorit obchod:");
					player.sendMessage("§6/shop create §e<pocet> §e<cena>");
					player.sendMessage("§7Pri vytvareni obchodu musis drzet item, ktery chces pridat.");
					return true;
				}
			}
			else if(args[0].equalsIgnoreCase("market")){
				if(args.length > 1 && args[1].equalsIgnoreCase("remove")){
					ShopMarketResidence market = ShopMarket.getPlayerMarket(player);
					if(market == null){
						ShopManager.sendMessage(player,"§cNa verejnem trhu nemas zadnou residenci.");
						return true;
					}
					ShopMarket.removeMarket(market);
					ShopManager.sendMessage(player,"§fResidence odebrana z verejneho trhu.");
					player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,1f);
				} else {
					if(args.length < 2){
						if(ShopMarket.getPlayerMarket(player) != null){
							player.sendMessage("");
							player.sendMessage("Odebrat residenci z verejneho trhu:");
							player.sendMessage("§6/shop market remove");
						} else {
							player.sendMessage("");
							player.sendMessage("Pridat residenci na verejny trh:");
							player.sendMessage("§6/shop market §e<cena> §f");
							player.sendMessage("§7Cena za zverejneni obchodu se plati jednou za den.");
							player.sendMessage("§7Vyssi nabidka te posune na predni pozice trhu.");
						}
						return true;
					}
					if(ShopMarket.getPlayerMarket(player) != null){
						ShopManager.sendMessage(player,"§cJednu residenci jiz na verejnem trhu mas.");
						return true;
					}
					try {
						int price = Integer.valueOf(args[1]);
						ShopMarket.createMarket(player,price);
					} catch (NumberFormatException exception){
						player.sendMessage("");
						player.sendMessage("Pridat residenci na verejny trh:");
						player.sendMessage("§6/shop market §e<cena> §f");
						player.sendMessage("§7Cena za zverejneni obchodu se plati jednou za den.");
						player.sendMessage("§7Vyssi nabidka te posune na predni pozice trhu.");
						return true;
					}
				}
			}
			else if(args[0].equalsIgnoreCase("stats")){
				ShopManager.getPlayer(player).showStats();
			}
		}
		return true;
	}
}