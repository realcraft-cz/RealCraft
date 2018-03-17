package realcraft.bukkit.anticheat.checks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.anticheat.events.AntiCheatDetectEvent;

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
		FLYHACK, SPEEDHACK, SNEAKHACK, KILLAURA, ENCHANT, FASTBREAK;

		public static CheckType getByName(String name){
			return CheckType.valueOf(name.toUpperCase());
		}

		public String toString(){
			switch(this){
				case FLYHACK: return "FlyHack";
				case SPEEDHACK: return "SpeedHack";
				case SNEAKHACK: return "SneakHack";
				case KILLAURA: return "KillAura";
				case ENCHANT: return "SuperEnchant";
				case FASTBREAK: return "FastBreak";
			}
			return null;
		}

		public int getId(){
			switch(this){
				case FLYHACK: return 1;
				case SPEEDHACK: return 8;
				case SNEAKHACK: return 10;
				case KILLAURA: return 4;
				case ENCHANT: return 7;
				case FASTBREAK: return 9;
			}
			return 0;
		}

		public int getRunSpeed(){
			switch(this){
				case FLYHACK: return 20;
				case SPEEDHACK: return 10;
				case KILLAURA: return 1;
				default:break;
			}
			return 0;
		}

		public int getBanLimit(){
			switch(this){
				case FLYHACK: return 3;
				case SPEEDHACK: return 999;
				case SNEAKHACK: return 999;
				case KILLAURA: return 5;
				case FASTBREAK: return 999;
				default:break;
			}
			return 0;
		}

		public int getBanTimeRange(){
			switch(this){
				case FLYHACK: return 60;
				case SPEEDHACK: return 60;
				case SNEAKHACK: return 60;
				case KILLAURA: return 60;
				case FASTBREAK: return 60;
				default:break;
			}
			return 0;
		}
	}
}