package com.realcraft.lobby;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.material.Bed;
import org.bukkit.material.TrapDoor;
import org.bukkit.util.Vector;

import com.anticheat.AntiCheat;
import com.google.common.collect.Sets;
import com.realcraft.RealCraft;
import com.realcraft.auth.AuthLoginEvent;
import com.realcraft.playermanazer.PlayerManazer.PlayerInfo;
import com.realcraft.utils.Particles;
import com.utils.CustomPathFinderGoalPanic;

import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EntityCreature;
import net.minecraft.server.v1_11_R1.EntityInsentient;
import net.minecraft.server.v1_11_R1.PathfinderGoalSelector;

public class Lobby implements Listener {
	RealCraft plugin;

	boolean enabled = false;

	public LobbyMenu lobbymenu = null;
	public LobbyJump lobbyjump = null;
	public LobbyFunGun lobbyfungun = null;
	public LobbyChests lobbychests = null;
	public LobbyCosmetics lobbycosmetics = null;
	public LobbyLanterns lobbylanterns = null;
	public LobbyCitizens lobbycitizens = null;
	public LobbyParkours lobbyparkours = null;
	public LobbyAutoParkour lobbyautoparkour = null;
	public LobbyPlayerRider lobbyplayerrider = null;
	public LobbySpawn lobbyspawn = null;
	public LobbyLottery lobbylottery = null;
	public LobbyPokemons lobbypokemons = null;

	private boolean isLobby = false;
	private boolean maintenance = false;

	public Lobby(RealCraft realcraft){
		plugin = realcraft;
		if(plugin.serverName.equalsIgnoreCase("lobby")) isLobby = true;
		if(plugin.config.getBoolean("lobby.enabled",false)){
			enabled = true;
			if(plugin.config.getBoolean("lobby.menu.enabled",false)) lobbymenu = new LobbyMenu(plugin);
			if(!plugin.serverName.equalsIgnoreCase("survival") && !plugin.serverName.equalsIgnoreCase("creative") && !plugin.serverName.equalsIgnoreCase("parkour")){
				if(plugin.config.getBoolean("lobby.jump.enabled",false) && plugin.serverName.equalsIgnoreCase("lobby")) lobbyjump = new LobbyJump(plugin);
				if(plugin.config.getBoolean("lobby.fungun.enabled",false)) lobbyfungun = new LobbyFunGun(plugin);
				if(plugin.config.getBoolean("lobby.chests.enabled",false)) lobbychests = new LobbyChests(plugin);
				if(plugin.config.getBoolean("lobby.cosmetics.enabled",false)) lobbycosmetics = new LobbyCosmetics(plugin);
				if(plugin.config.getBoolean("lobby.lanterns.enabled",false)) lobbylanterns = new LobbyLanterns(plugin);
				if(plugin.config.getBoolean("lobby.citizens.enabled",false) && plugin.serverName.equalsIgnoreCase("lobby")) lobbycitizens = new LobbyCitizens(plugin);
				if(plugin.config.getBoolean("lobby.parkours.enabled",false)) lobbyparkours = new LobbyParkours(plugin);
				if(plugin.config.getBoolean("lobby.autoparkour.enabled",false)) lobbyautoparkour = new LobbyAutoParkour(plugin);
				if(plugin.config.getBoolean("lobby.playerrider.enabled",false)) lobbyplayerrider = new LobbyPlayerRider(plugin);
				if(plugin.config.getBoolean("lobby.spawn.enabled",false)) lobbyspawn = new LobbySpawn(plugin);
				if(RealCraft.isTestServer()){
					new LobbyLottery(plugin);
					lobbypokemons = new LobbyPokemons(plugin);
				}
			}
			if(plugin.serverName.equalsIgnoreCase("parkour") && plugin.config.getBoolean("lobby.spawn.enabled",false)) lobbyspawn = new LobbySpawn(plugin);
			//if(isLobby) lobbylabyrinth = new LobbyLabyrinth(plugin);
		}
		if(!plugin.serverName.equalsIgnoreCase("survival") && !plugin.serverName.equalsIgnoreCase("creative") && !plugin.serverName.equalsIgnoreCase("parkour")){
			plugin.getServer().getPluginManager().registerEvents(this,plugin);
		}
		if(plugin.config.getBoolean("lobby.maintenance."+plugin.serverName,false)){
			this.maintenance = true;
		}
	}

	public void onReload(){
		enabled = true;
		if(lobbymenu != null) lobbymenu.onReload();
		if(lobbyjump != null) lobbyjump.onReload();
		if(lobbyfungun != null) lobbyfungun.onReload();
		if(lobbychests != null) lobbychests.onReload();
		if(lobbycosmetics != null) lobbycosmetics.onReload();
		if(lobbylanterns != null) lobbylanterns.onReload();
		if(lobbycitizens != null) lobbycitizens.onReload();
		if(lobbyparkours != null) lobbyparkours.onReload();
		if(lobbyautoparkour != null) lobbyautoparkour.onReload();
		if(lobbyplayerrider != null) lobbyplayerrider.onReload();
		if(lobbyspawn != null) lobbyspawn.onReload();
	}

