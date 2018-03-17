package realcraft.bukkit.minihry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.sockets.SocketData;
import realcraft.bukkit.sockets.SocketDataEvent;
import realcraft.bukkit.sockets.SocketManager;

public class GamesReminder implements Listener {

	RealCraft plugin;
	private static final String CHANNEL_REMINDER = "gamesReminder";

	public GamesReminder(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	public void onReload(){
	}

	public void printGameStartingMessage(String server,String game,String prefix,int seconds,int players){
		TextComponent message = new TextComponent("§e\u2726 §fPrave zacina hra §e"+game+" §fs §e"+players+" hraci §7[§a§lPRIPOJIT SE§7]");
		message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/server "+server));
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§7Klikni pro pripojeni do hry").create()));
		for(Player player : Bukkit.getServer().getOnlinePlayers()){
			if(player.getWorld().getName().equalsIgnoreCase("world") || player.getWorld().getName().equalsIgnoreCase("world_creative") || player.getWorld().getName().equalsIgnoreCase("world_parkour")){
				player.spigot().sendMessage(message);
			}
		}
	}

	public static void sendGameStartingMessage(String game,String prefix,int seconds,int players){
		SocketData data = new SocketData(CHANNEL_REMINDER);
		data.setString("game",game);
		data.setInt("players",players);
		SocketManager.sendToAll(data);
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(CHANNEL_REMINDER)){
			this.printGameStartingMessage(event.getServer().toString().toLowerCase(),data.getString("game"),null,0,data.getInt("players"));
		}
	}
}