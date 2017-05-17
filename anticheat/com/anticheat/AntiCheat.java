package com.anticheat;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.anticheat.checks.EnchantmentCheck;
import com.anticheat.checks.FightCheck;
import com.anticheat.checks.MovementCheck;
import com.anticheat.events.AntiCheatDetectEvent;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.realcraft.RealCraft;
import com.realcraft.banmanazer.BanUtils;
import com.realcraft.playermanazer.PlayerManazer;
import com.realcraft.utils.Title;

//https://github.com/m1enkrafftman/AntiCheatPlus/blob/master/src/main/resources/magic.yml
//https://github.com/m1enkrafftman/AntiCheatPlus/blob/master/src/main/java/net/dynamicdev/anticheat/check/checks/MovementCheck.java

public class AntiCheat implements Listener, PluginMessageListener, Runnable, CommandExecutor, TabCompleter {
	RealCraft plugin;

	boolean enabled = false;
	String warnMessage;

	static final String CHANNEL_REPORT = "ACReport";
	static final String CHANNEL_SPECT = "ACSpec";
	static final String CHANNEL_PLAYERLIST = "PlayerList";
	static final String REPORTS = "anticheat_reports";

	MovementCheck movementCheck;
	FightCheck fightCheck;
	EnchantmentCheck enchantmentCheck;

	static HashMap<String,Long> exemptTime = new HashMap<String,Long>();
	HashMap<String,Long> lastDetected = new HashMap<String,Long>();

	private HashMap<String,String> playerList = new HashMap<String,String>();
	static HashMap<String,AntiCheatSpec> playerSpec = new HashMap<String,AntiCheatSpec>();

