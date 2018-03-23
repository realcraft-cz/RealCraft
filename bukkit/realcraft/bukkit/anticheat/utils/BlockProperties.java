package realcraft.bukkit.anticheat.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BlockProperties {

	protected static final Map<Material, BlockProps> blocks = new HashMap<Material, BlockProps>();
	protected static Map<Material, ToolProps> tools = new LinkedHashMap<Material, ToolProps>(50, 0.5f);

	public static long getBreakingDuration(final Material type,final Player player){
		return getBreakingDuration(type,player.getInventory().getItemInMainHand(),player,player.getEyeHeight(),player.getLocation());
	}

	public static long getBreakingDuration(final Material type,final ItemStack itemInHand,final Player player,final double eyeHeight,final Location location){
		final double haste = getPotionEffectAmplifier(player,PotionEffectType.FAST_DIGGING);
		int efficiency = 0;
		if(itemInHand.containsEnchantment(Enchantment.DIG_SPEED)) efficiency = itemInHand.getEnchantmentLevel(Enchantment.DIG_SPEED);
		return getBreakingDuration(type,getBlockProps(type),getToolProps(itemInHand),player,eyeHeight,location,efficiency,haste);
	}

	public static long getBreakingDuration(final Material type,final BlockProps blockProps,final ToolProps toolProps,final Player player,final double eyeHeight,final Location location,final int efficiency,final double haste){
		final long dur = getBreakingDurationNoHaste(type,blockProps,toolProps,player,eyeHeight,location,efficiency);
		return (haste > 0 ? (long)(Math.pow(0.8,haste)*dur) : dur);
	}

	public static long getBreakingDurationNoHaste(final Material type,final BlockProps blockProps,final ToolProps toolProps,final Player player,final double eyeHeight,final Location location,final int efficiency){
		boolean isValidTool = isValidTool(type,blockProps,toolProps,efficiency);
		if (efficiency > 0) {
            // Workaround until something better is found..
            // TODO: Re-evaluate.
            if (type == Material.LEAVES || type == Material.LEAVES_2 || blockProps == glassType){
                if (efficiency == 1) {
                    return 100;
                }
                else {
                    return 0; // insta break.
                }
            }
            else if (blockProps == chestType) {
                // TODO: The no tool time might be reference anyway for some block types.
                return (long) ((double )blockProps.breakingTimes[0] / 5f / efficiency);
            }
        }

		long duration;

        if (isValidTool) {
            // appropriate tool
            duration = blockProps.breakingTimes[toolProps.materialBase.index];
            if (efficiency > 0) {
                duration = (long) (duration / blockProps.efficiencyMod);
            }
        }
        else {
            // Inappropriate tool.
            duration = blockProps.breakingTimes[0];
            // Swords are always appropriate.
            if (toolProps.toolType == ToolType.SWORD) {
                duration = (long) (duration / 1.5f);
            }
        }

        if (toolProps.toolType == ToolType.SHEARS) {
            // (Note: shears are not in the block props, anywhere)
            // Treat these extra (partly experimental):
            if (type == Material.WEB) {
                duration = 400;
                isValidTool = true;
            }
            else if (type == Material.WOOL) {
                duration = 240;
                isValidTool = true;
            }
            else if (type == Material.LEAVES || type == Material.LEAVES_2) {
                duration = 20;
                isValidTool = true;
            }
            else if (type == Material.VINE) {
                duration = 300;
                isValidTool = true;
            }
        }
        else if (type == Material.VINE && toolProps.toolType == ToolType.AXE) {
            isValidTool = true;
            if (toolProps.materialBase == MaterialBase.WOOD || toolProps.materialBase == MaterialBase.STONE) {
                duration = 100;
            }
            else {
                duration = 0;
            }
        }

        if (isValidTool || blockProps.tool.toolType == ToolType.NONE) {
            float mult = 1f;

            // Efficiency level.
            if (efficiency > 0) {
                // Workarounds ...
                if (type == Material.WOODEN_DOOR && toolProps.toolType != ToolType.AXE) {
                    // Heck [Cleanup pending]...
                    switch (efficiency) {
                        case 1:
                            return (long) (mult * 1500);
                        case 2:
                            return (long) (mult * 750);
                        case 3:
                            return (long) (mult * 450);
                        case 4:
                            return (long) (mult * 250);
                        case 5:
                            return (long) (mult * 150);
                    }
                }
                // This seems roughly correct.
                for (int i = 0; i < efficiency; i++) {
                    duration /= 1.33; // Matches well with obsidian.
                }
                // Formula from MC wiki.
                // TODO: Formula from mc wiki does not match well (too fast for obsidian).
                //				duration /= (1.0 + 0.5 * efficiency);

                // More Workarounds:
                // TODO: Consider checking a generic workaround (based on duration, assuming some dig packets lost, proportional to duration etc.).
                if (toolProps.materialBase == MaterialBase.WOOD) {
                    if (toolProps.toolType == ToolType.PICKAXE && (blockProps == ironDoorType || blockProps == dispenserType)) {
                        // Special correction.
                        // TODO: Uncomfortable: hide this in the blocks by some flags / other type of workarounds !
                        if (blockProps == dispenserType) {
                            duration = (long) (duration / 1.5 - (efficiency - 1) * 60);
                        }
                        else if (blockProps == ironDoorType) {
                            duration = (long) (duration / 1.5 - (efficiency - 1) * 100);
                        }
                    }
                    else if (type == Material.LOG) {
                        duration -= efficiency >= 4 ? 250 : 400;
                    }
                    else if (blockProps.tool.toolType == toolProps.toolType) {
                        duration -= 250;
                    }
                    else {
                        duration -= efficiency * 30;
                    }

                }
                else if (toolProps.materialBase == MaterialBase.STONE) {
                    if (type == Material.LOG) {
                        duration -= 100;
                    }
                }
            }
        }
        // Post/legacy workarounds for efficiency tools ("improper").
        if (efficiency > 0 && !isValidTool) {
            if (!isValidTool && type == Material.MELON_BLOCK) {
                // Fall back to pre-1.8 behavior.
                // 450, 200 , 100 , 50 , 0
                duration = Math.min(duration, 450 / (long) Math.pow(2, efficiency - 1));
            }
        }
		return duration;
	}

	private static final double getPotionEffectAmplifier(final Player player,final PotionEffectType type){
		if(!player.hasPotionEffect(type)){
			return Double.NEGATIVE_INFINITY;
		}
		final Collection<PotionEffect> effects = player.getActivePotionEffects();
		double max = Double.NEGATIVE_INFINITY;
		for(final PotionEffect effect : effects){
			if(effect.getType().equals(type)){
				max = Math.max(max,effect.getAmplifier());
			}
		}
		return max;
	}

	private static final BlockProps getBlockProps(final Material type){
		if(type == null) return defaultBlockProps;
		return blocks.get(type);
	}

	private static final ToolProps getToolProps(final ItemStack stack){
		if(stack == null) return noTool;
		return getToolProps(stack.getType());
	}

	private static final ToolProps getToolProps(final Material type){
		ToolProps props = tools.get(type);
		if(props == null) props = noTool;
		return props;
	}

	private static boolean isValidTool(final Material blockId, final BlockProps blockProps, final ToolProps toolProps, final int efficiency){
        boolean isValidTool = (blockProps != null && blockProps.tool.toolType == toolProps.toolType);

        if (!isValidTool && efficiency > 0) {
            if (blockId == Material.SNOW) {
                return toolProps.toolType == ToolType.SPADE;
            }
            if (blockId == Material.WOOL) {
                return true;
            }
            if (blockId == Material.WOODEN_DOOR) {
                return true;
            }
            if (blockProps.hardness <= 2
                    && (blockProps.tool.toolType == ToolType.AXE
                    || blockProps.tool.toolType == ToolType.SPADE
                    || (blockProps.hardness < 0.8 && (blockId != Material.NETHERRACK && blockId != Material.SNOW && blockId != Material.SNOW_BLOCK && blockId != Material.STONE_PLATE)))) {
                // Also roughly.
                return true;
            }
        }
        return isValidTool;
    }

	static {
		tools.clear();
        tools.put(Material.WOOD_SWORD, new ToolProps(ToolType.SWORD, MaterialBase.WOOD));
        tools.put(Material.WOOD_SPADE, new ToolProps(ToolType.SPADE, MaterialBase.WOOD));
        tools.put(Material.WOOD_PICKAXE, new ToolProps(ToolType.PICKAXE, MaterialBase.WOOD));
        tools.put(Material.WOOD_AXE, new ToolProps(ToolType.AXE, MaterialBase.WOOD));

        tools.put(Material.STONE_SWORD, new ToolProps(ToolType.SWORD, MaterialBase.STONE));
        tools.put(Material.STONE_SPADE, new ToolProps(ToolType.SPADE, MaterialBase.STONE));
        tools.put(Material.STONE_PICKAXE, new ToolProps(ToolType.PICKAXE, MaterialBase.STONE));
        tools.put(Material.STONE_AXE, new ToolProps(ToolType.AXE, MaterialBase.STONE));

        tools.put(Material.IRON_SWORD, new ToolProps(ToolType.SWORD, MaterialBase.IRON));
        tools.put(Material.IRON_SPADE, new ToolProps(ToolType.SPADE, MaterialBase.IRON));
        tools.put(Material.IRON_PICKAXE, new ToolProps(ToolType.PICKAXE, MaterialBase.IRON));
        tools.put(Material.IRON_AXE, new ToolProps(ToolType.AXE, MaterialBase.IRON));

        tools.put(Material.DIAMOND_SWORD, new ToolProps(ToolType.SWORD, MaterialBase.DIAMOND));
        tools.put(Material.DIAMOND_SPADE, new ToolProps(ToolType.SPADE, MaterialBase.DIAMOND));
        tools.put(Material.DIAMOND_PICKAXE, new ToolProps(ToolType.PICKAXE, MaterialBase.DIAMOND));
        tools.put(Material.DIAMOND_AXE, new ToolProps(ToolType.AXE, MaterialBase.DIAMOND));

        tools.put(Material.GOLD_SWORD, new ToolProps(ToolType.SWORD, MaterialBase.GOLD));
        tools.put(Material.GOLD_SPADE, new ToolProps(ToolType.SPADE, MaterialBase.GOLD));
        tools.put(Material.GOLD_PICKAXE, new ToolProps(ToolType.PICKAXE, MaterialBase.GOLD));
        tools.put(Material.GOLD_AXE, new ToolProps(ToolType.AXE, MaterialBase.GOLD));

        tools.put(Material.SHEARS, new ToolProps(ToolType.SHEARS, MaterialBase.NONE));
	}

	public static final ToolProps noTool = new ToolProps(ToolType.NONE, MaterialBase.NONE);

    /** The Constant woodSword. */
    public static final ToolProps woodSword = new ToolProps(ToolType.SWORD, MaterialBase.WOOD);

    /** The Constant woodSpade. */
    public static final ToolProps woodSpade = new ToolProps(ToolType.SPADE, MaterialBase.WOOD);

    /** The Constant woodPickaxe. */
    public static final ToolProps woodPickaxe = new ToolProps(ToolType.PICKAXE, MaterialBase.WOOD);

    /** The Constant woodAxe. */
    public static final ToolProps woodAxe = new ToolProps(ToolType.AXE, MaterialBase.WOOD);

    /** The Constant stonePickaxe. */
    public static final ToolProps stonePickaxe = new ToolProps(ToolType.PICKAXE, MaterialBase.STONE);

    /** The Constant ironPickaxe. */
    public static final ToolProps ironPickaxe = new ToolProps(ToolType.PICKAXE, MaterialBase.IRON);

    /** The Constant diamondPickaxe. */
    public static final ToolProps diamondPickaxe = new ToolProps(ToolType.PICKAXE, MaterialBase.DIAMOND);

    /** Times for instant breaking. */
    public static final long[] instantTimes = secToMs(0);

    /** The Constant leafTimes. */
    public static final long[] leafTimes = secToMs(0.3);

    /** The glass times. */
    public static long[] glassTimes = secToMs(0.45);

    /** The Constant gravelTimes. */
    public static final long[] gravelTimes = secToMs(0.9, 0.45, 0.25, 0.15, 0.15, 0.1);

    /** The rails times. */
    public static long[] railsTimes = secToMs(1.05, 0.55, 0.3, 0.2, 0.15, 0.1);

    /** The Constant woodTimes. */
    public static final long[] woodTimes = secToMs(3, 1.5, 0.75, 0.5, 0.4, 0.25);

    /** The Constant indestructibleTimes. */
    private static final long[] indestructibleTimes = new long[] {Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE};

    /** Instantly breakable. */
    public static final BlockProps instantType = new BlockProps(noTool, 0, instantTimes);

    /** The Constant glassType. */
    public static final BlockProps glassType = new BlockProps(noTool, 0.3f, glassTimes, 2f);

    /** The Constant gravelType. */
    public static final BlockProps gravelType = new BlockProps(woodSpade, 0.6f, gravelTimes);
    /** Stone type blocks. */
    public static final BlockProps stoneType = new BlockProps(woodPickaxe, 1.5f);

    /** The Constant woodType. */
    public static final BlockProps woodType = new BlockProps(woodAxe, 2, woodTimes);

    /** The Constant brickType. */
    public static final BlockProps brickType = new BlockProps(woodPickaxe, 2);

    /** The Constant coalType. */
    public static final BlockProps coalType = new BlockProps(woodPickaxe, 3);

    /** The Constant goldBlockType. */
    public static final BlockProps goldBlockType = new BlockProps(woodPickaxe, 3, secToMs(15, 7.5, 3.75, 0.7, 0.55, 1.2));

    /** The Constant ironBlockType. */
    public static final BlockProps ironBlockType = new BlockProps(woodPickaxe, 5, secToMs(25, 12.5, 1.875, 1.25, 0.95, 2.0));

    /** The Constant diamondBlockType. */
    public static final BlockProps diamondBlockType = new BlockProps(woodPickaxe, 5, secToMs(25, 12.5, 6.0, 1.25, 0.95, 2.0));

    /** The Constant hugeMushroomType. */
    public static final BlockProps hugeMushroomType = new BlockProps(woodAxe, 0.2f, secToMs(0.3, 0.15, 0.1, 0.05, 0.05, 0.05));

    /** The Constant leafType. */
    public static final BlockProps leafType = new BlockProps(noTool, 0.2f, leafTimes);

    /** The Constant sandType. */
    public static final BlockProps sandType = new BlockProps(woodSpade, 0.5f, secToMs(0.75, 0.4, 0.2, 0.15, 0.1, 0.1));

    /** The Constant leverType. */
    public static final BlockProps leverType = new BlockProps(noTool, 0.5f, secToMs(0.75));

    /** The Constant sandStoneType. */
    public static final BlockProps sandStoneType = new BlockProps(woodPickaxe, 0.8f);

    /** The Constant chestType. */
    public static final BlockProps chestType = new BlockProps(woodAxe, 2.5f, secToMs(3.75, 1.9, 0.95, 0.65, 0.5, 0.35));

    /** The Constant woodDoorType. */
    public static final BlockProps woodDoorType = new BlockProps(woodAxe, 3.0f, secToMs(4.5, 2.25, 1.15, 0.75, 0.6, 0.4));

    /** The Constant dispenserType. */
    public static final BlockProps dispenserType = new BlockProps(woodPickaxe, 3.5f);

    /** The Constant ironDoorType. */
    public static final BlockProps ironDoorType = new BlockProps(woodPickaxe, 5);

    /** The Constant indestructibleType. */
    public static final BlockProps indestructibleType = new BlockProps(noTool, -1f, indestructibleTimes);

    /** Returned if unknown. */
    private static BlockProps defaultBlockProps = instantType;

    protected static final Material[] instantMat = new Material[]{
        // Named in wiki.
        Material.CROPS,
        Material.TRIPWIRE_HOOK, Material.TRIPWIRE,
        Material.TORCH,
        Material.TNT,
        Material.SUGAR_CANE_BLOCK,
        Material.SAPLING,
        Material.RED_ROSE, Material.YELLOW_FLOWER,
        Material.REDSTONE_WIRE,
        Material.REDSTONE_TORCH_ON, Material.REDSTONE_TORCH_OFF,
        Material.DIODE_BLOCK_ON, Material.DIODE_BLOCK_OFF,
        Material.PUMPKIN_STEM,
        Material.NETHER_WARTS,
        Material.BROWN_MUSHROOM, Material.RED_MUSHROOM,
        Material.MELON_STEM,
        Material.WATER_LILY,
        Material.LONG_GRASS,
        Material.FIRE,
        Material.DEAD_BUSH,
        //
        Material.CROPS,

        // 1.4
        Material.COMMAND,
        Material.FLOWER_POT,
        Material.CARROT,
        Material.POTATO,
    };

    static {
		for (final Material mat : instantMat) {
            setBlock(mat, instantType);
        }
        // Leaf type
        for (Material mat : new Material[]{
                Material.LEAVES, Material.BED_BLOCK}) {
            setBlock(mat, leafType);
        }
        // Huge mushroom type (...)
        for (Material mat : new Material[]{
                Material.HUGE_MUSHROOM_1, Material.HUGE_MUSHROOM_2,
                Material.VINE, Material.COCOA}) {
            setBlock(mat, hugeMushroomType);
        }

        setBlock(Material.SNOW, new BlockProps(getToolProps(Material.WOOD_SPADE), 0.1f, secToMs(0.5, 0.1, 0.05, 0.05, 0.05, 0.05)));
        setBlock(Material.SNOW_BLOCK, new BlockProps(getToolProps(Material.WOOD_SPADE), 0.1f, secToMs(1, 0.15, 0.1, 0.05, 0.05, 0.05)));
        for (Material mat : new Material[]{
                Material.REDSTONE_LAMP_ON, Material.REDSTONE_LAMP_OFF,
                Material.GLOWSTONE, Material.GLASS,
        }) {
            setBlock(mat, glassType);
        }
        setBlock(Material.THIN_GLASS, glassType);
        setBlock(Material.NETHERRACK, new BlockProps(woodPickaxe, 0.4f, secToMs(2, 0.3, 0.15, 0.1, 0.1, 0.05)));
        setBlock(Material.LADDER, new BlockProps(noTool, 0.4f, secToMs(0.6), 2.5f));
        setBlock(Material.CACTUS, new BlockProps(noTool, 0.4f, secToMs(0.6)));
        setBlock(Material.WOOD_PLATE, new BlockProps(woodAxe, 0.5f, secToMs(0.75, 0.4, 0.2, 0.15, 0.1, 0.1)));
        setBlock(Material.STONE_PLATE, new BlockProps(woodPickaxe, 0.5f, secToMs(2.5, 0.4, 0.2, 0.15, 0.1, 0.07)));
        setBlock(Material.SAND, sandType);
        setBlock(Material.SOUL_SAND, sandType);
        for (Material mat: new Material[]{Material.LEVER, Material.PISTON_BASE,
                Material.PISTON_EXTENSION, Material.PISTON_STICKY_BASE,
                Material.STONE_BUTTON, Material.PISTON_MOVING_PIECE}) {
            setBlock(mat, leverType);
        }
        //		setBlock(Material.ICE, new BlockProps(woodPickaxe, 0.5f, secToMs(2.5, 0.4, 0.2, 0.15, 0.1, 0.1)));
        setBlock(Material.ICE, new BlockProps(woodPickaxe, 0.5f, secToMs(0.7, 0.35, 0.18, 0.12, 0.09, 0.06 )));
        setBlock(Material.DIRT, sandType);
        setBlock(Material.CAKE_BLOCK, leverType);
        setBlock(Material.BREWING_STAND, new BlockProps(woodPickaxe, 0.5f, secToMs(2.5, 0.4, 0.2, 0.15, 0.1, 0.1)));
        setBlock(Material.SPONGE, new BlockProps(noTool, 0.6f, secToMs(0.9)));
        for (Material mat : new Material[]{
                Material.MYCEL, Material.GRAVEL, Material.GRASS, Material.SOIL,
                Material.CLAY,
        }) {
            setBlock(mat, gravelType);
        }
        for (Material mat : new Material[]{
                Material.RAILS, Material.POWERED_RAIL, Material.DETECTOR_RAIL,
        }) {
            setBlock(mat, new BlockProps(woodPickaxe, 0.7f, railsTimes));
        }
        setBlock(Material.MONSTER_EGGS, new BlockProps(noTool, 0.75f, secToMs(1.15)));
        setBlock(Material.WOOL, new BlockProps(noTool, 0.8f, secToMs(1.2), 3f));
        setBlock(Material.SANDSTONE, sandStoneType);
        setBlock(Material.SANDSTONE_STAIRS, sandStoneType);
        for (Material mat : new Material[]{
                Material.STONE, Material.SMOOTH_BRICK, Material.SMOOTH_STAIRS,
        }) {
            setBlock(mat,  stoneType);
        }
        setBlock(Material.NOTE_BLOCK, new BlockProps(woodAxe, 0.8f, secToMs(1.2, 0.6, 0.3, 0.2, 0.15, 0.1)));
        final BlockProps pumpkinType = new BlockProps(woodAxe, 1, secToMs(1.5, 0.75, 0.4, 0.25, 0.2, 0.15));
        setBlock(Material.WALL_SIGN, pumpkinType);
        setBlock(Material.SIGN_POST, pumpkinType);
        setBlock(Material.PUMPKIN, pumpkinType);
        setBlock(Material.JACK_O_LANTERN, pumpkinType);
        setBlock(Material.MELON_BLOCK, new BlockProps(noTool, 1, secToMs(1.45), 3));
        setBlock(Material.BOOKSHELF, new BlockProps(woodAxe, 1.5f, secToMs(2.25, 1.15, 0.6, 0.4, 0.3, 0.2)));
        for (Material mat : new Material[]{
                Material.WOOD_STAIRS, Material.WOOD, Material.WOOD_STEP, Material.LOG,
                Material.FENCE, Material.FENCE_GATE, Material.JUKEBOX,
                Material.JUNGLE_WOOD_STAIRS, Material.SPRUCE_WOOD_STAIRS,
                Material.BIRCH_WOOD_STAIRS,
                Material.WOOD_DOUBLE_STEP, // ?
                // double slabs ?
        }) {
            setBlock(mat,  woodType);
        }
        for (Material mat : new Material[]{
                Material.COBBLESTONE_STAIRS, Material.COBBLESTONE,
                Material.NETHER_BRICK, Material.NETHER_BRICK_STAIRS, Material.NETHER_FENCE,
                Material.CAULDRON, Material.BRICK, Material.BRICK_STAIRS,
                Material.MOSSY_COBBLESTONE, Material.BRICK, Material.BRICK_STAIRS,
                Material.STEP, Material.DOUBLE_STEP, // ?

        }) {
            setBlock(mat,  brickType);
        }
        setBlock(Material.WORKBENCH, chestType);
        setBlock(Material.CHEST, chestType);
        setBlock(Material.WOODEN_DOOR, woodDoorType);
        setBlock(Material.TRAP_DOOR, woodDoorType);
        for (Material mat : new Material[]{
                Material.ENDER_STONE, Material.COAL_ORE,

        }) {
            setBlock(mat,  coalType);
        }
        setBlock(Material.DRAGON_EGG, new BlockProps(noTool, 3f, secToMs(4.5))); // Former: coalType.
        final long[] ironTimes = secToMs(15, 15, 1.15, 0.75, 0.6, 15);
        final BlockProps ironType = new BlockProps(stonePickaxe, 3, ironTimes);
        for (Material mat : new Material[]{
                Material.LAPIS_ORE, Material.LAPIS_BLOCK, Material.IRON_ORE,
        }) {
            setBlock(mat,  ironType);
        }
        final long[] diamondTimes = secToMs(15, 15, 15, 0.75, 0.6, 15);
        final BlockProps diamondType = new BlockProps(ironPickaxe, 3, diamondTimes);
        for (Material mat : new Material[]{
                Material.REDSTONE_ORE, Material.GLOWING_REDSTONE_ORE,
                Material.EMERALD_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE,
        }) {
            setBlock(mat,  diamondType);
        }
        setBlock(Material.GOLD_BLOCK,  goldBlockType);
        setBlock(Material.FURNACE, dispenserType);
        setBlock(Material.BURNING_FURNACE, dispenserType);
        setBlock(Material.DISPENSER, dispenserType);
        setBlock(Material.WEB, new BlockProps(woodSword, 4, secToMs(20, 0.4, 0.4, 0.4, 0.4, 0.4)));

        for (Material mat : new Material[]{
                Material.MOB_SPAWNER, Material.IRON_DOOR_BLOCK,
                Material.IRON_FENCE, Material.ENCHANTMENT_TABLE,
                Material.EMERALD_BLOCK,
        }) {
            setBlock(mat, ironDoorType);
        }
        setBlock(Material.IRON_BLOCK, ironBlockType);
        setBlock(Material.DIAMOND_BLOCK, diamondBlockType);
        setBlock(Material.ENDER_CHEST, new BlockProps(woodPickaxe, 22.5f));
        setBlock(Material.OBSIDIAN, new BlockProps(diamondPickaxe, 50, secToMs(250, 125, 62.5, 41.6, 9.4, 20.8)));

        // More 1.4 (not insta).
        // TODO: Either move all to an extra setup class, or integrate above.
        setBlock(Material.BEACON, new BlockProps(noTool, 25f, secToMs(4.45))); // TODO
        setBlock(Material.COBBLE_WALL, brickType);
        setBlock(Material.WOOD_BUTTON, leverType);
        setBlock(Material.SKULL, new BlockProps(noTool, 8.5f, secToMs(1.45))); // TODO
        setBlock(Material.ANVIL, new BlockProps(woodPickaxe, 5f)); // TODO

        // Indestructible.
        for (Material mat : new Material[]{
                Material.AIR, Material.ENDER_PORTAL, Material.ENDER_PORTAL_FRAME,
                Material.PORTAL, Material.LAVA, Material.WATER, Material.BEDROCK,
                Material.STATIONARY_LAVA, Material.STATIONARY_WATER,
        }) {
            setBlock(mat, indestructibleType);
        }
	}

	public static enum ToolType {
		NONE,
		SWORD,
		SHEARS,
		SPADE,
		AXE,
		PICKAXE,
	}

	public static enum MaterialBase {

        /** The none. */
        NONE(0, 1f),

        /** The wood. */
        WOOD(1, 2f),

        /** The stone. */
        STONE(2, 4f),

        /** The iron. */
        IRON(3, 6f),

        /** The diamond. */
        DIAMOND(4, 8f),

        /** The gold. */
        GOLD(5, 12f);
        /** Index for array. */
        public final int index;

        /** The break multiplier. */
        public final float breakMultiplier;

        /**
         * Instantiates a new material base.
         *
         * @param index
         *            the index
         * @param breakMultiplier
         *            the break multiplier
         */
        private MaterialBase(int index, float breakMultiplier) {
            this.index = index;
            this.breakMultiplier = breakMultiplier;
        }

        /**
         * Gets the by id.
         *
         * @param id
         *            the id
         * @return the by id
         * @deprecated Nothing to do with ids.
         */
        @Deprecated
        public static final MaterialBase getById(final int id) {
            return getByIndex(id);
        }

        /**
         * Get the index of this base material within the relevant materials or
         * breaking times array.
         *
         * @param index
         * @return
         */
        public static final MaterialBase getByIndex(final int index) {
            for (final MaterialBase base : MaterialBase.values()) {
                if (base.index == index) {
                    return base;
                }
            }
            throw new IllegalArgumentException("Bad index: " + index);
        }

    }

	public static class ToolProps {

		public final ToolType toolType;
		public final MaterialBase materialBase;

		public ToolProps(ToolType toolType, MaterialBase materialBase){
			this.toolType = toolType;
			this.materialBase = materialBase;
		}

		public void validate(){
			if(toolType == null){
				throw new IllegalArgumentException("ToolType must not be null.");
			}
			if(materialBase == null){
				throw new IllegalArgumentException("MaterialBase must not be null");
			}
		}
	}

	public static class BlockProps {

        public final ToolProps tool;
        public final long[] breakingTimes;
        public final float hardness;
        public final float efficiencyMod;

        public BlockProps(ToolProps tool, float hardness) {
            this(tool, hardness, 1);
        }

        public BlockProps(ToolProps tool, float hardness, float efficiencyMod) {
            this.tool = tool;
            this.hardness = hardness;
            breakingTimes = new long[6];
            for (int i = 0; i < 6; i++) {
                final float multiplier;
                if (tool.materialBase == null) {
                    multiplier = 1f;
                }
                else if (i < tool.materialBase.index) {
                    multiplier = 1f;
                }
                else {
                    multiplier = MaterialBase.getById(i).breakMultiplier * 3.33f;
                }
                breakingTimes[i] = (long) (1000f * 5f * hardness / multiplier);
            }
            this.efficiencyMod = efficiencyMod;
        }

        public BlockProps(ToolProps tool, float hardness, long[] breakingTimes) {
            this(tool, hardness, breakingTimes, 1f);
        }

        public BlockProps(ToolProps tool, float hardness, long[] breakingTimes, float efficiencyMod) {
            this.tool = tool;
            this.breakingTimes = breakingTimes;
            this.hardness = hardness;
            this.efficiencyMod = efficiencyMod;
        }

        public void validate() {
            if (breakingTimes == null) {
                throw new IllegalArgumentException("Breaking times must not be null.");
            }
            if (breakingTimes.length != 6) {
                throw new IllegalArgumentException("Breaking times length must match the number of available tool types (6).");
            }
            if (tool == null)  {
                throw new IllegalArgumentException("Tool must not be null.");
            }
            tool.validate();
        }
    }

	private static void setBlock(Material material,BlockProps props){
		blocks.put(material, props);
	}

	public static long[] secToMs(final double s1, final double s2, final double s3, final double s4, final double s5, final double s6){
		return new long[] { (long) (s1 * 1000d), (long) (s2 * 1000d), (long) (s3 * 1000d), (long) (s4 * 1000d), (long) (s5 * 1000d), (long) (s6 * 1000d) };
	}

	public static long[] secToMs(final double s1){
		final long v = (long) (s1 * 1000d);
		return new long[]{v, v, v, v, v, v};
	}
}