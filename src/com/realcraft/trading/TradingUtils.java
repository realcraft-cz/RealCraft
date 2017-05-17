package com.realcraft.trading;

import org.bukkit.entity.Player;

public class TradingUtils {

	public static String getInventoryName(Player player1,Player player2){
		int spaces = 23-player1.getName().length();
		String name = player1.getName();
		for(int i=0;i<spaces;i++) name += " ";
		name += player2.getName();
		return name;
	}

	public static boolean isOwnerSlot(int index){
		return ((index >= 0 && index <= 3) || (index >= 9 && index <= 12) || (index >= 18 && index <= 21) || (index >= 27 && index <= 30));
	}

	public static boolean isPlayerSlot(int index){
		return (index >= 45);
	}

	public static boolean isStarterSlot(int index){
		return (index >= 36 && index <= 39);
	}
}