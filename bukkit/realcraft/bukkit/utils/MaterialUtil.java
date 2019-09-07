package realcraft.bukkit.utils;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;

public class MaterialUtil {

	public static DyeColor getDyeColor(Material type){
		for(DyeColor color : DyeColor.values()){
			if(type.toString().startsWith(color.toString())) return color;
		}
		return DyeColor.WHITE;
	}

	public static EntityType getSpawnEggType(Material type){
		try {
			return EntityType.valueOf(type.toString().split("_SPAWN_EGG")[0]);
		} catch (Exception e){
		}
		return null;
	}

	public static boolean isSpawnEgg(Material type){
		return type.toString().contains("_SPAWN_EGG");
	}

	public static Material getSpawnEgg(EntityType type){
		return Material.getMaterial(type.toString()+"_SPAWN_EGG");
	}

	public static Material getBanner(DyeColor color){
		switch(color){
			case WHITE: return Material.WHITE_BANNER;
			case ORANGE: return Material.ORANGE_BANNER;
			case MAGENTA: return Material.MAGENTA_BANNER;
			case LIGHT_BLUE: return Material.LIGHT_BLUE_BANNER;
			case YELLOW: return Material.YELLOW_BANNER;
			case LIME: return Material.LIME_BANNER;
			case PINK: return Material.PINK_BANNER;
			case GRAY: return Material.GRAY_BANNER;
			case LIGHT_GRAY: return Material.LIGHT_GRAY_BANNER;
			case CYAN: return Material.CYAN_BANNER;
			case PURPLE: return Material.PURPLE_BANNER;
			case BLUE: return Material.BLUE_BANNER;
			case BROWN: return Material.BROWN_BANNER;
			case GREEN: return Material.GREEN_BANNER;
			case RED: return Material.RED_BANNER;
			case BLACK: return Material.BLACK_BANNER;
		}
		return Material.WHITE_BANNER;
	}

	public static Material getBed(DyeColor color){
		switch(color){
			case WHITE: return Material.WHITE_BED;
			case ORANGE: return Material.ORANGE_BED;
			case MAGENTA: return Material.MAGENTA_BED;
			case LIGHT_BLUE: return Material.LIGHT_BLUE_BED;
			case YELLOW: return Material.YELLOW_BED;
			case LIME: return Material.LIME_BED;
			case PINK: return Material.PINK_BED;
			case GRAY: return Material.GRAY_BED;
			case LIGHT_GRAY: return Material.LIGHT_GRAY_BED;
			case CYAN: return Material.CYAN_BED;
			case PURPLE: return Material.PURPLE_BED;
			case BLUE: return Material.BLUE_BED;
			case BROWN: return Material.BROWN_BED;
			case GREEN: return Material.GREEN_BED;
			case RED: return Material.RED_BED;
			case BLACK: return Material.BLACK_BED;
		}
		return Material.WHITE_BED;
	}

	public static Material getCarpet(DyeColor color){
		switch(color){
			case WHITE: return Material.WHITE_CARPET;
			case ORANGE: return Material.ORANGE_CARPET;
			case MAGENTA: return Material.MAGENTA_CARPET;
			case LIGHT_BLUE: return Material.LIGHT_BLUE_CARPET;
			case YELLOW: return Material.YELLOW_CARPET;
			case LIME: return Material.LIME_CARPET;
			case PINK: return Material.PINK_CARPET;
			case GRAY: return Material.GRAY_CARPET;
			case LIGHT_GRAY: return Material.LIGHT_GRAY_CARPET;
			case CYAN: return Material.CYAN_CARPET;
			case PURPLE: return Material.PURPLE_CARPET;
			case BLUE: return Material.BLUE_CARPET;
			case BROWN: return Material.BROWN_CARPET;
			case GREEN: return Material.GREEN_CARPET;
			case RED: return Material.RED_CARPET;
			case BLACK: return Material.BLACK_CARPET;
		}
		return Material.WHITE_CARPET;
	}

	public static Material getConcrete(DyeColor color){
		switch(color){
			case WHITE: return Material.WHITE_CONCRETE;
			case ORANGE: return Material.ORANGE_CONCRETE;
			case MAGENTA: return Material.MAGENTA_CONCRETE;
			case LIGHT_BLUE: return Material.LIGHT_BLUE_CONCRETE;
			case YELLOW: return Material.YELLOW_CONCRETE;
			case LIME: return Material.LIME_CONCRETE;
			case PINK: return Material.PINK_CONCRETE;
			case GRAY: return Material.GRAY_CONCRETE;
			case LIGHT_GRAY: return Material.LIGHT_GRAY_CONCRETE;
			case CYAN: return Material.CYAN_CONCRETE;
			case PURPLE: return Material.PURPLE_CONCRETE;
			case BLUE: return Material.BLUE_CONCRETE;
			case BROWN: return Material.BROWN_CONCRETE;
			case GREEN: return Material.GREEN_CONCRETE;
			case RED: return Material.RED_CONCRETE;
			case BLACK: return Material.BLACK_CONCRETE;
		}
		return Material.WHITE_CONCRETE;
	}

	public static Material getConcretePowder(DyeColor color){
		switch(color){
			case WHITE: return Material.WHITE_CONCRETE_POWDER;
			case ORANGE: return Material.ORANGE_CONCRETE_POWDER;
			case MAGENTA: return Material.MAGENTA_CONCRETE_POWDER;
			case LIGHT_BLUE: return Material.LIGHT_BLUE_CONCRETE_POWDER;
			case YELLOW: return Material.YELLOW_CONCRETE_POWDER;
			case LIME: return Material.LIME_CONCRETE_POWDER;
			case PINK: return Material.PINK_CONCRETE_POWDER;
			case GRAY: return Material.GRAY_CONCRETE_POWDER;
			case LIGHT_GRAY: return Material.LIGHT_GRAY_CONCRETE_POWDER;
			case CYAN: return Material.CYAN_CONCRETE_POWDER;
			case PURPLE: return Material.PURPLE_CONCRETE_POWDER;
			case BLUE: return Material.BLUE_CONCRETE_POWDER;
			case BROWN: return Material.BROWN_CONCRETE_POWDER;
			case GREEN: return Material.GREEN_CONCRETE_POWDER;
			case RED: return Material.RED_CONCRETE_POWDER;
			case BLACK: return Material.BLACK_CONCRETE_POWDER;
		}
		return Material.WHITE_CONCRETE_POWDER;
	}

	public static Material getGlazedTerracotta(DyeColor color){
		switch(color){
			case WHITE: return Material.WHITE_GLAZED_TERRACOTTA;
			case ORANGE: return Material.ORANGE_GLAZED_TERRACOTTA;
			case MAGENTA: return Material.MAGENTA_GLAZED_TERRACOTTA;
			case LIGHT_BLUE: return Material.LIGHT_BLUE_GLAZED_TERRACOTTA;
			case YELLOW: return Material.YELLOW_GLAZED_TERRACOTTA;
			case LIME: return Material.LIME_GLAZED_TERRACOTTA;
			case PINK: return Material.PINK_GLAZED_TERRACOTTA;
			case GRAY: return Material.GRAY_GLAZED_TERRACOTTA;
			case LIGHT_GRAY: return Material.LIGHT_GRAY_GLAZED_TERRACOTTA;
			case CYAN: return Material.CYAN_GLAZED_TERRACOTTA;
			case PURPLE: return Material.PURPLE_GLAZED_TERRACOTTA;
			case BLUE: return Material.BLUE_GLAZED_TERRACOTTA;
			case BROWN: return Material.BROWN_GLAZED_TERRACOTTA;
			case GREEN: return Material.GREEN_GLAZED_TERRACOTTA;
			case RED: return Material.RED_GLAZED_TERRACOTTA;
			case BLACK: return Material.BLACK_GLAZED_TERRACOTTA;
		}
		return Material.WHITE_GLAZED_TERRACOTTA;
	}

	public static Material getShulkerBox(DyeColor color){
		switch(color){
			case WHITE: return Material.WHITE_SHULKER_BOX;
			case ORANGE: return Material.ORANGE_SHULKER_BOX;
			case MAGENTA: return Material.MAGENTA_SHULKER_BOX;
			case LIGHT_BLUE: return Material.LIGHT_BLUE_SHULKER_BOX;
			case YELLOW: return Material.YELLOW_SHULKER_BOX;
			case LIME: return Material.LIME_SHULKER_BOX;
			case PINK: return Material.PINK_SHULKER_BOX;
			case GRAY: return Material.GRAY_SHULKER_BOX;
			case LIGHT_GRAY: return Material.LIGHT_GRAY_SHULKER_BOX;
			case CYAN: return Material.CYAN_SHULKER_BOX;
			case PURPLE: return Material.PURPLE_SHULKER_BOX;
			case BLUE: return Material.BLUE_SHULKER_BOX;
			case BROWN: return Material.BROWN_SHULKER_BOX;
			case GREEN: return Material.GREEN_SHULKER_BOX;
			case RED: return Material.RED_SHULKER_BOX;
			case BLACK: return Material.BLACK_SHULKER_BOX;
		}
		return Material.WHITE_SHULKER_BOX;
	}

	public static Material getStainedGlass(DyeColor color){
		switch(color){
			case WHITE: return Material.WHITE_STAINED_GLASS;
			case ORANGE: return Material.ORANGE_STAINED_GLASS;
			case MAGENTA: return Material.MAGENTA_STAINED_GLASS;
			case LIGHT_BLUE: return Material.LIGHT_BLUE_STAINED_GLASS;
			case YELLOW: return Material.YELLOW_STAINED_GLASS;
			case LIME: return Material.LIME_STAINED_GLASS;
			case PINK: return Material.PINK_STAINED_GLASS;
			case GRAY: return Material.GRAY_STAINED_GLASS;
			case LIGHT_GRAY: return Material.LIGHT_GRAY_STAINED_GLASS;
			case CYAN: return Material.CYAN_STAINED_GLASS;
			case PURPLE: return Material.PURPLE_STAINED_GLASS;
			case BLUE: return Material.BLUE_STAINED_GLASS;
			case BROWN: return Material.BROWN_STAINED_GLASS;
			case GREEN: return Material.GREEN_STAINED_GLASS;
			case RED: return Material.RED_STAINED_GLASS;
			case BLACK: return Material.BLACK_STAINED_GLASS;
		}
		return Material.WHITE_STAINED_GLASS;
	}

