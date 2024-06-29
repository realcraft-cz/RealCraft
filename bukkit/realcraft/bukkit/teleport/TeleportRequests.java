package realcraft.bukkit.teleport;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.earth2me.essentials.Essentials;

import realcraft.bukkit.RealCraft;

public class TeleportRequests implements Listener {
	RealCraft plugin;
	Essentials essentials;

	public TeleportRequests(RealCraft realcraft){
		plugin = realcraft;
		essentials = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	public void onReload(){
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		if(event.isCancelled()) return;
		Player player = event.getPlayer();
		String [] args = event.getMessage().substring(1).split(" ");

		if((args[0].equalsIgnoreCase("tpa") || args[0].equalsIgnoreCase("call") || args[0].equalsIgnoreCase("ecall") || args[0].equalsIgnoreCase("etpa") || args[0].equalsIgnoreCase("tpask") || args[0].equalsIgnoreCase("etpask") || args[0].equalsIgnoreCase("tpahere") || args[0].equalsIgnoreCase("etpahere")) && args.length > 1){
			Player recipient = plugin.getServer().getPlayer(args[1]);
			if(recipient != null){
				long timeout = essentials.getSettings().getTpaAcceptCancellation();
				if(essentials.getUser(recipient).getNextTpaRequest(false, false, false).getRequesterUuid() == essentials.getUser(player).getBase().getUniqueId() && (System.currentTimeMillis()-essentials.getUser(recipient).getTeleportRequestTime())/1000 <= timeout){
					player.sendMessage(RealCraft.parseColors("&cZadost o teleportaci je jiz odeslana."));
					event.setCancelled(true);
					event.setMessage("/");
				}
			}
		}
	}
}