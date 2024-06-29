package realcraft.bukkit.lobby;

import org.bukkit.*;
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
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.anticheat.AntiCheat;
import realcraft.bukkit.auth.AuthLoginEvent;
import realcraft.bukkit.spawn.ServerSpawn;
import realcraft.bukkit.spectator.Spectator;
import realcraft.bukkit.utils.BorderUtil;
import realcraft.bukkit.utils.MaterialUtil;
import realcraft.bukkit.utils.Particles;
import realcraft.share.ServerType;

import java.util.HashMap;
import java.util.Random;

public class Lobby implements Listener {
	RealCraft plugin;

	boolean enabled = false;

	public LobbyMenu lobbymenu = null;
	public LobbyFunGun lobbyfungun = null;
	public LobbyLanterns lobbylanterns = null;
	public LobbyAutoParkour lobbyautoparkour = null;
	public LobbySpawn lobbyspawn = null;
	public LobbyStands lobbystands = null;

	private boolean isLobby = false;

	public Lobby(RealCraft realcraft){
		plugin = realcraft;
		if(plugin.serverName.equalsIgnoreCase("lobby")) isLobby = true;
		if(plugin.config.getBoolean("lobby.enabled",false)){
			enabled = true;
			if(plugin.config.getBoolean("lobby.menu.enabled",false)) lobbymenu = new LobbyMenu(plugin);
			if(!plugin.serverName.equalsIgnoreCase("survival") && !plugin.serverName.equalsIgnoreCase("creative") && !plugin.serverName.equalsIgnoreCase("parkour")){
				lobbyfungun = new LobbyFunGun();
				lobbylanterns = new LobbyLanterns(plugin);
				lobbyautoparkour = new LobbyAutoParkour(plugin);
				lobbyspawn = new LobbySpawn(plugin);
				if(isLobby){
					new LobbyElytra();
					new LobbySpleef();
					new LobbyLottery(plugin);
					new LobbyLightsOff();
					//new LobbyPlayerRider(plugin);
					lobbystands = new LobbyStands(plugin);
				}
			}
			if(plugin.serverName.equalsIgnoreCase("parkour")) lobbyspawn = new LobbySpawn(plugin);
		}
		if(!plugin.serverName.equalsIgnoreCase("survival") && !plugin.serverName.equalsIgnoreCase("creative") && !plugin.serverName.equalsIgnoreCase("parkour")){
			plugin.getServer().getPluginManager().registerEvents(this,plugin);
		}
	}

