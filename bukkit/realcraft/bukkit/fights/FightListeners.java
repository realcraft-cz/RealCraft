package realcraft.bukkit.fights;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.material.Door;
import org.bukkit.material.Gate;
import org.bukkit.material.TrapDoor;

import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.anticheat.AntiCheat;
import realcraft.bukkit.fights.FightPlayer.FightPlayerState;
import realcraft.bukkit.lobby.LobbyMenu;

public class FightListeners implements Listener {

	public FightListeners(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		FightPlayer fPlayer = Fights.getFightPlayer(event.getPlayer());
		fPlayer.reload();
		fPlayer.reset();
		event.getPlayer().getInventory().setItem(0,LobbyMenu.getItem());
		event.getPlayer().teleport(Fights.getLobbyLocation());
		Fights.getLobbyScoreboard().addPlayer(fPlayer);
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
		Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				for(FightPlayer fPlayer : Fights.getOnlineFightPlayers()){
					PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER,((CraftPlayer)fPlayer.getPlayer()).getHandle());
					((CraftPlayer)fPlayer.getPlayer()).getHandle().playerConnection.sendPacket(packet);
				}
			}
		});
	}

	@EventHandler(priority=EventPriority.LOW)
	public void EntityDamageEvent(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			FightPlayer fPlayer = Fights.getFightPlayer((Player)event.getEntity());
			if(fPlayer.getState() != FightPlayerState.FIGHT){
				event.setCancelled(true);
				if(event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK) event.getEntity().setFireTicks(0);
				if(event.getCause() == DamageCause.VOID) event.getEntity().teleport(Fights.getLobbyLocation());
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
		if(block != null && block.getType() == Material.SOIL){
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
		if(event.getNewState().getType() == Material.SUGAR_CANE_BLOCK){
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void LeavesDecayEvent(LeavesDecayEvent event){
		event.setCancelled(true);
	}

	@EventHandler(priority=EventPriority.LOW)
	public void EntityExplodeEvent(EntityExplodeEvent event){
		if(event.getEntityType() == EntityType.PRIMED_TNT) event.blockList().clear();
	}

	@EventHandler(priority=EventPriority.LOW)
	public void EntityInteractEvent(EntityInteractEvent event){
		if(event.getEntity() instanceof Animals){
			Block block = event.getBlock();
			if(block != null && block.getType() == Material.SOIL){
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
				if(event.getAction() == Action.PHYSICAL && blockState != null && blockState.getType() == Material.SOIL){
					event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
					event.setCancelled(true);
				}
				else if((blockState.getData() instanceof Door || blockState.getData() instanceof TrapDoor || blockState.getData() instanceof Gate)){
					event.setCancelled(true);
				}
				else if((blockState.getType() == Material.CHEST || blockState.getType() == Material.ENDER_CHEST || blockState.getType() == Material.TRAPPED_CHEST)){
					event.setCancelled(true);
				}
				else if((blockState.getType() == Material.LEVER || blockState.getType() == Material.REDSTONE_COMPARATOR || blockState.getType() == Material.DIODE)){
					event.setCancelled(true);
				}
				else if(blockState.getType() == Material.WORKBENCH){
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
		if(Fights.getFightPlayer(event.getPlayer()).getState() != FightPlayerState.FIGHT) event.setCancelled(true);
	}

	@EventHandler(priority=EventPriority.LOW)
    public void EntityPickupItemEvent(EntityPickupItemEvent event){
		if(event.getEntity() instanceof Player && Fights.getFightPlayer((Player)event.getEntity()).getState() != FightPlayerState.FIGHT) event.setCancelled(true);
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerEggThrowEvent(PlayerEggThrowEvent event){
		event.setHatching(false);
	}
}