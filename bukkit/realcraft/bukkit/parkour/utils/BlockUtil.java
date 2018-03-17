package realcraft.bukkit.parkour.utils;

import org.bukkit.Material;

public class BlockUtil {

	public static boolean isBlockValid(Material material){
		switch(material){
			case END_CRYSTAL: return false;
			case TNT: return false;
			case REDSTONE: return false;
			case REDSTONE_COMPARATOR: return false;
			case REDSTONE_COMPARATOR_ON: return false;
			case REDSTONE_TORCH_OFF: return false;
			case REDSTONE_TORCH_ON: return false;
			case REDSTONE_WIRE: return false;
			case OBSERVER: return false;
			case DAYLIGHT_DETECTOR: return false;
			case DAYLIGHT_DETECTOR_INVERTED: return false;
			case MINECART: return false;
			case COMMAND_MINECART: return false;
			case EXPLOSIVE_MINECART: return false;
			case HOPPER_MINECART: return false;
			case POWERED_MINECART: return false;
			case STORAGE_MINECART: return false;
			case BOAT: return false;
			case BOAT_ACACIA: return false;
			case BOAT_BIRCH: return false;
			case BOAT_DARK_OAK: return false;
			case BOAT_JUNGLE: return false;
			case BOAT_SPRUCE: return false;
			case POTION: return false;
			case LINGERING_POTION: return false;
			case SPLASH_POTION: return false;
			case MOB_SPAWNER: return false;
			case EGG: return false;
			case MONSTER_EGG: return false;
			case MONSTER_EGGS: return false;
			case BEACON: return false;
			case ENCHANTED_BOOK: return false;
			case WRITTEN_BOOK: return false;
			case ENDER_PEARL: return false;
			case EYE_OF_ENDER: return false;
			case EXP_BOTTLE: return false;
			case FIREBALL: return false;
			case WATCH: return false;
			case COMPASS: return false;
			case BLAZE_POWDER: return false;
			case BLAZE_ROD: return false;
			case BREWING_STAND: return false;
			case BREWING_STAND_ITEM: return false;
			case DRAGONS_BREATH: return false;
			case SHULKER_SHELL: return false;
			default: return true;
		}
	}
}