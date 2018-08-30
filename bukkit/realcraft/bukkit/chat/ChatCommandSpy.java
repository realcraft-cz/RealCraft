package realcraft.bukkit.chat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.others.AbstractCommand;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.List;

public class ChatCommandSpy extends AbstractCommand implements Listener {
	RealCraft plugin;

	boolean enabled = false;
	private static String commandMessage;
	private String [] blockedCommands;

	public ChatCommandSpy(RealCraft realcraft){
		super("commandspy");
		plugin = realcraft;
		if(plugin.config.getBoolean("commandspy.enabled")){
			enabled = true;
			commandMessage = plugin.config.getString("commandspy.message","");
			this.loadBlockedCommands();
			plugin.getServer().getPluginManager().registerEvents(this,plugin);
		}
	}

	public void onReload(){
		this.loadBlockedCommands();
	}

	private void loadBlockedCommands(){
		List<String> tmpurls = plugin.config.getStringList("commandspy.blockedCommands");
		int index = 0;
		blockedCommands = new String[tmpurls.size()];
		for(String url : tmpurls){
			blockedCommands[index++] = url;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		if(!enabled || event.isCancelled()) return;
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1);
		String commandName = event.getMessage().substring(1).split(" ")[0].trim();
		if(commandName.length() == 0) return;
		for(String cmd : blockedCommands){
		    if(commandName.equalsIgnoreCase(cmd)) return;
		}
		sendCommandMessage(player,command);
	}

	public static void sendCommandMessage(Player sender,String command){
		String commandMessage = getCommandMessage(sender,command);
		for(Player player : Bukkit.getServer().getOnlinePlayers()){
			if(player.hasPermission("group.Admin")){
				String option = PermissionsEx.getUser(player).getOption("commandspy");
				if(Boolean.valueOf(option)){
					player.sendMessage(commandMessage);
				}
			}
		}
		RealCraft.getInstance().chatlog.onPlayerCommand(sender,command);
	}

	public static String getCommandMessage(Player player,String command){
		String result = commandMessage;
		result = result.replaceAll("%player%",player.getName());
		result = result.replaceAll("%command%",command);
		return RealCraft.parseColors(result);
	}

	@Override
	public void perform(Player player,String[] args){
		if(player.hasPermission("group.Admin")){
			PermissionUser user = PermissionsEx.getUser(player);
			String option = user.getOption("commandspy");
			if(Boolean.valueOf(option)){
				user.setOption("commandspy","false");
				player.sendMessage("§6CommandSpy §cvypnut");
			} else {
				user.setOption("commandspy","true");
				player.sendMessage("§6CommandSpy §azapnut");
			}
		}
	}
}