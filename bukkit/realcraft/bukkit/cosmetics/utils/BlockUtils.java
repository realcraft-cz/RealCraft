package realcraft.bukkit.cosmetics.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import realcraft.bukkit.RealCraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sacha on 03/08/15.
 */
public class BlockUtils {

	/**
	 * List of all the blocks to restore.
	 */
	public static Map<Location, String> blocksToRestore = new HashMap<>();

	/**
	 * List of all the current Treasure Blocks.
	 */
	public static List<Block> treasureBlocks = new ArrayList<>();

	/**
	 * Gets blocks in radius.
	 *
	 * @param location The center.
	 * @param radius   The radius.
	 * @param hollow   if the sphere of blocks should be hollow.
	 * @return The list of all the blocks in the given radius.
	 */
	public static List<Block> getBlocksInRadius(Location location, int radius, boolean hollow) {
		List<Block> blocks = new ArrayList<>();
		int bX = location.getBlockX(),
				bY = location.getBlockY(),
				bZ = location.getBlockZ();
		for (int x = bX - radius; x <= bX + radius; x++)
			for (int y = bY - radius; y <= bY + radius; y++)
				for (int z = bZ - radius; z <= bZ + radius; z++) {
					double distance = ((bX - x) * (bX - x) + (bY - y) * (bY - y) + (bZ - z) * (bZ - z));
					if (distance < radius * radius
							&& !(hollow && distance < ((radius - 1) * (radius - 1)))) {
						Location l = new Location(location.getWorld(), x, y, z);
						if (l.getBlock().getType() != Material.BARRIER)
							blocks.add(l.getBlock());
					}
				}
		return blocks;
	}

	/**
	 * Checks if an entity is on ground.
	 *
	 * @param entity The entity to check.
	 * @return {@code true} if entity is on ground, otherwise {@code false}.
	 */
	public static boolean isOnGround(Entity entity) {
		Block block = entity.getLocation().getBlock().getRelative(BlockFace.DOWN);
		if (block.getType().isSolid())
			return true;
		return false;
	}

	/**
	 * Checks if a block is part of a rocket.
	 *
	 * @param b The block to check.
	 * @return {@code true} if the block is part of a rocket, otherwise {@code false}.
	 */


	/**
	 * Force-restores the blocks.
	 */
	@SuppressWarnings("deprecation")
	public static void forceRestore() {
		for (Location loc : blocksToRestore.keySet()) {
			Block b = loc.getBlock();
			String s = blocksToRestore.get(loc);
			Material m = Material.valueOf(s.split(",")[0]);
			byte d = Byte.valueOf(s.split(",")[1]);
			b.setType(m);
		}
	}

