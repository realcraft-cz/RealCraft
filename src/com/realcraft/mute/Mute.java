package com.realcraft.mute;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.earth2me.essentials.Essentials;
import com.realcraft.RealCraft;
import com.realcraft.banmanazer.BanUtils;
import com.realcraft.utils.DateUtil;

public class Mute implements Listener, CommandExecutor {
	RealCraft plugin;
	Essentials essentials;
	
	boolean enabled = false;
	String muteMessage;
	String unmuteMessage;
	String muteWarnMessage;
	String permWarnMessage;
	
	public Mute(RealCraft realcraft){
		plugin = realcraft;
		if(plugin.config.getBoolean("mute.enabled")){
			essentials = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
			enabled = true;
			muteMessage = plugin.config.getString("mute.muteMessage",null);
			unmuteMessage = plugin.config.getString("mute.unmuteMessage",null);
			muteWarnMessage = plugin.config.getString("mute.muteWarnMessage",null);
			permWarnMessage = plugin.config.getString("mute.permWarnMessage",null);
			plugin.getServer().getPluginManager().registerEvents(this,plugin);
			plugin.getCommand("mute").setExecutor(this);
			plugin.getCommand("unmute").setExecutor(this);
		}
	}
	
	public void onReload(){
		enabled = false;
		if(plugin.config.getBoolean("mute.enabled")){
			enabled = true;
			muteMessage = plugin.config.getString("mute.muteMessage",null);
			unmuteMessage = plugin.config.getString("mute.unmuteMessage",null);
			muteWarnMessage = plugin.config.getString("mute.muteWarnMessage",null);
			permWarnMessage = plugin.config.getString("mute.permWarnMessage",null);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!enabled) return false;
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("mute")){
			if(player.hasPermission("group.Admin") || player.hasPermission("group.Moderator")){
				if(args.length == 1 && args[0].equalsIgnoreCase("help")){
					player.sendMessage("");
					player.sendMessage(RealCraft.parseColors("Priklad: &6/mute "+player.getName()+" 10m nadavky"));
					player.sendMessage(RealCraft.parseColors("Casove jednotky: &6s &r(sekundy), &6m &r(minuty), &6h &r(hodiny), &6d &r(dny)"));
					player.sendMessage("/mute <player> <time> <reason>");
					return true;
				}
				else if(args.length >= 3){
					Player victim = plugin.getServer().getPlayer(args[0]);
					int expire = 0;
					if(victim == null){
						player.sendMessage(RealCraft.parseColors("&cHrac nenalezen."));
						return true;
					}
					else if(victim == player){
						player.sendMessage(RealCraft.parseColors("&cNemuzes umlcet sam sebe."));
						return true;
					}
					try {
						expire = DateUtil.parseDateDiff(args[1],true);
					}
					catch (Exception e){
						player.sendMessage(RealCraft.parseColors("&cNespravny format data."));
						return true;
					}
					String reason = BanUtils.combineSplit(2,args);
					if(reason != null && reason.length() > 0){
						mutePlayer(victim,expire,reason,player);
						return true;
					}
				}
				player.sendMessage("/mute <player> <time> <reason>");
				return true;
			}
			else player.sendMessage(RealCraft.parseColors(permWarnMessage));
		}
		else if(command.getName().equalsIgnoreCase("unmute")){
			if(player.hasPermission("group.Admin") || player.hasPermission("group.Moderator")){
				if(args.length == 1){
					Player victim = plugin.getServer().getPlayer(args[0]);
					if(victim == null){
						player.sendMessage(RealCraft.parseColors("&cHrac nenalezen."));
						return true;
					}
					if(!essentials.getUser(victim).isMuted()){
						player.sendMessage(RealCraft.parseColors("&cHrac neni umlcen."));
						return true;
					}
					unMutePlayer(victim,player);
					return true;
				}
				player.sendMessage("/unmute <player>");
				return true;
			}
			else player.sendMessage(RealCraft.parseColors(permWarnMessage));
		}
		return true;
	}
	
	public void mutePlayer(Player player,int expire,String reason,Player admin){
		essentials.getUser(player).setMuted(true);
		essentials.getUser(player).setMuteTimeout((long)expire*1000);
		plugin.getServer().broadcastMessage(getMuteMessage(player,expire,reason,admin));
	}
	
	public void unMutePlayer(Player player,Player admin){
		essentials.getUser(player).setMuted(false);
		plugin.getServer().broadcastMessage(getUnMuteMessage(player,admin));
	}
	
	public String getMuteMessage(Player player,int expire,String reason,Player admin){
		String result = muteMessage;
		result = result.replaceAll("%time%",DateUtil.formatDateDiff((long)expire*1000));
		result = result.replaceAll("%victim%",player.getName());
		result = result.replaceAll("%admin%",admin.getName());
		result = result.replaceAll("%reason%",reason);
		return RealCraft.parseColors(result);
	}
	
	public String getUnMuteMessage(Player player,Player admin){
		String result = unmuteMessage;
		result = result.replaceAll("%victim%",player.getName());
		result = result.replaceAll("%admin%",admin.getName());
		return RealCraft.parseColors(result);
	}
}
