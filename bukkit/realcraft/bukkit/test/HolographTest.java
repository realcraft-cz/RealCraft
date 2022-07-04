package realcraft.bukkit.test;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import realcraft.bukkit.RealCraft;

public class HolographTest implements Listener {

	public HolographTest(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1);
		if(command.startsWith("hdt") && player.hasPermission("group.Manazer")){
			event.setCancelled(true);

			Hologram hologramName = HolographicDisplaysAPI.get(RealCraft.getInstance()).createHologram(player.getLocation());
			hologramName.getLines().insertText(0,"0 hracu");
		}
	}
}