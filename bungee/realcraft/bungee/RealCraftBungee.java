package realcraft.bungee;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import realcraft.bungee.commands.ListCommand;
import realcraft.bungee.commands.LoginCommand;
import realcraft.bungee.commands.RegisterCommand;
import realcraft.bungee.config.Config;
import realcraft.bungee.database.MySQL;
import realcraft.bungee.listeners.PlayerEvents;
import realcraft.bungee.motd.ServerMotd;
import realcraft.bungee.playermanazer.PlayerManazer;
import realcraft.bungee.skins.Skins;
import realcraft.bungee.sockets.SocketManager;

public class RealCraftBungee extends Plugin implements Runnable {
	private static RealCraftBungee instance;
	public static boolean publicServer = false;

	public Config config;
	public MySQL db;
	public SocketManager socketmanager;

	int restartHour = 4;

	public void onEnable(){
		instance = this;
		config = new Config(this);
		db = new MySQL(this);
		socketmanager = new SocketManager();
		new PlayerManazer(this);
		new Skins(this);
		this.getProxy().getPluginManager().registerListener(this, new PlayerEvents(this));
		getProxy().getPluginManager().registerCommand(this,new LoginCommand(this));
		getProxy().getPluginManager().registerCommand(this,new RegisterCommand(this));
		getProxy().getPluginManager().registerCommand(this,new ListCommand(this));
		getProxy().registerChannel("RealCraftPing");
		getProxy().getScheduler().schedule(this,this,3600,60,TimeUnit.SECONDS);
		getProxy().getScheduler().schedule(this,new Runnable(){
			@Override
			public void run(){
				if(publicServer) db.update("UPDATE bungee_status SET value = '"+(System.currentTimeMillis()/1000)+"' WHERE variable = 'lastping'");
				PlayerManazer.updatePlayTime();
			}
		},10,10,TimeUnit.SECONDS);

		publicServer = config.getBoolean("settings.public",false);
		if(publicServer) db.update("UPDATE authme SET user_logged = '0'");
		new ServerMotd(this);
	}

	public void onDisable(){
		if(publicServer) db.update("UPDATE authme SET user_logged = '0'");
		config.onDisable();
		db.onDisable();
		socketmanager.onDisable();
	}

	public static RealCraftBungee getInstance(){
		return instance;
	}

	public static boolean isTestServer(){
		return !publicServer;
	}

	public static String parseColors(String message){
		return ChatColor.translateAlternateColorCodes('&',message);
	}

	@Override
	public void run(){
		LocalDateTime now = LocalDateTime.now();
		if(now.getHour() == restartHour){
			executeRestart();
		}
	}

	public void executeRestart(){
		getProxy().stop("Restart serveru, pripojte se znovu prosim.");
	}
}