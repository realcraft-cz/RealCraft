package realcraft.bukkit.mapmanager;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.NullExtent;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.player.*;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.anticheat.AntiCheat;
import realcraft.bukkit.mapmanager.commands.MapCommandList;
import realcraft.bukkit.mapmanager.events.MapPlayerJoinMapEvent;
import realcraft.bukkit.mapmanager.events.MapPlayerLeaveMapEvent;
import realcraft.bukkit.mapmanager.events.MapRegionLoadEvent;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapPermission;
import realcraft.bukkit.others.AbstractCommand;
import realcraft.bukkit.spawn.ServerSpawn;
import realcraft.share.users.UserRank;

import java.util.ArrayList;

public class MapListeners implements Listener {

	public MapListeners(){
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
				MapManager.getMapPlayer(player).toggleWEByPass();
				MapManager.sendMessage(player,"§6WorldEdit bypass "+(MapManager.getMapPlayer(player).isWEByPass() ? "§aenabled" : "§cdisabled"));
			}
		};
	}

	@Subscribe
	public void EditSessionEvent(EditSessionEvent event){
		if(event.getActor() != null && event.getActor().isPlayer()){
			if(!event.getWorld().getName().equalsIgnoreCase(MapManager.getWorld().getName())){
				return;
			}
			Player player = Bukkit.getServer().getPlayer(event.getActor().getName());
			if(player != null){
				MapPlayer mPlayer = MapManager.getMapPlayer(player);
				if(mPlayer.isWEByPass()) return;
				if(mPlayer.getMap() == null || !mPlayer.getMap().getPermission(mPlayer).isMinimum(MapPermission.BUILD)){
					event.setExtent(new NullExtent());
					return;
				}
				event.setExtent(mPlayer.getMap().getRegion().getExtent(event.getExtent()));
				mPlayer.getMap().getRegion().setToSave(true);
			}
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1).toLowerCase();
		if(command.startsWith("up ")){
			MapPlayer mPlayer = MapManager.getMapPlayer(player);
			if(mPlayer.getMap() == null || !mPlayer.getMap().getPermission(mPlayer).isMinimum(MapPermission.BUILD) || !mPlayer.getMap().getRegion().isLocationInside(player.getLocation())){
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void PlayerLoginEvent(PlayerLoginEvent event){
		MapPlayer mPlayer = MapManager.getMapPlayer(event.getPlayer());
		if(mPlayer == null || !mPlayer.getUser().getRank().isMinimum(UserRank.BUILDER)){
			event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
			event.setKickMessage("§cTento server je pouze pro cleny AT");
		}
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
		MapPlayer mPlayer = MapManager.getMapPlayer(event.getPlayer());
		if(mPlayer.getMap() != null){
			event.setRespawnLocation(mPlayer.getMap().getRegion().getCenterLocation());
			mPlayer.updateBorder();
		}
	}

	@EventHandler
	public void PlayerTeleportEvent(PlayerTeleportEvent event){
		MapPlayer mPlayer = MapManager.getMapPlayer(event.getPlayer());
		if(!event.getTo().getWorld().getName().equals(MapManager.getWorld().getName())) return;
		Map map = MapManager.getMap(event.getTo());
		if(map == null){
			event.setCancelled(true);
			return;
		}
		if(!map.equals(mPlayer.getMap())) mPlayer.joinMap(map);
	}

	@EventHandler
	public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event){
		MapPlayer mPlayer = MapManager.getMapPlayer(event.getPlayer());
		if(event.getFrom().getName().equals(MapManager.getWorld().getName())){
			mPlayer.leaveMap();
		}
	}

	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent event){
		if(event.getPlayer().getLocation().getBlockY() < -10){
			AntiCheat.exempt(event.getPlayer(),1000);
			event.getPlayer().setAllowFlight(true);
			event.getPlayer().setFlying(true);
			event.getPlayer().setVelocity(event.getPlayer().getVelocity().setY(2.0));
		}
	}

	@EventHandler
	public void MapPlayerJoinMapEvent(MapPlayerJoinMapEvent event){
		MapPlayer mPlayer = event.getPlayer();
		Map map = event.getMap();
		MapManager.sendMessage("§b"+mPlayer.getPlayer().getName()+"§7 se pripojil do mapy §e"+map.getName()+" §7[#"+map.getId()+"]");
		mPlayer.getPlayer().setAllowFlight(true);
		mPlayer.getPlayer().setFlying(true);
		if(!map.getRegion().isLoaded() && !map.getRegion().isLoading()){
			MapManager.sendMessage("§7Nacitani mapy §e"+event.getMap().getName()+" §7[#"+event.getMap().getId()+"] ...");
			map.getRegion().load();
		}
	}

	@EventHandler
	public void MapPlayerLeaveMapEvent(MapPlayerLeaveMapEvent event){
		MapPlayer mPlayer = event.getPlayer();
		mPlayer.getPlayer().setFlying(false);
		mPlayer.getPlayer().resetPlayerTime();
	}

	@EventHandler
	public void MapRegionLoadEvent(MapRegionLoadEvent event){
		MapManager.sendMessage("§7Nacteni mapy §e"+event.getMap().getName()+" §7[#"+event.getMap().getId()+"] dokonceno");
	}

	@EventHandler
	public void BlockBreakEvent(BlockBreakEvent event){
		MapPlayer mPlayer = MapManager.getMapPlayer(event.getPlayer());
		if(mPlayer.getMap() == null || mPlayer.isWEByPass()) return;
		if(!mPlayer.getMap().getRegion().isLocationInside(event.getBlock().getLocation()) || !mPlayer.getMap().getPermission(mPlayer).isMinimum(MapPermission.BUILD)){
			event.setCancelled(true);
			return;
		}
		mPlayer.getMap().getRegion().setToSave(true);
	}

	@EventHandler
	public void BlockPlaceEvent(BlockPlaceEvent event){
		MapPlayer mPlayer = MapManager.getMapPlayer(event.getPlayer());
		if(mPlayer.getMap() == null || mPlayer.isWEByPass()) return;
		if(!mPlayer.getMap().getRegion().isLocationInside(event.getBlock().getLocation()) || !mPlayer.getMap().getPermission(mPlayer).isMinimum(MapPermission.BUILD)){
			event.setCancelled(true);
			return;
		}
		mPlayer.getMap().getRegion().setToSave(true);
	}

	@EventHandler
	public void StructureGrowEvent(StructureGrowEvent event){
		Map map = MapManager.getMap(event.getLocation());
		if(map == null){
			event.setCancelled(true);
			return;
		}
		ArrayList<BlockState> toRemove = new ArrayList<>();
		for(BlockState block : event.getBlocks()){
			if(!map.getRegion().isLocationInside(block.getLocation())){
				toRemove.add(block);
			}
		}
		for(BlockState block : toRemove){
			event.getBlocks().remove(block);
		}
		map.getRegion().setToSave(true);
	}

	@EventHandler
	public void HangingBreakEvent(HangingBreakEvent event){
		if(event.getCause() == RemoveCause.EXPLOSION){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void HangingBreakByEntityEvent(HangingBreakByEntityEvent event){
		if(event.getRemover() instanceof Player){
			if(((Player)event.getRemover()).getGameMode() != GameMode.CREATIVE) event.setCancelled(true);
		}
		else event.setCancelled(true);
	}

	@EventHandler
	public void EntityExplodeEvent(EntityExplodeEvent event){
		if(event.getEntityType() == EntityType.TNT){
			event.setCancelled(true);
			event.blockList().clear();
		}
	}

	@EventHandler
	public void ExplosionPrimeEvent(ExplosionPrimeEvent event){
		if(event.getEntityType() == EntityType.TNT){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void PlayerBedEnterEvent(PlayerBedEnterEvent event){
		event.setCancelled(true);
	}

	@EventHandler
	public void PlayerDropItemEvent(PlayerDropItemEvent event){
		event.getItemDrop().remove();
	}

	@EventHandler
	public void PlayerEggThrowEvent(PlayerEggThrowEvent event){
		event.setHatching(false);
	}

	@EventHandler
	public void PlayerDeathEvent(PlayerDeathEvent event){
		event.getDrops().clear();
		event.setDroppedExp(0);
	}

	@EventHandler
	public void ProjectileHitEvent(ProjectileHitEvent event){
		if(event.getEntity().getType() == EntityType.ARROW || event.getEntity().getType() == EntityType.SPECTRAL_ARROW || event.getEntity().getType() == EntityType.TRIDENT){
			event.getEntity().remove();
		}
	}

	@EventHandler
	public void PortalCreateEvent(PortalCreateEvent event){
		event.setCancelled(true);
	}

	@EventHandler
	public void EntityCreatePortalEvent(EntityCreatePortalEvent event){
		event.setCancelled(true);
	}

	@EventHandler
	public void CreatureSpawnEvent(CreatureSpawnEvent event){
		if(event.getEntityType() == EntityType.ENDER_DRAGON || event.getEntityType() == EntityType.GHAST){
			event.setCancelled(true);
		}
	}
}