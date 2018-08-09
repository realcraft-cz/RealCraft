package realcraft.bukkit.lobby;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.spawn.ServerSpawn;

public class LobbySpawn implements Listener {
	RealCraft plugin;

	public LobbySpawn(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void PlayerJoinEvent(PlayerJoinEvent event){
		if(ServerSpawn.getLocation() != null) event.getPlayer().teleport(ServerSpawn.getLocation());
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		if(ServerSpawn.getLocation() != null) event.getPlayer().teleport(ServerSpawn.getLocation());
	}
}