package realcraft.bukkit.test;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.RegionSelector;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.utils.MapUtil;

public class ImageTest implements Listener {

	public ImageTest(){
		RealCraft.getInstance().getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1);
		if(command.startsWith("image") && (player.hasPermission("group.Manazer") || player.hasPermission("group.Admin"))){
			event.setCancelled(true);
			String[] args = command.split(" ");
			if(args.length < 2){
				player.sendMessage("�f/image <url>");
				return;
			}
			LocalSession session = WorldEdit.getInstance().getSessionManager().findByName(player.getName());
			if(session != null && session.getSelectionWorld() != null){
				RegionSelector selector = session.getRegionSelector(session.getSelectionWorld());
				if(selector == null || !selector.isDefined()){
					player.sendMessage("�cMake a region selection first.");
					return;
				}
				Location location1 = player.getLocation();
				Location location2 = player.getLocation();
				try {
					BlockVector3 vec = selector.getPrimaryPosition();
					BlockVector3 vector;
					vector = selector.getRegion().getMinimumPoint();
					if(vec.getBlockX() == vector.getBlockX() && vec.getBlockZ() == vector.getBlockZ()){
						location1.setX(vector.getBlockX());
						location1.setY(vector.getBlockY());
						location1.setZ(vector.getBlockZ());
					} else {
						location2.setX(vector.getBlockX());
						location2.setY(vector.getBlockY());
						location2.setZ(vector.getBlockZ());
					}
					vector = selector.getRegion().getMaximumPoint();
					if(vec.getBlockX() == vector.getBlockX() && vec.getBlockZ() == vector.getBlockZ()){
						location1.setX(vector.getBlockX());
						location1.setY(vector.getBlockY());
						location1.setZ(vector.getBlockZ());
					} else {
						location2.setX(vector.getBlockX());
						location2.setY(vector.getBlockY());
						location2.setZ(vector.getBlockZ());
					}
				} catch (IncompleteRegionException e) {
					player.sendMessage("�cMake a region selection first.");
					return;
				}
				MapUtil.pasteMap(args[1],location1,location2);
			}
		}
	}
}