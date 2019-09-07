package realcraft.bukkit.skins;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.sockets.SocketData;
import realcraft.bukkit.sockets.SocketDataEvent;

import java.util.UUID;

public class Skins implements Listener {

	public static final String CHANNEL_SKIN_RESET = "bungeeSkinReset";

	public Skins(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(CHANNEL_SKIN_RESET)){
			Player player = Bukkit.getPlayer(UUID.fromString(data.getString("uuid")));
			if(player != null && player.isOnline()) SkinHandler_v1_13_R1.updateSkin(player);
		}
	}
}