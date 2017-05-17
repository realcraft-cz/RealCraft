package com.anticheat.checks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffectType;

import com.anticheat.AntiCheat;
import com.anticheat.events.AntiCheatDetectEvent;
import com.anticheat.utils.Distance;
import com.anticheat.utils.Magic;
import com.anticheat.utils.Utils;
import com.realcraft.RealCraft;

public class MovementCheck implements Listener {
	AntiCheat anticheat;

	private Map<String, Double> blocksOverFlight = new HashMap<String, Double>();
	private Map<String, Integer> nofallViolation = new HashMap<String, Integer>();
	private List<String> isInWater = new ArrayList<String>();
	private List<String> isAscending = new ArrayList<String>();
	private Map<String, Integer> waterSpeedViolation = new HashMap<String, Integer>();
	private Map<String, Integer> waterAscensionViolation = new HashMap<String, Integer>();

	public MovementCheck(AntiCheat anticheat){
		this.anticheat = anticheat;
		Bukkit.getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(!AntiCheat.isPlayerExempted(player)){
			Location from = event.getFrom();
	        Location to = event.getTo();
	        Distance distance = new Distance(from,to);
	        this.logAscension(player,from.getY(),to.getY());
	        this.checkFlight(player,distance);
	        if(from.getY() > to.getY()) this.checkNoFall(player,distance);
	        //this.checkWaterWalk(player,distance);
		}
	}

	@EventHandler
	public void PlayerTeleportEvent(PlayerTeleportEvent event){
		this.resetCheck(event.getPlayer());
	}

	@EventHandler
	public void EntityDamageEvent(EntityDamageEvent event){
		if(event.getEntity() instanceof Player && event.getCause() == DamageCause.ENTITY_EXPLOSION){
			this.resetCheck((Player)event.getEntity());
		}
	}

	public void resetCheck(Player player){
		blocksOverFlight.remove(player.getName());
	}

	public void checkFlight(Player player,Distance distance){
		String name = player.getName();
		if(player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR && !player.hasPotionEffect(PotionEffectType.LEVITATION) && !player.getAllowFlight() && !player.isFlying() && !player.isInsideVehicle() && distance.getYDifference() < Magic.TELEPORT_MIN){
			double y1 = distance.fromY();
	        double y2 = distance.toY();
			if(!Utils.isHoveringOverWater(player.getLocation(),1)
					&& Utils.cantStandAtExp(player.getLocation())
					&& Utils.blockIsnt(player.getLocation().getBlock().getRelative(BlockFace.DOWN), new Material[]{
							Material.FENCE, Material.ACACIA_FENCE, Material.BIRCH_FENCE, Material.DARK_OAK_FENCE, Material.IRON_FENCE, Material.JUNGLE_FENCE, Material.NETHER_FENCE, Material.SPRUCE_FENCE,
							Material.FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.SPRUCE_FENCE_GATE,
							Material.COBBLE_WALL
					})
					&& (player.getInventory().getChestplate() == null || player.getInventory().getChestplate().getType() != Material.ELYTRA)){
				if(!blocksOverFlight.containsKey(name)){
					blocksOverFlight.put(name, 0D);
				}
				blocksOverFlight.put(name, (blocksOverFlight.get(name) + distance.getXDifference() + distance.getYDifference() + distance.getZDifference()));
				if(y1 > y2) blocksOverFlight.put(name, (blocksOverFlight.get(name) - distance.getYDifference()));
				if(blocksOverFlight.get(name) > Magic.FLIGHT_BLOCK_LIMIT && (y1 <= y2)){
					if(!this.isPlayerAboveSlimeBlocks(player)){
						AntiCheatDetectEvent callevent = new AntiCheatDetectEvent(player.getPlayer(),CheckType.FLYHACK);
						Bukkit.getServer().getPluginManager().callEvent(callevent);
					}
					blocksOverFlight.put(name, 0D);
				}
			}
			else blocksOverFlight.put(name, 0D);
		}
		else blocksOverFlight.put(name, 0D);
	}

