package realcraft.bukkit.falling;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.PortalCreateEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.falling.arena.FallArenaPermission;
import realcraft.bukkit.mapmanager.commands.MapCommandList;
import realcraft.bukkit.spawn.ServerSpawn;
import realcraft.bukkit.utils.LocationUtil;

public class FallListeners implements Listener  {

	public FallListeners(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		Player player = event.getPlayer();
		player.getInventory().clear();
		player.getInventory().setItem(0,MapCommandList.getHotbarItem());
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
	public void BlockBreakEvent(BlockBreakEvent event){
		FallPlayer fPlayer = FallManager.getFallPlayer(event.getPlayer());
		if(fPlayer.getArena() == null || !fPlayer.getArena().getRegion().isLocationInside(event.getBlock().getLocation()) || !fPlayer.getArena().getPermission(fPlayer).isMinimum(FallArenaPermission.BUILD)){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void BlockPlaceEvent(BlockPlaceEvent event){
		FallPlayer fPlayer = FallManager.getFallPlayer(event.getPlayer());
		if(fPlayer.getArena() == null || !fPlayer.getArena().getRegion().isLocationInside(event.getBlock().getLocation()) || !fPlayer.getArena().getPermission(fPlayer).isMinimum(FallArenaPermission.BUILD)){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void HangingBreakByEntityEvent(HangingBreakByEntityEvent event){
		if(event.getRemover() instanceof Player){
			FallPlayer fPlayer = FallManager.getFallPlayer((Player)event.getRemover());
			if(fPlayer.getArena() == null || !fPlayer.getArena().getRegion().isLocationInside(event.getEntity().getLocation()) || !fPlayer.getArena().getPermission(fPlayer).isMinimum(FallArenaPermission.BUILD)){
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event){
		FallPlayer fPlayer = FallManager.getFallPlayer(event.getPlayer());
		if(event.getRightClicked().getType() == EntityType.ITEM_FRAME){
			if(fPlayer.getArena() == null || !fPlayer.getArena().getRegion().isLocationInside(event.getRightClicked().getLocation()) || !fPlayer.getArena().getPermission(fPlayer).isMinimum(FallArenaPermission.BUILD)){
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void PlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent event){
		FallPlayer fPlayer = FallManager.getFallPlayer(event.getPlayer());
		if(fPlayer.getArena() == null || !fPlayer.getArena().getRegion().isLocationInside(event.getRightClicked().getLocation()) || !fPlayer.getArena().getPermission(fPlayer).isMinimum(FallArenaPermission.BUILD)){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void PlayerEggThrowEvent(PlayerEggThrowEvent event){
		FallPlayer fPlayer = FallManager.getFallPlayer(event.getPlayer());
		if(fPlayer.getArena() == null || !fPlayer.getArena().getRegion().isLocationInside(event.getEgg().getLocation()) || !fPlayer.getArena().getPermission(fPlayer).isMinimum(FallArenaPermission.BUILD)){
			event.setHatching(false);
		}
	}

	@EventHandler
	public void PortalCreateEvent(PortalCreateEvent event){
		event.setCancelled(true);
	}
}