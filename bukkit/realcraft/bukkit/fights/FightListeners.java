package realcraft.bukkit.fights;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.material.Door;
import org.bukkit.material.Gate;
import org.bukkit.material.TrapDoor;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.anticheat.AntiCheat;
import realcraft.bukkit.fights.FightPlayer.FightPlayerState;
import realcraft.bukkit.fights.duels.FightDuels;
import realcraft.bukkit.fights.events.FightPlayerJoinLobbyEvent;
import realcraft.bukkit.fights.events.FightPlayerLeaveLobbyEvent;
import realcraft.bukkit.fights.events.FightPlayerRankChange;
import realcraft.bukkit.fights.events.FightPlayerRankCreatedEvent;
import realcraft.bukkit.utils.Title;

import java.util.UUID;

public class FightListeners implements Listener {

	public FightListeners(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RealCraft.getInstance(),PacketType.Play.Client.USE_ENTITY,PacketType.Play.Server.PLAYER_INFO){
			@Override
			public void onPacketReceiving(PacketEvent event){
				if(event.getPacketType() == PacketType.Play.Client.USE_ENTITY){
					FightPlayer fPlayer = Fights.getFightPlayer(event.getPlayer());
					if(fPlayer.getState() == FightPlayerState.SPECTATOR){
						event.setCancelled(true);
					}
				}
			}
			@Override
			public void onPacketSending(PacketEvent event){
				if(event.getPacketType() == PacketType.Play.Server.PLAYER_INFO){
					try {
						UUID uuid = event.getPacket().getPlayerInfoDataLists().read(0).get(0).getProfile().getUUID();
						Player player = Bukkit.getPlayer(uuid);
						if(player != null && player.isOnline() && event.getPlayer().getUniqueId() != uuid){
							FightPlayer fPlayer = Fights.getFightPlayer(player);
							if(fPlayer != null && fPlayer.getState() == FightPlayerState.SPECTATOR && !fPlayer.isLeaving()){
								/*PacketPlayOutPlayerInfo packet = (PacketPlayOutPlayerInfo) event.getPacket().getHandle();
								PacketPlayOutPlayerInfo.EnumPlayerInfoAction action = (PacketPlayOutPlayerInfo.EnumPlayerInfoAction) ReflectionUtils.getField(packet.getClass(),true,"a").get(packet);
								if(action == PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e){
									event.setCancelled(true);
								}*/
							}
						}
					} catch (Exception e){
						e.printStackTrace();
					}
				}
			}
		});
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		FightPlayer fPlayer = Fights.getFightPlayer(event.getPlayer());
		fPlayer.setLeaving(false);
		fPlayer.reload();
		Fights.joinLobby(fPlayer);
	}

	@EventHandler
	public void PlayerSpawnLocationEvent(PlayerSpawnLocationEvent event){
		event.setSpawnLocation(Fights.getLobbyLocation());
	}

	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		FightPlayer fPlayer = Fights.getFightPlayer(event.getPlayer());
		if(fPlayer.getState() != FightPlayerState.FIGHT) event.setRespawnLocation(Fights.getLobbyLocation());
	}

	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent event){
		FightPlayer fPlayer = Fights.getFightPlayer(event.getPlayer());
		fPlayer.setLeaving(true);
		Fights.joinLobby(fPlayer);
		Player player = fPlayer.getPlayer();
		Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				for(FightPlayer fPlayer2 : Fights.getOnlineFightPlayers()){
					/*PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e,((CraftPlayer)player).getHandle());
					((CraftPlayer)fPlayer2.getPlayer()).getHandle().b.a(packet);*/
				}
			}
		});
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerDeathEvent(PlayerDeathEvent event){
		Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				event.getEntity().spigot().respawn();
			}
		},2);
		event.setDeathMessage(null);
		event.setDroppedExp(0);
		event.getDrops().clear();
	}

	@EventHandler(priority=EventPriority.LOW)
	public void EntityDamageEvent(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			FightPlayer fPlayer = Fights.getFightPlayer((Player)event.getEntity());
			if(fPlayer.getState() != FightPlayerState.FIGHT){
				event.setCancelled(true);
				if(event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK) event.getEntity().setFireTicks(0);
				if(event.getCause() == DamageCause.VOID && fPlayer.getState() == FightPlayerState.NONE) event.getEntity().teleport(Fights.getLobbyLocation());
			}
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Player){
			Player player = (Player)event.getDamager();
			FightPlayer fPlayer = Fights.getFightPlayer((Player)event.getDamager());
			if(fPlayer.getState() != FightPlayerState.FIGHT && player.getGameMode() != GameMode.CREATIVE){
				event.setCancelled(true);
			}
			if(event.getEntity() instanceof ItemFrame && player.getGameMode() != GameMode.CREATIVE){
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void ProjectileHitEvent(ProjectileHitEvent event){
		if(event.getEntity() instanceof Arrow){
			event.getEntity().remove();
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void BlockBreakEvent(BlockBreakEvent event){
		if(event.getPlayer().getGameMode() != GameMode.CREATIVE){
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void BlockPlaceEvent(BlockPlaceEvent event){
		if(event.getPlayer().getGameMode() != GameMode.CREATIVE){
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void BlockFadeEvent(BlockFadeEvent event){
		Block block = event.getBlock();
		if(block != null && block.getType() == Material.FARMLAND){
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void BlockSpreadEvent(BlockSpreadEvent event){
		if(event.getSource().getType() == Material.VINE || event.getSource().getType() == Material.BROWN_MUSHROOM || event.getSource().getType() == Material.RED_MUSHROOM){
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void BlockGrowEvent(BlockGrowEvent event){
		if(event.getNewState().getType() == Material.SUGAR_CANE){
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void LeavesDecayEvent(LeavesDecayEvent event){
		event.setCancelled(true);
	}

	@EventHandler(priority=EventPriority.LOW)
	public void EntityExplodeEvent(EntityExplodeEvent event){
		if(event.getEntityType() == EntityType.TNT) event.blockList().clear();
	}

	@EventHandler(priority=EventPriority.LOW)
	public void EntityInteractEvent(EntityInteractEvent event){
		if(event.getEntity() instanceof Animals){
			Block block = event.getBlock();
			if(block != null && block.getType() == Material.FARMLAND){
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void HangingBreakEvent(HangingBreakEvent event){
		if(event.getCause() == RemoveCause.EXPLOSION){
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void HangingBreakByEntityEvent(HangingBreakByEntityEvent event){
		if(event.getRemover() instanceof Player){
			if(((Player)event.getRemover()).getGameMode() != GameMode.CREATIVE) event.setCancelled(true);
		}
		else event.setCancelled(true);
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event){
		FightPlayer fPlayer = Fights.getFightPlayer(event.getPlayer());
		if(event.getPlayer().getGameMode() != GameMode.CREATIVE && fPlayer.getState() != FightPlayerState.FIGHT) event.setCancelled(true);
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent event){
		if(event.getPlayer().getGameMode() != GameMode.CREATIVE) event.setCancelled(true);
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void PlayerInteractEventFix(PlayerInteractEvent event){
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR){
			event.setCancelled(false);
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerMoveEvent(PlayerMoveEvent event){
		if(Fights.getFightPlayer(event.getPlayer()).getState() == FightPlayerState.SPECTATOR){
			AntiCheat.exempt(event.getPlayer(),1000);
			if(event.getPlayer().getLocation().getBlockY() < 0){
				event.getPlayer().setAllowFlight(true);
				event.getPlayer().setFlying(true);
				event.getPlayer().setVelocity(event.getPlayer().getVelocity().setY(2.0));
			}
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerInteractEvent(PlayerInteractEvent event){
		if(Fights.getFightPlayer(event.getPlayer()).getState() == FightPlayerState.SPECTATOR){
			event.setCancelled(true);
			return;
		}
		Block block = event.getClickedBlock();
		if(block != null){
			BlockState blockState = block.getState();
			if(blockState != null){
				if(event.getAction() == Action.PHYSICAL && blockState != null && blockState.getType() == Material.FARMLAND){
					event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
					event.setCancelled(true);
				}
				else if((blockState.getData() instanceof Door || blockState.getData() instanceof TrapDoor || blockState.getData() instanceof Gate)){
					event.setCancelled(true);
				}
				else if((blockState.getType() == Material.CHEST || blockState.getType() == Material.ENDER_CHEST || blockState.getType() == Material.TRAPPED_CHEST)){
					event.setCancelled(true);
				}
				else if((blockState.getType() == Material.LEVER || blockState.getType() == Material.COMPARATOR || blockState.getType() == Material.REPEATER)){
					event.setCancelled(true);
				}
				else if(blockState.getType() == Material.CRAFTING_TABLE){
					event.setCancelled(true);
				}
				else if(blockState instanceof Furnace){
					event.setCancelled(true);
				}
				else if(blockState.getType() == Material.ANVIL){
					event.setCancelled(true);
				}
				else if(blockState.getType() == Material.FLOWER_POT){
					event.setCancelled(true);
				}
				else if(event.getAction() == Action.LEFT_CLICK_BLOCK){
					if(block.getRelative(BlockFace.UP).getType() == Material.FIRE){
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerBedEnterEvent(PlayerBedEnterEvent event){
		event.setCancelled(true);
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event){
		if(Fights.getFightPlayer(event.getPlayer()).getState() != FightPlayerState.FIGHT) event.setCancelled(true);
	}

	@EventHandler(priority=EventPriority.LOW)
	public void CraftItemEvent(CraftItemEvent event){
		event.setCancelled(true);
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerDropItemEvent(PlayerDropItemEvent event){
		event.setCancelled(true);
	}

	@EventHandler(priority=EventPriority.LOW)
    public void EntityPickupItemEvent(EntityPickupItemEvent event){
		if(event.getEntity() instanceof Player && Fights.getFightPlayer((Player)event.getEntity()).getState() != FightPlayerState.FIGHT) event.setCancelled(true);
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerEggThrowEvent(PlayerEggThrowEvent event){
		event.setHatching(false);
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PortalCreateEvent(PortalCreateEvent event){
		event.setCancelled(true);
	}

	@EventHandler(priority=EventPriority.LOW)
	public void EntityCreatePortalEvent(EntityCreatePortalEvent event){
		event.setCancelled(true);
	}

	@EventHandler(priority=EventPriority.LOW)
	public void CreatureSpawnEvent(CreatureSpawnEvent event){
		if(event.getEntityType() == EntityType.ENDER_DRAGON){
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		if(Fights.getCommands() != null && Fights.getCommands().length > 0){
			for(String command : Fights.getCommands()){
				if(event.getMessage().substring(1).toLowerCase().indexOf(command) == 0){
					return;
				}
			}
		}
		if(event.getMessage().equalsIgnoreCase("/spawn") || event.getMessage().equalsIgnoreCase("/leave")){
			Fights.joinLobby(Fights.getFightPlayer(event.getPlayer()));
			event.setCancelled(true);
			return;
		}
		if(!event.getPlayer().hasPermission("group.Admin")){
			event.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void FightPlayerJoinLobbyEvent(FightPlayerJoinLobbyEvent event){
		FightPlayer fPlayer = event.getPlayer();
		fPlayer.setState(FightPlayerState.NONE);
		fPlayer.setDuel(null);
		fPlayer.setArena(null);
		if(!fPlayer.isLeaving()){
			fPlayer.reset();
			fPlayer.getPlayer().teleport(Fights.getLobbyLocation());
			fPlayer.getLobbyScoreboard().addPlayer(fPlayer);
			for(FightPlayer fPlayer2 : Fights.getOnlineFightPlayers()){
				fPlayer2.getPlayer().showPlayer(fPlayer.getPlayer());
				fPlayer.getPlayer().showPlayer(fPlayer2.getPlayer());
			}
			fPlayer.updateNick();
			Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
				public void run(){
					if(fPlayer.getPlayer() != null) fPlayer.getPlayer().teleport(Fights.getLobbyLocation());
				}
			},5);
		}
	}

	@EventHandler
	public void FightPlayerLeaveLobbyEvent(FightPlayerLeaveLobbyEvent event){
	}

	@EventHandler
	public void FightPlayerRankChange(FightPlayerRankChange event){
		FightPlayer fPlayer = event.getPlayer();
		Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
			public void run(){
				if(fPlayer.getPlayer() != null){
					fPlayer.getPlayer().playSound(fPlayer.getPlayer().getLocation(),Sound.ENTITY_ENDER_DRAGON_DEATH,1f,1f);
					if(fPlayer.getRank().getId() > event.getOldRank().getId()){
						Title.showTitle(fPlayer.getPlayer(),"§aRank zvysen",0.5,7,0.5);
						FightDuels.sendMessage("§a"+FightRank.CHAR_UP+" §b"+fPlayer.getUser().getName()+"§r zvysil svuj rank na "+fPlayer.getRank().getChatColor()+"§l"+fPlayer.getRank().getName());
					} else {
						Title.showTitle(fPlayer.getPlayer(),"§cRank snizen",0.5,7,0.5);
						FightDuels.sendMessage("§c"+FightRank.CHAR_DOWN+" §b"+fPlayer.getUser().getName()+"§r snizil svuj rank na "+fPlayer.getRank().getChatColor()+"§l"+fPlayer.getRank().getName());
					}
					Title.showSubTitle(fPlayer.getPlayer(),fPlayer.getRank().getChatColor()+"§l"+fPlayer.getRank().getName(),0.5,7,0.5);
				}
			}
		},40);
	}

	@EventHandler
	public void FightPlayerRankCreatedEvent(FightPlayerRankCreatedEvent event){
		FightPlayer fPlayer = event.getPlayer();
		Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
			public void run(){
				if(fPlayer.getPlayer() != null){
					fPlayer.getPlayer().playSound(fPlayer.getPlayer().getLocation(),Sound.ENTITY_ENDER_DRAGON_DEATH,1f,1f);
					Title.showTitle(fPlayer.getPlayer(),"§aRank nastaven",0.5,7,0.5);
					Title.showSubTitle(fPlayer.getPlayer(),fPlayer.getRank().getChatColor()+"§l"+fPlayer.getRank().getName(),0.5,7,0.5);
					FightDuels.sendMessage("§7"+FightRank.CHAR_SET+" §b"+fPlayer.getUser().getName()+"§r ma nyni rank "+fPlayer.getRank().getChatColor()+"§l"+fPlayer.getRank().getName());
				}
			}
		},40);
	}
}