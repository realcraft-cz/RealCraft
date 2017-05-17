package com.realcraft.test;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.realcraft.RealCraft;
import com.realcraft.playermanazer.PlayerManazer;

public class CoinsTest implements CommandExecutor {

	public CoinsTest(){
		RealCraft.getInstance().getCommand("coinstest").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("coinstest")){
			if(args.length == 0){
				player.sendMessage("/coinstest <amount>");
				return true;
			}
			int coins = Integer.valueOf(args[0]);
			PlayerManazer.getPlayerInfo(player).runCoinsEffect("§aVyhra!",coins);
		}
		return true;
	}
}
