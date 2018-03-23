package realcraft.bukkit.creative;

import java.util.HashMap;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

import realcraft.bukkit.RealCraft;

public class CancelGrow implements Listener {
	private RealCraft plugin;
	private HashMap<Block,Long> bonemealInteractions = new HashMap<Block,Long>();

	public CancelGrow(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	public void onReload(){
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		if(event.getAction() == Action.PHYSICAL){
			if(event.getClickedBlock().getType() == Material.SOIL){
				event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
				event.setCancelled(true);
			}
		}
		else if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
			if(item != null && item.getType() == Material.INK_SACK && item.getData() instanceof Dye && ((Dye)item.getData()).getColor() == DyeColor.WHITE){
				Block block = event.getClickedBlock();
				if(block.getType() != Material.SAPLING && block.getType() != Material.LONG_GRASS){
					bonemealInteractions.put(block,System.currentTimeMillis());
				}
			}
		}
	}

	@EventHandler
	public void BlockGrowEvent(BlockGrowEvent event){
		if(event.getNewState().getType() == Material.LONG_GRASS || event.getNewState().getType() == Material.RED_ROSE || event.getNewState().getType() == Material.YELLOW_FLOWER) return;
		if(!bonemealInteractions.containsKey(event.getBlock()) || bonemealInteractions.get(event.getBlock())+1000 < System.currentTimeMillis()){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void BlockSpreadEvent(BlockSpreadEvent event){
		if(event.getSource().getType() == Material.VINE || event.getSource().getType() == Material.BROWN_MUSHROOM || event.getSource().getType() == Material.RED_MUSHROOM) event.setCancelled(true);
	}

	@EventHandler
	public void BlockFadeEvent(BlockFadeEvent event){
		if(event.getBlock().getType() == Material.SOIL) event.setCancelled(true);
	}

	@EventHandler
	public void LeavesDecayEvent(LeavesDecayEvent event){
		event.setCancelled(true);
	}
}