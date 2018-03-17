package realcraft.bukkit.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.earth2me.essentials.Essentials;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.banmanazer.BanUtils;
import realcraft.bukkit.sockets.SocketData;
import realcraft.bukkit.sockets.SocketDataEvent;
import realcraft.bukkit.sockets.SocketManager;

public class ChatAdmin implements CommandExecutor, Listener {
	RealCraft plugin;
	Essentials essentials;
	private static final String CHANNEL_CHAT = "adminChat";

	public ChatAdmin(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		plugin.getCommand("ac").setExecutor(this);
	}

	public void onReload(){
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("ac")){
			if(!player.hasPermission("group.Admin") && !player.hasPermission("group.Moderator") && !player.hasPermission("group.Builder")) return true;
			if(args.length < 1){
				player.sendMessage("/ac <message>");
				return true;
			}
			sendAdminMessage(player,BanUtils.combineSplit(0,args));
		}
		return true;
	}

	private void sendAdminMessage(Player player,String message){
		this.printAdminMessage(player.getDisplayName(),message);
		plugin.chatlog.onPlayerAdminChat(player,message);

        SocketData data = new SocketData(CHANNEL_CHAT);
        data.setString("name",player.getDisplayName());
        data.setString("message",message);
        SocketManager.sendToAll(data);
	}

	private void printAdminMessage(String sender,String message){
		message = RealCraft.parseColors("&a[AdminChat] "+sender+": &c"+message);
		for(Player player : plugin.getServer().getOnlinePlayers()){
			if(player.hasPermission("group.Admin") || player.hasPermission("group.Moderator") || player.hasPermission("group.Builder")){
				player.sendMessage(message);
			}
		}
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(CHANNEL_CHAT)){
			this.printAdminMessage(data.getString("name"),data.getString("message"));
		}
	}
}