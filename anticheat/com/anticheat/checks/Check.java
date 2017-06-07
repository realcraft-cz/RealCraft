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
		if(this.getType().getRunSpeed() > 0) Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,this.getType().getRunSpeed(),this.getType().getRunSpeed());
	}

	public CheckType getType(){
		return type;
	}

	public void detect(Player player){
		AntiCheatDetectEvent callevent = new AntiCheatDetectEvent(player.getPlayer(),this.getType());
		Bukkit.getServer().getPluginManager().callEvent(callevent);
	}

	public enum CheckType {
		FLYHACK, SPEEDHACK, KILLAURA, ENCHANT;

		public static CheckType getByName(String name){
			return CheckType.valueOf(name.toUpperCase());
		}

		public String toString(){
			switch(this){
				case FLYHACK: return "FlyHack";
				case SPEEDHACK: return "SpeedHack";
				case KILLAURA: return "KillAura";
				case ENCHANT: return "SuperEnchant";
			}
			return null;
		}

		public int getId(){
			switch(this){
				case FLYHACK: return 1;
				case SPEEDHACK: return 8;
				case KILLAURA: return 4;
				case ENCHANT: return 7;
			}
			return 0;
		}

		public int getRunSpeed(){
			switch(this){
				case FLYHACK: return 20;
				case KILLAURA: return 1;
				default:break;
			}
			return 0;
		}

		public int getBanLimit(){
			switch(this){
				case FLYHACK: return 3;
				case SPEEDHACK: return 3;
				case KILLAURA: return 5;
				default:break;
			}
			return 0;
		}

		public int getBanTimeRange(){
			switch(this){
				case FLYHACK: return 30000;
				case SPEEDHACK: return 30000;
				case KILLAURA: return 30000;
				default:break;
			}
			return 0;
		}
	}
}