	public void onDisable(){
		if(lobbystands != null) lobbystands.onDisable();
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerJoinEvent(PlayerJoinEvent event){
		if(!enabled) return;
		Player player = event.getPlayer();
		player.getInventory().clear();
		player.setGameMode(GameMode.ADVENTURE);
		player.setWalkSpeed(0.2f);
		player.setFoodLevel(20);
		player.setSaturation(20);
		if(RealCraft.getServerType() == ServerType.LOBBY){
			Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					BorderUtil.setBorder(player,ServerSpawn.getLocation(),1024);
				}
			},20);
		}
		new LobbyScoreboard(this,player);
	}

	@EventHandler
	public void AuthLoginEvent(AuthLoginEvent event){
		final Player player = event.getPlayer();
		if(!Spectator.isPlayerSpectating(player)){
			player.setGameMode(GameMode.ADVENTURE);
			if(RealCraft.getServerType() == ServerType.LOBBY){
				player.setWalkSpeed(0.2f);
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable(){
					@Override
					public void run(){
						player.setGameMode(GameMode.ADVENTURE);
						player.setWalkSpeed(0.2f);
						player.setAllowFlight(false);
						player.setFlying(false);
					}
				},20);
			}
		}
	}

	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(player.getWorld().getName().equalsIgnoreCase("world")){
			player.setGameMode(GameMode.ADVENTURE);
			if(RealCraft.getServerType() == ServerType.LOBBY){
				player.setWalkSpeed(0.2f);
				Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
					@Override
					public void run(){
						BorderUtil.setBorder(player,ServerSpawn.getLocation(),1024);
					}
				},20);
			}
		}
	}

	@EventHandler
	public void PlayerChangedWorldEvent(final PlayerChangedWorldEvent event){
		if(event.getFrom().getName().equalsIgnoreCase("world")){
			event.getPlayer().setWalkSpeed(0.2f);
		}
	}

	@EventHandler
	public void PlayerToggleFlightEvent(PlayerToggleFlightEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode() != GameMode.CREATIVE && player.getWorld().getName().equalsIgnoreCase("world")){
			event.setCancelled(true);
			player.setAllowFlight(false);
			player.setFlying(false);
		}
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		if(event.getPlayer().getWorld().getName().equalsIgnoreCase("world")){
			if(event.getAction() == Action.PHYSICAL){
				Block block = event.getClickedBlock();
				if(block != null && block.getType() == Material.FARMLAND){
					event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
					event.setCancelled(true);
				}
				/*else if(block != null && (block.getType() == Material.GOLD_PLATE || block.getType() == Material.IRON_PLATE)){
					if(event.getPlayer().getWorld().getName().equalsIgnoreCase("world")){
						event.setCancelled(true);
						this.throwPlayerForward(event.getPlayer());
					}
				}*/
			} else {
				if(event.getPlayer().getGameMode() != GameMode.CREATIVE){
					Block block = event.getClickedBlock();
					if(block != null){
						BlockState blockState = block.getState();
						if(blockState != null){
							if(MaterialUtil.isTrapdoor(blockState.getType())){
								event.setCancelled(true);
							}
							else if(MaterialUtil.isBed(blockState.getType())){
								event.setCancelled(true);
							}
							else if(blockState.getType() == Material.LEVER || blockState.getType() == Material.COMPARATOR || blockState.getType() == Material.REPEATER){
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
			}
		}
	}

	@EventHandler
	public void EntityInteractEvent(EntityInteractEvent event){
		if(event.getEntity() instanceof Animals){
			Block block = event.getBlock();
			if(block != null && block.getType() == Material.FARMLAND && block.getWorld().getName().equalsIgnoreCase("world")){
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void BlockFadeEvent(BlockFadeEvent event){
		Block block = event.getBlock();
		if(block != null && block.getType() == Material.FARMLAND && block.getWorld().getName().equalsIgnoreCase("world")){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void BlockGrowEvent(BlockGrowEvent event){
		if(event.getNewState().getType() == Material.SUGAR_CANE && event.getBlock().getWorld().getName().equalsIgnoreCase("world")){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void LeavesDecayEvent(LeavesDecayEvent event){
		if(event.getBlock().getWorld().getName().equalsIgnoreCase("world")){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void EntityExplodeEvent(EntityExplodeEvent event){
		if(event.getEntityType() == EntityType.TNT && event.getEntity().getWorld().getName().equalsIgnoreCase("world")) event.blockList().clear();
	}

	@EventHandler
	public void EntityDamageEvent(EntityDamageEvent event){
		if(event.getEntity().getWorld().getName().equalsIgnoreCase("world")){
			if(event.getEntity() instanceof Animals){
				event.setCancelled(true);
			}
			else if(event.getEntity() instanceof Player){
				if(event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK){
					((Player)event.getEntity()).setFireTicks(0);
					event.setCancelled(true);
				}
				else if(event.getCause() == EntityDamageEvent.DamageCause.FALL){
					event.setCancelled(true);
				}
				else if(event.getCause() == EntityDamageEvent.DamageCause.VOID){
					event.setCancelled(true);
					((Player)event.getEntity()).teleport(ServerSpawn.getLocation());
				}
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(event.getEntity().getWorld().getName().equalsIgnoreCase("world")){
			if(event.getEntity() instanceof Animals){
				if(event.getDamager() instanceof Player){
					Player player = (Player) event.getDamager();
					if(player.getGameMode() != GameMode.CREATIVE){
						event.setCancelled(true);
						if(isLobby && event.getEntity() instanceof Sheep){
							this.sheepEffect((Sheep) event.getEntity());
						}
					}
				}
				else event.setCancelled(true);
			}
			else if(event.getEntity() instanceof ItemFrame && event.getDamager() instanceof Player){
				Player player = (Player) event.getDamager();
				if(player.getGameMode() != GameMode.CREATIVE){
					event.setCancelled(true);
				}
			}
			else if(!(event.getDamager() instanceof Player) || ((Player)event.getDamager()).getGameMode() != GameMode.CREATIVE) event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event){
		if(event.getHand().equals(EquipmentSlot.HAND) && event.getPlayer().getWorld().getName().equalsIgnoreCase("world")){
			if(event.getRightClicked() instanceof Sheep){
				this.sheepEffect((Sheep) event.getRightClicked());
			}
		}
		if(event.getPlayer().getGameMode() != GameMode.CREATIVE && event.getRightClicked() != null && event.getRightClicked().getType() == EntityType.ITEM_FRAME){
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
	public void CreatureSpawnEvent(CreatureSpawnEvent event){
		if(event.getEntityType() != EntityType.PLAYER && event.getEntity().getWorld().getName().equalsIgnoreCase("world")){
			if(event.getSpawnReason() == SpawnReason.NATURAL && event.getEntityType() != EntityType.SHEEP) event.setCancelled(true);
			else if(event.getSpawnReason() != SpawnReason.CUSTOM && event.getEntityType() != EntityType.ARMOR_STAND) event.setCancelled(true);
		}
	}

	@EventHandler
	public void BlockBreakEvent(BlockBreakEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode() != GameMode.CREATIVE && player.getWorld().getName().equalsIgnoreCase("world")) event.setCancelled(true);
	}

	@EventHandler
	public void BlockPlaceEvent(BlockPlaceEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode() != GameMode.CREATIVE && player.getWorld().getName().equalsIgnoreCase("world")) event.setCancelled(true);
	}

	@EventHandler
	public void PlayerDeathEvent(PlayerDeathEvent event){
		if(event.getEntity().getWorld().getName().equalsIgnoreCase("world")) event.getDrops().clear();
	}

	@EventHandler
	public void PlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event){
		if(event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) event.setCancelled(true);
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void EntityPickupItemEvent(EntityPickupItemEvent event){
		if(event.getEntityType() == EntityType.PLAYER && ((Player)event.getEntity()).getGameMode() != GameMode.CREATIVE){
			event.setCancelled(true);
			event.getItem().remove();
		}
	}

	public void sheepEffect(final Sheep sheep){
		if(Math.abs(sheep.getVelocity().getY()) < 0.2){
			Random random = new Random();
			DyeColor oldColor = sheep.getColor();
			while(true){
				sheep.setColor(DyeColor.values()[random.nextInt(16)]);
				if(sheep.getColor() != oldColor) break;
			}
			sheep.setVelocity(sheep.getVelocity().add(new Vector(0,1.0,0)));
			Particles.LAVA.display(0.5f,2f,0.5f,0f,8,sheep.getLocation(),128);
			sheep.getLocation().getWorld().playSound(sheep.getLocation(),Sound.ENTITY_SHEEP_SHEAR,1.0f,1.0f);
			sheep.getLocation().getWorld().playSound(sheep.getLocation(),Sound.ENTITY_SHEEP_AMBIENT,1.0f,1.0f);
			/*clearPathfinders(sheep);
            makePanic(sheep);
            Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
                @Override
                public void run(){
                	clearPathfinders(sheep);
                }
            },120);*/
		}
	}

	private HashMap<Player,Long> throwedPlayers = new HashMap<Player,Long>();
	public void throwPlayerForward(Player player){
		if(throwedPlayers.get(player) == null || throwedPlayers.get(player)+500 < System.currentTimeMillis()){
			throwedPlayers.put(player,System.currentTimeMillis());
			AntiCheat.exempt(player,2000);
			player.teleport(player.getLocation().add(0,0.3,0));
			player.setVelocity(new Vector(-4.5,1.0,3.0));
			player.playSound(player.getLocation(),Sound.ENTITY_BAT_TAKEOFF,1,1);
		}
	}
}