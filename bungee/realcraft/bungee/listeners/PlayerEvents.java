package realcraft.bungee.listeners;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import realcraft.bungee.RealCraftBungee;
import realcraft.bungee.geoip.GeoLiteAPI;
import realcraft.bungee.playermanazer.PlayerManazer;
import realcraft.bungee.playermanazer.PlayerManazer.PlayerInfo;
import realcraft.bungee.sockets.SocketData;
import realcraft.bungee.sockets.SocketDataEvent;
import realcraft.bungee.sockets.SocketManager;

public class PlayerEvents implements Listener {
	private RealCraftBungee plugin;

	String loginCommand;
	String registerCommand;
	String notLogged;
	String blockedCountry;
	int minNicknameLength;
	Pattern allowedNicknameChars;

	public PlayerEvents(RealCraftBungee realcraft){
		plugin = realcraft;
		loginCommand = plugin.config.getString("messages.loginCommand");
		registerCommand = plugin.config.getString("messages.registerCommand");
		notLogged = plugin.config.getString("messages.notLogged");
		blockedCountry = plugin.config.getString("messages.blockedCountry");
		minNicknameLength = plugin.config.getInt("settings.minNicknameLength",3);
		allowedNicknameChars = Pattern.compile(plugin.config.getString("settings.allowedNicknameChars"));
	}

	@EventHandler
	public void PreLoginEvent(PreLoginEvent event){
		Matcher match = allowedNicknameChars.matcher(event.getConnection().getName());
		if(event.getConnection().getName().length() < minNicknameLength){
			event.setCancelReason(TextComponent.fromLegacyText("Spatny nick, minimalni delka jsou 3 znaky!"));
			event.setCancelled(true);
			return;
		}
		else if(!match.matches()){
			event.setCancelReason(TextComponent.fromLegacyText("Spatny nick, povolene znaky: a-zA-Z0-9_"));
			event.setCancelled(true);
			return;
		}
		event.getConnection().setUniqueId(UUID.nameUUIDFromBytes(("OfflinePlayer:"+event.getConnection().getName()).getBytes(Charsets.UTF_8)));
		PlayerManazer.removePlayerInfo(event.getConnection());
		if(PlayerManazer.getPlayerInfo(event.getConnection()).isPremium()){
			event.getConnection().setOnlineMode(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void LoginEvent(LoginEvent event){
		try {
			UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + event.getConnection().getName()).getBytes(Charsets.UTF_8));
			Field idField = InitialHandler.class.getDeclaredField("uniqueId");
	        idField.setAccessible(true);
			idField.set(event.getConnection(),offlineUUID);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void PostLoginEvent(PostLoginEvent event){
		ProxiedPlayer player = event.getPlayer();
		if(this.isCountryBlocked(player)){
			player.disconnect(RealCraftBungee.parseColors(blockedCountry));
			player.getName();
		} else {
			PlayerInfo playerinfo = PlayerManazer.getPlayerInfo(player);
			if(!playerinfo.isPremium()) player.sendMessage(RealCraftBungee.parseColors((playerinfo.isRegistered() ? loginCommand : registerCommand)));
		}
	}

	@EventHandler
	public void ServerConnectedEvent(ServerConnectedEvent event){
		PlayerInfo playerinfo = PlayerManazer.getPlayerInfo(event.getPlayer());
		if(event.getServer().getInfo().getName().equalsIgnoreCase("lobby") && playerinfo.isPremium()){
			plugin.getProxy().getScheduler().schedule(plugin,new Runnable(){
				@Override
				public void run(){
					if(event.getPlayer().isConnected()) playerinfo.performPremiumLogin();
				}
			},200,TimeUnit.MILLISECONDS);
		}
	}

	@EventHandler
	public void PlayerDisconnectEvent(PlayerDisconnectEvent event){
		System.out.println("PlayerDisconnectEvent: "+event.getPlayer().getName());
		ProxiedPlayer player = event.getPlayer();
		PlayerInfo playerinfo = PlayerManazer.getPlayerInfo(player);
		if(playerinfo != null && playerinfo.isLogged()){
			playerinfo.performLogout();
		}
		PlayerManazer.removePlayerInfo(player);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void ServerSwitchEvent(ServerSwitchEvent event){
		ProxiedPlayer player = event.getPlayer();
		PlayerInfo playerinfo = PlayerManazer.getPlayerInfo(player);
		if((playerinfo == null || !playerinfo.isLogged()) && player.getServer().getInfo().getName().equalsIgnoreCase("lobby") == false){
			player.disconnect(RealCraftBungee.parseColors(notLogged));
		} else {
			playerinfo.performSwitch();
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void ChatEvent(ChatEvent event){
		String [] args = event.getMessage().split(" ");
		if(!args[0].equalsIgnoreCase("/login") && !args[0].equalsIgnoreCase("/register")){
			ProxiedPlayer player = (ProxiedPlayer) event.getSender();
			PlayerInfo playerinfo = PlayerManazer.getPlayerInfo(player);
			if(playerinfo == null || playerinfo.isLogged() == false){
				event.setCancelled(true);
				player.sendMessage(RealCraftBungee.parseColors(loginCommand));
			}
		}
	}

	private static final String CHANNEL_PING = "playerPing";

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(CHANNEL_PING)){
			String name = data.getString("name");
			ProxiedPlayer player = plugin.getProxy().getPlayer(name);
			if(player != null && player.isConnected()){
				data = new SocketData(CHANNEL_PING);
				data.setString("name",name);
				data.setInt("ping",player.getPing());
				SocketManager.send(event.getServer(),data);
			}
		}
	}

	public boolean isCountryBlocked(ProxiedPlayer player){
		String address = player.getAddress().getAddress().getHostAddress().replace("/", "");
		if(address.length() > 0){
			String country = GeoLiteAPI.getCountryCode(plugin,address);
			if(country.equalsIgnoreCase("CZ") || country.equalsIgnoreCase("SK")) return false;
			else System.out.println(player.getName()+" ("+address+") country: "+country);
		}
		return true;
	}
}