	public void checkNoFall(Player player,Distance distance){
		String name = player.getName();
		if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR && !player.hasPotionEffect(PotionEffectType.LEVITATION) && !player.isFlying() && !player.isInsideVehicle() && !player.isSleeping() && !Utils.isClimbableBlock(player.getLocation().getBlock()) && !Utils.isInWater(player) && !Utils.isInWeb(player) && !Utils.hasNoFallBoots(player)) {
			if (player.getFallDistance() == 0) {
				if (nofallViolation.get(name) == null) {
					nofallViolation.put(name, 1);
				}
				else nofallViolation.put(name, nofallViolation.get(player.getName()) + 1);
				int i = nofallViolation.get(name);
				if (i >= Magic.NOFALL_LIMIT) {
					AntiCheatDetectEvent callevent = new AntiCheatDetectEvent(player.getPlayer(),CheckType.NOFALL);
					Bukkit.getServer().getPluginManager().callEvent(callevent);
					nofallViolation.put(name, 0);
				}
			}
			else nofallViolation.put(name,0);
		}
    }

	public void checkWaterWalk(Player player,Distance distance){
		double y = distance.getYDifference();
        Block block = player.getLocation().getBlock();
        if (!player.isInsideVehicle() && !player.isFlying() && !player.hasPotionEffect(PotionEffectType.LEVITATION) && (player.getInventory().getChestplate() == null || player.getInventory().getChestplate().getType() != Material.ELYTRA)) {
            if (block.isLiquid()) {
                if (isInWater.contains(player.getName())) {
                    if (player.getNearbyEntities(1, 1, 1).isEmpty()) {
                        if (!Utils.isFullyInWater(player.getLocation()) && Utils.isHoveringOverWater(player.getLocation(), 1) && y == 0D && this.cantStandAboveWater(block.getRelative(BlockFace.DOWN))) {
                            if (waterSpeedViolation.containsKey(player.getName())) {
                                int v = waterSpeedViolation.get(player.getName());
                                if (v >= Magic.WATER_SPEED_VIOLATION_MAX) {
                                    waterSpeedViolation.put(player.getName(), 0);
                                    AntiCheatDetectEvent callevent = new AntiCheatDetectEvent(player.getPlayer(),CheckType.WATERWALK);
                					Bukkit.getServer().getPluginManager().callEvent(callevent);
                                } else {
                                    waterSpeedViolation.put(player.getName(), v + 1);
                                }
                            } else {
                                waterSpeedViolation.put(player.getName(), 1);
                            }
                        }
                    }
                } else {
                    isInWater.add(player.getName());
                }
            } else if (block.getRelative(BlockFace.DOWN).isLiquid() && this.isAscending(player) && this.cantStandAboveWater(block.getRelative(BlockFace.DOWN))) {
                if (waterAscensionViolation.containsKey(player.getName())) {
                    int v = waterAscensionViolation.get(player.getName());
                    if (v >= Magic.WATER_ASCENSION_VIOLATION_MAX) {
                        waterAscensionViolation.put(player.getName(), 0);
                        AntiCheatDetectEvent callevent = new AntiCheatDetectEvent(player.getPlayer(),CheckType.WATERWALK);
    					Bukkit.getServer().getPluginManager().callEvent(callevent);
                    } else {
                        waterAscensionViolation.put(player.getName(), v + 1);
                    }
                } else {
                    waterAscensionViolation.put(player.getName(), 1);
                }
            } else {
                isInWater.remove(player.getName());
            }
        }
    }

	public boolean isAscending(Player player){
		return isAscending.contains(player.getName());
	}

	public void logAscension(Player player, double y1, double y2) {
		String name = player.getName();
		if (y1 < y2 && !isAscending.contains(name)) {
			isAscending.add(name);
		} else {
			isAscending.remove(name);
		}
	}

	public boolean cantStandAboveWater(Block block){
		return (
			!Utils.canStand(block.getRelative(BlockFace.NORTH)) &&
			!Utils.canStand(block.getRelative(BlockFace.EAST)) &&
			!Utils.canStand(block.getRelative(BlockFace.SOUTH)) &&
			!Utils.canStand(block.getRelative(BlockFace.WEST)) &&

			!Utils.canStand(block.getRelative(BlockFace.NORTH_WEST)) &&
			!Utils.canStand(block.getRelative(BlockFace.NORTH_EAST)) &&
			!Utils.canStand(block.getRelative(BlockFace.SOUTH_WEST)) &&
			!Utils.canStand(block.getRelative(BlockFace.SOUTH_EAST)) &&

			!Utils.canStand(block.getRelative(BlockFace.EAST_NORTH_EAST)) &&
			!Utils.canStand(block.getRelative(BlockFace.EAST_SOUTH_EAST)) &&
			!Utils.canStand(block.getRelative(BlockFace.NORTH_NORTH_EAST)) &&
			!Utils.canStand(block.getRelative(BlockFace.NORTH_NORTH_WEST)) &&
			!Utils.canStand(block.getRelative(BlockFace.SOUTH_SOUTH_EAST)) &&
			!Utils.canStand(block.getRelative(BlockFace.SOUTH_SOUTH_WEST)) &&
			!Utils.canStand(block.getRelative(BlockFace.WEST_NORTH_WEST)) &&
			!Utils.canStand(block.getRelative(BlockFace.WEST_SOUTH_WEST))
		);
	}

	public boolean isPlayerAboveSlimeBlocks(Player player){
		Block block = player.getLocation().getBlock();
		return (
			this.isBlockAboveSlimeBlock(block.getRelative(BlockFace.NORTH)) ||
			this.isBlockAboveSlimeBlock(block.getRelative(BlockFace.EAST)) ||
			this.isBlockAboveSlimeBlock(block.getRelative(BlockFace.SOUTH)) ||
			this.isBlockAboveSlimeBlock(block.getRelative(BlockFace.WEST)) ||
			this.isBlockAboveSlimeBlock(block.getRelative(BlockFace.NORTH_WEST)) ||
			this.isBlockAboveSlimeBlock(block.getRelative(BlockFace.NORTH_EAST)) ||
			this.isBlockAboveSlimeBlock(block.getRelative(BlockFace.SOUTH_WEST)) ||
			this.isBlockAboveSlimeBlock(block.getRelative(BlockFace.SOUTH_EAST))
		);
	}

	public boolean isBlockAboveSlimeBlock(Block block){
		int x = block.getX();
		int z = block.getZ();
		if(block.getY() > 0){
			for(int y=block.getY();y>=0;y--){
				if(block.getWorld().getBlockAt(x,y,z).getType() == Material.SLIME_BLOCK){
					return true;
				}
			}
		}
		return false;
	}
}