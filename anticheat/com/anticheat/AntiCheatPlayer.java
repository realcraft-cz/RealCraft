package com.anticheat;

import org.bukkit.entity.Player;

import com.anticheat.utils.Utils;

public class AntiCheatPlayer {

	private Player player;

	public double blocksOverFlight = 0;
	public int flightChecks = 0;
	private long lastAboveSlimeBlocks = 0;
	public int speedChecks = 0;
	public int speedChecksWurst = 0;

	public AntiCheatPlayer(Player player){
		this.player = player;
	}

	public boolean isAboveSlimeBlocks(){
		return (lastAboveSlimeBlocks > System.currentTimeMillis());
	}

	public void run(){
		if(Utils.isPlayerAboveSlimeBlocks(player)){
			lastAboveSlimeBlocks = System.currentTimeMillis()+2000;
		}
	}

	public void reset(){
		blocksOverFlight = 0;
		flightChecks = 0;
		lastAboveSlimeBlocks = 0;
	}
}