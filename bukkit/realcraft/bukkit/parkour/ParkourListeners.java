package realcraft.bukkit.parkour;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Bed;
import org.bukkit.material.Door;
import org.bukkit.material.Gate;
import org.bukkit.material.TrapDoor;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.NullExtent;
import com.sk89q.worldedit.util.eventbus.EventHandler.Priority;
import com.sk89q.worldedit.util.eventbus.Subscribe;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.parkour.exceptions.ParkourAlreadyRatedException;
import realcraft.bukkit.parkour.exceptions.ParkourInvalidNameException;
import realcraft.bukkit.parkour.exceptions.ParkourNameExistsException;
import realcraft.bukkit.parkour.exceptions.ParkourNotReadyException;
import realcraft.bukkit.parkour.exceptions.ParkourOwnRatingException;
import realcraft.bukkit.parkour.exceptions.ParkourPlayerNotFoundException;
import realcraft.bukkit.parkour.menu.ParkourMenu;
import realcraft.bukkit.parkour.menu.ParkourMenuArenas;
import realcraft.bukkit.parkour.menu.ParkourMenuBiome;
import realcraft.bukkit.parkour.menu.ParkourMenuClock;
import realcraft.bukkit.parkour.menu.ParkourMenuFloor;
import realcraft.bukkit.parkour.menu.ParkourMenuRating;
import realcraft.bukkit.parkour.menu.ParkourMenuRename;
import realcraft.bukkit.parkour.menu.ParkourMenuSettings;
import realcraft.bukkit.parkour.menu.ParkourMenuType;
import realcraft.bukkit.parkour.utils.BlockUtil;
import realcraft.bukkit.utils.FireworkUtil;
import realcraft.bukkit.utils.Title;

public class ParkourListeners implements Listener, TabCompleter {
	WorldEditPlugin worldEdit;

