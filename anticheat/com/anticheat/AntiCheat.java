package com.anticheat;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.anticheat.checks.Check.CheckType;
import com.anticheat.checks.CheckEnchant;
import com.anticheat.checks.CheckFlyHack;
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

	private static final String CHANNEL_REPORT = "ACReport";
	private static final String CHANNEL_BAN = "ACBan";
	private static final String REPORTS = "anticheat_reports";
	private static final int REPORT_TIMEOUT = 0;

	private static HashMap<String,Long> exemptTime = new HashMap<String,Long>();

	private static HashMap<Player,AntiCheatPlayer> players = new HashMap<Player,AntiCheatPlayer>();

	public AntiCheat(RealCraft realcraft){
		plugin = realcraft;
		new CheckFlyHack();
		new CheckSpeedHack();
		//new CheckKillAura();
		if(plugin.serverName.equalsIgnoreCase("survival") || plugin.serverName.equalsIgnoreCase("creative")){
			new CheckEnchant();
		}
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
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
		RealCraft.getInstance().db.update("INSERT INTO "+REPORTS+" (user_id,report_type,report_server,report_created) VALUES('"+PlayerManazer.getPlayerInfo(player).getId()+"','"+event.getType().getId()+"','"+RealCraft.getServerType().toString()+"','"+(System.currentTimeMillis()/1000)+"')");
		if(event.getType().getBanLimit() != 0){
			if(AntiCheat.getPlayer(player).getTypeChecks(event.getType()) >= event.getType().getBanLimit()){
				AntiCheat.getPlayer(player).reset();
				Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
					@Override
					public void run(){
						AntiCheat.this.sendBanReport(player,event.getType());
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
			printReport(RealCraft.getServerName(event.getServer().toString()),data.getString("player"),data.getString("type"),data.getInt("checks"));
		}
		else if(data.getChannel().equalsIgnoreCase(CHANNEL_BAN)){
			printBanReport(RealCraft.getServerName(event.getServer().toString()),data.getString("player"),data.getString("type"));
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
				player.playSound(player.getLocation(),PlayerManazer.getPlayerInfo(player).getNoticeSound(),1,1);
			}
		}
	}

	private void sendReport(Player player,String type,int checks){
		if(AntiCheat.getPlayer(player).lastReported+AntiCheat.REPORT_TIMEOUT < System.currentTimeMillis()){
			printReport(RealCraft.getServerName(plugin.serverName),player.getName(),type,checks);
			AntiCheat.getPlayer(player).lastReported = System.currentTimeMillis();

			SocketData data = new SocketData(CHANNEL_REPORT);
			data.setString("player",player.getName());
			data.setString("type",type);
			data.setInt("checks",checks);
			SocketManager.sendToAll(data);
		}
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
}