package realcraft.bukkit.coins;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.Title;

public class Coins implements Listener, CommandExecutor {

	private static final int[] coinsPercentages = new int[]{0,10,20,30,40,50,60,68,75,81,86,90,93,95,96,97,98,99,100};
	private static final int[] coinsPercentages2 = new int[]{0,10,20,30,40,50,60,70,80,90,100};
	private static final int[] coinsTimings = new int[]{2,4,6,8,10,12,14,16,18,20,22,24,26,29,33,38,44,51,59};
	private static final int[] coinsTimings2 = new int[]{18,20,22,24,26,29,33,38,44,51,59};

	public Coins(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		RealCraft.getInstance().getCommand("coins").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("coins")){
			player.sendMessage("§fNyni mas §a"+Users.getUser(player).getCoins()+" coins");
		}
		return true;
	}

	public static void runCoinsEffect(Player player,int coins){
		runCoinsEffect(player," ",coins,true);
	}

	public static void runCoinsEffect(Player player,String title,int coins){
		runCoinsEffect(player,title,coins,true);
	}

	public static void runCoinsEffect(Player player,String title,int coins,boolean boost){
		int i = 0;
		for(int percent : (coins >= 100 ? coinsPercentages : coinsPercentages2)){
			Bukkit.getScheduler().scheduleSyncDelayedTask(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					showCoinsEffect(player,title,(int)Math.round((coins/100.0)*percent),boost);
				}
			},(coins >= 100 ? coinsTimings[i++] : coinsTimings2[i++]-16));
		}
	}

	private static void showCoinsEffect(Player player,String title,int coins,boolean boost){
		Title.showTitle(player,title,0.0,2,0.5);
		Title.showSubTitle(player,"§a+"+coins+" coins"+(Users.getUser(player).hasCoinsBoost() && boost ? " §b(2x)" : ""),0.0,2,0.5);
		player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
	}
}