package realcraft.bukkit.falling;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.NullExtent;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
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
import org.bukkit.event.world.StructureGrowEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.falling.arena.FallArena;
import realcraft.bukkit.falling.arena.FallArenaPermission;
import realcraft.bukkit.falling.events.FallArenaRegionGenerateEvent;
import realcraft.bukkit.falling.events.FallPlayerJoinArenaEvent;
import realcraft.bukkit.falling.events.FallPlayerLeaveArenaEvent;
import realcraft.bukkit.falling.exceptions.FallArenaLockedException;
import realcraft.bukkit.others.AbstractCommand;
import realcraft.bukkit.spawn.ServerSpawn;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.LocationUtil;

import java.util.ArrayList;

public class FallListeners implements Listener  {

	public FallListeners(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		WorldEdit.getInstance().getEventBus().register(this);
		new AbstractCommand("leave"){
			@Override
			public void perform(Player player,String[] args){
				player.teleport(ServerSpawn.getLocation());
			}
		};
		new AbstractCommand("wea"){
			@Override
			public void perform(Player player,String[] args){
				FallManager.getFallPlayer(player).toggleWEByPass();
				FallManager.sendMessage(player,"§6WorldEdit bypass "+(FallManager.getFallPlayer(player).isWEByPass() ? "§aenabled" : "§cdisabled"));
			}
		};
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		Player player = event.getPlayer();
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
			Location bedLocation = event.getPlayer().getBedSpawnLocation();
			if(bedLocation != null && fPlayer.getArena().getRegion().isLocationInside(bedLocation)){
				event.setRespawnLocation(bedLocation);
			} else {
				event.setRespawnLocation(LocationUtil.getSafeDestination(fPlayer.getArena().getRegion().getCenterLocation()));
			}
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

	@Subscribe
	public void EditSessionEvent(EditSessionEvent event){
		if(event.getActor() != null && event.getActor().isPlayer()){
			if(!event.getWorld().getName().equalsIgnoreCase(FallManager.getWorld().getName())){
				return;
			}
			Player player = Bukkit.getServer().getPlayer(event.getActor().getName());
			if(player != null){
				FallPlayer fPlayer = FallManager.getFallPlayer(player);
				if(fPlayer.isWEByPass()) return;
				if(fPlayer.getArena() == null || !fPlayer.getArena().getPermission(fPlayer).isMinimum(FallArenaPermission.TRUSTED)){
					event.setExtent(new NullExtent());
					return;
				}
				event.setExtent(fPlayer.getArena().getRegion().getExtent(event.getExtent()));
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
		if(fPlayer.getArena() == null || !fPlayer.getArena().getRegion().isLocationInsideFull(event.getBlock().getLocation()) || !fPlayer.getArena().getPermission(fPlayer).isMinimum(FallArenaPermission.TRUSTED)){
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
	public void StructureGrowEvent(StructureGrowEvent event){
		FallArena arena = FallManager.getArena(event.getLocation());
		if(arena == null){
			event.setCancelled(true);
			return;
		}
		ArrayList<BlockState> toRemove = new ArrayList<>();
		for(BlockState block : event.getBlocks()){
			if(!arena.getRegion().isLocationInside(block.getLocation())){
				toRemove.add(block);
			}
		}
		for(BlockState block : toRemove){
			event.getBlocks().remove(block);
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
		event.getArena().sendMessage("§b"+event.getPlayer().getPlayer().getName()+"§7 se pripojil na ostrov",true);
	}

	@EventHandler
	public void FallPlayerLeaveArenaEvent(FallPlayerLeaveArenaEvent event){
	}
}