package com.realcraft.spectator;

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
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.realcraft.RealCraft;
import com.realcraft.sockets.SocketData;
import com.realcraft.sockets.SocketDataEvent;
import com.realcraft.sockets.SocketManager;
import com.realcraft.sockets.SocketManager.SocketServer;
import com.realcraft.utils.Title;

public class Spectator implements CommandExecutor, TabCompleter, Runnable {
	RealCraft plugin;

	private static final String CHANNEL_SPEC = "spectatorSpec";
	private static final String CHANNEL_PLAYERLIST = "spectatorPlayers";
	private static HashMap<String,PlayerSpectate> playerSpec = new HashMap<String,PlayerSpectate>();
	private HashMap<String,SpectatorServerPlayer> playerList = new HashMap<String,SpectatorServerPlayer>();

	public Spectator(RealCraft realcraft){
		plugin = realcraft;
		plugin.getCommand("spec").setExecutor(this);
		plugin.getCommand("specoff").setExecutor(this);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,this,2*20,2*20);
	}

	@Override
	public void run(){
		this.sendUpdatePlayers();
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(CHANNEL_SPEC)){
			playerSpec.put(data.getString("player"),new PlayerSpectate(data.getString("player"),data.getString("victim")));
		}
		else if(data.getChannel().equalsIgnoreCase(CHANNEL_PLAYERLIST)){
			String[] players = data.getString("players").split(";");
			for(String player : players){
				if(player.length() > 1){
					playerList.put(player,new SpectatorServerPlayer(player,event.getServer().toString().toLowerCase()));
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void PlayerJoinEvent(PlayerJoinEvent event){
		final Player player = event.getPlayer();
		if(isPlayerSpectating(player)){
			PlayerSpectate spec = playerSpec.get(player.getName());
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
		for(PlayerSpectate spec : playerSpec.values()){
			if(spec.getVictim().equals(event.getPlayer().getName())){
				spec.cancel();
			}
		}
	}

	@EventHandler
	public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event){
		for(PlayerSpectate spec : playerSpec.values()){
			if(spec.getVictim().equals(event.getPlayer().getName())){
				spec.changeWorld();
			}
		}
	}

	private List<String> findPlayers(String victim){
		victim = victim.toLowerCase();
		List<String> players = new ArrayList<String>();
		for(SpectatorServerPlayer player : this.playerList.values()){
			if(player.getCreated()+4000 > System.currentTimeMillis() && player.getPlayer().toLowerCase().startsWith(victim)) players.add(player.getPlayer());
		}
		return players;
	}

	private String findPlayer(String victim){
		victim = victim.toLowerCase();
		for(SpectatorServerPlayer player : this.playerList.values()){
			if(player.getCreated()+4000 > System.currentTimeMillis() && player.getPlayer().toLowerCase().startsWith(victim)) return player.getPlayer();
		}
		return null;
	}

	public static boolean isPlayerSpectating(Player player){
		if(playerSpec.get(player.getName()) != null) return true;
		return false;
	}

	private void spectatePlayer(Player player,String victim){
		Player victimPlayer = plugin.getServer().getPlayer(victim);
		if(victimPlayer == null){
			SpectatorServerPlayer serverPlayer = this.playerList.get(victim);

	        SocketData data = new SocketData(CHANNEL_SPEC);
			data.setString("player",serverPlayer.getPlayer());
			data.setString("victim",victim);
			SocketManager.send(SocketServer.getByName(serverPlayer.getServer()),data);

			this.connectPlayerToServer(player,serverPlayer.getServer());
		} else {
			if(isPlayerSpectating(player)){
				playerSpec.get(player.getName()).setVictim(victim);
				playerSpec.get(player.getName()).spectate();
			} else {
				PlayerSpectate spec = new PlayerSpectate(player.getName(),victim);
				playerSpec.put(player.getName(),spec);
				spec.spectate();
			}
		}
	}

	private void sendUpdatePlayers(){
		SocketData data = new SocketData(CHANNEL_PLAYERLIST);
		String players = "";
		for(Player player : Bukkit.getOnlinePlayers()){
			players += player.getName()+";";
			playerList.put(player.getName(),new SpectatorServerPlayer(player.getName(),RealCraft.getInstance().serverName));
		}
		data.setString("players",players);
		SocketManager.sendToAll(data);
	}

	public void connectPlayerToServer(Player player,String server){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(server);
		player.sendPluginMessage(plugin,"BungeeCord",out.toByteArray());
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

	private class SpectatorServerPlayer {

		private String player;
		private String server;
		private long created;

		public SpectatorServerPlayer(String player,String server){
			this.player = player;
			this.server = server;
			this.created = System.currentTimeMillis();
		}

		public String getPlayer(){
			return player;
		}

		public String getServer(){
			return server;
		}

		public long getCreated(){
			return created;
		}
	}

	private class PlayerSpectate implements Runnable {
		private String player;
		private String victim;
		private long created;
		private boolean enabled = false;

		private Location lastLocation;
		private GameMode lastGameMode;

		public PlayerSpectate(String player,String victim){
			this.player = player;
			this.victim = victim;
			this.created = System.currentTimeMillis();
			Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,3*20,3*20);
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