package com.anticheat;

import java.util.HashMap;

import org.bukkit.entity.Player;

import com.anticheat.checks.Check.CheckType;
import com.anticheat.utils.ActionFrequency;
import com.anticheat.utils.Utils;

public class AntiCheatPlayer {

	private Player player;
	public long lastReported = 0;
	private HashMap<CheckType,PlayerCheck> typeChecks = new HashMap<CheckType,PlayerCheck>();

	// FLYHACK
	public double blocksOverFlight = 0;
	public int flightChecks = 0;
	public long lastAboveSlimeBlocks = 0;

	// SPEEDHACK
	public int speedChecks = 0;

	// KILLAURA
	public ActionFrequency killBuckets = new ActionFrequency(6,333);
	public int killShortTermTick = 0;
	public int killShortTermCount = 0;
	public int killChecks = 0;

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

	public void addTypeCheck(CheckType type){
		if(!typeChecks.containsKey(type)) typeChecks.put(type,new PlayerCheck(0));
		if(typeChecks.get(type).lastCheck+type.getBanTimeRange() >= System.currentTimeMillis()) typeChecks.get(type).checks ++;
		else typeChecks.get(type).checks = 1;
		typeChecks.get(type).lastCheck = System.currentTimeMillis();
	}

	public int getTypeChecks(CheckType type){
		return typeChecks.get(type).checks;
	}

	public void reset(){
		blocksOverFlight = 0;
		flightChecks = 0;
		lastAboveSlimeBlocks = 0;
		speedChecks = 0;
		killBuckets = new ActionFrequency(6,333);
		killShortTermTick = 0;
		killShortTermCount = 0;
		killChecks = 0;
		typeChecks = new HashMap<CheckType,PlayerCheck>();
	}

	private class PlayerCheck {

		public int checks = 0;
		public long lastCheck = 0;

		public PlayerCheck(int checks){
			this.checks = checks;
		}
	}
}