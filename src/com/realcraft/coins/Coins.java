package com.realcraft.coins;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.realcraft.RealCraft;
import com.realcraft.playermanazer.PlayerManazer;

public class Coins implements Listener, CommandExecutor {

	public Coins(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		RealCraft.getInstance().getCommand("coins").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("coins")){
			player.sendMessage("§fNyni mas §a"+PlayerManazer.getPlayerInfo(player).getCoins()+" coins");
		}
		return true;
	}
}