package realcraft.bukkit.anticheat;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import realcraft.bukkit.anticheat.checks.Check.CheckType;
import realcraft.bukkit.anticheat.checks.CheckClickAura.HitFrequency;
import realcraft.bukkit.anticheat.checks.CheckSneakHack.SneakFrequency;
import realcraft.bukkit.anticheat.utils.Utils;

import java.util.HashMap;

public class AntiCheatPlayer {

	private final Player player;
	private HashMap<CheckType,PlayerCheck> typeChecks = new HashMap<CheckType,PlayerCheck>();

	/** FLYHACK */
	public double blocksOverFlight = 0;
	public double blocksOverFlightY = 0;
	public int flightChecks = 0;
	public int flightChecksY = 0;
	public long lastAboveSlimeBlocks = 0;

	/** SPEEDHACK */
	public int speedChecks = 0;
	public long lastSpeedCheck = 0;
	public Location lastLocation = null;

	/** SNEAKHACK */
	public int sneakChecks = 0;
	public SneakFrequency sneakFrequency = new SneakFrequency(1000);

	/** CLICKAURA */
	public int hitChecks = 0;
	public HitFrequency hitFrequency = new HitFrequency(1000);

	/** KILLAURA */
	public int ghostChecks = 0;
	/*public GhostFrequency ghostFrequency = new GhostFrequency(50*5);
	public EntityPlayer ghostPlayer;*/
	public long lastGhost = 0;

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
		if(typeChecks.get(type).lastCheck+(type.getBanTimeRange()*1000) >= System.currentTimeMillis()) typeChecks.get(type).checks ++;
		else typeChecks.get(type).checks = 1;
		typeChecks.get(type).lastCheck = System.currentTimeMillis();
	}

	public int getTypeChecks(CheckType type){
		return typeChecks.get(type).checks;
	}

	public void reset(){
		blocksOverFlight = 0;
		blocksOverFlightY = 0;
		flightChecks = 0;
		flightChecksY = 0;
		lastAboveSlimeBlocks = 0;

		speedChecks = 0;
		lastSpeedCheck = 0;
		lastLocation = null;

		sneakChecks = 0;
		sneakFrequency = new SneakFrequency(1000);

		hitChecks = 0;
		hitFrequency = new HitFrequency(1000);

		ghostChecks = 0;
		//ghostFrequency = new GhostFrequency(50*5);

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