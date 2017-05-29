package com.anticheat;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.anticheat.checks.CheckEnchant;
import com.anticheat.checks.CheckFlyHack;
import com.anticheat.checks.CheckKillAura;
import com.anticheat.checks.CheckSpeedHack;
import com.anticheat.events.AntiCheatDetectEvent;
import com.realcraft.RealCraft;
import com.realcraft.banmanazer.BanManazer;
import com.realcraft.playermanazer.PlayerManazer;
import com.realcraft.sockets.SocketData;
import com.realcraft.sockets.SocketDataEvent;
import com.realcraft.sockets.SocketManager;

//https://github.com/m1enkrafftman/AntiCheatPlus/blob/master/src/main/java/net/dynamicdev/anticheat/check/checks/MovementCheck.java

public class AntiCheat implements Listener {
	RealCraft plugin;

	boolean enabled = false;
	String warnMessage;

	static final String CHANNEL_REPORT = "ACReport";
	static final String REPORTS = "anticheat_reports";
	static final int REPORT_TIMEOUT = 0;

	static HashMap<String,Long> exemptTime = new HashMap<String,Long>();

	private static HashMap<Player,AntiCheatPlayer> players = new HashMap<Player,AntiCheatPlayer>();

	public AntiCheat(RealCraft realcraft){
		plugin = realcraft;
		if(plugin.config.getBoolean("anticheat.enabled")){
			enabled = true;
			new CheckFlyHack();
			new CheckSpeedHack();
			new CheckKillAura();
			if(plugin.serverName.equalsIgnoreCase("survival") || plugin.serverName.equalsIgnoreCase("creative")){
				new CheckEnchant();
			}

			plugin.getServer().getPluginManager().registerEvents(this,plugin);
			warnMessage = plugin.config.getString("anticheat.warnMessage","&c[AntiCheat | &7%server%&c] &f%player% &7 | %reason%");
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
		this.sendReport(player,event.getType().toString());
		RealCraft.getInstance().db.update("INSERT INTO "+REPORTS+" (user_id,report_type,report_created) VALUES('"+PlayerManazer.getPlayerInfo(player).getId()+"','"+event.getType().getId()+"','"+(System.currentTimeMillis()/1000)+"')");
		if(event.getType().getBanLimit() != 0){
			AntiCheat.getPlayer(player).addTypeCheck(event.getType());
			if(AntiCheat.getPlayer(player).getTypeChecks(event.getType()) >= event.getType().getBanLimit()){
				AntiCheat.getPlayer(player).reset();
				Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
					@Override
					public void run(){
						BanManazer.banPlayer(player,(int)((System.currentTimeMillis()/1000)+30*86400),event.getType().toString(),null);
					}
				});
			}
		}
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(CHANNEL_REPORT)){
			printReport(RealCraft.getServerName(event.getServer().toString()),data.getString("player"),data.getString("reason"));
		}
	}

	public static void exempt(Player player,long time){
		exemptTime.put(player.getName(),System.currentTimeMillis()+time);
	}

	public static boolean isPlayerExempted(Player player){
		if(exemptTime.containsKey(player.getName()) && exemptTime.get(player.getName()) >= System.currentTimeMillis()) return true;
		return false;
	}

	public static void DEBUG(String message){
		System.out.println("[AC] "+message);
	}

	private void printReport(String server,String name,String reason){
		String reportMessage = warnMessage;
		reportMessage = reportMessage.replaceAll("%server%",RealCraft.getServerName(server));
		reportMessage = reportMessage.replaceAll("%player%",name);
		reportMessage = reportMessage.replaceAll("%reason%",reason);
		reportMessage = RealCraft.parseColors(reportMessage);

		for(Player player : plugin.getServer().getOnlinePlayers()){
			if(player.hasPermission("group.Admin")){
				player.sendMessage(reportMessage);
				player.playSound(player.getLocation(),PlayerManazer.getPlayerInfo(player).getNoticeSound(),1,1);
			}
		}
	}

	private void sendReport(Player player,String reason){
		if(AntiCheat.getPlayer(player).lastReported+REPORT_TIMEOUT < System.currentTimeMillis()){
			printReport(plugin.serverName,player.getName(),reason);
			AntiCheat.getPlayer(player).lastReported = System.currentTimeMillis();

			SocketData data = new SocketData(CHANNEL_REPORT);
			data.setString("player",player.getName());
			data.setString("reason",reason);
			SocketManager.sendToAll(data);
		}
	}
}