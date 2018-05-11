package realcraft.bukkit.lobby;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import realcraft.bukkit.RealCraft;

public class LobbyLightsOff implements Listener {

	public LobbyLightsOff(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}
}