	/**
	 * Restores the block at the location "loc".
	 *
	 * @param LOCATION The location of the block to restore.
	 */
	public static void restoreBlockAt(final Location LOCATION) {
		Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(), new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (!blocksToRestore.containsKey(LOCATION)) return;
				Block b = LOCATION.getBlock();
				String s = blocksToRestore.get(LOCATION);
				Material m = Material.valueOf(s.split(",")[0]);
				byte d = Byte.valueOf(s.split(",")[1]);
				for (Player player : b.getLocation().getWorld().getPlayers())
					player.sendBlockChange(LOCATION, m, d);
				blocksToRestore.remove(LOCATION);
			}
		});
	}

	/**
	 * Replaces a block with a new material and data, and after delay, restore it.
	 *
	 * @param BLOCK      The block.
	 * @param NEW_TYPE   The new material.
	 * @param NEW_DATA   The new data.
	 * @param TICK_DELAY The delay after which the block is restored.
	 */
	public static void setToRestoreIgnoring(final Block BLOCK, final Material NEW_TYPE, final byte NEW_DATA, final int TICK_DELAY) {
		Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(), new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (blocksToRestore.containsKey(BLOCK.getLocation())) return;
				if (!blocksToRestore.containsKey(BLOCK.getLocation())) {
					blocksToRestore.put(BLOCK.getLocation(), BLOCK.getType().toString() + "," + BLOCK.getData());
					for (Player player : BLOCK.getLocation().getWorld().getPlayers())
						player.sendBlockChange(BLOCK.getLocation(), NEW_TYPE, NEW_DATA);
					Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
						@Override
						public void run() {
							restoreBlockAt(BLOCK.getLocation());

						}
					}, TICK_DELAY);
				}
			}
		});
	}

	/**
	 * Replaces a block with a new material and data, and after delay, restore it.
	 *
	 * @param BLOCK      The block.
	 * @param NEW_TYPE   The new material.
	 * @param TICK_DELAY The delay after which the block is restored.
	 */
	public static void setToRestore(final Block BLOCK, final Material NEW_TYPE, final int TICK_DELAY) {
		Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(), new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (blocksToRestore.containsKey(BLOCK.getLocation())) return;
				Block bUp = BLOCK.getRelative(BlockFace.UP);
				if (BLOCK.getType() != Material.AIR
						&& !Tag.SIGNS.isTagged(BLOCK.getType())
						&& !Tag.STANDING_SIGNS.isTagged(BLOCK.getType())
						&& !Tag.WALL_SIGNS.isTagged(BLOCK.getType())
						&& BLOCK.getType() != Material.CHEST
						&& BLOCK.getType() != Material.STONE_PRESSURE_PLATE
						&& BLOCK.getType() != Material.ACACIA_PRESSURE_PLATE
						&& BLOCK.getType() != Material.BIRCH_PRESSURE_PLATE
						&& BLOCK.getType() != Material.DARK_OAK_PRESSURE_PLATE
						&& BLOCK.getType() != Material.HEAVY_WEIGHTED_PRESSURE_PLATE
						&& BLOCK.getType() != Material.JUNGLE_PRESSURE_PLATE
						&& BLOCK.getType() != Material.LIGHT_WEIGHTED_PRESSURE_PLATE
						&& BLOCK.getType() != Material.OAK_PRESSURE_PLATE
						&& BLOCK.getType() != Material.SPRUCE_PRESSURE_PLATE
						&& BLOCK.getType() != Material.STONE_PRESSURE_PLATE
						&& BLOCK.getType() != Material.WHEAT
						&& BLOCK.getType() != Material.GRASS
						&& BLOCK.getType() != Material.TALL_GRASS
						&& BLOCK.getType() != Material.DEAD_BUSH
						&& BLOCK.getType() != Material.POPPY
						&& BLOCK.getType() != Material.RED_MUSHROOM
						&& BLOCK.getType() != Material.BROWN_MUSHROOM
						&& BLOCK.getType() != Material.TORCH
						&& BLOCK.getType() != Material.LADDER
						&& BLOCK.getType() != Material.VINE
						&& BLOCK.getType() != Material.END_PORTAL
						&& BLOCK.getType() != Material.NETHER_PORTAL
						&& BLOCK.getType() != Material.CACTUS
						&& BLOCK.getType() != Material.WATER
						&& BLOCK.getType() != Material.LAVA
						&& BLOCK.getType() != Material.FARMLAND
						&& BLOCK.getType() != Material.BARRIER
						&& BLOCK.getType() != Material.COMMAND_BLOCK
						&& BLOCK.getType() != Material.DROPPER
						&& BLOCK.getType() != Material.DISPENSER
						&& BLOCK.getType() != Material.WHITE_BANNER
						&& BLOCK.getType() != Material.ORANGE_BANNER
						&& BLOCK.getType() != Material.MAGENTA_BANNER
						&& BLOCK.getType() != Material.LIGHT_BLUE_BANNER
						&& BLOCK.getType() != Material.YELLOW_BANNER
						&& BLOCK.getType() != Material.LIME_BANNER
						&& BLOCK.getType() != Material.PINK_BANNER
						&& BLOCK.getType() != Material.GRAY_BANNER
						&& BLOCK.getType() != Material.LIGHT_GRAY_BANNER
						&& BLOCK.getType() != Material.CYAN_BANNER
						&& BLOCK.getType() != Material.PURPLE_BANNER
						&& BLOCK.getType() != Material.BLUE_BANNER
						&& BLOCK.getType() != Material.BROWN_BANNER
						&& BLOCK.getType() != Material.GREEN_BANNER
						&& BLOCK.getType() != Material.RED_BANNER
						&& BLOCK.getType() != Material.BLACK_BANNER
						&& BLOCK.getType() != Material.WHITE_WALL_BANNER
						&& BLOCK.getType() != Material.ORANGE_WALL_BANNER
						&& BLOCK.getType() != Material.MAGENTA_WALL_BANNER
						&& BLOCK.getType() != Material.LIGHT_BLUE_WALL_BANNER
						&& BLOCK.getType() != Material.YELLOW_WALL_BANNER
						&& BLOCK.getType() != Material.LIME_WALL_BANNER
						&& BLOCK.getType() != Material.PINK_WALL_BANNER
						&& BLOCK.getType() != Material.GRAY_WALL_BANNER
						&& BLOCK.getType() != Material.LIGHT_GRAY_WALL_BANNER
						&& BLOCK.getType() != Material.CYAN_WALL_BANNER
						&& BLOCK.getType() != Material.PURPLE_WALL_BANNER
						&& BLOCK.getType() != Material.BLUE_WALL_BANNER
						&& BLOCK.getType() != Material.BROWN_WALL_BANNER
						&& BLOCK.getType() != Material.GREEN_WALL_BANNER
						&& BLOCK.getType() != Material.RED_WALL_BANNER
						&& BLOCK.getType() != Material.BLACK_WALL_BANNER
						&& BLOCK.getType() != Material.ACACIA_SAPLING
						&& BLOCK.getType() != Material.WHITE_BANNER
						&& BLOCK.getType() != Material.ORANGE_BANNER
						&& BLOCK.getType() != Material.MAGENTA_BANNER
						&& BLOCK.getType() != Material.LIGHT_BLUE_BANNER
						&& BLOCK.getType() != Material.YELLOW_BANNER
						&& BLOCK.getType() != Material.LIME_BANNER
						&& BLOCK.getType() != Material.PINK_BANNER
						&& BLOCK.getType() != Material.GRAY_BANNER
						&& BLOCK.getType() != Material.LIGHT_GRAY_BANNER
						&& BLOCK.getType() != Material.CYAN_BANNER
						&& BLOCK.getType() != Material.PURPLE_BANNER
						&& BLOCK.getType() != Material.BLUE_BANNER
						&& BLOCK.getType() != Material.BROWN_BANNER
						&& BLOCK.getType() != Material.GREEN_BANNER
						&& BLOCK.getType() != Material.RED_BANNER
						&& BLOCK.getType() != Material.BLACK_BANNER
						&& BLOCK.getType() != Material.WHITE_WALL_BANNER
						&& BLOCK.getType() != Material.ORANGE_WALL_BANNER
						&& BLOCK.getType() != Material.MAGENTA_WALL_BANNER
						&& BLOCK.getType() != Material.LIGHT_BLUE_WALL_BANNER
						&& BLOCK.getType() != Material.YELLOW_WALL_BANNER
						&& BLOCK.getType() != Material.LIME_WALL_BANNER
						&& BLOCK.getType() != Material.PINK_WALL_BANNER
						&& BLOCK.getType() != Material.GRAY_WALL_BANNER
						&& BLOCK.getType() != Material.LIGHT_GRAY_WALL_BANNER
						&& BLOCK.getType() != Material.CYAN_WALL_BANNER
						&& BLOCK.getType() != Material.PURPLE_WALL_BANNER
						&& BLOCK.getType() != Material.BLUE_WALL_BANNER
						&& BLOCK.getType() != Material.BROWN_WALL_BANNER
						&& BLOCK.getType() != Material.GREEN_WALL_BANNER
						&& BLOCK.getType() != Material.RED_WALL_BANNER
						&& BLOCK.getType() != Material.BLACK_WALL_BANNER
						&& BLOCK.getType() != Material.ACACIA_SAPLING
						&& BLOCK.getType() != Material.BIRCH_SAPLING
						&& BLOCK.getType() != Material.DARK_OAK_SAPLING
						&& BLOCK.getType() != Material.JUNGLE_SAPLING
						&& BLOCK.getType() != Material.OAK_SAPLING
						&& BLOCK.getType() != Material.POTTED_ACACIA_SAPLING
						&& BLOCK.getType() != Material.POTTED_BIRCH_SAPLING
						&& BLOCK.getType() != Material.POTTED_DARK_OAK_SAPLING
						&& BLOCK.getType() != Material.POTTED_JUNGLE_SAPLING
						&& BLOCK.getType() != Material.POTTED_OAK_SAPLING
						&& BLOCK.getType() != Material.POTTED_SPRUCE_SAPLING
						&& BLOCK.getType() != Material.SPRUCE_SAPLING
						&& BLOCK.getType() != Material.WHITE_BED
						&& BLOCK.getType() != Material.ORANGE_BED
						&& BLOCK.getType() != Material.MAGENTA_BED
						&& BLOCK.getType() != Material.LIGHT_BLUE_BED
						&& BLOCK.getType() != Material.YELLOW_BED
						&& BLOCK.getType() != Material.LIME_BED
						&& BLOCK.getType() != Material.PINK_BED
						&& BLOCK.getType() != Material.GRAY_BED
						&& BLOCK.getType() != Material.LIGHT_GRAY_BED
						&& BLOCK.getType() != Material.CYAN_BED
						&& BLOCK.getType() != Material.PURPLE_BED
						&& BLOCK.getType() != Material.BLUE_BED
						&& BLOCK.getType() != Material.BROWN_BED
						&& BLOCK.getType() != Material.GREEN_BED
						&& BLOCK.getType() != Material.RED_BED
						&& BLOCK.getType() != Material.BLACK_BED
						&& BLOCK.getType() != Material.ACACIA_DOOR
						&& BLOCK.getType() != Material.BIRCH_DOOR
						&& BLOCK.getType() != Material.DARK_OAK_DOOR
						&& BLOCK.getType() != Material.IRON_DOOR
						&& BLOCK.getType() != Material.JUNGLE_DOOR
						&& BLOCK.getType() != Material.OAK_DOOR
						&& BLOCK.getType() != Material.SPRUCE_DOOR
						&& BLOCK.getType() != Material.ACACIA_TRAPDOOR
						&& BLOCK.getType() != Material.BIRCH_TRAPDOOR
						&& BLOCK.getType() != Material.DARK_OAK_TRAPDOOR
						&& BLOCK.getType() != Material.IRON_TRAPDOOR
						&& BLOCK.getType() != Material.JUNGLE_TRAPDOOR
						&& BLOCK.getType() != Material.OAK_TRAPDOOR
						&& BLOCK.getType() != Material.SPRUCE_TRAPDOOR
						&& !isPortalBlock(BLOCK)
						&& !isTreasureChestBlock(BLOCK)
						&& !blocksToRestore.containsKey(BLOCK.getLocation())
						&& BLOCK.getType().isSolid()
						&& a(bUp)) {
					blocksToRestore.put(BLOCK.getLocation(), BLOCK.getType().toString() + "," + BLOCK.getData());
					for (Player player : BLOCK.getLocation().getWorld().getPlayers())
						player.sendBlockChange(BLOCK.getLocation(),Bukkit.createBlockData(NEW_TYPE));
					Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
						@Override
						public void run() {
							restoreBlockAt(BLOCK.getLocation());
						}
					}, TICK_DELAY);
				}

			}
		});
	}

	/**
	 * Checks if a block is part of a Treasure Chest.
	 *
	 * @param block The block to check.
	 * @return {@code true} if yes, otherwise {@code false}.
	 */
	public static boolean isTreasureChestBlock(Block block) {
		return treasureBlocks.contains(block);
	}


	private static boolean a(Block b) {
		if (b.getType() == Material.AIR
				|| b.getType().isSolid())
			return true;
		return false;
	}

	/**
	 * Checks if a block is part of a Nether Portal.
	 *
	 * @param b The block to check
	 * @return {@code true} if a block is part of a Nether Portal, otherwise {@code false}.
	 */
	public static boolean isPortalBlock(Block b) {
		for (BlockFace face : BlockFace.values())
			if (b.getRelative(face).getType() == Material.END_PORTAL)
				return true;
		return false;
	}


}