	public ParkourListeners(){
		worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		WorldEdit.getInstance().getEventBus().register(this);
		RealCraft.getInstance().getCommand("join").setTabCompleter(this);
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RealCraft.getInstance(),ListenerPriority.HIGH,PacketType.Play.Client.WINDOW_CLICK,PacketType.Play.Server.ENTITY_METADATA){
			@Override
			public void onPacketSending(PacketEvent event){
				if(event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA){
					if(event.getPlayer().getEntityId() == event.getPacket().getIntegers().read(0)){
						event.setCancelled(true);
					}
				}
			}

			@Override
			public void onPacketReceiving(PacketEvent event){
				ParkourPlayer player = Parkour.getPlayer(event.getPlayer());
				if(event.getPacketType() == PacketType.Play.Client.WINDOW_CLICK){
					if(event.getPacket().getIntegers().read(1).shortValue() == 2){
						ItemStack item = event.getPacket().getItemModifier().read(0);
						if(item.getType() == Material.EMPTY_MAP){
							final String tmpName = (item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : "");
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RealCraft.getInstance(),new Runnable(){
								@Override
								public void run(){
									String name = ChatColor.stripColor(tmpName);
									player.getPlayer().closeInventory();
									if(name.length() > 0){
										try {
											player.getArena().setName(name);
											player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
											Title.showActionTitle(player.getPlayer(),"?a\u2714 ?fParkour prejmenovan ?a\u2714",3*20);
										} catch (ParkourInvalidNameException e){
											player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
											Parkour.sendMessage(player,"?cZadany nazev obsahuje nepovolene znaky.");
										} catch (ParkourNameExistsException e){
											event.getPlayer().getPlayer().playSound(event.getPlayer().getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
											Parkour.sendMessage(player,"?cZadany nazev jiz existuje.");
										}
									}
								}
							});
						}
						else if(item.getType() == Material.SKULL_ITEM){
							final String name = (item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : "");
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RealCraft.getInstance(),new Runnable(){
								@Override
								public void run(){
									player.getPlayer().closeInventory();
									if(name.length() > 0){
										try {
											player.getArena().addCollaborator(player,name);
											player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
											ParkourMenuSettings.openMenu(player);
										} catch (ParkourPlayerNotFoundException e){
											player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
											Parkour.sendMessage(player,"?cHrac nenalezen.");
										}
									}
								}
							});
						}
					}
				}
			}
		});
	}

	@Subscribe(priority = Priority.NORMAL)
    public void EditSessionEvent(EditSessionEvent event){
		if(event.getActor() != null && event.getActor().isPlayer()){
			Player playerTmp = Bukkit.getServer().getPlayer(event.getActor().getName());
			if(playerTmp != null){
				ParkourPlayer player = Parkour.getPlayer(playerTmp);
				if(player.getArena() != null){
					if(!player.getArena().isReady() && player.getMode() == ParkourPlayerMode.BUILD){
						event.setExtent(new ParkourWEExtent(player.getArena(),event.getExtent()));
						player.getArena().checkPlates();
					}
					else event.setExtent(new NullExtent());
				}
				else if(player.getPlayer().getGameMode() != GameMode.CREATIVE) event.setExtent(new NullExtent());
			}
		}
	}

	/*@Subscribe(priority = Priority.VERY_EARLY)
    public void CommandEvent(CommandEvent event){
		if(event.getActor() != null && event.getActor().isPlayer()){
			Player playerTmp = Bukkit.getServer().getPlayer(event.getActor().getName());
			if(playerTmp != null){
				ParkourPlayer player = Parkour.getPlayer(playerTmp);
				if(player.getArena() != null){
					if(player.getArena().isReady() || player.getMode() != ParkourPlayerMode.BUILD){
						event.setCancelled(true);
					}
				}
			}
		}
	}*/

	/*
	 * worldedit.clipboard.*
	 * worldedit.generation.*
	 * worldedit.history.*
	 * worldedit.region.*
	 * worldedit.selection.*
	 * worldedit.superpickaxe.*
	 * worldedit.tool.*
	 * worldedit.brush.*
	 * worldedit.wand
	 * worldedit.fill
	 */

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		ParkourPlayer player = Parkour.getPlayer(event.getPlayer());
		player.setInventory();
		player.getPlayer().getInventory().setHeldItemSlot(0);
		player.getPlayer().setWalkSpeed(0.2f);
	}

	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent event){
		Parkour.removePlayer(event.getPlayer());
	}

	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		ParkourPlayer player = Parkour.getPlayer(event.getPlayer());
		player.setInventory();
		player.getPlayer().getInventory().setHeldItemSlot(0);
	}

	@EventHandler
	public void PlayerDeathEvent(PlayerDeathEvent event){
		ParkourPlayer player = Parkour.getPlayer(event.getEntity());
		event.getDrops().clear();
		event.setDroppedExp(0);
		if(player.getArena() != null) player.getArena().leavePlayer(player);
	}

	@EventHandler
	public void EntityDamageEvent(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			if(event.getCause() == EntityDamageEvent.DamageCause.FALL
				|| event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL
				|| event.getCause() == EntityDamageEvent.DamageCause.VOID
				|| event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
				|| event.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR){
				event.setCancelled(true);
			}
			else if(event.getCause() == DamageCause.LAVA
					|| event.getCause() == DamageCause.FIRE
					|| event.getCause() == DamageCause.FIRE_TICK){
				((Player)event.getEntity()).setFireTicks(0);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void PlayerDropItemEvent(PlayerDropItemEvent event){
		event.setCancelled(true);
	}

	@EventHandler
	public void PlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event){
		event.setCancelled(true);
	}

	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent event){
		ParkourPlayer player = Parkour.getPlayer(event.getPlayer());
		if(player.getArena() != null){
			if(player.getMode() == ParkourPlayerMode.BUILD){
				if(player.getPlayer().getLocation().getBlockY() < -5){
					player.getPlayer().setAllowFlight(true);
					player.getPlayer().setFlying(true);
					player.getPlayer().setVelocity(player.getPlayer().getVelocity().setY(2.5));
				}
			}
			else if(player.getMode() == ParkourPlayerMode.NORMAL || player.getMode() == ParkourPlayerMode.TEST){
				if(player.getPlayer().getLocation().getBlockY() < -5){
					player.teleportToCheckPoint();
				}
			}
		} else {
			if(player.getPlayer().getLocation().getBlockY() < -5){
				Parkour.teleportToLobby(player);
			}
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event){
		if(event.getPlayer().getWorld().getName().equalsIgnoreCase("world")){
			ParkourPlayer player = Parkour.getPlayer(event.getPlayer());
			if(player.getArena() != null) player.getArena().leavePlayer(player);
		}
	}

	@EventHandler
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		ParkourPlayer player = Parkour.getPlayer(event.getPlayer());
		String commandName = event.getMessage().substring(1).split(" ")[0].trim();
		if(commandName.equalsIgnoreCase("join")){
			String [] args = event.getMessage().split(" ");
			if(args.length < 2 || args[1].isEmpty()){
				Parkour.sendMessage(player,"Pripojit se do parkouru");
				Parkour.sendMessage(player,"/join <parkour>");
				event.setCancelled(true);
				return;
			}
			ParkourArena arena = Parkour.getArena(args[1]);
			if(arena == null){
				Parkour.sendMessage(player,"?cParkour nenalezen.");
				event.setCancelled(true);
				return;
			}
			try {
				arena.joinPlayer(player);
			} catch (ParkourNotReadyException e){
				Parkour.sendMessage(player,"?cTento parkour neni dokonceny.");
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
			}
			event.setCancelled(true);
		}
		else if(commandName.equalsIgnoreCase("leave")){
			if(player.getArena() == null){
				Parkour.sendMessage(player,"?cNejsi v zadnem parkouru.");
				event.setCancelled(true);
				return;
			}
			player.getArena().leavePlayer(player);
			event.setCancelled(true);
		}
		else if(commandName.equalsIgnoreCase("tpa")){
			String [] args = event.getMessage().split(" ");
			if(args.length < 2 || args[1].isEmpty()){
				Parkour.sendMessage(player,"Pripojit se k hraci");
				Parkour.sendMessage(player,"/tpa <player>");
				event.setCancelled(true);
				return;
			}
			Player victim = Bukkit.getServer().getPlayer(args[1]);
			if(victim == null || victim == player.getPlayer()){
				Parkour.sendMessage(player,"?cHrac nenalezen.");
				event.setCancelled(true);
				return;
			}
			ParkourPlayer victimPlayer = Parkour.getPlayer(victim);
			if(victimPlayer.getArena() == null){
				Parkour.teleportToLobby(player);
			} else {
				try {
					victimPlayer.getArena().joinPlayer(player);
				} catch (ParkourNotReadyException e){
					Parkour.sendMessage(player,"?cTento parkour neni dokonceny.");
					player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
				}
			}
			event.setCancelled(true);
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
		if(command.getName().equalsIgnoreCase("join")){
			if(args.length == 1){
				ArrayList<ParkourArena> arenas = Parkour.findArenas(args[0]);
				if(!arenas.isEmpty()){
					List<String> names = new ArrayList<String>();
					for(ParkourArena arena : arenas) names.add(arena.getName());
					return names;
				}
			}
		}
		return null;
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		if(event.getAction() == Action.PHYSICAL){
			ParkourPlayer player = Parkour.getPlayer(event.getPlayer());
			if(player.getArena() != null){
				Block block = event.getClickedBlock();
				if(block != null && (block.getType() == Material.STONE_PLATE || block.getType() == Material.IRON_PLATE || block.getType() == Material.GOLD_PLATE)){
					if(block.getType() == Material.IRON_PLATE){
						Location location = block.getLocation();
						location.setYaw(Math.round(player.getPlayer().getLocation().getYaw()));
						location.setPitch(Math.round(player.getPlayer().getLocation().getPitch()));
						player.setCheckPoint(location,true);
					}
					else if(block.getType() == Material.GOLD_PLATE) player.getArena().finishPlayer(player);
					event.setCancelled(true);
				}
			} else {
				Block block = event.getClickedBlock();
				if(block != null && block.getType() == Material.IRON_PLATE){
					Location location = block.getLocation();
					location.setYaw(Math.round(player.getPlayer().getLocation().getYaw()));
					location.setPitch(Math.round(player.getPlayer().getLocation().getPitch()));
					player.setCheckPoint(location,true);
					event.setCancelled(true);
				}
				else if(block != null && block.getType() == Material.GOLD_PLATE && player.getPlayer().getGameMode() != GameMode.CREATIVE && !player.getPlayer().getAllowFlight()){
					Parkour.teleportToLobby(player);
					Title.showTitle(player.getPlayer(),"?aParkour dokoncen",0.2,7,0.2);
					FireworkUtil.spawnFirework(player.getPlayer().getLocation(),FireworkEffect.Type.BALL,Color.WHITE,true,true);
					player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,1);
					player.reset();
				}
			}
		}
		else if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			ParkourPlayer player = Parkour.getPlayer(event.getPlayer());
			Block block = event.getClickedBlock();
			BlockState blockState = null;
			if(block != null) blockState = block.getState();
			if(player.getPlayer().getInventory().getItemInMainHand().getType() == ParkourMenuType.MAIN.getMaterial()){
				ParkourMenu.openMenu(player);
				event.setCancelled(true);
			}
			else if(player.getPlayer().getInventory().getItemInMainHand().getType() == ParkourMenuType.SETTINGS.getMaterial()){
				ParkourMenuSettings.openMenu(player);
				event.setCancelled(true);
			}
			if(blockState != null && player.getPlayer().getGameMode() != GameMode.CREATIVE){
				if(blockState.getData() instanceof Door
					|| blockState.getData() instanceof TrapDoor
					|| blockState.getData() instanceof Gate
					|| blockState.getData() instanceof Bed
					|| blockState.getType() == Material.CHEST
					|| blockState.getType() == Material.ENDER_CHEST
					|| blockState.getType() == Material.TRAPPED_CHEST
					|| blockState.getType() == Material.LEVER
					|| blockState.getType() == Material.REDSTONE_COMPARATOR
					|| blockState.getType() == Material.DIODE
					|| blockState.getType() == Material.WORKBENCH
					|| blockState.getType() == Material.ENCHANTMENT_TABLE
					|| blockState instanceof Furnace
					|| blockState.getType() == Material.ANVIL
					|| (event.getAction() == Action.LEFT_CLICK_BLOCK && block.getRelative(BlockFace.UP).getType() == Material.FIRE)){
					event.setCancelled(true);
				}
			}
			if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				if(player.getPlayer().getInventory().getItemInMainHand().getType() == ParkourMenuType.RESPAWN.getMaterial()){
					if(player.getMode() == ParkourPlayerMode.NONE || player.getMode() == ParkourPlayerMode.NORMAL || player.getMode() == ParkourPlayerMode.TEST){
						player.teleportToCheckPoint();
					}
					event.setCancelled(true);
				}
				else if(player.getPlayer().getInventory().getItemInMainHand().getType() == ParkourMenuType.RESET.getMaterial()){
					if(player.getMode() == ParkourPlayerMode.NORMAL || player.getMode() == ParkourPlayerMode.TEST){
						player.setCheckPoint(player.getArena().getStartLocation());
						player.teleportToCheckPoint();
					}
					else if(player.getMode() == ParkourPlayerMode.NONE){
						if(player.getLastArena() != null){
							try {
								player.getLastArena().joinPlayer(player);
							} catch (ParkourNotReadyException e){
								Parkour.sendMessage(player,"?cTento parkour neni dokonceny.");
								player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
							}
						}
					}
					event.setCancelled(true);
				}
				else if(player.getPlayer().getInventory().getItemInMainHand().getType() == ParkourMenuType.RATING.getMaterial()){
					if(player.getMode() == ParkourPlayerMode.NORMAL){
						player.setRatingArena(player.getArena());
						try {
							ParkourMenuRating.openMenu(player);
						} catch (ParkourAlreadyRatedException e){
							Parkour.sendMessage(player,"?cParkour jsi jiz hodnotil.");
							player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
						} catch (ParkourOwnRatingException e){
							Parkour.sendMessage(player,"?cVlastni parkour nelze hodnotit.");
							player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
						}
					}
					event.setCancelled(true);
				}
				else if(player.getPlayer().getInventory().getItemInMainHand().getType() == ParkourMenuType.EXIT.getMaterial()){
					if(player.getMode() == ParkourPlayerMode.NORMAL || player.getMode() == ParkourPlayerMode.BUILD){
						player.getArena().leavePlayer(player);
					}
					else if(player.getMode() == ParkourPlayerMode.TEST){
						player.getPlayer().teleport(player.getPlayer().getLocation().add(0,0.2,0));
						player.getPlayer().setVelocity(player.getPlayer().getVelocity().setY(0.5));
						player.setMode(ParkourPlayerMode.BUILD);
					}
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void BlockSpreadEvent(BlockSpreadEvent event){
		event.setCancelled(true);
	}

	@EventHandler
	public void InventoryCreativeEvent(InventoryCreativeEvent event){
		if(event.getCurrentItem() != null){
			ParkourPlayer player = Parkour.getPlayer((Player)event.getWhoClicked());
			if(player.getArena() != null){
				Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
					@Override
					public void run(){
						int slot = event.getRawSlot();
						if(slot >= 36 && slot <= 44) slot -= 36;
						if(player.getPlayer().getInventory().getItem(slot) != null && !BlockUtil.isBlockValid(player.getPlayer().getInventory().getItem(slot).getType())){
							player.getPlayer().getInventory().setItem(slot,new ItemStack(Material.AIR));
						}
					}
				});
			}
		}
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getCurrentItem() != null && event.getCurrentItem().getType() == ParkourMenuType.SETTINGS.getMaterial()){
			ParkourPlayer player = Parkour.getPlayer((Player)event.getWhoClicked());
			ParkourMenuSettings.openMenu(player);
			event.setCancelled(true);
		}
		else if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.MAIN.getInventoryName())){
			ParkourMenu.InventoryClickEvent(event);
		}
		else if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.NEWEST.getInventoryName())
				|| event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.BEST.getInventoryName())
				|| event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.LIKED.getInventoryName())
				|| event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.OWN.getInventoryName())){
			ParkourMenuArenas.InventoryClickEvent(event);
		}
		else if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.SETTINGS.getInventoryName())){
			ParkourMenuSettings.InventoryClickEvent(event);
		}
		else if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.CLOCK.getInventoryName())){
			ParkourMenuClock.InventoryClickEvent(event);
		}
		else if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.FLOOR.getInventoryName())){
			ParkourMenuFloor.InventoryClickEvent(event);
		}
		else if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.BIOME.getInventoryName())){
			ParkourMenuBiome.InventoryClickEvent(event);
		}
		else if(event.getInventory().getTitle().equalsIgnoreCase(ParkourMenuType.RATING.getInventoryName())){
			ParkourMenuRating.InventoryClickEvent(event);
		}
		else if(event.getInventory().getType() == InventoryType.ANVIL){
			ParkourMenuRename.InventoryClickEvent(event);
		}
		else if(event.getCurrentItem() != null && event.getCurrentItem().getType() == ParkourMenuType.MAIN.getMaterial()){
			ParkourPlayer player = Parkour.getPlayer((Player)event.getWhoClicked());
			ParkourMenu.openMenu(player);
			event.setCancelled(true);
		}
		else {
			ParkourPlayer player = Parkour.getPlayer((Player)event.getWhoClicked());
			if(player.getArena() != null){
				if(event.getCurrentItem() != null && !BlockUtil.isBlockValid(event.getCurrentItem().getType())){
					event.setCancelled(true);
					event.setCurrentItem(new ItemStack(Material.AIR));
				}
				if(player.getMode() != ParkourPlayerMode.BUILD){
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void BlockBreakEvent(BlockBreakEvent event){
		ParkourPlayer player = Parkour.getPlayer(event.getPlayer());
		Block block = event.getBlock();
		if(player.getMode() != ParkourPlayerMode.BUILD || (player.getArena() != null && !player.getArena().isLocationInArena(block.getLocation()))){
			event.setCancelled(true);
		} else {
			if(block.getType() == ParkourMenuType.START.getMaterial()){
				if(player.getArena().getStartLocation() != null){
					player.getArena().setStartLocation(null);
					player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_PICKUP,1,1);
					Title.showActionTitle(event.getPlayer(),"?6\u2714 ?fStartovni pozice odebrana ?6\u2714",3*20);
				}
			}
			else if(block.getType() == ParkourMenuType.FINISH.getMaterial()){
				if(player.getArena().getFinishLocation() != null){
					player.getArena().setFinishLocation(null);
					player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_PICKUP,1,1);
					Title.showActionTitle(event.getPlayer(),"?6\u2714 ?fCilova pozice odebrana ?6\u2714",3*20);
				}
			}
			else if(block.getType() == ParkourMenuType.CHECKPOINT.getMaterial()){
				if(player.getArena().removeCheckPoint(block.getLocation())){
					player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_PICKUP,1,1);
					Title.showActionTitle(event.getPlayer(),"?6\u2714 ?fCheckPoint odebran ("+player.getArena().getCheckPoints().size()+") ?6\u2714",3*20);
				}
			} else {
				if(block.getRelative(BlockFace.UP).getType() == ParkourMenuType.START.getMaterial()){
					event.setCancelled(true);
				}
				else if(block.getRelative(BlockFace.UP).getType() == ParkourMenuType.FINISH.getMaterial()){
					event.setCancelled(true);
				}
				else if(block.getRelative(BlockFace.UP).getType() == ParkourMenuType.CHECKPOINT.getMaterial()){
					event.setCancelled(true);
				}
			}
			if(!event.isCancelled()){
				player.getArena().setTested(false);
				player.getArena().checkNoBlocks();
				player.getArena().checkPlates();
			}
		}
	}

	@EventHandler
	public void BlockPlaceEvent(BlockPlaceEvent event){
		ParkourPlayer player = Parkour.getPlayer(event.getPlayer());
		Block block = event.getBlock();
		if(player.getMode() != ParkourPlayerMode.BUILD || !BlockUtil.isBlockValid(block.getType()) || (player.getArena() != null && !player.getArena().isLocationInArena(block.getLocation()))){
			event.setCancelled(true);
		} else {
			if(block.getType() == ParkourMenuType.START.getMaterial()){
				if(player.getArena().getStartLocation() == null){
					Location location = block.getLocation();
					location.setYaw(Math.round(player.getPlayer().getLocation().getYaw()));
					player.getArena().setStartLocation(location);
					player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
					Title.showActionTitle(event.getPlayer(),"?a\u2714 ?fStartovni pozice nastavena ?a\u2714",3*20);
				} else {
					event.setCancelled(true);
					player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
					Title.showActionTitle(event.getPlayer(),"?c\u2716 ?fStartovni pozice jiz existuje ?c\u2716",3*20);
				}
			}
			else if(block.getType() == ParkourMenuType.FINISH.getMaterial()){
				if(player.getArena().getFinishLocation() == null){
					player.getArena().setFinishLocation(block.getLocation());
					player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
					Title.showActionTitle(event.getPlayer(),"?a\u2714 ?fCilova pozice nastavena ?a\u2714",3*20);
				} else {
					event.setCancelled(true);
					player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
					Title.showActionTitle(event.getPlayer(),"?c\u2716 ?fCilova pozice jiz existuje ?c\u2716",3*20);
				}
			}
			else if(block.getType() == ParkourMenuType.CHECKPOINT.getMaterial()){
				player.getArena().addCheckPoint(block.getLocation());
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
				Title.showActionTitle(event.getPlayer(),"?a\u2714 ?fCheckPoint pridan ("+player.getArena().getCheckPoints().size()+") ?a\u2714",3*20);
			}
			if(!event.isCancelled()){
				player.getArena().setTested(false);
			}
		}
	}

	@EventHandler
	public void HangingBreakByEntityEvent(HangingBreakByEntityEvent  event){
		if(event.getRemover() instanceof Player){
			ParkourPlayer player = Parkour.getPlayer((Player)event.getRemover());
			if(event.getEntity() != null && event.getEntity().getType() == EntityType.ITEM_FRAME){
				if(player.getPlayer().getGameMode() != GameMode.CREATIVE || (player.getArena() != null && !player.getArena().isLocationInArena(event.getEntity().getLocation()))) event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event){
		ParkourPlayer player = Parkour.getPlayer(event.getPlayer());
		if(event.getRightClicked() != null && event.getRightClicked().getType() == EntityType.ITEM_FRAME){
			if(player.getPlayer().getGameMode() != GameMode.CREATIVE || (player.getArena() != null && !player.getArena().isLocationInArena(event.getRightClicked().getLocation()))) event.setCancelled(true);
		}
	}

	@EventHandler
	public void PlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent event){
		ParkourPlayer player = Parkour.getPlayer(event.getPlayer());
		if(player.getPlayer().getGameMode() != GameMode.CREATIVE || (player.getArena() != null && !player.getArena().isLocationInArena(event.getRightClicked().getLocation()))) event.setCancelled(true);
	}

	@EventHandler
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Player){
			ParkourPlayer player = Parkour.getPlayer((Player)event.getDamager());
			if(event.getEntity() instanceof ItemFrame){
				if(player.getPlayer().getGameMode() != GameMode.CREATIVE){
					event.setCancelled(true);
				}
			}
			else event.setCancelled(true);
		}
	}
}