	public static Material getStainedGlassPane(DyeColor color){
		switch(color){
			case WHITE: return Material.WHITE_STAINED_GLASS_PANE;
			case ORANGE: return Material.ORANGE_STAINED_GLASS_PANE;
			case MAGENTA: return Material.MAGENTA_STAINED_GLASS_PANE;
			case LIGHT_BLUE: return Material.LIGHT_BLUE_STAINED_GLASS_PANE;
			case YELLOW: return Material.YELLOW_STAINED_GLASS_PANE;
			case LIME: return Material.LIME_STAINED_GLASS_PANE;
			case PINK: return Material.PINK_STAINED_GLASS_PANE;
			case GRAY: return Material.GRAY_STAINED_GLASS_PANE;
			case LIGHT_GRAY: return Material.LIGHT_GRAY_STAINED_GLASS_PANE;
			case CYAN: return Material.CYAN_STAINED_GLASS_PANE;
			case PURPLE: return Material.PURPLE_STAINED_GLASS_PANE;
			case BLUE: return Material.BLUE_STAINED_GLASS_PANE;
			case BROWN: return Material.BROWN_STAINED_GLASS_PANE;
			case GREEN: return Material.GREEN_STAINED_GLASS_PANE;
			case RED: return Material.RED_STAINED_GLASS_PANE;
			case BLACK: return Material.BLACK_STAINED_GLASS_PANE;
		}
		return Material.WHITE_STAINED_GLASS_PANE;
	}

	public static Material getTerracotta(DyeColor color){
		switch(color){
			case WHITE: return Material.WHITE_TERRACOTTA;
			case ORANGE: return Material.ORANGE_TERRACOTTA;
			case MAGENTA: return Material.MAGENTA_TERRACOTTA;
			case LIGHT_BLUE: return Material.LIGHT_BLUE_TERRACOTTA;
			case YELLOW: return Material.YELLOW_TERRACOTTA;
			case LIME: return Material.LIME_TERRACOTTA;
			case PINK: return Material.PINK_TERRACOTTA;
			case GRAY: return Material.GRAY_TERRACOTTA;
			case LIGHT_GRAY: return Material.LIGHT_GRAY_TERRACOTTA;
			case CYAN: return Material.CYAN_TERRACOTTA;
			case PURPLE: return Material.PURPLE_TERRACOTTA;
			case BLUE: return Material.BLUE_TERRACOTTA;
			case BROWN: return Material.BROWN_TERRACOTTA;
			case GREEN: return Material.GREEN_TERRACOTTA;
			case RED: return Material.RED_TERRACOTTA;
			case BLACK: return Material.BLACK_TERRACOTTA;
		}
		return Material.WHITE_TERRACOTTA;
	}

	public static Material getWallBanner(DyeColor color){
		switch(color){
			case WHITE: return Material.WHITE_WALL_BANNER;
			case ORANGE: return Material.ORANGE_WALL_BANNER;
			case MAGENTA: return Material.MAGENTA_WALL_BANNER;
			case LIGHT_BLUE: return Material.LIGHT_BLUE_WALL_BANNER;
			case YELLOW: return Material.YELLOW_WALL_BANNER;
			case LIME: return Material.LIME_WALL_BANNER;
			case PINK: return Material.PINK_WALL_BANNER;
			case GRAY: return Material.GRAY_WALL_BANNER;
			case LIGHT_GRAY: return Material.LIGHT_GRAY_WALL_BANNER;
			case CYAN: return Material.CYAN_WALL_BANNER;
			case PURPLE: return Material.PURPLE_WALL_BANNER;
			case BLUE: return Material.BLUE_WALL_BANNER;
			case BROWN: return Material.BROWN_WALL_BANNER;
			case GREEN: return Material.GREEN_WALL_BANNER;
			case RED: return Material.RED_WALL_BANNER;
			case BLACK: return Material.BLACK_WALL_BANNER;
		}
		return Material.WHITE_WALL_BANNER;
	}

	public static Material getWool(DyeColor color){
		switch(color){
			case WHITE: return Material.WHITE_WOOL;
			case ORANGE: return Material.ORANGE_WOOL;
			case MAGENTA: return Material.MAGENTA_WOOL;
			case LIGHT_BLUE: return Material.LIGHT_BLUE_WOOL;
			case YELLOW: return Material.YELLOW_WOOL;
			case LIME: return Material.LIME_WOOL;
			case PINK: return Material.PINK_WOOL;
			case GRAY: return Material.GRAY_WOOL;
			case LIGHT_GRAY: return Material.LIGHT_GRAY_WOOL;
			case CYAN: return Material.CYAN_WOOL;
			case PURPLE: return Material.PURPLE_WOOL;
			case BLUE: return Material.BLUE_WOOL;
			case BROWN: return Material.BROWN_WOOL;
			case GREEN: return Material.GREEN_WOOL;
			case RED: return Material.RED_WOOL;
			case BLACK: return Material.BLACK_WOOL;
		}
		return Material.WHITE_WOOL;
	}

	public static boolean isBanner(Material type){
		switch(type){
			case WHITE_BANNER: return true;
			case ORANGE_BANNER: return true;
			case MAGENTA_BANNER: return true;
			case LIGHT_BLUE_BANNER: return true;
			case YELLOW_BANNER: return true;
			case LIME_BANNER: return true;
			case PINK_BANNER: return true;
			case GRAY_BANNER: return true;
			case LIGHT_GRAY_BANNER: return true;
			case CYAN_BANNER: return true;
			case PURPLE_BANNER: return true;
			case BLUE_BANNER: return true;
			case BROWN_BANNER: return true;
			case GREEN_BANNER: return true;
			case RED_BANNER: return true;
			case BLACK_BANNER: return true;
			default:break;
		}
		return false;
	}

	public static boolean isBed(Material type){
		switch(type){
			case WHITE_BED: return true;
			case ORANGE_BED: return true;
			case MAGENTA_BED: return true;
			case LIGHT_BLUE_BED: return true;
			case YELLOW_BED: return true;
			case LIME_BED: return true;
			case PINK_BED: return true;
			case GRAY_BED: return true;
			case LIGHT_GRAY_BED: return true;
			case CYAN_BED: return true;
			case PURPLE_BED: return true;
			case BLUE_BED: return true;
			case BROWN_BED: return true;
			case GREEN_BED: return true;
			case RED_BED: return true;
			case BLACK_BED: return true;
			default:break;
		}
		return false;
	}

	public static boolean isCarpet(Material type){
		switch(type){
			case WHITE_CARPET: return true;
			case ORANGE_CARPET: return true;
			case MAGENTA_CARPET: return true;
			case LIGHT_BLUE_CARPET: return true;
			case YELLOW_CARPET: return true;
			case LIME_CARPET: return true;
			case PINK_CARPET: return true;
			case GRAY_CARPET: return true;
			case LIGHT_GRAY_CARPET: return true;
			case CYAN_CARPET: return true;
			case PURPLE_CARPET: return true;
			case BLUE_CARPET: return true;
			case BROWN_CARPET: return true;
			case GREEN_CARPET: return true;
			case RED_CARPET: return true;
			case BLACK_CARPET: return true;
			default:break;
		}
		return false;
	}

	public static boolean isConcrete(Material type){
		switch(type){
			case WHITE_CONCRETE: return true;
			case ORANGE_CONCRETE: return true;
			case MAGENTA_CONCRETE: return true;
			case LIGHT_BLUE_CONCRETE: return true;
			case YELLOW_CONCRETE: return true;
			case LIME_CONCRETE: return true;
			case PINK_CONCRETE: return true;
			case GRAY_CONCRETE: return true;
			case LIGHT_GRAY_CONCRETE: return true;
			case CYAN_CONCRETE: return true;
			case PURPLE_CONCRETE: return true;
			case BLUE_CONCRETE: return true;
			case BROWN_CONCRETE: return true;
			case GREEN_CONCRETE: return true;
			case RED_CONCRETE: return true;
			case BLACK_CONCRETE: return true;
			default:break;
		}
		return false;
	}

	public static boolean isConcretePowder(Material type){
		switch(type){
			case WHITE_CONCRETE_POWDER: return true;
			case ORANGE_CONCRETE_POWDER: return true;
			case MAGENTA_CONCRETE_POWDER: return true;
			case LIGHT_BLUE_CONCRETE_POWDER: return true;
			case YELLOW_CONCRETE_POWDER: return true;
			case LIME_CONCRETE_POWDER: return true;
			case PINK_CONCRETE_POWDER: return true;
			case GRAY_CONCRETE_POWDER: return true;
			case LIGHT_GRAY_CONCRETE_POWDER: return true;
			case CYAN_CONCRETE_POWDER: return true;
			case PURPLE_CONCRETE_POWDER: return true;
			case BLUE_CONCRETE_POWDER: return true;
			case BROWN_CONCRETE_POWDER: return true;
			case GREEN_CONCRETE_POWDER: return true;
			case RED_CONCRETE_POWDER: return true;
			case BLACK_CONCRETE_POWDER: return true;
			default:break;
		}
		return false;
	}

	public static boolean isGlazedTerracotta(Material type){
		switch(type){
			case WHITE_GLAZED_TERRACOTTA: return true;
			case ORANGE_GLAZED_TERRACOTTA: return true;
			case MAGENTA_GLAZED_TERRACOTTA: return true;
			case LIGHT_BLUE_GLAZED_TERRACOTTA: return true;
			case YELLOW_GLAZED_TERRACOTTA: return true;
			case LIME_GLAZED_TERRACOTTA: return true;
			case PINK_GLAZED_TERRACOTTA: return true;
			case GRAY_GLAZED_TERRACOTTA: return true;
			case LIGHT_GRAY_GLAZED_TERRACOTTA: return true;
			case CYAN_GLAZED_TERRACOTTA: return true;
			case PURPLE_GLAZED_TERRACOTTA: return true;
			case BLUE_GLAZED_TERRACOTTA: return true;
			case BROWN_GLAZED_TERRACOTTA: return true;
			case GREEN_GLAZED_TERRACOTTA: return true;
			case RED_GLAZED_TERRACOTTA: return true;
			case BLACK_GLAZED_TERRACOTTA: return true;
			default:break;
		}
		return false;
	}

	public static boolean isShulkerBox(Material type){
		switch(type){
			case WHITE_SHULKER_BOX: return true;
			case ORANGE_SHULKER_BOX: return true;
			case MAGENTA_SHULKER_BOX: return true;
			case LIGHT_BLUE_SHULKER_BOX: return true;
			case YELLOW_SHULKER_BOX: return true;
			case LIME_SHULKER_BOX: return true;
			case PINK_SHULKER_BOX: return true;
			case GRAY_SHULKER_BOX: return true;
			case LIGHT_GRAY_SHULKER_BOX: return true;
			case CYAN_SHULKER_BOX: return true;
			case PURPLE_SHULKER_BOX: return true;
			case BLUE_SHULKER_BOX: return true;
			case BROWN_SHULKER_BOX: return true;
			case GREEN_SHULKER_BOX: return true;
			case RED_SHULKER_BOX: return true;
			case BLACK_SHULKER_BOX: return true;
			default:break;
		}
		return false;
	}

	public static boolean isStainedGlass(Material type){
		switch(type){
			case WHITE_STAINED_GLASS: return true;
			case ORANGE_STAINED_GLASS: return true;
			case MAGENTA_STAINED_GLASS: return true;
			case LIGHT_BLUE_STAINED_GLASS: return true;
			case YELLOW_STAINED_GLASS: return true;
			case LIME_STAINED_GLASS: return true;
			case PINK_STAINED_GLASS: return true;
			case GRAY_STAINED_GLASS: return true;
			case LIGHT_GRAY_STAINED_GLASS: return true;
			case CYAN_STAINED_GLASS: return true;
			case PURPLE_STAINED_GLASS: return true;
			case BLUE_STAINED_GLASS: return true;
			case BROWN_STAINED_GLASS: return true;
			case GREEN_STAINED_GLASS: return true;
			case RED_STAINED_GLASS: return true;
			case BLACK_STAINED_GLASS: return true;
			default:break;
		}
		return false;
	}