	public void onDisable(){
		if(lobbycosmetics != null) lobbycosmetics.onDisable();
		if(lobbyautoparkour != null) lobbyautoparkour.onDisable();
		if(lobbypokemons != null) lobbypokemons.onDisable();
	}

	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled = false)
	public void onPlayerJoin(PlayerLoginEvent event){
		if(maintenance){
			PlayerInfo playerInfo = plugin.playermanazer.getPlayerInfo(event.getPlayer());
			if(playerInfo == null || playerInfo.getRank() < 45){
				event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
				event.setKickMessage("§fServer je docasne nedostupny, zkuste to prosim pozdeji.");
			}
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerJoinEvent(PlayerJoinEvent event){
		if(!enabled) return;
		Player player = event.getPlayer();
		player.getInventory().clear();
		player.setGameMode(GameMode.ADVENTURE);
		player.setWalkSpeed(0.3f);
		new LobbyScoreboard(this,player);
	}

	@EventHandler
	public void AuthLoginEvent(AuthLoginEvent event){
		final Player player = event.getPlayer();
		if(!AntiCheat.isPlayerSpectating(player)){
			player.setGameMode(GameMode.ADVENTURE);
			player.setWalkSpeed(0.3f);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable(){
				@Override
				public void run(){
					player.setGameMode(GameMode.ADVENTURE);
					player.setWalkSpeed(0.3f);
					player.setAllowFlight(false);
					player.setFlying(false);
				}
			},20);
		}
	}

	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(player.getWorld().getName().equalsIgnoreCase("world")){
			player.setGameMode(GameMode.ADVENTURE);
			player.setWalkSpeed(0.3f);
		}
	}

	@EventHandler
	public void PlayerChangedWorldEvent(final PlayerChangedWorldEvent event){
		if(event.getFrom().getName().equalsIgnoreCase("world")){
			event.getPlayer().setWalkSpeed(0.2f);
			//event.getPlayer().setAllowFlight(false);
			//event.getPlayer().setFlying(false);
		}
		else if(event.getPlayer().getWorld().getName().equalsIgnoreCase("world")){
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable(){
				@Override
				public void run(){
					event.getPlayer().setWalkSpeed(0.3f);
					if(!AntiCheat.isPlayerSpectating(event.getPlayer())){
						event.getPlayer().setGameMode(GameMode.ADVENTURE);
						event.getPlayer().setAllowFlight(false);
						event.getPlayer().setFlying(false);
					}
				}
			},20);
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
				if(block != null && block.getType() == Material.SOIL){
					event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
					event.setCancelled(true);
				}
				else if(block != null && (block.getType() == Material.GOLD_PLATE || block.getType() == Material.IRON_PLATE)){
					if(event.getPlayer().getWorld().getName().equalsIgnoreCase("world")){
						event.setCancelled(true);
						this.throwPlayerForward(event.getPlayer());
					}
				}
			} else {
				if(event.getPlayer().getGameMode() != GameMode.CREATIVE){
					Block block = event.getClickedBlock();
					if(block != null){
						BlockState blockState = block.getState();
						if(blockState != null){
							if(blockState.getData() instanceof TrapDoor){
								event.setCancelled(true);
							}
							else if(blockState.getData() instanceof Bed){
								event.setCancelled(true);
							}
							else if(blockState.getType() == Material.LEVER || blockState.getType() == Material.REDSTONE_COMPARATOR || blockState.getType() == Material.DIODE){
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
			if(block != null && block.getType() == Material.SOIL && block.getWorld().getName().equalsIgnoreCase("world")){
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void BlockFadeEvent(BlockFadeEvent event){
		Block block = event.getBlock();
		if(block != null && block.getType() == Material.SOIL && block.getWorld().getName().equalsIgnoreCase("world")){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void BlockGrowEvent(BlockGrowEvent event){
		if(event.getNewState().getType() == Material.SUGAR_CANE_BLOCK && event.getBlock().getWorld().getName().equalsIgnoreCase("world")){
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
		if(event.getEntityType() == EntityType.PRIMED_TNT && event.getEntity().getWorld().getName().equalsIgnoreCase("world")) event.blockList().clear();
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
					((Player)event.getEntity()).performCommand("spawn");
				}
			}
		}
	}

	@EventHandler
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
			else event.setCancelled(true);
		}
	}

	@EventHandler
	public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event){
		if(event.getHand().equals(EquipmentSlot.HAND) && event.getPlayer().getWorld().getName().equalsIgnoreCase("world")){
			if(lobbycitizens != null && !lobbycitizens.npcRegistry.isNPC(event.getRightClicked())) event.setCancelled(true);
			if(event.getRightClicked() instanceof Sheep){
				this.sheepEffect((Sheep) event.getRightClicked());
			}
		}
	}

	@EventHandler
	public void CreatureSpawnEvent(CreatureSpawnEvent event){
		if(event.getEntityType() != EntityType.PLAYER && event.getEntity().getWorld().getName().equalsIgnoreCase("world")){
			if(event.getSpawnReason() == SpawnReason.NATURAL && event.getEntityType() != EntityType.SHEEP) event.setCancelled(true);
			else if(event.getSpawnReason() != SpawnReason.CUSTOM) event.setCancelled(true);
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
	public void PlayerPickupItemEvent(PlayerPickupItemEvent event){
		if(event.getPlayer().getGameMode() != GameMode.CREATIVE && event.getPlayer().getWorld().getName().equalsIgnoreCase("world") && event.getItem().getItemStack().getType() != Material.TRIPWIRE_HOOK){
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

	public void clearPathfinders(org.bukkit.entity.Entity entity) {
        Entity nmsEntity = ((CraftEntity) entity).getHandle();
        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(((EntityInsentient) nmsEntity).goalSelector, Sets.newLinkedHashSet());
            bField.set(((EntityInsentient) nmsEntity).targetSelector, Sets.newLinkedHashSet());
            cField.set(((EntityInsentient) nmsEntity).goalSelector, Sets.newLinkedHashSet());
            cField.set(((EntityInsentient) nmsEntity).targetSelector, Sets.newLinkedHashSet());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

	public void makePanic(org.bukkit.entity.Entity entity) {
        EntityInsentient insentient = (EntityInsentient) ((CraftEntity) entity).getHandle();
        insentient.goalSelector.a(3, new CustomPathFinderGoalPanic((EntityCreature) insentient, 0.4d));
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