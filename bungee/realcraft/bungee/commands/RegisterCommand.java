package realcraft.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import realcraft.bungee.RealCraftBungee;
import realcraft.bungee.playermanazer.PlayerManazer;
import realcraft.bungee.playermanazer.PlayerManazer.PlayerInfo;

public class RegisterCommand extends Command {
	RealCraftBungee plugin;
	
	String loginCommand;
	String registerCommand;
	String wrongPasswords;
	String alreadyLogged;
	
	public RegisterCommand(RealCraftBungee plugin){
		super("register","");
		this.plugin = plugin;
		loginCommand = plugin.config.getString("messages.loginCommand");
		registerCommand = plugin.config.getString("messages.registerCommand");
		wrongPasswords = plugin.config.getString("messages.wrongPasswords");
		alreadyLogged = plugin.config.getString("messages.alreadyLogged");
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
			else if(playerinfo.isRegistered()){
				player.sendMessage(RealCraftBungee.parseColors(loginCommand));
			}
			else if(args.length < 2){
				player.sendMessage(RealCraftBungee.parseColors(registerCommand));
			} else {
				if(!playerinfo.performRegister(args[0],args[1])){
					player.sendMessage(RealCraftBungee.parseColors(wrongPasswords));
				}
			}
		}
	}
}