	public static boolean isStainedGlassPane(Material type){
		switch(type){
			case WHITE_STAINED_GLASS_PANE: return true;
			case ORANGE_STAINED_GLASS_PANE: return true;
			case MAGENTA_STAINED_GLASS_PANE: return true;
			case LIGHT_BLUE_STAINED_GLASS_PANE: return true;
			case YELLOW_STAINED_GLASS_PANE: return true;
			case LIME_STAINED_GLASS_PANE: return true;
			case PINK_STAINED_GLASS_PANE: return true;
			case GRAY_STAINED_GLASS_PANE: return true;
			case LIGHT_GRAY_STAINED_GLASS_PANE: return true;
			case CYAN_STAINED_GLASS_PANE: return true;
			case PURPLE_STAINED_GLASS_PANE: return true;
			case BLUE_STAINED_GLASS_PANE: return true;
			case BROWN_STAINED_GLASS_PANE: return true;
			case GREEN_STAINED_GLASS_PANE: return true;
			case RED_STAINED_GLASS_PANE: return true;
			case BLACK_STAINED_GLASS_PANE: return true;
			default:break;
		}
		return false;
	}

	public static boolean isTerracotta(Material type){
		switch(type){
			case WHITE_TERRACOTTA: return true;
			case ORANGE_TERRACOTTA: return true;
			case MAGENTA_TERRACOTTA: return true;
			case LIGHT_BLUE_TERRACOTTA: return true;
			case YELLOW_TERRACOTTA: return true;
			case LIME_TERRACOTTA: return true;
			case PINK_TERRACOTTA: return true;
			case GRAY_TERRACOTTA: return true;
			case LIGHT_GRAY_TERRACOTTA: return true;
			case CYAN_TERRACOTTA: return true;
			case PURPLE_TERRACOTTA: return true;
			case BLUE_TERRACOTTA: return true;
			case BROWN_TERRACOTTA: return true;
			case GREEN_TERRACOTTA: return true;
			case RED_TERRACOTTA: return true;
			case BLACK_TERRACOTTA: return true;
			default:break;
		}
		return false;
	}

	public static boolean isWallBanner(Material type){
		switch(type){
			case WHITE_WALL_BANNER: return true;
			case ORANGE_WALL_BANNER: return true;
			case MAGENTA_WALL_BANNER: return true;
			case LIGHT_BLUE_WALL_BANNER: return true;
			case YELLOW_WALL_BANNER: return true;
			case LIME_WALL_BANNER: return true;
			case PINK_WALL_BANNER: return true;
			case GRAY_WALL_BANNER: return true;
			case LIGHT_GRAY_WALL_BANNER: return true;
			case CYAN_WALL_BANNER: return true;
			case PURPLE_WALL_BANNER: return true;
			case BLUE_WALL_BANNER: return true;
			case BROWN_WALL_BANNER: return true;
			case GREEN_WALL_BANNER: return true;
			case RED_WALL_BANNER: return true;
			case BLACK_WALL_BANNER: return true;
			default:break;
		}
		return false;
	}

	public static boolean isWool(Material type){
		switch(type){
			case WHITE_WOOL: return true;
			case ORANGE_WOOL: return true;
			case MAGENTA_WOOL: return true;
			case LIGHT_BLUE_WOOL: return true;
			case YELLOW_WOOL: return true;
			case LIME_WOOL: return true;
			case PINK_WOOL: return true;
			case GRAY_WOOL: return true;
			case LIGHT_GRAY_WOOL: return true;
			case CYAN_WOOL: return true;
			case PURPLE_WOOL: return true;
			case BLUE_WOOL: return true;
			case BROWN_WOOL: return true;
			case GREEN_WOOL: return true;
			case RED_WOOL: return true;
			case BLACK_WOOL: return true;
			default:break;
		}
		return false;
	}

	public static boolean isBoat(Material type){
		switch(type){
			case ACACIA_BOAT: return true;
			case BIRCH_BOAT: return true;
			case DARK_OAK_BOAT: return true;
			case JUNGLE_BOAT: return true;
			case OAK_BOAT: return true;
			case SPRUCE_BOAT: return true;
			default:break;
		}
		return false;
	}

	public static boolean isButton(Material type){
		switch(type){
			case ACACIA_BUTTON: return true;
			case BIRCH_BUTTON: return true;
			case DARK_OAK_BUTTON: return true;
			case JUNGLE_BUTTON: return true;
			case OAK_BUTTON: return true;
			case SPRUCE_BUTTON: return true;
			case STONE_BUTTON: return true;
			default:break;
		}
		return false;
	}

	public static boolean isDoor(Material type){
		switch(type){
			case ACACIA_DOOR: return true;
			case BIRCH_DOOR: return true;
			case DARK_OAK_DOOR: return true;
			case IRON_DOOR: return true;
			case JUNGLE_DOOR: return true;
			case OAK_DOOR: return true;
			case SPRUCE_DOOR: return true;
			default:break;
		}
		return false;
	}

	public static boolean isFence(Material type){
		switch(type){
			case ACACIA_FENCE: return true;
			case BIRCH_FENCE: return true;
			case DARK_OAK_FENCE: return true;
			case JUNGLE_FENCE: return true;
			case NETHER_BRICK_FENCE: return true;
			case OAK_FENCE: return true;
			case SPRUCE_FENCE: return true;
			default:break;
		}
		return false;
	}

	public static boolean isFenceGate(Material type){
		switch(type){
			case ACACIA_FENCE_GATE: return true;
			case BIRCH_FENCE_GATE: return true;
			case DARK_OAK_FENCE_GATE: return true;
			case JUNGLE_FENCE_GATE: return true;
			case OAK_FENCE_GATE: return true;
			case SPRUCE_FENCE_GATE: return true;
			default:break;
		}
		return false;
	}

	public static boolean isLeaves(Material type){
		switch(type){
			case ACACIA_LEAVES: return true;
			case BIRCH_LEAVES: return true;
			case DARK_OAK_LEAVES: return true;
			case JUNGLE_LEAVES: return true;
			case OAK_LEAVES: return true;
			case SPRUCE_LEAVES: return true;
			default:break;
		}
		return false;
	}

	public static boolean isLog(Material type){
		switch(type){
			case ACACIA_LOG: return true;
			case BIRCH_LOG: return true;
			case DARK_OAK_LOG: return true;
			case JUNGLE_LOG: return true;
			case OAK_LOG: return true;
			case SPRUCE_LOG: return true;
			case STRIPPED_ACACIA_LOG: return true;
			case STRIPPED_BIRCH_LOG: return true;
			case STRIPPED_DARK_OAK_LOG: return true;
			case STRIPPED_JUNGLE_LOG: return true;
			case STRIPPED_OAK_LOG: return true;
			case STRIPPED_SPRUCE_LOG: return true;
			default:break;
		}
		return false;
	}

	public static boolean isPlanks(Material type){
		switch(type){
			case ACACIA_PLANKS: return true;
			case BIRCH_PLANKS: return true;
			case DARK_OAK_PLANKS: return true;
			case JUNGLE_PLANKS: return true;
			case OAK_PLANKS: return true;
			case SPRUCE_PLANKS: return true;
			default:break;
		}
		return false;
	}

	public static boolean isPressurePlate(Material type){
		switch(type){
			case ACACIA_PRESSURE_PLATE: return true;
			case BIRCH_PRESSURE_PLATE: return true;
			case DARK_OAK_PRESSURE_PLATE: return true;
			case HEAVY_WEIGHTED_PRESSURE_PLATE: return true;
			case JUNGLE_PRESSURE_PLATE: return true;
			case LIGHT_WEIGHTED_PRESSURE_PLATE: return true;
			case OAK_PRESSURE_PLATE: return true;
			case SPRUCE_PRESSURE_PLATE: return true;
			case STONE_PRESSURE_PLATE: return true;
			default:break;
		}
		return false;
	}

	public static boolean isSapling(Material type){
		switch(type){
			case ACACIA_SAPLING: return true;
			case BIRCH_SAPLING: return true;
			case DARK_OAK_SAPLING: return true;
			case JUNGLE_SAPLING: return true;
			case OAK_SAPLING: return true;
			case POTTED_ACACIA_SAPLING: return true;
			case POTTED_BIRCH_SAPLING: return true;
			case POTTED_DARK_OAK_SAPLING: return true;
			case POTTED_JUNGLE_SAPLING: return true;
			case POTTED_OAK_SAPLING: return true;
			case POTTED_SPRUCE_SAPLING: return true;
			case SPRUCE_SAPLING: return true;
			default:break;
		}
		return false;
	}

	public static boolean isSlab(Material type){
		switch(type){
			case ACACIA_SLAB: return true;
			case BIRCH_SLAB: return true;
			case BRICK_SLAB: return true;
			case COBBLESTONE_SLAB: return true;
			case DARK_OAK_SLAB: return true;
			case DARK_PRISMARINE_SLAB: return true;
			case JUNGLE_SLAB: return true;
			case NETHER_BRICK_SLAB: return true;
			case OAK_SLAB: return true;
			case PETRIFIED_OAK_SLAB: return true;
			case PRISMARINE_BRICK_SLAB: return true;
			case PRISMARINE_SLAB: return true;
			case PURPUR_SLAB: return true;
			case QUARTZ_SLAB: return true;
			case RED_SANDSTONE_SLAB: return true;
			case SANDSTONE_SLAB: return true;
			case SPRUCE_SLAB: return true;
			case STONE_BRICK_SLAB: return true;
			case STONE_SLAB: return true;
			default:break;
		}
		return false;
	}

	public static boolean isStairs(Material type){
		switch(type){
			case ACACIA_STAIRS: return true;
			case BIRCH_STAIRS: return true;
			case BRICK_STAIRS: return true;
			case COBBLESTONE_STAIRS: return true;
			case DARK_OAK_STAIRS: return true;
			case DARK_PRISMARINE_STAIRS: return true;
			case JUNGLE_STAIRS: return true;
			case NETHER_BRICK_STAIRS: return true;
			case OAK_STAIRS: return true;
			case PRISMARINE_BRICK_STAIRS: return true;
			case PRISMARINE_STAIRS: return true;
			case PURPUR_STAIRS: return true;
			case QUARTZ_STAIRS: return true;
			case RED_SANDSTONE_STAIRS: return true;
			case SANDSTONE_STAIRS: return true;
			case SPRUCE_STAIRS: return true;
			case STONE_BRICK_STAIRS: return true;
			default:break;
		}
		return false;
	}

	public static boolean isTrapdoor(Material type){
		switch(type){
			case ACACIA_TRAPDOOR: return true;
			case BIRCH_TRAPDOOR: return true;
			case DARK_OAK_TRAPDOOR: return true;
			case IRON_TRAPDOOR: return true;
			case JUNGLE_TRAPDOOR: return true;
			case OAK_TRAPDOOR: return true;
			case SPRUCE_TRAPDOOR: return true;
			default:break;
		}
		return false;
	}

