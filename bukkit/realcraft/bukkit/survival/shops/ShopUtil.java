package realcraft.bukkit.survival.shops;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;

public class ShopUtil {

	public static boolean isChest(Block block){
		return isChest(block,true);
	}

	public static boolean isChest(Block block,boolean checkdouble){
		if(block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST){
			if(!checkdouble) return true;
			Chest chest = (Chest) block.getState();
			if(!(chest.getInventory().getHolder() instanceof DoubleChest)) return true;
		}
		return false;
	}
}