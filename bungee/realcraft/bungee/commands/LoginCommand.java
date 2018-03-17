package realcraft.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import realcraft.bungee.RealCraftBungee;
import realcraft.bungee.playermanazer.PlayerManazer;
import realcraft.bungee.playermanazer.PlayerManazer.PlayerInfo;

public class LoginCommand extends Command {
	RealCraftBungee plugin;

	String loginCommand;
	String registerCommand;
	String wrongPassword;
	String alreadyLogged;
	String tooManyAttempts;

	public LoginCommand(RealCraftBungee plugin){
		super("login","");
		this.plugin = plugin;
		loginCommand = plugin.config.getString("messages.loginCommand");
		registerCommand = plugin.config.getString("messages.registerCommand");
		wrongPassword = plugin.config.getString("messages.wrongPassword");
		alreadyLogged = plugin.config.getString("messages.alreadyLogged");
		tooManyAttempts = plugin.config.getString("messages.tooManyAttempts");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender,String[] args){
		if(sender instanceof ProxiedPlayer){
			ProxiedPlayer player = (ProxiedPlayer) sender;
			PlayerInfo playerinfo = PlayerManazer.getPlayerInfo(player);
			if(playerinfo.isLogged()){
				player.sendMessage(RealCraftBungee.parseColors(alreadyLogged));
			}
			else if(!playerinfo.isRegistered()){
				player.sendMessage(RealCraftBungee.parseColors(registerCommand));
			}
			else if(args.length == 0){
				player.sendMessage(RealCraftBungee.parseColors(loginCommand));
			} else {
				if(!playerinfo.tooManyAttempts()){
					if(playerinfo.checkPassword(args[0])){
						playerinfo.performLogin(args[0]);
					} else {
						playerinfo.addAttempt();
						player.sendMessage(RealCraftBungee.parseColors(wrongPassword));
					}
				} else {
					String message = tooManyAttempts;
					message = message.replace("{seconds}",""+playerinfo.getTooManyAttemptsSeconds());
					player.sendMessage(RealCraftBungee.parseColors(message));
				}
			}
		}
	}
}