	public static boolean isWood(Material type){
		switch(type){
			case ACACIA_WOOD: return true;
			case BIRCH_WOOD: return true;
			case DARK_OAK_WOOD: return true;
			case JUNGLE_WOOD: return true;
			case OAK_WOOD: return true;
			case SPRUCE_WOOD: return true;
			case STRIPPED_ACACIA_WOOD: return true;
			case STRIPPED_BIRCH_WOOD: return true;
			case STRIPPED_DARK_OAK_WOOD: return true;
			case STRIPPED_JUNGLE_WOOD: return true;
			case STRIPPED_OAK_WOOD: return true;
			case STRIPPED_SPRUCE_WOOD: return true;
			default:break;
		}
		return false;
	}

	public static String getName(Material type){
		switch(type){
			case AIR: return "Air";
			case BARRIER: return "Barrier";
			case STONE: return "Stone";
			case GRANITE: return "Granite";
			case POLISHED_GRANITE: return "Polished Granite";
			case DIORITE: return "Diorite";
			case POLISHED_DIORITE: return "Polished Diorite";
			case ANDESITE: return "Andesite";
			case POLISHED_ANDESITE: return "Polished Andesite";
			case HAY_BLOCK: return "Hay Bale";
			case GRASS_BLOCK: return "Grass Block";
			case DIRT: return "Dirt";
			case COARSE_DIRT: return "Coarse Dirt";
			case PODZOL: return "Podzol";
			case COBBLESTONE: return "Cobblestone";
			case OAK_PLANKS: return "Oak Planks";
			case SPRUCE_PLANKS: return "Spruce Planks";
			case BIRCH_PLANKS: return "Birch Planks";
			case JUNGLE_PLANKS: return "Jungle Planks";
			case ACACIA_PLANKS: return "Acacia Planks";
			case DARK_OAK_PLANKS: return "Dark Oak Planks";
			case OAK_SAPLING: return "Oak Sapling";
			case SPRUCE_SAPLING: return "Spruce Sapling";
			case BIRCH_SAPLING: return "Birch Sapling";
			case JUNGLE_SAPLING: return "Jungle Sapling";
			case ACACIA_SAPLING: return "Acacia Sapling";
			case DARK_OAK_SAPLING: return "Dark Oak Sapling";
			case OAK_DOOR: return "Oak Door";
			case SPRUCE_DOOR: return "Spruce Door";
			case BIRCH_DOOR: return "Birch Door";
			case JUNGLE_DOOR: return "Jungle Door";
			case ACACIA_DOOR: return "Acacia Door";
			case DARK_OAK_DOOR: return "Dark Oak Door";
			case BEDROCK: return "Bedrock";
			case WATER: return "Water";
			case LAVA: return "Lava";
			case SAND: return "Sand";
			case RED_SAND: return "Red Sand";
			case SANDSTONE: return "Sandstone";
			case CHISELED_SANDSTONE: return "Chiseled Sandstone";
			case CUT_SANDSTONE: return "Cut Sandstone";
			case RED_SANDSTONE: return "Red Sandstone";
			case CHISELED_RED_SANDSTONE: return "Chiseled Red Sandstone";
			case CUT_RED_SANDSTONE: return "Cut Red Sandstone";
			case GRAVEL: return "Gravel";
			case GOLD_ORE: return "Gold Ore";
			case IRON_ORE: return "Iron Ore";
			case COAL_ORE: return "Coal Ore";
			case OAK_WOOD: return "Oak Wood";
			case SPRUCE_WOOD: return "Spruce Wood";
			case BIRCH_WOOD: return "Birch Wood";
			case JUNGLE_WOOD: return "Jungle Wood";
			case ACACIA_WOOD: return "Acacia Wood";
			case DARK_OAK_WOOD: return "Dark Oak Wood";
			case OAK_LOG: return "Oak Log";
			case SPRUCE_LOG: return "Spruce Log";
			case BIRCH_LOG: return "Birch Log";
			case JUNGLE_LOG: return "Jungle Log";
			case ACACIA_LOG: return "Acacia Log";
			case DARK_OAK_LOG: return "Dark Oak Log";
			case STRIPPED_OAK_LOG: return "Stripped Oak Log";
			case STRIPPED_SPRUCE_LOG: return "Stripped Spruce Log";
			case STRIPPED_BIRCH_LOG: return "Stripped Birch Log";
			case STRIPPED_JUNGLE_LOG: return "Stripped Jungle Log";
			case STRIPPED_ACACIA_LOG: return "Stripped Acacia Log";
			case STRIPPED_DARK_OAK_LOG: return "Stripped Dark Oak Log";
			case STRIPPED_OAK_WOOD: return "Stripped Oak Wood";
			case STRIPPED_SPRUCE_WOOD: return "Stripped Spruce Wood";
			case STRIPPED_BIRCH_WOOD: return "Stripped Birch Wood";
			case STRIPPED_JUNGLE_WOOD: return "Stripped Jungle Wood";
			case STRIPPED_ACACIA_WOOD: return "Stripped Acacia Wood";
			case STRIPPED_DARK_OAK_WOOD: return "Stripped Dark Oak Wood";
			case OAK_LEAVES: return "Oak Leaves";
			case SPRUCE_LEAVES: return "Spruce Leaves";
			case BIRCH_LEAVES: return "Birch Leaves";
			case JUNGLE_LEAVES: return "Jungle Leaves";
			case ACACIA_LEAVES: return "Acacia Leaves";
			case DARK_OAK_LEAVES: return "Dark Oak Leaves";
			case DEAD_BUSH: return "Dead Bush";
			case GRASS: return "Grass";
			case FERN: return "Fern";
			case SPONGE: return "Sponge";
			case WET_SPONGE: return "Wet Sponge";
			case GLASS: return "Glass";
			case KELP_PLANT: return "Kelp Plant";
			case KELP: return "Kelp";
			case DRIED_KELP_BLOCK: return "Dried Kelp Block";
			case WHITE_STAINED_GLASS: return "White Stained Glass";
			case ORANGE_STAINED_GLASS: return "Orange Stained Glass";
			case MAGENTA_STAINED_GLASS: return "Magenta Stained Glass";
			case LIGHT_BLUE_STAINED_GLASS: return "Light Blue Stained Glass";
			case YELLOW_STAINED_GLASS: return "Yellow Stained Glass";
			case LIME_STAINED_GLASS: return "Lime Stained Glass";
			case PINK_STAINED_GLASS: return "Pink Stained Glass";
			case GRAY_STAINED_GLASS: return "Gray Stained Glass";
			case LIGHT_GRAY_STAINED_GLASS: return "Light Gray Stained Glass";
			case CYAN_STAINED_GLASS: return "Cyan Stained Glass";
			case PURPLE_STAINED_GLASS: return "Purple Stained Glass";
			case BLUE_STAINED_GLASS: return "Blue Stained Glass";
			case BROWN_STAINED_GLASS: return "Brown Stained Glass";
			case GREEN_STAINED_GLASS: return "Green Stained Glass";
			case RED_STAINED_GLASS: return "Red Stained Glass";
			case BLACK_STAINED_GLASS: return "Black Stained Glass";
			case WHITE_STAINED_GLASS_PANE: return "White Stained Glass Pane";
			case ORANGE_STAINED_GLASS_PANE: return "Orange Stained Glass Pane";
			case MAGENTA_STAINED_GLASS_PANE: return "Magenta Stained Glass Pane";
			case LIGHT_BLUE_STAINED_GLASS_PANE: return "Light Blue Stained Glass Pane";
			case YELLOW_STAINED_GLASS_PANE: return "Yellow Stained Glass Pane";
			case LIME_STAINED_GLASS_PANE: return "Lime Stained Glass Pane";
			case PINK_STAINED_GLASS_PANE: return "Pink Stained Glass Pane";
			case GRAY_STAINED_GLASS_PANE: return "Gray Stained Glass Pane";
			case LIGHT_GRAY_STAINED_GLASS_PANE: return "Light Gray Stained Glass Pane";
			case CYAN_STAINED_GLASS_PANE: return "Cyan Stained Glass Pane";
			case PURPLE_STAINED_GLASS_PANE: return "Purple Stained Glass Pane";
			case BLUE_STAINED_GLASS_PANE: return "Blue Stained Glass Pane";
			case BROWN_STAINED_GLASS_PANE: return "Brown Stained Glass Pane";
			case GREEN_STAINED_GLASS_PANE: return "Green Stained Glass Pane";
			case RED_STAINED_GLASS_PANE: return "Red Stained Glass Pane";
			case BLACK_STAINED_GLASS_PANE: return "Black Stained Glass Pane";
			case GLASS_PANE: return "Glass Pane";
			case DANDELION: return "Dandelion";
			case POPPY: return "Poppy";
			case BLUE_ORCHID: return "Blue Orchid";
			case ALLIUM: return "Allium";
			case AZURE_BLUET: return "Azure Bluet";
			case RED_TULIP: return "Red Tulip";
			case ORANGE_TULIP: return "Orange Tulip";
			case WHITE_TULIP: return "White Tulip";
			case PINK_TULIP: return "Pink Tulip";
			case OXEYE_DAISY: return "Oxeye Daisy";
			case SUNFLOWER: return "Sunflower";
			case LILAC: return "Lilac";
			case TALL_GRASS: return "Tall Grass";
			case TALL_SEAGRASS: return "Tall Seagrass";
			case LARGE_FERN: return "Large Fern";
			case ROSE_BUSH: return "Rose Bush";
			case PEONY: return "Peony";
			case SEAGRASS: return "Seagrass";
			case SEA_PICKLE: return "Sea Pickle";
			case BROWN_MUSHROOM: return "Brown Mushroom";
			case RED_MUSHROOM_BLOCK: return "Red Mushroom Block";
			case BROWN_MUSHROOM_BLOCK: return "Brown Mushroom Block";
			case MUSHROOM_STEM: return "Mushroom Stem";
			case GOLD_BLOCK: return "Block of Gold";
			case IRON_BLOCK: return "Block of Iron";
			case SMOOTH_STONE: return "Smooth Stone";
			case SMOOTH_SANDSTONE: return "Smooth Sandstone";
			case SMOOTH_RED_SANDSTONE: return "Smooth Red Sandstone";
			case SMOOTH_QUARTZ: return "Smooth Quartz";
			case STONE_SLAB: return "Stone Slab";
			case SANDSTONE_SLAB: return "Sandstone Slab";
			case RED_SANDSTONE_SLAB: return "Red Sandstone Slab";
			case PETRIFIED_OAK_SLAB: return "Petrified Oak Slab";
			case COBBLESTONE_SLAB: return "Cobblestone Slab";
			case BRICK_SLAB: return "Brick Slab";
			case STONE_BRICK_SLAB: return "Stone Brick Slab";
			case NETHER_BRICK_SLAB: return "Nether Brick Slab";
			case QUARTZ_SLAB: return "Quartz Slab";
			case OAK_SLAB: return "Oak Slab";
			case SPRUCE_SLAB: return "Spruce Slab";
			case BIRCH_SLAB: return "Birch Slab";
			case JUNGLE_SLAB: return "Jungle Slab";
			case ACACIA_SLAB: return "Acacia Slab";
			case DARK_OAK_SLAB: return "Dark Oak Slab";
			case DARK_PRISMARINE_SLAB: return "Dark Prismarine Slab";
			case PRISMARINE_SLAB: return "Prismarine Slab";
			case PRISMARINE_BRICK_SLAB: return "Prismarine Brick Slab";
			case BRICKS: return "Bricks";
			case TNT: return "TNT";
			case BOOKSHELF: return "Bookshelf";
			case MOSSY_COBBLESTONE: return "Mossy Cobblestone";
			case OBSIDIAN: return "Obsidian";
			case TORCH: return "Torch";
			case WALL_TORCH: return "Wall Torch";
			case FIRE: return "Fire";
			case SPAWNER: return "Spawner";
			case OAK_STAIRS: return "Oak Stairs";
			case SPRUCE_STAIRS: return "Spruce Stairs";
			case BIRCH_STAIRS: return "Birch Stairs";
			case JUNGLE_STAIRS: return "Jungle Stairs";
			case ACACIA_STAIRS: return "Acacia Stairs";
			case DARK_OAK_STAIRS: return "Dark Oak Stairs";
			case DARK_PRISMARINE_STAIRS: return "Dark Prismarine Stairs";
			case PRISMARINE_STAIRS: return "Prismarine Stairs";
			case PRISMARINE_BRICK_STAIRS: return "Prismarine Brick Stairs";
			case CHEST: return "Chest";
			case TRAPPED_CHEST: return "Trapped Chest";
			case REDSTONE_WIRE: return "Redstone Dust";
			case DIAMOND_ORE: return "Diamond Ore";
			case COAL_BLOCK: return "Block of Coal";
			case DIAMOND_BLOCK: return "Block of Diamond";
			case CRAFTING_TABLE: return "Crafting Table";
			case FARMLAND: return "Farmland";
			case FURNACE: return "Furnace";
			case LADDER: return "Ladder";
			case RAIL: return "Rail";
			case POWERED_RAIL: return "Powered Rail";
			case ACTIVATOR_RAIL: return "Activator Rail";
			case DETECTOR_RAIL: return "Detector Rail";
			case COBBLESTONE_STAIRS: return "Cobblestone Stairs";
			case SANDSTONE_STAIRS: return "Sandstone Stairs";
			case RED_SANDSTONE_STAIRS: return "Red Sandstone Stairs";
			case LEVER: return "Lever";
			case STONE_PRESSURE_PLATE: return "Stone Pressure Plate";
			case OAK_PRESSURE_PLATE: return "Oak Pressure Plate";
			case SPRUCE_PRESSURE_PLATE: return "Spruce Pressure Plate";
			case BIRCH_PRESSURE_PLATE: return "Birch Pressure Plate";
			case JUNGLE_PRESSURE_PLATE: return "Jungle Pressure Plate";
			case ACACIA_PRESSURE_PLATE: return "Acacia Pressure Plate";
			case DARK_OAK_PRESSURE_PLATE: return "Dark Oak Pressure Plate";
			case LIGHT_WEIGHTED_PRESSURE_PLATE: return "Light Weighted Pressure Plate";
			case HEAVY_WEIGHTED_PRESSURE_PLATE: return "Heavy Weighted Pressure Plate";
			case IRON_DOOR: return "Iron Door";
			case REDSTONE_ORE: return "Redstone Ore";
			case REDSTONE_TORCH: return "Redstone Torch";
			case REDSTONE_WALL_TORCH: return "Redstone Wall Torch";
			case STONE_BUTTON: return "Stone Button";
			case OAK_BUTTON: return "Oak Button";
			case SPRUCE_BUTTON: return "Spruce Button";
			case BIRCH_BUTTON: return "Birch Button";
			case JUNGLE_BUTTON: return "Jungle Button";
			case ACACIA_BUTTON: return "Acacia Button";
			case DARK_OAK_BUTTON: return "Dark Oak Button";
			case SNOW: return "Snow";
			case WHITE_CARPET: return "White Carpet";
			case ORANGE_CARPET: return "Orange Carpet";
			case MAGENTA_CARPET: return "Magenta Carpet";
			case LIGHT_BLUE_CARPET: return "Light Blue Carpet";
			case YELLOW_CARPET: return "Yellow Carpet";
			case LIME_CARPET: return "Lime Carpet";
			case PINK_CARPET: return "Pink Carpet";
			case GRAY_CARPET: return "Gray Carpet";
			case LIGHT_GRAY_CARPET: return "Light Gray Carpet";
			case CYAN_CARPET: return "Cyan Carpet";
			case PURPLE_CARPET: return "Purple Carpet";
			case BLUE_CARPET: return "Blue Carpet";
			case BROWN_CARPET: return "Brown Carpet";
			case GREEN_CARPET: return "Green Carpet";
			case RED_CARPET: return "Red Carpet";
			case BLACK_CARPET: return "Black Carpet";
			case ICE: return "Ice";
			case FROSTED_ICE: return "Frosted Ice";
			case PACKED_ICE: return "Packed Ice";
			case BLUE_ICE: return "Blue Ice";
			case CACTUS: return "Cactus";
			case CLAY: return "Clay";
			case WHITE_TERRACOTTA: return "White Terracotta";
			case ORANGE_TERRACOTTA: return "Orange Terracotta";
			case MAGENTA_TERRACOTTA: return "Magenta Terracotta";
			case LIGHT_BLUE_TERRACOTTA: return "Light Blue Terracotta";
			case YELLOW_TERRACOTTA: return "Yellow Terracotta";
			case LIME_TERRACOTTA: return "Lime Terracotta";
			case PINK_TERRACOTTA: return "Pink Terracotta";
			case GRAY_TERRACOTTA: return "Gray Terracotta";
			case LIGHT_GRAY_TERRACOTTA: return "Light Gray Terracotta";
			case CYAN_TERRACOTTA: return "Cyan Terracotta";
			case PURPLE_TERRACOTTA: return "Purple Terracotta";
			case BLUE_TERRACOTTA: return "Blue Terracotta";
			case BROWN_TERRACOTTA: return "Brown Terracotta";
			case GREEN_TERRACOTTA: return "Green Terracotta";
			case RED_TERRACOTTA: return "Red Terracotta";
			case BLACK_TERRACOTTA: return "Black Terracotta";
			case TERRACOTTA: return "Terracotta";
			case SUGAR_CANE: return "Sugar Cane";
			case JUKEBOX: return "Jukebox";
			case OAK_FENCE: return "Oak Fence";
			case SPRUCE_FENCE: return "Spruce Fence";
			case BIRCH_FENCE: return "Birch Fence";
			case JUNGLE_FENCE: return "Jungle Fence";
			case DARK_OAK_FENCE: return "Dark Oak Fence";
			case ACACIA_FENCE: return "Acacia Fence";
			case OAK_FENCE_GATE: return "Oak Fence Gate";
			case SPRUCE_FENCE_GATE: return "Spruce Fence Gate";
			case BIRCH_FENCE_GATE: return "Birch Fence Gate";
			case JUNGLE_FENCE_GATE: return "Jungle Fence Gate";
			case DARK_OAK_FENCE_GATE: return "Dark Oak Fence Gate";
			case ACACIA_FENCE_GATE: return "Acacia Fence Gate";
			case PUMPKIN_STEM: return "Pumpkin Stem";
			case ATTACHED_PUMPKIN_STEM: return "Attached Pumpkin Stem";
			case PUMPKIN: return "Pumpkin";
			case CARVED_PUMPKIN: return "Carved Pumpkin";
			case JACK_O_LANTERN: return "Jack o'Lantern";
			case NETHERRACK: return "Netherrack";
			case SOUL_SAND: return "Soul Sand";
			case GLOWSTONE: return "Glowstone";
			case NETHER_PORTAL: return "Nether Portal";
			case WHITE_WOOL: return "White Wool";
			case ORANGE_WOOL: return "Orange Wool";
			case MAGENTA_WOOL: return "Magenta Wool";
			case LIGHT_BLUE_WOOL: return "Light Blue Wool";
			case YELLOW_WOOL: return "Yellow Wool";
			case LIME_WOOL: return "Lime Wool";
			case PINK_WOOL: return "Pink Wool";
			case GRAY_WOOL: return "Gray Wool";
			case LIGHT_GRAY_WOOL: return "Light Gray Wool";
			case CYAN_WOOL: return "Cyan Wool";
			case PURPLE_WOOL: return "Purple Wool";
			case BLUE_WOOL: return "Blue Wool";
			case BROWN_WOOL: return "Brown Wool";
			case GREEN_WOOL: return "Green Wool";
			case RED_WOOL: return "Red Wool";
			case BLACK_WOOL: return "Black Wool";
			case LAPIS_ORE: return "Lapis Lazuli Ore";
			case LAPIS_BLOCK: return "Lapis Lazuli Block";
			case DISPENSER: return "Dispenser";
			case DROPPER: return "Dropper";
			case NOTE_BLOCK: return "Note Block";
			case CAKE: return "Cake";
			case OAK_TRAPDOOR: return "Oak Trapdoor";
			case SPRUCE_TRAPDOOR: return "Spruce Trapdoor";
			case BIRCH_TRAPDOOR: return "Birch Trapdoor";
			case JUNGLE_TRAPDOOR: return "Jungle Trapdoor";
			case ACACIA_TRAPDOOR: return "Acacia Trapdoor";
			case DARK_OAK_TRAPDOOR: return "Dark Oak Trapdoor";
			case IRON_TRAPDOOR: return "Iron Trapdoor";
			case COBWEB: return "Cobweb";
			case STONE_BRICKS: return "Stone Bricks";
			case MOSSY_STONE_BRICKS: return "Mossy Stone Bricks";
			case CRACKED_STONE_BRICKS: return "Cracked Stone Bricks";
			case CHISELED_STONE_BRICKS: return "Chiseled Stone Bricks";
			case INFESTED_STONE: return "Infested Stone";
			case INFESTED_COBBLESTONE: return "Infested Cobblestone";
			case INFESTED_STONE_BRICKS: return "Infested Stone Bricks";
			case INFESTED_MOSSY_STONE_BRICKS: return "Infested Mossy Stone Bricks";
			case INFESTED_CRACKED_STONE_BRICKS: return "Infested Cracked Stone Bricks";
			case INFESTED_CHISELED_STONE_BRICKS: return "Infested Chiseled Stone Bricks";
			case PISTON: return "Piston";
			case STICKY_PISTON: return "Sticky Piston";
			case IRON_BARS: return "Iron Bars";
			case MELON: return "Melon";
			case BRICK_STAIRS: return "Brick Stairs";
			case STONE_BRICK_STAIRS: return "Stone Brick Stairs";
			case VINE: return "Vines";
			case NETHER_BRICKS: return "Nether Bricks";
			case NETHER_BRICK_FENCE: return "Nether Brick Fence";
			case NETHER_BRICK_STAIRS: return "Nether Brick Stairs";
			case ENCHANTING_TABLE: return "Enchanting Table";
			case ANVIL: return "Anvil";
			case CHIPPED_ANVIL: return "Chipped Anvil";
			case DAMAGED_ANVIL: return "Damaged Anvil";
			case END_STONE: return "End Stone";
			case END_PORTAL_FRAME: return "End Portal Frame";
			case MYCELIUM: return "Mycelium";
			case LILY_PAD: return "Lily Pad";
			case DRAGON_EGG: return "Dragon Egg";
			case REDSTONE_LAMP: return "Redstone Lamp";
			case COCOA: return "Cocoa";
			case ENDER_CHEST: return "Ender Chest";
			case EMERALD_ORE: return "Emerald Ore";
			case EMERALD_BLOCK: return "Block of Emerald";
			case REDSTONE_BLOCK: return "Block of Redstone";
			case TRIPWIRE: return "Tripwire";
			case TRIPWIRE_HOOK: return "Tripwire Hook";
			case COMMAND_BLOCK: return "Command Block";
			case REPEATING_COMMAND_BLOCK: return "Repeating Command Block";
			case CHAIN_COMMAND_BLOCK: return "Chain Command Block";
			case BEACON: return "Beacon";
			case COBBLESTONE_WALL: return "Cobblestone Wall";
			case MOSSY_COBBLESTONE_WALL: return "Mossy Cobblestone Wall";
			case CARROTS: return "Carrots";
			case POTATOES: return "Potatoes";
			case DAYLIGHT_DETECTOR: return "Daylight Detector";
			case NETHER_QUARTZ_ORE: return "Nether Quartz Ore";
			case HOPPER: return "Hopper";
			case QUARTZ_BLOCK: return "Block of Quartz";
			case CHISELED_QUARTZ_BLOCK: return "Chiseled Quartz Block";
			case QUARTZ_PILLAR: return "Quartz Pillar";
			case QUARTZ_STAIRS: return "Quartz Stairs";
			case SLIME_BLOCK: return "Slime Block";
			case PRISMARINE: return "Prismarine";
			case PRISMARINE_BRICKS: return "Prismarine Bricks";
			case DARK_PRISMARINE: return "Dark Prismarine";
			case SEA_LANTERN: return "Sea Lantern";
			case END_ROD: return "End Rod";
			case CHORUS_PLANT: return "Chorus Plant";
			case CHORUS_FLOWER: return "Chorus Flower";
			case PURPUR_BLOCK: return "Purpur Block";
			case PURPUR_PILLAR: return "Purpur Pillar";
			case PURPUR_STAIRS: return "Purpur Stairs";
			case PURPUR_SLAB: return "Purpur Slab";
			case END_STONE_BRICKS: return "End Stone Bricks";
			case BEETROOTS: return "Beetroots";
			case GRASS_PATH: return "Grass Path";
			case MAGMA_BLOCK: return "Magma Block";
			case NETHER_WART_BLOCK: return "Nether Wart Block";
			case RED_NETHER_BRICKS: return "Red Nether Bricks";
			case BONE_BLOCK: return "Bone Block";
			case OBSERVER: return "Observer";
			case SHULKER_BOX: return "Shulker Box";
			case WHITE_SHULKER_BOX: return "White Shulker Box";
			case ORANGE_SHULKER_BOX: return "Orange Shulker Box";
			case MAGENTA_SHULKER_BOX: return "Magenta Shulker Box";
			case LIGHT_BLUE_SHULKER_BOX: return "Light Blue Shulker Box";
			case YELLOW_SHULKER_BOX: return "Yellow Shulker Box";
			case LIME_SHULKER_BOX: return "Lime Shulker Box";
			case PINK_SHULKER_BOX: return "Pink Shulker Box";
			case GRAY_SHULKER_BOX: return "Gray Shulker Box";
			case LIGHT_GRAY_SHULKER_BOX: return "Light Gray Shulker Box";
			case CYAN_SHULKER_BOX: return "Cyan Shulker Box";
			case PURPLE_SHULKER_BOX: return "Purple Shulker Box";
			case BLUE_SHULKER_BOX: return "Blue Shulker Box";
			case BROWN_SHULKER_BOX: return "Brown Shulker Box";
			case GREEN_SHULKER_BOX: return "Green Shulker Box";
			case RED_SHULKER_BOX: return "Red Shulker Box";
			case BLACK_SHULKER_BOX: return "Black Shulker Box";
			case WHITE_GLAZED_TERRACOTTA: return "White Glazed Terracotta";
			case ORANGE_GLAZED_TERRACOTTA: return "Orange Glazed Terracotta";
			case MAGENTA_GLAZED_TERRACOTTA: return "Magenta Glazed Terracotta";
			case LIGHT_BLUE_GLAZED_TERRACOTTA: return "Light Blue Glazed Terracotta";
			case YELLOW_GLAZED_TERRACOTTA: return "Yellow Glazed Terracotta";
			case LIME_GLAZED_TERRACOTTA: return "Lime Glazed Terracotta";
			case PINK_GLAZED_TERRACOTTA: return "Pink Glazed Terracotta";
			case GRAY_GLAZED_TERRACOTTA: return "Gray Glazed Terracotta";
			case LIGHT_GRAY_GLAZED_TERRACOTTA: return "Light Gray Glazed Terracotta";
			case CYAN_GLAZED_TERRACOTTA: return "Cyan Glazed Terracotta";
			case PURPLE_GLAZED_TERRACOTTA: return "Purple Glazed Terracotta";
			case BLUE_GLAZED_TERRACOTTA: return "Blue Glazed Terracotta";
			case BROWN_GLAZED_TERRACOTTA: return "Brown Glazed Terracotta";
			case GREEN_GLAZED_TERRACOTTA: return "Green Glazed Terracotta";
			case RED_GLAZED_TERRACOTTA: return "Red Glazed Terracotta";
			case BLACK_GLAZED_TERRACOTTA: return "Black Glazed Terracotta";
			case BLACK_CONCRETE: return "Black Concrete";
			case RED_CONCRETE: return "Red Concrete";
			case GREEN_CONCRETE: return "Green Concrete";
			case BROWN_CONCRETE: return "Brown Concrete";
			case BLUE_CONCRETE: return "Blue Concrete";
			case PURPLE_CONCRETE: return "Purple Concrete";
			case CYAN_CONCRETE: return "Cyan Concrete";
			case LIGHT_GRAY_CONCRETE: return "Light Gray Concrete";
			case GRAY_CONCRETE: return "Gray Concrete";
			case PINK_CONCRETE: return "Pink Concrete";
			case LIME_CONCRETE: return "Lime Concrete";
			case YELLOW_CONCRETE: return "Yellow Concrete";
			case LIGHT_BLUE_CONCRETE: return "Light Blue Concrete";
			case MAGENTA_CONCRETE: return "Magenta Concrete";
			case ORANGE_CONCRETE: return "Orange Concrete";
			case WHITE_CONCRETE: return "White Concrete";
			case BLACK_CONCRETE_POWDER: return "Black Concrete Powder";
			case RED_CONCRETE_POWDER: return "Red Concrete Powder";
			case GREEN_CONCRETE_POWDER: return "Green Concrete Powder";
			case BROWN_CONCRETE_POWDER: return "Brown Concrete Powder";
			case BLUE_CONCRETE_POWDER: return "Blue Concrete Powder";
			case PURPLE_CONCRETE_POWDER: return "Purple Concrete Powder";
			case CYAN_CONCRETE_POWDER: return "Cyan Concrete Powder";
			case LIGHT_GRAY_CONCRETE_POWDER: return "Light Gray Concrete Powder";
			case GRAY_CONCRETE_POWDER: return "Gray Concrete Powder";
			case PINK_CONCRETE_POWDER: return "Pink Concrete Powder";
			case LIME_CONCRETE_POWDER: return "Lime Concrete Powder";
			case YELLOW_CONCRETE_POWDER: return "Yellow Concrete Powder";
			case LIGHT_BLUE_CONCRETE_POWDER: return "Light Blue Concrete Powder";
			case MAGENTA_CONCRETE_POWDER: return "Magenta Concrete Powder";
			case ORANGE_CONCRETE_POWDER: return "Orange Concrete Powder";
			case WHITE_CONCRETE_POWDER: return "White Concrete Powder";
			case TURTLE_EGG: return "Turtle Egg";
			case PISTON_HEAD: return "Piston Head";
			case MOVING_PISTON: return "Moving Piston";
			case RED_MUSHROOM: return "Red Mushroom";
			case SNOW_BLOCK: return "Snow Block";
			case ATTACHED_MELON_STEM: return "Attached Melon Stem";
			case MELON_STEM: return "Melon Stem";
			case BREWING_STAND: return "Brewing Stand";
			case END_PORTAL: return "End Portal";
			case FLOWER_POT: return "Flower Pot";
			case POTTED_OAK_SAPLING: return "Potted Oak Sapling";
			case POTTED_SPRUCE_SAPLING: return "Potted Spruce Sapling";
			case POTTED_BIRCH_SAPLING: return "Potted Birch Sapling";
			case POTTED_JUNGLE_SAPLING: return "Potted Jungle Sapling";
			case POTTED_ACACIA_SAPLING: return "Potted Acacia Sapling";
			case POTTED_DARK_OAK_SAPLING: return "Potted Dark Oak Sapling";
			case POTTED_FERN: return "Potted Fern";
			case POTTED_DANDELION: return "Potted Dandelion";
			case POTTED_POPPY: return "Potted Poppy";
			case POTTED_BLUE_ORCHID: return "Potted Blue Orchid";
			case POTTED_ALLIUM: return "Potted Allium";
			case POTTED_AZURE_BLUET: return "Potted Azure Bluet";
			case POTTED_RED_TULIP: return "Potted Red Tulip";
			case POTTED_ORANGE_TULIP: return "Potted Orange Tulip";
			case POTTED_WHITE_TULIP: return "Potted White Tulip";
			case POTTED_PINK_TULIP: return "Potted Pink Tulip";
			case POTTED_OXEYE_DAISY: return "Potted Oxeye Daisy";
			case POTTED_RED_MUSHROOM: return "Potted Red Mushroom";
			case POTTED_BROWN_MUSHROOM: return "Potted Brown Mushroom";
			case POTTED_DEAD_BUSH: return "Potted Dead Bush";
			case POTTED_CACTUS: return "Potted Cactus";
			case SKELETON_WALL_SKULL: return "Skeleton Wall Skull";
			case SKELETON_SKULL: return "Skeleton Skull";
			case WITHER_SKELETON_WALL_SKULL: return "Wither Skeleton Wall Skull";
			case WITHER_SKELETON_SKULL: return "Wither Skeleton Skull";
			case ZOMBIE_WALL_HEAD: return "Zombie Wall Head";
			case ZOMBIE_HEAD: return "Zombie Head";
			case PLAYER_WALL_HEAD: return "Player Wall Head";
			case PLAYER_HEAD: return "Player Head";
			case CREEPER_WALL_HEAD: return "Creeper Wall Head";
			case CREEPER_HEAD: return "Creeper Head";
			case DRAGON_WALL_HEAD: return "Dragon Wall Head";
			case DRAGON_HEAD: return "Dragon Head";
			case END_GATEWAY: return "End Gateway";
			case STRUCTURE_VOID: return "Structure Void";
			case STRUCTURE_BLOCK: return "Structure Block";
			case VOID_AIR: return "Void Air";
			case CAVE_AIR: return "Cave Air";
			case BUBBLE_COLUMN: return "Bubble Column";
			case DEAD_TUBE_CORAL_BLOCK: return "Dead Tube Coral Block";
			case DEAD_BRAIN_CORAL_BLOCK: return "Dead Brain Coral Block";
			case DEAD_BUBBLE_CORAL_BLOCK: return "Dead Bubble Coral Block";
			case DEAD_FIRE_CORAL_BLOCK: return "Dead Fire Coral Block";
			case DEAD_HORN_CORAL_BLOCK: return "Dead Horn Coral Block";
			case TUBE_CORAL_BLOCK: return "Tube Coral Block";
			case BRAIN_CORAL_BLOCK: return "Brain Coral Block";
			case BUBBLE_CORAL_BLOCK: return "Bubble Coral Block";
			case FIRE_CORAL_BLOCK: return "Fire Coral Block";
			case HORN_CORAL_BLOCK: return "Horn Coral Block";
			case TUBE_CORAL: return "Tube Coral";
			case BRAIN_CORAL: return "Brain Coral";
			case BUBBLE_CORAL: return "Bubble Coral";
			case FIRE_CORAL: return "Fire Coral";
			case HORN_CORAL: return "Horn Coral";
			case TUBE_CORAL_FAN: return "Tube Coral Fan";
			case BRAIN_CORAL_FAN: return "Brain Coral Fan";
			case BUBBLE_CORAL_FAN: return "Bubble Coral Fan";
			case FIRE_CORAL_FAN: return "Fire Coral Fan";
			case HORN_CORAL_FAN: return "Horn Coral Fan";
			case DEAD_TUBE_CORAL_FAN: return "Dead Tube Coral Fan";
			case DEAD_BRAIN_CORAL_FAN: return "Dead Brain Coral Fan";
			case DEAD_BUBBLE_CORAL_FAN: return "Dead Bubble Coral Fan";
			case DEAD_FIRE_CORAL_FAN: return "Dead Fire Coral Fan";
			case DEAD_HORN_CORAL_FAN: return "Dead Horn Coral Fan";
			case TUBE_CORAL_WALL_FAN: return "Tube Coral Wall Fan";
			case BRAIN_CORAL_WALL_FAN: return "Brain Coral Wall Fan";
			case BUBBLE_CORAL_WALL_FAN: return "Bubble Coral Wall Fan";
			case FIRE_CORAL_WALL_FAN: return "Fire Coral Wall Fan";
			case HORN_CORAL_WALL_FAN: return "Horn Coral Wall Fan";
			case DEAD_TUBE_CORAL_WALL_FAN: return "Dead Tube Coral Wall Fan";
			case DEAD_BRAIN_CORAL_WALL_FAN: return "Dead Brain Coral Wall Fan";
			case DEAD_BUBBLE_CORAL_WALL_FAN: return "Dead Bubble Coral Wall Fan";
			case DEAD_FIRE_CORAL_WALL_FAN: return "Dead Fire Coral Wall Fan";
			case DEAD_HORN_CORAL_WALL_FAN: return "Dead Horn Coral Wall Fan";
			case CONDUIT: return "Conduit";
			case NAME_TAG: return "Name Tag";
			case LEAD: return "Lead";
			case IRON_SHOVEL: return "Iron Shovel";
			case IRON_PICKAXE: return "Iron Pickaxe";
			case IRON_AXE: return "Iron Axe";
			case FLINT_AND_STEEL: return "Flint and Steel";
			case APPLE: return "Apple";
			case COOKIE: return "Cookie";
			case BOW: return "Bow";
			case ARROW: return "Arrow";
			case SPECTRAL_ARROW: return "Spectral Arrow";
			case TIPPED_ARROW: return "Tipped Arrow";
			case DRIED_KELP: return "Dried Kelp";
			case COAL: return "Coal";
			case CHARCOAL: return "Charcoal";
			case DIAMOND: return "Diamond";
			case EMERALD: return "Emerald";
			case IRON_INGOT: return "Iron Ingot";
			case GOLD_INGOT: return "Gold Ingot";
			case IRON_SWORD: return "Iron Sword";
			case WOODEN_SWORD: return "Wooden Sword";
			case WOODEN_SHOVEL: return "Wooden Shovel";
			case WOODEN_PICKAXE: return "Wooden Pickaxe";
			case WOODEN_AXE: return "Wooden Axe";
			case STONE_SWORD: return "Stone Sword";
			case STONE_SHOVEL: return "Stone Shovel";
			case STONE_PICKAXE: return "Stone Pickaxe";
			case STONE_AXE: return "Stone Axe";
			case DIAMOND_SWORD: return "Diamond Sword";
			case DIAMOND_SHOVEL: return "Diamond Shovel";
			case DIAMOND_PICKAXE: return "Diamond Pickaxe";
			case DIAMOND_AXE: return "Diamond Axe";
			case STICK: return "Stick";
			case BOWL: return "Bowl";
			case MUSHROOM_STEW: return "Mushroom Stew";
			case GOLDEN_SWORD: return "Golden Sword";
			case GOLDEN_SHOVEL: return "Golden Shovel";
			case GOLDEN_PICKAXE: return "Golden Pickaxe";
			case GOLDEN_AXE: return "Golden Axe";
			case STRING: return "String";
			case FEATHER: return "Feather";
			case GUNPOWDER: return "Gunpowder";
			case WOODEN_HOE: return "Wooden Hoe";
			case STONE_HOE: return "Stone Hoe";
			case IRON_HOE: return "Iron Hoe";
			case DIAMOND_HOE: return "Diamond Hoe";
			case GOLDEN_HOE: return "Golden Hoe";
			case WHEAT_SEEDS: return "Wheat Seeds";
			case PUMPKIN_SEEDS: return "Pumpkin Seeds";
			case MELON_SEEDS: return "Melon Seeds";
			case MELON_SLICE: return "Melon Slice";
			case WHEAT: return "Wheat";
			case BREAD: return "Bread";
			case LEATHER_HELMET: return "Leather Cap";
			case LEATHER_CHESTPLATE: return "Leather Tunic";
			case LEATHER_LEGGINGS: return "Leather Pants";
			case LEATHER_BOOTS: return "Leather Boots";
			case CHAINMAIL_HELMET: return "Chainmail Helmet";
			case CHAINMAIL_CHESTPLATE: return "Chainmail Chestplate";
			case CHAINMAIL_LEGGINGS: return "Chainmail Leggings";
			case CHAINMAIL_BOOTS: return "Chainmail Boots";
			case IRON_HELMET: return "Iron Helmet";
			case IRON_CHESTPLATE: return "Iron Chestplate";
			case IRON_LEGGINGS: return "Iron Leggings";
			case IRON_BOOTS: return "Iron Boots";
			case DIAMOND_HELMET: return "Diamond Helmet";
			case DIAMOND_CHESTPLATE: return "Diamond Chestplate";
			case DIAMOND_LEGGINGS: return "Diamond Leggings";
			case DIAMOND_BOOTS: return "Diamond Boots";
			case GOLDEN_HELMET: return "Golden Helmet";
			case GOLDEN_CHESTPLATE: return "Golden Chestplate";
			case GOLDEN_LEGGINGS: return "Golden Leggings";
			case GOLDEN_BOOTS: return "Golden Boots";
			case FLINT: return "Flint";
			case PORKCHOP: return "Raw Porkchop";
			case COOKED_PORKCHOP: return "Cooked Porkchop";
			case CHICKEN: return "Raw Chicken";
			case COOKED_CHICKEN: return "Cooked Chicken";
			case MUTTON: return "Raw Mutton";
			case COOKED_MUTTON: return "Cooked Mutton";
			case RABBIT: return "Raw Rabbit";
			case COOKED_RABBIT: return "Cooked Rabbit";
			case RABBIT_STEW: return "Rabbit Stew";
			case RABBIT_FOOT: return "Rabbit's Foot";
			case RABBIT_HIDE: return "Rabbit Hide";
			case BEEF: return "Raw Beef";
			case COOKED_BEEF: return "Steak";
			case PAINTING: return "Painting";
			case ITEM_FRAME: return "Item Frame";
			case GOLDEN_APPLE: return "Golden Apple";
			case ENCHANTED_GOLDEN_APPLE: return "Enchanted Golden Apple";
			case BUCKET: return "Bucket";
			case WATER_BUCKET: return "Water Bucket";
			case LAVA_BUCKET: return "Lava Bucket";
			case PUFFERFISH_BUCKET: return "Bucket of Pufferfish";
			case SALMON_BUCKET: return "Bucket of Salmon";
			case COD_BUCKET: return "Bucket of Cod";
			case TROPICAL_FISH_BUCKET: return "Bucket of Tropical Fish";
			case MINECART: return "Minecart";
			case SADDLE: return "Saddle";
			case REDSTONE: return "Redstone";
			case SNOWBALL: return "Snowball";
			case OAK_BOAT: return "Oak Boat";
			case SPRUCE_BOAT: return "Spruce Boat";
			case BIRCH_BOAT: return "Birch Boat";
			case JUNGLE_BOAT: return "Jungle Boat";
			case ACACIA_BOAT: return "Acacia Boat";
			case DARK_OAK_BOAT: return "Dark Oak Boat";
			case LEATHER: return "Leather";
			case MILK_BUCKET: return "Milk Bucket";
			case BRICK: return "Brick";
			case CLAY_BALL: return "Clay";
			case PAPER: return "Paper";
			case BOOK: return "Book";
			case SLIME_BALL: return "Slimeball";
			case CHEST_MINECART: return "Minecart with Chest";
			case FURNACE_MINECART: return "Minecart with Furnace";
			case TNT_MINECART: return "Minecart with TNT";
			case HOPPER_MINECART: return "Minecart with Hopper";
			case COMMAND_BLOCK_MINECART: return "Minecart with Command Block";
			case EGG: return "Egg";
			case COMPASS: return "Compass";
			case FISHING_ROD: return "Fishing Rod";
			case CLOCK: return "Clock";
			case GLOWSTONE_DUST: return "Glowstone Dust";
			case COD: return "Raw Cod";
			case SALMON: return "Raw Salmon";
			case PUFFERFISH: return "Pufferfish";
			case TROPICAL_FISH: return "Tropical Fish";
			case COOKED_COD: return "Cooked Cod";
			case COOKED_SALMON: return "Cooked Salmon";
			case MUSIC_DISC_13: return "Music Disc";
			case MUSIC_DISC_CAT: return "Music Disc";
			case MUSIC_DISC_BLOCKS: return "Music Disc";
			case MUSIC_DISC_CHIRP: return "Music Disc";
			case MUSIC_DISC_FAR: return "Music Disc";
			case MUSIC_DISC_MALL: return "Music Disc";
			case MUSIC_DISC_MELLOHI: return "Music Disc";
			case MUSIC_DISC_STAL: return "Music Disc";
			case MUSIC_DISC_STRAD: return "Music Disc";
			case MUSIC_DISC_WARD: return "Music Disc";
			case MUSIC_DISC_11: return "Music Disc";
			case MUSIC_DISC_WAIT: return "Music Disc";
			case BONE: return "Bone";
			case INK_SAC: return "Ink Sac";
			case COCOA_BEANS: return "Cocoa Beans";
			case LAPIS_LAZULI: return "Lapis Lazuli";
			case PURPLE_DYE: return "Purple Dye";
			case CYAN_DYE: return "Cyan Dye";
			case LIGHT_GRAY_DYE: return "Light Gray Dye";
			case GRAY_DYE: return "Gray Dye";
			case PINK_DYE: return "Pink Dye";
			case LIME_DYE: return "Lime Dye";
			case LIGHT_BLUE_DYE: return "Light Blue Dye";
			case MAGENTA_DYE: return "Magenta Dye";
			case ORANGE_DYE: return "Orange Dye";
			case BONE_MEAL: return "Bone Meal";
			case SUGAR: return "Sugar";
			case BLACK_BED: return "Black Bed";
			case RED_BED: return "Red Bed";
			case GREEN_BED: return "Green Bed";
			case BROWN_BED: return "Brown Bed";
			case BLUE_BED: return "Blue Bed";
			case PURPLE_BED: return "Purple Bed";
			case CYAN_BED: return "Cyan Bed";
			case LIGHT_GRAY_BED: return "Light Gray Bed";
			case GRAY_BED: return "Gray Bed";
			case PINK_BED: return "Pink Bed";
			case LIME_BED: return "Lime Bed";
			case YELLOW_BED: return "Yellow Bed";
			case LIGHT_BLUE_BED: return "Light Blue Bed";
			case MAGENTA_BED: return "Magenta Bed";
			case ORANGE_BED: return "Orange Bed";
			case WHITE_BED: return "White Bed";
			case REPEATER: return "Redstone Repeater";
			case COMPARATOR: return "Redstone Comparator";
			case FILLED_MAP: return "Map";
			case SHEARS: return "Shears";
			case ROTTEN_FLESH: return "Rotten Flesh";
			case ENDER_PEARL: return "Ender Pearl";
			case BLAZE_ROD: return "Blaze Rod";
			case GHAST_TEAR: return "Ghast Tear";
			case NETHER_WART: return "Nether Wart";
			case POTION: return "Potion";
			case SPLASH_POTION: return "Splash Potion";
			case LINGERING_POTION: return "Lingering Potion";
			case END_CRYSTAL: return "End Crystal";
			case GOLD_NUGGET: return "Gold Nugget";
			case GLASS_BOTTLE: return "Glass Bottle";
			case SPIDER_EYE: return "Spider Eye";
			case FERMENTED_SPIDER_EYE: return "Fermented Spider Eye";
			case BLAZE_POWDER: return "Blaze Powder";
			case MAGMA_CREAM: return "Magma Cream";
			case CAULDRON: return "Cauldron";
			case ENDER_EYE: return "Eye of Ender";
			case GLISTERING_MELON_SLICE: return "Glistering Melon Slice";
			case BAT_SPAWN_EGG: return "Bat Spawn Egg";
			case BLAZE_SPAWN_EGG: return "Blaze Spawn Egg";
			case CAVE_SPIDER_SPAWN_EGG: return "Cave Spider Spawn Egg";
			case CHICKEN_SPAWN_EGG: return "Chicken Spawn Egg";
			case COD_SPAWN_EGG: return "Cod Spawn Egg";
			case COW_SPAWN_EGG: return "Cow Spawn Egg";
			case CREEPER_SPAWN_EGG: return "Creeper Spawn Egg";
			case DOLPHIN_SPAWN_EGG: return "Dolphin Spawn Egg";
			case DONKEY_SPAWN_EGG: return "Donkey Spawn Egg";
			case DROWNED_SPAWN_EGG: return "Drowned Spawn Egg";
			case ELDER_GUARDIAN_SPAWN_EGG: return "Elder Guardian Spawn Egg";
			case ENDERMAN_SPAWN_EGG: return "Enderman Spawn Egg";
			case ENDERMITE_SPAWN_EGG: return "Endermite Spawn Egg";
			case EVOKER_SPAWN_EGG: return "Evoker Spawn Egg";
			case GHAST_SPAWN_EGG: return "Ghast Spawn Egg";
			case GUARDIAN_SPAWN_EGG: return "Guardian Spawn Egg";
			case HORSE_SPAWN_EGG: return "Horse Spawn Egg";
			case HUSK_SPAWN_EGG: return "Husk Spawn Egg";
			case LLAMA_SPAWN_EGG: return "Llama Spawn Egg";
			case MAGMA_CUBE_SPAWN_EGG: return "Magma Cube Spawn Egg";
			case MOOSHROOM_SPAWN_EGG: return "Mooshroom Spawn Egg";
			case MULE_SPAWN_EGG: return "Mule Spawn Egg";
			case OCELOT_SPAWN_EGG: return "Ocelot Spawn Egg";
			case PARROT_SPAWN_EGG: return "Parrot Spawn Egg";
			case PIG_SPAWN_EGG: return "Pig Spawn Egg";
			case PHANTOM_SPAWN_EGG: return "Phantom Spawn Egg";
			case POLAR_BEAR_SPAWN_EGG: return "Polar Bear Spawn Egg";
			case PUFFERFISH_SPAWN_EGG: return "Pufferfish Spawn Egg";
			case RABBIT_SPAWN_EGG: return "Rabbit Spawn Egg";
			case SALMON_SPAWN_EGG: return "Salmon Spawn Egg";
			case SHEEP_SPAWN_EGG: return "Sheep Spawn Egg";
			case SHULKER_SPAWN_EGG: return "Shulker Spawn Egg";
			case SILVERFISH_SPAWN_EGG: return "Silverfish Spawn Egg";
			case SKELETON_SPAWN_EGG: return "Skeleton Spawn Egg";
			case SKELETON_HORSE_SPAWN_EGG: return "Skeleton Horse Spawn Egg";
			case SLIME_SPAWN_EGG: return "Slime Spawn Egg";
			case SPIDER_SPAWN_EGG: return "Spider Spawn Egg";
			case SQUID_SPAWN_EGG: return "Squid Spawn Egg";
			case STRAY_SPAWN_EGG: return "Stray Spawn Egg";
			case TROPICAL_FISH_SPAWN_EGG: return "Tropical Fish Spawn Egg";
			case TURTLE_SPAWN_EGG: return "Turtle Spawn Egg";
			case VEX_SPAWN_EGG: return "Vex Spawn Egg";
			case VILLAGER_SPAWN_EGG: return "Villager Spawn Egg";
			case VINDICATOR_SPAWN_EGG: return "Vindicator Spawn Egg";
			case WITCH_SPAWN_EGG: return "Witch Spawn Egg";
			case WITHER_SKELETON_SPAWN_EGG: return "Wither Skeleton Spawn Egg";
			case WOLF_SPAWN_EGG: return "Wolf Spawn Egg";
			case ZOMBIE_SPAWN_EGG: return "Zombie Spawn Egg";
			case ZOMBIE_HORSE_SPAWN_EGG: return "Zombie Horse Spawn Egg";
			case ZOMBIE_PIGMAN_SPAWN_EGG: return "Zombie Pigman Spawn Egg";
			case ZOMBIE_VILLAGER_SPAWN_EGG: return "Zombie Villager Spawn Egg";
			case EXPERIENCE_BOTTLE: return "Bottle o' Enchanting";
			case FIRE_CHARGE: return "Fire Charge";
			case WRITABLE_BOOK: return "Book and Quill";
			case WRITTEN_BOOK: return "Written Book";
			case MAP: return "Empty Map";
			case CARROT: return "Carrot";
			case GOLDEN_CARROT: return "Golden Carrot";
			case POTATO: return "Potato";
			case BAKED_POTATO: return "Baked Potato";
			case POISONOUS_POTATO: return "Poisonous Potato";
			case CARROT_ON_A_STICK: return "Carrot on a Stick";
			case NETHER_STAR: return "Nether Star";
			case PUMPKIN_PIE: return "Pumpkin Pie";
			case ENCHANTED_BOOK: return "Enchanted Book";
			case FIREWORK_ROCKET: return "Firework Rocket";
			case NETHER_BRICK: return "Nether Brick";
			case QUARTZ: return "Nether Quartz";
			case ARMOR_STAND: return "Armor Stand";
			case IRON_HORSE_ARMOR: return "Iron Horse Armor";
			case GOLDEN_HORSE_ARMOR: return "Golden Horse Armor";
			case DIAMOND_HORSE_ARMOR: return "Diamond Horse Armor";
			case PRISMARINE_SHARD: return "Prismarine Shard";
			case PRISMARINE_CRYSTALS: return "Prismarine Crystals";
			case CHORUS_FRUIT: return "Chorus Fruit";
			case POPPED_CHORUS_FRUIT: return "Popped Chorus Fruit";
			case BEETROOT: return "Beetroot";
			case BEETROOT_SEEDS: return "Beetroot Seeds";
			case BEETROOT_SOUP: return "Beetroot Soup";
			case DRAGON_BREATH: return "Dragon's Breath";
			case ELYTRA: return "Elytra";
			case TOTEM_OF_UNDYING: return "Totem of Undying";
			case SHULKER_SHELL: return "Shulker Shell";
			case IRON_NUGGET: return "Iron Nugget";
			case KNOWLEDGE_BOOK: return "Knowledge Book";
			case DEBUG_STICK: return "Debug Stick";
			case TRIDENT: return "Trident";
			case SCUTE: return "Scute";
			case TURTLE_HELMET: return "Turtle Shell";
			case PHANTOM_MEMBRANE: return "Phantom Membrane";
			case NAUTILUS_SHELL: return "Nautilus Shell";
			case HEART_OF_THE_SEA: return "Heart of the Sea";
			case BLACK_BANNER: return "Black Banner";
			case RED_BANNER: return "Red Banner";
			case GREEN_BANNER: return "Green Banner";
			case BROWN_BANNER: return "Brown Banner";
			case BLUE_BANNER: return "Blue Banner";
			case PURPLE_BANNER: return "Purple Banner";
			case CYAN_BANNER: return "Cyan Banner";
			case LIGHT_GRAY_BANNER: return "Light Gray Banner";
			case GRAY_BANNER: return "Gray Banner";
			case PINK_BANNER: return "Pink Banner";
			case LIME_BANNER: return "Lime Banner";
			case YELLOW_BANNER: return "Yellow Banner";
			case LIGHT_BLUE_BANNER: return "Light Blue Banner";
			case MAGENTA_BANNER: return "Magenta Banner";
			case ORANGE_BANNER: return "Orange Banner";
			case WHITE_BANNER: return "White Banner";
			case SHIELD: return "Shield";
		}
		return null;
	}
}