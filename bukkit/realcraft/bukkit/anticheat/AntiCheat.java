package realcraft.bukkit.anticheat;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.anticheat.checks.Check.CheckType;
import realcraft.bukkit.anticheat.checks.*;
import realcraft.bukkit.anticheat.events.AntiCheatDetectEvent;
import realcraft.bukkit.banmanazer.BanManazer;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.sockets.SocketData;
import realcraft.bukkit.sockets.SocketDataEvent;
import realcraft.bukkit.sockets.SocketManager;
import realcraft.bukkit.users.Users;

import java.util.HashMap;

//https://github.com/m1enkrafftman/AntiCheatPlus/blob/master/src/main/java/net/dynamicdev/anticheat/check/checks/MovementCheck.java

public class AntiCheat implements Listener {
	RealCraft plugin;

	public static boolean DEBUG = false;
	private static final String CHANNEL_REPORT = "ACReport";
	private static final String CHANNEL_BAN = "ACBan";
	private static final String REPORTS = "anticheat_reports";

	private static HashMap<String,Long> exemptTime = new HashMap<String,Long>();

	private static HashMap<Player,AntiCheatPlayer> players = new HashMap<Player,AntiCheatPlayer>();

	public AntiCheat(RealCraft realcraft){
		plugin = realcraft;
		new CheckFlyHack();
		new CheckSpeedHack();
		new CheckSneakHack();
		new CheckClickAura();
		new CheckKillAura();
		if(plugin.serverName.equalsIgnoreCase("survival") || plugin.serverName.equalsIgnoreCase("creative")){
			new CheckEnchant();
		}
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		if(RealCraft.isTestServer()){
			DEBUG = true;
		}
	}

	public static AntiCheatPlayer getPlayer(Player player){
		if(!players.containsKey(player)) players.put(player,new AntiCheatPlayer(player));
		return players.get(player);
	}

	public static void removePlayer(Player player){
		players.remove(player);
	}

	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent event){
		removePlayer(event.getPlayer());
	}

	@EventHandler
	public void AntiCheatCheckEvent(AntiCheatDetectEvent event){
		Player player = event.getPlayer();
		if(event.getType().getBanLimit() != 0) AntiCheat.getPlayer(player).addTypeCheck(event.getType());
		this.sendReport(player,event.getType().toString(),AntiCheat.getPlayer(player).getTypeChecks(event.getType()));
		if(!DEBUG) DB.update("INSERT INTO "+REPORTS+" (user_id,report_type,report_ping,report_server,report_created) VALUES('"+Users.getUser(player).getId()+"','"+event.getType().getId()+"','"+Users.getUser(player).getPing()+"','"+RealCraft.getServerType().toString()+"','"+(System.currentTimeMillis()/1000)+"')");
		if(event.getType().getBanLimit() != 0){
			if(AntiCheat.getPlayer(player).getTypeChecks(event.getType()) >= event.getType().getBanLimit()){
				AntiCheat.getPlayer(player).reset();
				Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
					@Override
					public void run(){
						AntiCheat.this.sendBanReport(player,event.getType());
						if(!DEBUG) BanManazer.banPlayer(player,(int)((System.currentTimeMillis()/1000)+30*86400),event.getType().toString(),null);
					}
				});
			}
		}
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(CHANNEL_REPORT)){
			printReport(event.getServer().getName(),data.getString("player"),data.getString("type"),data.getInt("checks"));
		}
		else if(data.getChannel().equalsIgnoreCase(CHANNEL_BAN)){
			printBanReport(event.getServer().getName(),data.getString("player"),data.getString("type"));
		}
	}

	public static void exempt(Player player,long time){
		exemptTime.put(player.getName(),System.currentTimeMillis()+time);
	}

	public static boolean isPlayerExempted(Player player){
		if(exemptTime.containsKey(player.getName()) && exemptTime.get(player.getName()) >= System.currentTimeMillis()) return true;
		return false;
	}

	private void printReport(String server,String name,String _type,int checks){
		for(Player player : plugin.getServer().getOnlinePlayers()){
			if(player.hasPermission("group.Admin")){
				CheckType type = CheckType.getByName(_type);
				player.sendMessage("§c[AC | §7"+server+"§c] §f"+name+" §7| "+type.toString()+" [§f"+checks+"/"+type.getBanLimit()+"§7]");
				player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_PLING,1f,1f);
			}
		}
	}

	private void sendReport(Player player,String type,int checks){
		SocketData data = new SocketData(CHANNEL_REPORT);
		data.setString("player",player.getName());
		data.setString("type",type);
		data.setInt("checks",checks);
		SocketManager.sendToAll(data,true);
	}

	private void sendBanReport(Player player,CheckType type){
		SocketData data = new SocketData(CHANNEL_BAN);
		data.setString("player",player.getName());
		data.setString("type",type.toString());
		SocketManager.sendToAll(data);
	}

	private void printBanReport(String server,String name,String type){
		for(Player player : plugin.getServer().getOnlinePlayers()){
			if(player.hasPermission("group.Admin")){
				player.sendMessage("§c[AC | §7"+server+"§c] §f"+name+" §6byl zabanovan za §7"+type);
			}
		}
	}

	public static void DEBUG(String message){
		if(DEBUG) System.out.println("[AntiCheat] "+message);
	}
}