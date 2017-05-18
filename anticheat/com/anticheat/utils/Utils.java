package com.anticheat.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class Utils {
	public static final int lagMaxTicks = 80;
	private static final long lagMaxCoveredMs = 50L * (1L + lagMaxTicks * (1L + lagMaxTicks));
	private static final long[] tickDurations = new long[lagMaxTicks];
    private static final long[] tickDurationsSq = new long[lagMaxTicks];

	public static final float getLag(final long ms, final boolean exact) {
        if (ms < 0) {
            // Account for freezing (i.e. check timeLast, might be an extra method)!
            return getLag(0, exact);
        }
        else if (ms > lagMaxCoveredMs) {
            return getLag(lagMaxCoveredMs, exact);
        }

        final int add = ms > 0 && (ms % 50) == 0 ? 0 : 1;
        // TODO: Consider: Put "exact" block here, subtract a tick if appropriate?
        final int totalTicks = Math.min(1, add + (int) (ms / 50));
        final int maxTick = Math.min(lagMaxTicks, totalTicks);
        long sum = tickDurations[maxTick - 1];
        long covered = maxTick * 50;

        // Only count fully covered:
        if (totalTicks > lagMaxTicks) {
            int maxTickSq = Math.min(lagMaxTicks, totalTicks / lagMaxTicks);
            if (lagMaxTicks * maxTickSq == totalTicks) {
                maxTickSq -= 1;
            }
            sum += tickDurationsSq[maxTickSq - 1];
            covered += lagMaxTicks * 50 * maxTickSq;
        }

        return Math.max(1f, (float) sum / (float) covered);
    }

	private static final List<Material> INSTANT_BREAK = new ArrayList<Material>();
    private static final List<Material> FOOD = new ArrayList<Material>();
    private static final List<Material> INTERACTABLE = new ArrayList<Material>();
    private static final Map<Material, Material> COMBO = new HashMap<Material, Material>();

	public static double getXDelta(Location one, Location two)
    {
    	return Math.abs(one.getX() - two.getX());
    }

    public static double getZDelta(Location one, Location two)
    {
    	return Math.abs(one.getZ() - two.getZ());
    }

    /**
     * Gets the three dimensional distance between two Locations
     * @param one the first location
     * @param two the second location
     * @return the distance
     */
    public static double getDistance3D(Location one, Location two) {
		double toReturn = 0.0;
		double xSqr = (two.getX() - one.getX()) * (two.getX() - one.getX());
		double ySqr = (two.getY() - one.getY()) * (two.getY() - one.getY());
		double zSqr = (two.getZ() - one.getZ()) * (two.getZ() - one.getZ());
		double sqrt = Math.sqrt(xSqr + ySqr + zSqr);
		toReturn = Math.abs(sqrt);
		return toReturn;
	}

    public static double getDistance3D(SimpleLocation one, SimpleLocation two)
    {
    	double toReturn = 0.0;
		double xSqr = (two.getX() - one.getX()) * (two.getX() - one.getX());
		double ySqr = (two.getY() - one.getY()) * (two.getY() - one.getY());
		double zSqr = (two.getZ() - one.getZ()) * (two.getZ() - one.getZ());
		double sqrt = Math.sqrt(xSqr + ySqr + zSqr);
		toReturn = Math.abs(sqrt);
		return toReturn;
    }

    /**
     * Gets the horizontal distance between two Locations
     * @param one the first location
     * @param two the second location
     * @return the horizontal distance between the two points
     */
    public static double getHorizontalDistance(Location one, Location two) {
		double toReturn = 0.0;
		double xSqr = (two.getX() - one.getX()) * (two.getX() - one.getX());
		double zSqr = (two.getZ() - one.getZ()) * (two.getZ() - one.getZ());
		double sqrt = Math.sqrt(xSqr + zSqr);
		toReturn = Math.abs(sqrt);
		return toReturn;
	}

    public static double getHorizontalDistance(SimpleLocation one, SimpleLocation two) {
		double toReturn = 0.0;
		double xSqr = (two.getX() - one.getX()) * (two.getX() - one.getX());
		double zSqr = (two.getZ() - one.getZ()) * (two.getZ() - one.getZ());
		double sqrt = Math.sqrt(xSqr + zSqr);
		toReturn = Math.abs(sqrt);
		return toReturn;
	}

    public static boolean cantStandAboveWater(Block block){
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

    public static boolean isAboveIce(Block block){
		if(
			Utils.isIce(block.getRelative(BlockFace.NORTH)) ||
			Utils.isIce(block.getRelative(BlockFace.EAST)) ||
			Utils.isIce(block.getRelative(BlockFace.SOUTH)) ||
			Utils.isIce(block.getRelative(BlockFace.WEST)) ||
			Utils.isIce(block.getRelative(BlockFace.NORTH_WEST)) ||
			Utils.isIce(block.getRelative(BlockFace.NORTH_EAST)) ||
			Utils.isIce(block.getRelative(BlockFace.SOUTH_WEST)) ||
			Utils.isIce(block.getRelative(BlockFace.SOUTH_EAST))
		) return true;
		block = block.getRelative(BlockFace.DOWN);
		if(
			Utils.isIce(block.getRelative(BlockFace.NORTH)) ||
			Utils.isIce(block.getRelative(BlockFace.EAST)) ||
			Utils.isIce(block.getRelative(BlockFace.SOUTH)) ||
			Utils.isIce(block.getRelative(BlockFace.WEST)) ||
			Utils.isIce(block.getRelative(BlockFace.NORTH_WEST)) ||
			Utils.isIce(block.getRelative(BlockFace.NORTH_EAST)) ||
			Utils.isIce(block.getRelative(BlockFace.SOUTH_WEST)) ||
			Utils.isIce(block.getRelative(BlockFace.SOUTH_EAST))
		) return true;
		return false;
	}

    public static boolean isIce(Block block){
    	return (block.getType() == Material.ICE || block.getType() == Material.FROSTED_ICE || block.getType() == Material.PACKED_ICE);
    }

	public static boolean isPlayerAboveSlimeBlocks(Player player){
		Block block = player.getLocation().getBlock();
		return (
			Utils.isBlockAboveSlimeBlock(block.getRelative(BlockFace.NORTH)) ||
			Utils.isBlockAboveSlimeBlock(block.getRelative(BlockFace.EAST)) ||
			Utils.isBlockAboveSlimeBlock(block.getRelative(BlockFace.SOUTH)) ||
			Utils.isBlockAboveSlimeBlock(block.getRelative(BlockFace.WEST)) ||
			Utils.isBlockAboveSlimeBlock(block.getRelative(BlockFace.NORTH_WEST)) ||
			Utils.isBlockAboveSlimeBlock(block.getRelative(BlockFace.NORTH_EAST)) ||
			Utils.isBlockAboveSlimeBlock(block.getRelative(BlockFace.SOUTH_WEST)) ||
			Utils.isBlockAboveSlimeBlock(block.getRelative(BlockFace.SOUTH_EAST))
		);
	}

	public static boolean isBlockAboveSlimeBlock(Block block){
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

	public static boolean canPlayerFly(Player player){
		return (player.getGameMode() == GameMode.CREATIVE ||
				player.getGameMode() == GameMode.SPECTATOR ||
				player.hasPotionEffect(PotionEffectType.LEVITATION) ||
				player.isFlying() ||
				player.isInsideVehicle() ||
				player.isGliding());
	}

    /**
     * Determine whether or not a player can stand in a given location,
     * and do so correctly
     *
     * @param theBlock The block to be checked
     * @return true if the player should be unable to stand here
     */
    public static boolean cantStandAtBetter(Block block)
    {
    	Block otherBlock = block.getRelative(BlockFace.DOWN);

    	boolean center1 = otherBlock.getType() == Material.AIR;
    	boolean north1 = otherBlock.getRelative(BlockFace.NORTH).getType() == Material.AIR;
    	boolean east1 = otherBlock.getRelative(BlockFace.EAST).getType() == Material.AIR;
    	boolean south1 = otherBlock.getRelative(BlockFace.SOUTH).getType() == Material.AIR;
    	boolean west1 = otherBlock.getRelative(BlockFace.WEST).getType() == Material.AIR;
    	boolean northeast1 = otherBlock.getRelative(BlockFace.NORTH_EAST).getType() == Material.AIR;
    	boolean northwest1 = otherBlock.getRelative(BlockFace.NORTH_WEST).getType() == Material.AIR;
    	boolean southeast1 = otherBlock.getRelative(BlockFace.SOUTH_EAST).getType() == Material.AIR;
    	boolean southwest1 = otherBlock.getRelative(BlockFace.SOUTH_WEST).getType() == Material.AIR;
    	boolean overAir1 = (otherBlock.getRelative(BlockFace.DOWN).getType() == Material.AIR
    						|| otherBlock.getRelative(BlockFace.DOWN).getType() == Material.WATER
    						|| otherBlock.getRelative(BlockFace.DOWN).getType() == Material.LAVA);

    	return (center1 && north1 && east1 && south1 && west1 && northeast1 && southeast1
    			&& northwest1 && southwest1 && overAir1);
    }

    /**
     * Check if only the block beneath them is standable (includes water + lava)
     * @param block the block to check (under)
     * @return true if they cannot stand there
     */
    public static boolean cantStandAtSingle(Block block)
    {
    	// TODO: Implement Better to reduce false positives
    	Block otherBlock = block.getRelative(BlockFace.DOWN);
    	boolean center = otherBlock.getType() == Material.AIR;
    	return center;
    }

    /**
     * Eh, I got lazy; sue me.
     * TODO: Improve
     * @param block
     * @return
     */
    public static boolean cantStandAtWater(Block block)
    {
    	Block otherBlock = block.getRelative(BlockFace.DOWN);
    	boolean isHover = block.getType() == Material.AIR;
    	boolean n = otherBlock.getRelative(BlockFace.NORTH).getType() == Material.WATER;
    	boolean s = otherBlock.getRelative(BlockFace.SOUTH).getType() == Material.WATER;
    	boolean e = otherBlock.getRelative(BlockFace.EAST).getType() == Material.WATER;
    	boolean w = otherBlock.getRelative(BlockFace.WEST).getType() == Material.WATER;
    	boolean ne = otherBlock.getRelative(BlockFace.NORTH_EAST).getType() == Material.WATER;
    	boolean nw = otherBlock.getRelative(BlockFace.NORTH_WEST).getType() == Material.WATER;
    	boolean se = otherBlock.getRelative(BlockFace.SOUTH_EAST).getType() == Material.WATER;
    	boolean sw = otherBlock.getRelative(BlockFace.SOUTH_WEST).getType() == Material.WATER;
    	return(n && s && e && w && ne && nw && se && sw && isHover);
    }

    /**
     * What it says.
     * @param block
     * @return
     */
    public static boolean canStandWithin(Block block)
    {
    	boolean isSand = block.getType() == Material.SAND;
    	boolean isGravel = block.getType() == Material.GRAVEL;
    	boolean solid = block.getType().isSolid()
    			&& !(block.getType().name().toLowerCase().contains("door"))
    				&& !(block.getType().name().toLowerCase().contains("fence"))
    					&& !(block.getType().name().toLowerCase().contains("bars"))
    						&& !(block.getType().name().toLowerCase().contains("sign"));
    	return !isSand && !isGravel && !solid;
    }

    /**
     * Get optimal rotation for looking between to points
     * @param one the first location
     * @param two the second location
     * @return the vector for the rotation
     */
    public static Vector getRotation(Location one, Location two) {
   		double dx = two.getX() - one.getX();
   		double dy = two.getY() - one.getY();
   		double dz = two.getZ() - one.getZ();
   		double distanceXZ = Math.sqrt(dx * dx + dz * dz);
   		float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
   		float pitch = (float) -(Math.atan2(dy, distanceXZ) * 180.0D / Math.PI);
   		return new Vector(yaw, pitch, 0);
    }

    /**
     * Clamp a rotation to fit into 180 degrees
     * @param theta
     * @return
     */
    public static double clamp180(double theta) {
    	theta %= 360;
    	if (theta >= 180.0D) theta -= 360.0D;
    	if (theta < -180.0D) theta += 360.0D;
    	return theta;
    }

    /**
     * Determine if a player has a given Enchantment type
     *
     * @param The name of the enchantment
     * @return the level, -1 if not contained
     */
    public static int getLevelForEnchantment(Player player, String enchantment)
    {
    	Enchantment theEnchantment;
    	//For some odd reason, this throwns a NPE *sarcasm* hooray for Bukkit not updating...
    	try
    	{
    		theEnchantment = Enchantment.getByName(enchantment);
    		for(ItemStack item : player.getInventory().getArmorContents())
    		{
    			if(item.containsEnchantment(theEnchantment))
    			{
    				return item.getEnchantmentLevel(theEnchantment);
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		return -1;
    	}
    	return -1;
    }

    public static boolean hasNoFallBoots(Player player){
    	ItemStack item = player.getInventory().getBoots();
    	if(item != null && item.containsEnchantment(Enchantment.PROTECTION_FALL)) return true;
    	return false;
    }

    /**
     * Determine whether a player cannot stand on or around the given block
     *
     * @param block the block to check
     * @return true if the player should be unable to stand here
     */
    public static boolean cantStandAt(Block block) {
        return !canStand(block) && cantStandClose(block) && cantStandFar(block);
    }

    /**
     * Determine whether a player should be unable to stand at a given location
     *
     * @param location the location to check
     * @return true if the player should be unable to stand here
     */
    public static boolean cantStandAtExp(Location location) {
        return cantStandAt(new Location(location.getWorld(), fixXAxis(location.getX()), location.getY() - 0.01D, location.getBlockZ()).getBlock());
    }

    /**
     * Determine whether cannot stand on the block's immediately surroundings (North, East, South, West)
     *
     * @param block the block to check
     * @return true if a player cannot stand in the immediate vicinity
     */
    public static boolean cantStandClose(Block block) {
        return !canStand(block.getRelative(BlockFace.NORTH)) && !canStand(block.getRelative(BlockFace.EAST)) && !canStand(block.getRelative(BlockFace.SOUTH)) && !canStand(block.getRelative(BlockFace.WEST));
    }

    /**
     * Determine whether cannot stand on the block's outer surroundings
     *
     * @param block the block to check
     * @return true if a player cannot stand in areas further away from the block
     */
    public static boolean cantStandFar(Block block) {
        return !canStand(block.getRelative(BlockFace.NORTH_WEST)) && !canStand(block.getRelative(BlockFace.NORTH_EAST)) && !canStand(block.getRelative(BlockFace.SOUTH_WEST)) && !canStand(block.getRelative(BlockFace.SOUTH_EAST));
    }

    /**
     * Determine whether a player can stand on the given block
     *
     * @param block the block to check
     * @return true if the player can stand here
     */
    public static boolean canStand(Block block) {
        return !(block.isLiquid() || block.getType() == Material.AIR);
    }

    /**
     * Determine whether a player is fully submerged in water
     *
     * @param player the player's location
     * @return true if the player is fully in the water
     */
    public static boolean isFullyInWater(Location player) {
        double touchedX = fixXAxis(player.getX());

        // Yes, this doesn't make sense, but it's supposed to fix some false positives in water walk.
        // Think of it as 2 negatives = a positive :)
        if (!(new Location(player.getWorld(), touchedX, player.getY(), player.getBlockZ()).getBlock()).isLiquid() && !(new Location(player.getWorld(), touchedX, Math.round(player.getY()), player.getBlockZ()).getBlock()).isLiquid()) {
            return true;
        }

        return (new Location(player.getWorld(), touchedX, player.getY(), player.getBlockZ()).getBlock()).isLiquid() && (new Location(player.getWorld(), touchedX, Math.round(player.getY()), player.getBlockZ()).getBlock()).isLiquid();
    }

    /**
     * Fixes a player's X position to determine the block they are on, even if they're on the edge
     *
     * @param x player's x position
     * @return fixed x position
     */
    public static double fixXAxis(double x) {
        /* For Z axis, just use Math.round(xaxis); */
        double touchedX = x;
        double rem = touchedX - Math.round(touchedX) + 0.01D;
        if (rem < 0.30D) {
            touchedX = NumberConversions.floor(x) - 1;
        }
        return touchedX;
    }

    /**
     * Determine if the player is hovering over water with the given limit
     *
     * @param player the player's location
     * @param blocks max blocks to check
     * @return true if the player is hovering over water
     */
    public static boolean isHoveringOverWater(Location player, int blocks) {
        for (int i = player.getBlockY(); i > player.getBlockY() - blocks; i--) {
            Block newloc = (new Location(player.getWorld(), player.getBlockX(), i, player.getBlockZ())).getBlock();
            if (newloc.getType() != Material.AIR) {
                return newloc.isLiquid();
            }
        }

        return false;
    }

    /**
     * Determine if the player is hovering over water with a hard limit of 25 blocks
     *
     * @param player the player's location
     * @return true if the player is hovering over water
     */
    public static boolean isHoveringOverWater(Location player) {
        return isHoveringOverWater(player, 25);
    }

    /**
     * Determine whether a material will break instantly when hit
     *
     * @param m the material to check
     * @return true if the material is instant break
     */
    public static boolean isInstantBreak(Material m) {
        return INSTANT_BREAK.contains(m);
    }

    /**
     * Determine whether a material is edible
     *
     * @param m the material to check
     * @return true if the material is food
     */
    public static boolean isFood(Material m) {
        return FOOD.contains(m);
    }

    /**
     * Determine whether a block is a slab
     *
     * @param block block to check
     * @return true if slab
     */
    public static boolean isSlab(Block block) {
        Material type = block.getType();
        switch (type) {
            case STEP:
            case DOUBLE_STEP:
            case WOOD_STEP:
            case WOOD_DOUBLE_STEP:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether a block is a stair
     *
     * @param block block to check
     * @return true if stair
     */
    public static boolean isStair(Block block) {
        Material type = block.getType();
        switch (type) {
            case WOOD_STAIRS:
            case SPRUCE_WOOD_STAIRS:
            case SMOOTH_STAIRS:
            case SANDSTONE_STAIRS:
            case QUARTZ_STAIRS:
            case JUNGLE_WOOD_STAIRS:
            case NETHER_BRICK_STAIRS:
            case BIRCH_WOOD_STAIRS:
            case COBBLESTONE_STAIRS:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether a player can interact with this material
     *
     * @param m material to check
     * @return true if interactable
     */
    public static boolean isInteractable(Material m) {
        return INTERACTABLE.contains(m);
    }

    /**
     * Determine whether a player is sprinting or flying
     *
     * @param player player to check
     * @return true if sprinting or flying
     */
    public static boolean sprintFly(Player player) {
        return player.isSprinting() || player.isFlying();
    }

    /**
     * Determine whether a player is standing on a lily pad
     *
     * @param player player to check
     * @return true if on lily pad
     */
    public static boolean isOnLilyPad(Player player) {
        Block block = player.getLocation().getBlock();
        Material lily = Material.WATER_LILY;
        // TODO: Can we fix X this?
        return block.getType() == lily || block.getRelative(BlockFace.NORTH).getType() == lily || block.getRelative(BlockFace.SOUTH).getType() == lily || block.getRelative(BlockFace.EAST).getType() == lily || block.getRelative(BlockFace.WEST).getType() == lily;
    }

    /**
     * Determine whether a player is fully submersed in liquid
     *
     * @param player player to check
     * @return true if submersed
     */
    public static boolean isSubmersed(Player player) {
        return player.getLocation().getBlock().isLiquid() && player.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid();
    }

    /**
     * Determine whether a player is in water
     *
     * @param player player to check
     * @return true if in water
     */
    public static boolean isInWater(Player player) {
        return player.getLocation().getBlock().isLiquid() || player.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid() || player.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid();
    }

    /**
     * Determine whether a player is in a web
     *
     * @param player player to check
     * @return true if in web
     */
    public static boolean isInWeb(Player player) {
        return player.getLocation().getBlock().getType() == Material.WEB || player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.WEB || player.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.WEB;
    }

    /**
     * Determine whether a block is climbable
     *
     * @param block block to check
     * @return true if climbable
     */
    public static boolean isClimbableBlock(Block block) {
        return block.getType() == Material.VINE || block.getType() == Material.LADDER || block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER;
    }

    /**
     * Determine whether a player is on a vine (can be free hanging)
     *
     * @param player to check
     * @return true if on vine
     */
    public static boolean isOnVine(Player player) {
        return player.getLocation().getBlock().getType() == Material.VINE;
    }

    /**
     * Determine whether a String can be cast to an Integer
     *
     * @param string text to check
     * @return true if int
     */
    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Determine whether a String can be cast to a Double
     *
     * @param string text to check
     * @return true if double
     */
    public static boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Calculate the time in milliseconds that it should take to break the given block with the given tool
     *
     * @param tool  tool to check
     * @param block block to check
     * @return time in milliseconds to break
     */


    /**
     * Determine whether the given tool is a combination that makes the breaking of this block faster
     *
     * @param tool  tool to check
     * @param block block to check
     * @return true if quick combo
     */
    /*private static boolean isQuickCombo(ItemStack tool, Material block) {
        for (Material t : COMBO.keySet()) {
            if (tool.getType() == t && COMBO.get(t) == block) {
                return true;
            }
        }
        return false;
    }*/

    /**
     * Determine if a block ISN'T one of the specified types
     *
     * @param block     block to check
     * @param materials array of possible materials
     * @return true if the block isn't any of the materials
     */
    public static boolean blockIsnt(Block block, Material[] materials) {
        Material type = block.getType();
        for (Material m : materials) {
            if (m == type) {
                return false;
            }
        }
        return true;
    }

    /**
     * Parse a COMMAND[] input to a set of commands to execute
     *
     * @param command input string
     * @return parsed commands
     */
    public static String[] getCommands(String command) {
        return command.replaceAll("COMMAND\\[", "").replaceAll("]", "").split(";");
    }

    /**
     * Remove all whitespace from the given string to ready it for parsing
     *
     * @param string the string to parse
     * @return string with whitespace removed
     */
    public static String removeWhitespace(String string) {
        return string.replaceAll(" ", "");
    }

    /**
     * Determine if a player has the given enchantment on their armor
     *
     * @param player player to check
     * @param e      enchantment to check
     * @return true if the armor has this enchantment
     */
    public static boolean hasArmorEnchantment(Player player, Enchantment e) {
        for (ItemStack is : player.getInventory().getArmorContents()) {
            if (is != null && is.containsEnchantment(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create a list with the given string for execution
     *
     * @param string the string to parse
     * @return ArrayList with string
     */
    /*public static ArrayList<String> stringToList(final String string) {
        return new ArrayList<String>() {{ add(string); }};
    }*/

    /**
     * Create a comma-delimited string from a list
     *
     * @param list the list to parse
     * @return the list in a string format
     */
    public static String listToCommaString(List<String> list) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            b.append(list.get(i));
            if (i < list.size() - 1) {
                b.append(",");
            }
        }
        return b.toString();
    }

    /**
     * Parse a string in the format of "XdXhXmXs" to seconds
     * @param string The string to parse
     * @return seconds
     */
    public static long lifeToSeconds(String string) {
        if (string.equals("0") || string.equals("")) return 0;
        String[] lifeMatch = new String[]{ "d", "h", "m", "s" };
        int[] lifeInterval = new int[]{ 86400, 3600, 60, 1 };
        long seconds = 0L;

        for (int i=0;i<lifeMatch.length;i++) {
            Matcher matcher = Pattern.compile("([0-9]*)" + lifeMatch[i]).matcher(string);
            while (matcher.find()) {
                seconds += Integer.parseInt(matcher.group(1)) * lifeInterval[i];
            }

        }
        return seconds;
    }


    static {
        // START INSTANT BREAK MATERIALS
        INSTANT_BREAK.add(Material.RED_MUSHROOM);
        INSTANT_BREAK.add(Material.RED_ROSE);
        INSTANT_BREAK.add(Material.BROWN_MUSHROOM);
        INSTANT_BREAK.add(Material.YELLOW_FLOWER);
        INSTANT_BREAK.add(Material.REDSTONE);
        INSTANT_BREAK.add(Material.REDSTONE_TORCH_OFF);
        INSTANT_BREAK.add(Material.REDSTONE_TORCH_ON);
        INSTANT_BREAK.add(Material.REDSTONE_WIRE);
        INSTANT_BREAK.add(Material.LONG_GRASS);
        INSTANT_BREAK.add(Material.PAINTING);
        INSTANT_BREAK.add(Material.WHEAT);
        INSTANT_BREAK.add(Material.SUGAR_CANE);
        INSTANT_BREAK.add(Material.SUGAR_CANE_BLOCK);
        INSTANT_BREAK.add(Material.DIODE);
        INSTANT_BREAK.add(Material.DIODE_BLOCK_OFF);
        INSTANT_BREAK.add(Material.DIODE_BLOCK_ON);
        INSTANT_BREAK.add(Material.SAPLING);
        INSTANT_BREAK.add(Material.TORCH);
        INSTANT_BREAK.add(Material.CROPS);
        INSTANT_BREAK.add(Material.SNOW);
        INSTANT_BREAK.add(Material.TNT);
        INSTANT_BREAK.add(Material.POTATO);
        INSTANT_BREAK.add(Material.CARROT);
        // END INSTANT BREAK MATERIALS

        // START INTERACTABLE MATERIALS
        INTERACTABLE.add(Material.STONE_BUTTON);
        INTERACTABLE.add(Material.LEVER);
        INTERACTABLE.add(Material.CHEST);
        // END INTERACTABLE MATERIALS

        // START FOOD
        FOOD.add(Material.COOKED_BEEF);
        FOOD.add(Material.COOKED_CHICKEN);
        FOOD.add(Material.COOKED_FISH);
        FOOD.add(Material.GRILLED_PORK);
        FOOD.add(Material.PORK);
        FOOD.add(Material.MUSHROOM_SOUP);
        FOOD.add(Material.RAW_BEEF);
        FOOD.add(Material.RAW_CHICKEN);
        FOOD.add(Material.RAW_FISH);
        FOOD.add(Material.APPLE);
        FOOD.add(Material.GOLDEN_APPLE);
        FOOD.add(Material.MELON);
        FOOD.add(Material.COOKIE);
        FOOD.add(Material.BREAD);
        FOOD.add(Material.SPIDER_EYE);
        FOOD.add(Material.ROTTEN_FLESH);
        FOOD.add(Material.POTATO_ITEM);
        // END FOOD

        // START COMBOS
        COMBO.put(Material.SHEARS, Material.WOOL);
        COMBO.put(Material.IRON_SWORD, Material.WEB);
        COMBO.put(Material.DIAMOND_SWORD, Material.WEB);
        COMBO.put(Material.STONE_SWORD, Material.WEB);
        COMBO.put(Material.WOOD_SWORD, Material.WEB);
        // END COMBOS
    }
}