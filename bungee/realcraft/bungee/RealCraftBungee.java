package realcraft.bungee;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import realcraft.bungee.commands.ListCommand;
import realcraft.bungee.config.Config;
import realcraft.bungee.database.DB;
import realcraft.bungee.motd.ServerMotd;
import realcraft.bungee.skins.Skins;
import realcraft.bungee.sockets.SocketManager;
import realcraft.bungee.users.Users;

public class RealCraftBungee extends Plugin implements Runnable {
	private static RealCraftBungee instance;
	public static boolean testServer = false;

	public Config config;
	public DB db;
	public SocketManager socketmanager;

	int restartHour = 4;

	public void onEnable(){
		instance = this;
		config = new Config(this);
		testServer = !config.getBoolean("settings.public",false);
		DB.init();
		socketmanager = new SocketManager();
		//new PlayerManazer(this);
		new Users();
		new Skins(this);
		getProxy().getPluginManager().registerCommand(this,new ListCommand(this));
		getProxy().getScheduler().schedule(this,this,3600,60,TimeUnit.SECONDS);
		getProxy().getScheduler().schedule(this,new Runnable(){
			@Override
			public void run(){
				if(!testServer) DB.update("UPDATE bungee_status SET value = '"+(System.currentTimeMillis()/1000)+"' WHERE variable = 'lastping'");
				Users.updatePlayTime();
			}
		},10,10,TimeUnit.SECONDS);

		if(!testServer) DB.update("UPDATE authme SET user_logged = '0'");
		new ServerMotd(this);
	}

	public void onDisable(){
		if(!testServer) DB.update("UPDATE authme SET user_logged = '0'");
		config.onDisable();
		DB.onDisable();
		socketmanager.onDisable();
	}

	public static RealCraftBungee getInstance(){
		return instance;
	}

	public static boolean isTestServer(){
		return testServer;
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