package com.anticheat.checks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.anticheat.events.AntiCheatDetectEvent;
import com.realcraft.RealCraft;

public abstract class Check implements Listener, Runnable {

	private CheckType type;

	public Check(CheckType type){
		this.type = type;
		Bukkit.getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
		Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,this.getType().getRunSpeed(),this.getType().getRunSpeed());
	}

	public CheckType getType(){
		return type;
	}

	public void detect(Player player){
		AntiCheatDetectEvent callevent = new AntiCheatDetectEvent(player.getPlayer(),this.getType());
		Bukkit.getServer().getPluginManager().callEvent(callevent);
	}

	public enum CheckType {
		FLYHACK, SPEEDHACK, NOFALL, WATERWALK, KILLAURA_SPEED, KILLAURA_DIR, KILLAURA_NOSWING, ENCHANT;

		public String toString(){
			switch(this){
				case FLYHACK: return "FlyHack";
				case SPEEDHACK: return "SpeedHack";
				case NOFALL: return "NoFall";
				case WATERWALK: return "WaterWalk";
				case KILLAURA_SPEED: return "KillAura (speed)";
				case KILLAURA_DIR: return "KillAura (direction)";
				case KILLAURA_NOSWING: return "KillAura (noswing)";
				case ENCHANT: return "SuperEnchant";
			}
			return null;
		}

		public int getId(){
			switch(this){
				case FLYHACK: return 1;
				case SPEEDHACK: return 8;
				case NOFALL: return 2;
				case WATERWALK: return 3;
				case KILLAURA_SPEED: return 4;
				case KILLAURA_DIR: return 5;
				case KILLAURA_NOSWING: return 6;
				case ENCHANT: return 7;
			}
			return 0;
		}

		public int getRunSpeed(){
			switch(this){
				case FLYHACK: return 20;
				default:break;
			}
			return 0;
		}
	}
}