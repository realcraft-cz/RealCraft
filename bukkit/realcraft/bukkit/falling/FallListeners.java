package realcraft.bukkit.falling;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.PortalCreateEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.falling.arena.FallArena;
import realcraft.bukkit.falling.arena.FallArenaPermission;
import realcraft.bukkit.falling.events.FallArenaRegionGenerateEvent;
import realcraft.bukkit.falling.events.FallPlayerJoinArenaEvent;
import realcraft.bukkit.falling.events.FallPlayerLeaveArenaEvent;
import realcraft.bukkit.falling.exceptions.FallArenaLockedException;
import realcraft.bukkit.spawn.ServerSpawn;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.LocationUtil;

public class FallListeners implements Listener  {

	public FallListeners(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		Player player = event.getPlayer();
		player.getInventory().clear();
		player.getInventory().setHeldItemSlot(0);
		player.setFlying(false);
	}

	@EventHandler
	public void PlayerSpawnLocationEvent(PlayerSpawnLocationEvent event){
		event.setSpawnLocation(ServerSpawn.getLocation());
	}

	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		FallPlayer fPlayer  = FallManager.getFallPlayer(event.getPlayer());
		if(fPlayer.getArena() != null){
			event.setRespawnLocation(LocationUtil.getSafeDestination(fPlayer.getArena().getRegion().getCenterLocation()));
			fPlayer.updateBorder();
		}
	}

	@EventHandler
	public void PlayerTeleportEvent(PlayerTeleportEvent event){
		FallPlayer fPlayer = FallManager.getFallPlayer(event.getPlayer());
		if(!event.getTo().getWorld().getName().equals(FallManager.getWorld().getName())) return;
		FallArena arena = FallManager.getArena(event.getTo());
		if(arena == null){
			event.setCancelled(true);
			return;
		}
		if(!arena.equals(fPlayer.getArena())){
			try {
				fPlayer.joinArena(arena);
			} catch (FallArenaLockedException e){
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event){
		FallPlayer fPlayer = FallManager.getFallPlayer(event.getPlayer());
		if(event.getFrom().getName().equals(FallManager.getWorld().getName())){
			fPlayer.leaveArena();
		}
	}

	@EventHandler
	public void BlockBreakEvent(BlockBreakEvent event){
		FallPlayer fPlayer = FallManager.getFallPlayer(event.getPlayer());
		if(fPlayer.getArena() == null || !fPlayer.getArena().getRegion().isLocationInside(event.getBlock().getLocation()) || !fPlayer.getArena().getPermission(fPlayer).isMinimum(FallArenaPermission.TRUSTED)){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void BlockPlaceEvent(BlockPlaceEvent event){
		FallPlayer fPlayer = FallManager.getFallPlayer(event.getPlayer());
		if(fPlayer.getArena() == null || !fPlayer.getArena().getRegion().isLocationInside(event.getBlock().getLocation()) || !fPlayer.getArena().getPermission(fPlayer).isMinimum(FallArenaPermission.TRUSTED)){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void HangingBreakByEntityEvent(HangingBreakByEntityEvent event){
		if(event.getRemover() instanceof Player){
			FallPlayer fPlayer = FallManager.getFallPlayer((Player)event.getRemover());
			if(fPlayer.getArena() == null || !fPlayer.getArena().getRegion().isLocationInside(event.getEntity().getLocation()) || !fPlayer.getArena().getPermission(fPlayer).isMinimum(FallArenaPermission.TRUSTED)){
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event){
		FallPlayer fPlayer = FallManager.getFallPlayer(event.getPlayer());
		if(event.getRightClicked().getType() == EntityType.ITEM_FRAME){
			if(fPlayer.getArena() == null || !fPlayer.getArena().getRegion().isLocationInside(event.getRightClicked().getLocation()) || !fPlayer.getArena().getPermission(fPlayer).isMinimum(FallArenaPermission.TRUSTED)){
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void PlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent event){
		FallPlayer fPlayer = FallManager.getFallPlayer(event.getPlayer());
		if(fPlayer.getArena() == null || !fPlayer.getArena().getRegion().isLocationInside(event.getRightClicked().getLocation()) || !fPlayer.getArena().getPermission(fPlayer).isMinimum(FallArenaPermission.TRUSTED)){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void PlayerEggThrowEvent(PlayerEggThrowEvent event){
		FallPlayer fPlayer = FallManager.getFallPlayer(event.getPlayer());
		if(fPlayer.getArena() == null || !fPlayer.getArena().getRegion().isLocationInside(event.getEgg().getLocation()) || !fPlayer.getArena().getPermission(fPlayer).isMinimum(FallArenaPermission.TRUSTED)){
			event.setHatching(false);
		}
	}

	@EventHandler
	public void PortalCreateEvent(PortalCreateEvent event){
		event.setCancelled(true);
	}

	@EventHandler
	public void CreatureSpawnEvent(CreatureSpawnEvent event){
		if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL && event.getEntityType() != EntityType.SLIME){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void FallArenaRegionGenerateEvent(FallArenaRegionGenerateEvent event){
		event.getArena().getRegion().setGenerating(false);
		Player player = Users.getPlayer(event.getArena().getOwner());
		if(player != null && player.isOnline()){
			FallManager.sendMessage(player, "§aOstrov vytvoren");
			try {
				FallManager.getFallPlayer(player).joinArena(event.getArena());
			} catch (FallArenaLockedException e){
			}
		}
	}

	@EventHandler
	public void FallPlayerJoinArenaEvent(FallPlayerJoinArenaEvent event){
		event.getArena().checkHasPlayers();
	}

	@EventHandler
	public void FallPlayerLeaveArenaEvent(FallPlayerLeaveArenaEvent event){
		event.getArena().checkHasPlayers();
	}
}