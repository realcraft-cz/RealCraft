package realcraft.bukkit.develop;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import realcraft.bukkit.RealCraft;

public class LampControl implements Listener {

	public LampControl(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1).toLowerCase();
		boolean light = true;
		if((command.startsWith("lamp") || command.startsWith("/lamp")) && player.hasPermission("group.Manazer")){
			event.setCancelled(true);
			if(command.contains("off")) light = false;
			WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
			Region region;
			try {
				region = worldEdit.getSession(player).getSelection(worldEdit.getSession(player).getSelectionWorld());
			} catch (IncompleteRegionException e){
				player.sendMessage("§cMake a region selection first.");
				return;
			}
			World world = Bukkit.getWorld(region.getWorld().getName());
			BlockVector3 min = region.getMinimumPoint();
			BlockVector3 max = region.getMaximumPoint();
			int affected = 0;
			for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
				for (int y = min.getBlockY(); y <= max.getBlockY(); y++){
					for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
						Location loc = new Location(world, x, y, z);
						BlockVector3 vec = BlockVector3.at(x,y,z);
						if(region.contains(vec)){
							Block block = world.getBlockAt(loc);
							if(block.getType() == Material.REDSTONE_LAMP && switchLamp(block,light)){
								affected ++;
							}
						}
					}
				}
			}
			player.sendMessage("§dOperation completed ("+affected+" blocks affected).");
		}
	}

	public static boolean switchLamp(Block block,boolean light){
		Lightable lamp = (Lightable)block.getBlockData();
		if(light && !lamp.isLit()){
			lamp.setLit(true);
			block.setBlockData(lamp,false);
			return true;
		}
		else if(!light && lamp.isLit()){
			lamp.setLit(false);
			block.setBlockData(lamp,false);
			return true;
		}
		return false;
	}
}