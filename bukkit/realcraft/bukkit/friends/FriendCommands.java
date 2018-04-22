package realcraft.bukkit.friends;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.friends.commands.FriendCommand;
import realcraft.bukkit.friends.commands.FriendCommandAccept;
import realcraft.bukkit.friends.commands.FriendCommandAdd;
import realcraft.bukkit.friends.commands.FriendCommandChat;
import realcraft.bukkit.friends.commands.FriendCommandDeny;
import realcraft.bukkit.friends.commands.FriendCommandList;
import realcraft.bukkit.friends.commands.FriendCommandRemove;
import realcraft.bukkit.friends.commands.FriendCommandSettings;
import realcraft.bukkit.friends.commands.FriendCommandTp;

public class FriendCommands implements CommandExecutor, TabCompleter {

	private FriendCommand[] commands;

	public FriendCommands(){
		RealCraft.getInstance().getCommand("friend").setExecutor(this);
		commands = new FriendCommand[]{
			new FriendCommandAdd(),
			new FriendCommandRemove(),
			new FriendCommandAccept(),
			new FriendCommandDeny(),
			new FriendCommandChat(),
			new FriendCommandTp(),
			new FriendCommandList(),
			new FriendCommandSettings(),
		};
	}

	@Override
	public boolean onCommand(CommandSender sender,Command command,String label,String[] args){
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("friend")){
			if(args.length == 0 || this.getCommand(args[0]) == null){
				this.showHelpPage(player);
				return true;
			}
			String[] arguments = new String[args.length-1];
			System.arraycopy(args,1,arguments,0,args.length-1);
			this.getCommand(args[0]).perform(player,arguments);
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender,Command command,String alias,String[] args){
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("friend")){
			if(args.length == 1) return this.findCommands(args[0]);
			else if(args.length == 2 && this.getCommand(args[0]) != null) return this.getCommand(args[0]).onTabComplete(player,args[1]);
		}
		return null;
	}

	private List<String> findCommands(String name){
		List<String> cmds = new ArrayList<String>();
		for(FriendCommand command : commands){
			if(command.startsWith(name)) cmds.add(command.getNames()[0]);
		}
		return cmds;
	}

	private FriendCommand getCommand(String name){
		for(FriendCommand command : commands){
			if(command.match(name)) return command;
		}
		return null;
	}

	private void showHelpPage(Player player){
		player.sendMessage("§7§m"+StringUtils.repeat(" ",10)+"§r §a§lFriends §7§m"+StringUtils.repeat(" ",47-"Friends".length()));
		player.sendMessage("§6/friend add §e<player> §f- Odeslat zadost o pratelstvi");
		player.sendMessage("§6/friend remove §e<player> §f- Odebrat hrace z pratel");
		player.sendMessage("§6/friend accept §e<player> §f- Prijmout zadost od hrace");
		player.sendMessage("§6/friend deny §e<player> §f- Odmitnout zadost od hrace");
		player.sendMessage("§6/friend tp §e<player> §f- Teleportovat k hraci");
		player.sendMessage("§6/friend chat §f- Soukromy chat mezi prateli");
		player.sendMessage("§6/friend list §f- Seznam tvych pratel");
		player.sendMessage("§6/friend settings §f- Nastaveni pratel");
		player.sendMessage("§7§m"+StringUtils.repeat(" ",62));
	}
}