	public AntiCheat(RealCraft realcraft){
		plugin = realcraft;
		if(plugin.config.getBoolean("anticheat.enabled")){
			enabled = true;
			movementCheck = new MovementCheck(this);
			fightCheck = new FightCheck(this);
			if(plugin.serverName.equalsIgnoreCase("survival") || plugin.serverName.equalsIgnoreCase("creative")) enchantmentCheck = new EnchantmentCheck(this);

			plugin.getServer().getPluginManager().registerEvents(this,plugin);
			plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin,"BungeeCord");
			plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin,"BungeeCord",this);
			plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,this,2*20,2*20);

			plugin.getCommand("spec").setExecutor(this);
			plugin.getCommand("specoff").setExecutor(this);

			warnMessage = plugin.config.getString("anticheat.warnMessage","&c[AntiCheat | &7%server%&c] &f%player% &7 | %reason%");
		}
	}

	public void onReload(){
	}

	@Override
	public void run(){
		this.sendUpdatePlayers();
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void PlayerJoinEvent(PlayerJoinEvent event){
		final Player player = event.getPlayer();
		if(isPlayerSpectating(player)){
			final AntiCheatSpec spec = playerSpec.get(player.getName());
			if(spec.getCreated()+10000 > System.currentTimeMillis()){
				spec.setLastState(player);
				player.setGameMode(GameMode.SPECTATOR);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RealCraft.getInstance(),new Runnable(){
					@Override
					public void run(){
						spec.spectate();
					}
				},20);
			}
		}
	}

	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent event){
		for(AntiCheatSpec spec : playerSpec.values()){
			if(spec.getVictim().equals(event.getPlayer().getName())){
				spec.cancel();
			}
		}
	}

	@EventHandler
	public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event){
		for(AntiCheatSpec spec : playerSpec.values()){
			if(spec.getVictim().equals(event.getPlayer().getName())){
				spec.changeWorld();
			}
		}
	}

	@EventHandler
	public void AntiCheatCheckEvent(AntiCheatDetectEvent event){
		Player player = event.getPlayer();
		this.sendReport(player,event.getType().toString());
		AntiCheat.DEBUG(player.getName()+" | "+event.getType().toString());
		RealCraft.getInstance().db.update("INSERT INTO "+REPORTS+" (user_id,report_type,report_created) VALUES('"+PlayerManazer.getPlayerInfo(player).getId()+"','"+event.getType().getId()+"','"+(System.currentTimeMillis()/1000)+"')");
	}

	public static void exempt(Player player,long time){
		exemptTime.put(player.getName(),System.currentTimeMillis()+time);
	}

	public static boolean isPlayerExempted(Player player){
		if(exemptTime.containsKey(player.getName()) && exemptTime.get(player.getName()) >= System.currentTimeMillis()) return true;
		return false;
	}

	public static boolean isPlayerSpectating(Player player){
		if(playerSpec.get(player.getName()) != null) return true;
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
				player.playSound(player.getLocation(),plugin.playermanazer.getPlayerInfo(player).getNoticeSound(),1,1);
			}
		}
	}

	private void sendReport(Player player,String reason){
		if(!lastDetected.containsKey(player.getName()) || lastDetected.get(player.getName())+10000 < System.currentTimeMillis()){
			printReport(plugin.serverName,player.getDisplayName(),reason);
			lastDetected.put(player.getName(),System.currentTimeMillis());

			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Forward");
			out.writeUTF("ONLINE");
			out.writeUTF(AntiCheat.CHANNEL_REPORT);

			reason = plugin.serverName+";"+player.getDisplayName()+";"+reason;
			byte[] data = reason.getBytes();
	        out.writeShort(data.length);
	        out.write(data);

			player.sendPluginMessage(plugin,"BungeeCord",out.toByteArray());
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("spec")){
			if(player.hasPermission("group.Admin") || player.hasPermission("group.Moderator")){
				if(args.length == 0){
					if(isPlayerSpectating(player)){
						playerSpec.get(player.getName()).cancel();
					} else {
						player.sendMessage("Sledovat hrace.");
						player.sendMessage("/spec <player>");
					}
					return true;
				}
				else if(args.length == 1){
					String victim = this.findPlayer(args[0]);
					if(victim == null){
						player.sendMessage(RealCraft.parseColors("&cHrac nenalezen."));
						return true;
					}
					else if(victim.equalsIgnoreCase(player.getName())){
						player.sendMessage(RealCraft.parseColors("&cNemuzes spectovat sam sebe."));
						return true;
					}
					this.spectatePlayer(player,victim);
				}
			}
		}
		else if(command.getName().equalsIgnoreCase("specoff")){
			if(player.hasPermission("group.Admin") || player.hasPermission("group.Moderator")){
				if(args.length == 0){
					if(isPlayerSpectating(player)){
						playerSpec.get(player.getName()).cancel();
						return true;
					}
				}
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("spec")){
			if(player.hasPermission("group.Admin") && player.hasPermission("group.Moderator")){
				if(args.length == 1){
					return this.findPlayers(args[0]);
				}
			}
		}
		return null;
	}

	private List<String> findPlayers(String victim){
		victim = victim.toLowerCase();
		List<String> players = new ArrayList<String>();
		for(String player : this.playerList.keySet()){
			if(player.toLowerCase().startsWith(victim)) players.add(player);
		}
		return players;
	}

	private String findPlayer(String victim){
		victim = victim.toLowerCase();
		for(String player : this.playerList.keySet()){
			if(player.toLowerCase().startsWith(victim)) return player;
		}
		return null;
	}

	private void spectatePlayer(Player player,String victim){
		Player victimPlayer = plugin.getServer().getPlayer(victim);
		if(victimPlayer == null){
			String server = this.playerList.get(victim);
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Forward");
			out.writeUTF(server);
			out.writeUTF(AntiCheat.CHANNEL_SPECT);

			String message = player.getName()+";"+victim;
			byte[] data = message.getBytes();
	        out.writeShort(data.length);
	        out.write(data);

			player.sendPluginMessage(plugin,"BungeeCord",out.toByteArray());
			this.connectPlayerToServer(player,server);
		} else {
			if(isPlayerSpectating(player)){
				playerSpec.get(player.getName()).setVictim(victim);
				playerSpec.get(player.getName()).spectate();
			} else {
				AntiCheatSpec spec = new AntiCheatSpec(player.getName(),victim);
				playerSpec.put(player.getName(),spec);
				spec.spectate();
			}
		}
	}

	private void sendUpdatePlayers(){
		this.playerList = new HashMap<String,String>();
		for(String server : plugin.lobby.lobbymenu.servers){
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF(AntiCheat.CHANNEL_PLAYERLIST);
			out.writeUTF(server);
			Bukkit.getServer().sendPluginMessage(plugin,"BungeeCord",out.toByteArray());
		}
	}

	@Override
	public void onPluginMessageReceived(String channel,Player _player,byte[] message){
		if(!channel.equals("BungeeCord")) return;
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
			String subchannel = in.readUTF();
			if(!subchannel.equals(AntiCheat.CHANNEL_REPORT) && !subchannel.equals(AntiCheat.CHANNEL_SPECT) && !subchannel.equals(AntiCheat.CHANNEL_PLAYERLIST)) return;

			if(subchannel.equals(AntiCheat.CHANNEL_REPORT)){
				short len = in.readShort();
				byte[] data = new byte[len];
				in.readFully(data);
				String [] messageData = new String(data).split(";");
				String server = messageData[0];
				String name = messageData[1];
				String reason = BanUtils.combineSplit(2,messageData);
				printReport(server,name,reason);
			}
			else if(subchannel.equals(AntiCheat.CHANNEL_SPECT)){
				short len = in.readShort();
				byte[] data = new byte[len];
				in.readFully(data);
				String [] messageData = new String(data).split(";");
				String player = messageData[0];
				String victim = messageData[1];
				playerSpec.put(player,new AntiCheatSpec(player,victim));
			}
			else if(subchannel.equals(AntiCheat.CHANNEL_PLAYERLIST)){
				String server = in.readUTF();
				String[] players = in.readUTF().split(", ");
				for(String player : players){
					if(player.length() > 1){
						this.playerList.put(player,server);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void connectPlayerToServer(Player player,String server){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(server);
		player.sendPluginMessage(plugin,"BungeeCord",out.toByteArray());
	}

	private class AntiCheatSpec implements Runnable {
		private String player;
		private String victim;
		private long created;
		private boolean enabled = false;

		private Location lastLocation;
		private GameMode lastGameMode;

		public AntiCheatSpec(String player,String victim){
			this.player = player;
			this.victim = victim;
			this.created = System.currentTimeMillis();
			plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,this,3*20,3*20);
		}

		public void setVictim(String victim){
			this.victim = victim;
		}

		public String getVictim(){
			return this.victim;
		}

		public long getCreated(){
			return this.created;
		}

		public void setLastState(Player player){
			this.lastLocation = player.getLocation();
			if(this.lastGameMode == null) this.lastGameMode = player.getGameMode();
		}

		public void spectate(){
			Player player = plugin.getServer().getPlayer(this.player);
			Player victim = plugin.getServer().getPlayer(this.victim);
			if(player != null && victim != null && player.isOnline() && victim.isOnline()){
				if(!this.enabled) this.setLastState(player);
				this.enabled = true;
				player.setGameMode(GameMode.SPECTATOR);
				player.teleport(victim.getLocation().add(0,2,0));
				player.setSpectatorTarget(victim);
				Title.showTitle(player," ",0,3,0);
				Title.showSubTitle(player,"§f"+victim.getDisplayName(),0,3,0);
				Title.showActionTitle(player,"Napis §6/spec§f pro ukonceni spectatora",3*20);
			}
			else this.cancel();
		}

		public void changeWorld(){
			if(this.enabled){
				final Player player = plugin.getServer().getPlayer(this.player);
				final Player victim = plugin.getServer().getPlayer(this.victim);
				if(player != null && victim != null && player.isOnline() && victim.isOnline()){
					player.setGameMode(GameMode.SPECTATOR);
					if(player.getSpectatorTarget() == victim){
						player.setSpectatorTarget(null);
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RealCraft.getInstance(),new Runnable(){
							@Override
							public void run(){
								player.teleport(victim.getLocation().add(0,2,0));
								player.setSpectatorTarget(victim);
							}
						},10);
					}
				}
			}
		}

		public void cancel(){
			if(this.enabled){
				this.enabled = false;
				Player player = plugin.getServer().getPlayer(this.player);
				if(player != null && player.isOnline()){
					if(player.getSpectatorTarget() != null) player.setSpectatorTarget(null);
					player.setGameMode(this.lastGameMode);
					player.teleport(lastLocation);
					Title.showTitle(player," ",0,1,0);
					Title.showSubTitle(player," ",0,1,0);
					Title.showActionTitle(player," ",20);
				}
			}
		}

		@Override
		public void run(){
			if(this.enabled){
				Player player = plugin.getServer().getPlayer(this.player);
				Player victim = plugin.getServer().getPlayer(this.victim);
				if(player != null && victim != null && player.isOnline() && victim.isOnline() && player.getGameMode() == GameMode.SPECTATOR){
					Title.showTitle(player," ",0,3,0);
					Title.showSubTitle(player,"§f"+victim.getDisplayName(),0,3,0);
					Title.showActionTitle(player,"Napis §6/spec§f pro ukonceni spectatora",3*20);
				}
				else this.cancel();
			}
		}
	}
}