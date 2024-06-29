package realcraft.bukkit.creative;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import realcraft.bukkit.RealCraft;
import realcraft.share.ServerType;

import java.util.HashMap;

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
			if(event.getClickedBlock().getType() == Material.FARMLAND){
				event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
				event.setCancelled(true);
			}
		}
		else if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
			if(item != null && item.getType() == Material.INK_SAC && item.getData() instanceof Dye && ((Dye)item.getData()).getColor() == DyeColor.WHITE){
				Block block = event.getClickedBlock();
				if(block.getType() != Material.ACACIA_SAPLING && block.getType() != Material.TALL_GRASS){
					bonemealInteractions.put(block,System.currentTimeMillis());
				}
			}
		}
	}

	@EventHandler
	public void BlockGrowEvent(BlockGrowEvent event){
		if(event.getNewState().getType() == Material.SHORT_GRASS || event.getNewState().getType() == Material.TALL_GRASS || event.getNewState().getType() == Material.POPPY || event.getNewState().getType() == Material.DANDELION) return;
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
		if(event.getBlock().getType() == Material.FARMLAND) event.setCancelled(true);
	}

	@EventHandler
	public void LeavesDecayEvent(LeavesDecayEvent event){
		event.setCancelled(true);
	}

	@EventHandler
	public void ChunkLoadEvent(ChunkLoadEvent event){
		if(RealCraft.getServerType() == ServerType.CREATIVE){
			int count = 0;
			int removed = 0;
			for(Entity entity : event.getChunk().getEntities()){
				count ++;
				if(entity.getType() == EntityType.MINECART || entity.getType() == EntityType.CHEST_MINECART || entity.getType() == EntityType.FURNACE_MINECART || entity.getType() == EntityType.HOPPER_MINECART || entity.getType() == EntityType.TNT_MINECART){
					removed ++;
					entity.remove();
				}
			}
			if(removed > 0){
				System.out.println("ChunkLoadEvent removed "+removed);
			}
			if(count > 0){
				System.out.println("ChunkLoadEvent count "+count);
			}
		}
	}
}