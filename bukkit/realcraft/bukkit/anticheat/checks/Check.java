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
		FLYHACK, SPEEDHACK, SNEAKHACK, CLICKAURA, KILLAURA, ENCHANT, FASTBREAK, XRAY;

		public static CheckType getByName(String name){
			return CheckType.valueOf(name.toUpperCase());
		}

		public String toString(){
			switch(this){
				case FLYHACK: return "FlyHack";
				case SPEEDHACK: return "SpeedHack";
				case SNEAKHACK: return "SneakHack";
				case CLICKAURA: return "ClickAura";
				case KILLAURA: return "KillAura";
				case ENCHANT: return "SuperEnchant";
				case FASTBREAK: return "FastBreak";
				case XRAY: return "X-Ray";
			}
			return null;
		}

		public int getId(){
			switch(this){
				case FLYHACK: return 1;
				case SPEEDHACK: return 8;
				case SNEAKHACK: return 10;
				case CLICKAURA: return 4;
				case KILLAURA: return 11;
				case ENCHANT: return 7;
				case FASTBREAK: return 9;
				case XRAY: return 12;
			}
			return 0;
		}

		public int getRunSpeed(){
			switch(this){
				case FLYHACK: return 20;
				case SPEEDHACK: return 10;
				default:break;
			}
			return 0;
		}

		public int getBanLimit(){
			switch(this){
				case FLYHACK: return 3;
				case SNEAKHACK: return 3;
				case CLICKAURA: return 5;
				case KILLAURA: return 5;
				default:break;
			}
			return 999;
		}

		public int getBanTimeRange(){
			switch(this){
				case FLYHACK: return 60;
				case SPEEDHACK: return 60;
				case SNEAKHACK: return 60;
				case CLICKAURA: return 120;
				case KILLAURA: return 180;
				default:break;
			}
			return 0;
		}
	}
}