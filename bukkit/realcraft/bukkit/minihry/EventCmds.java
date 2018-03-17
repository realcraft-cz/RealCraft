package realcraft.bukkit.minihry;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import realcraft.bukkit.RealCraft;

public class EventCmds implements Listener {
	RealCraft plugin;

	boolean enabled = false;
	String eventWarnMessage;
	String [] blockedCommands;

	public EventCmds(RealCraft realcraft){
		plugin = realcraft;
		if(plugin.config.getBoolean("eventcmds.enabled")){
			enabled = true;
			eventWarnMessage = plugin.config.getString("eventcmds.eventWarnMessage",null);
			List<String> tmpcmds = plugin.config.getStringList("eventcmds.blockedCommands."+plugin.serverName);
			blockedCommands = tmpcmds.toArray(new String[tmpcmds.size()]);
			plugin.getServer().getPluginManager().registerEvents(this,plugin);
		}
	}

	public void onReload(){
		enabled = false;
		if(plugin.config.getBoolean("eventcmds.enabled")){
			enabled = true;
			eventWarnMessage = plugin.config.getString("eventcmds.eventWarnMessage",null);
			List<String> tmpcmds = plugin.config.getStringList("eventcmds.blockedCommands."+plugin.serverName);
			blockedCommands = tmpcmds.toArray(new String[tmpcmds.size()]);
		}
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		if(!enabled || event.isCancelled()) return;
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1).toLowerCase();
		if(!player.hasPermission("group.Moderator") && !player.hasPermission("group.Admin")){
			boolean blocked = false;
			for(String cmd : blockedCommands){
				if(command.indexOf(cmd) == 0){
					blocked = true;
					break;
				}
			}
			if(blocked){
				player.sendMessage(RealCraft.parseColors(eventWarnMessage));
				event.setCancelled(true);
			}
		}
	}
}