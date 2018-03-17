package realcraft.bukkit.anticheat.checks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffectType;

import realcraft.bukkit.anticheat.AntiCheat;
import realcraft.bukkit.anticheat.utils.Utils;

public class CheckSpeedHack extends Check {

	private static final int CHECKS_LIMIT = 5;

	private static final double DEFAULT_SPEED = 0.21;
	private static final double SNEAK_MODIFIER = 0.43;
	private static final double SPRINT_MODIFIER = 1.33;

	private static final double SPEED_OFFSET = 0.15;

	public CheckSpeedHack(){
		super(CheckType.SPEEDHACK);
	}

	@Override
	public void run(){
		for(Player player : Bukkit.getOnlinePlayers()){
			if(!AntiCheat.isPlayerExempted(player)){
				this.check(player);
			}
		}
	}

	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent event){
		if(Math.abs(event.getFrom().getY()-event.getTo().getY()) > 0.1){
			AntiCheat.getPlayer(event.getPlayer()).speedChecks = 0;
		}
	}

	@EventHandler
	public void PlayerToggleSneakEvent(PlayerToggleSneakEvent event){
		AntiCheat.getPlayer(event.getPlayer()).speedChecks = 0;
	}

	@EventHandler
	public void PlayerTeleportEvent(PlayerTeleportEvent event){
		AntiCheat.getPlayer(event.getPlayer()).reset();
	}

	@EventHandler
	public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event){
		AntiCheat.getPlayer(event.getPlayer()).reset();
	}

	public void check(Player player){
		if(!Utils.canPlayerFly(player) && !player.hasPotionEffect(PotionEffectType.SPEED)){
			double speed = this.getPlayerSpeed(player);
			if(player.getWalkSpeed() > 0.1 && player.getWalkSpeed() < 1 && speed > this.getPlayerMaxSpeed(player) && (1D-(this.getPlayerMaxSpeed(player)/speed)) > SPEED_OFFSET && !Utils.cantStandAtBetter(player.getLocation().getBlock()) && !Utils.isAboveIce(player.getLocation().getBlock())){
				AntiCheat.getPlayer(player).speedChecks ++;
				if(AntiCheat.getPlayer(player).speedChecks >= CHECKS_LIMIT){
					AntiCheat.getPlayer(player).speedChecks = 0;
					this.detect(player);
				}
			} else {
				AntiCheat.getPlayer(player).speedChecks = 0;
			}
		} else {
			AntiCheat.getPlayer(player).speedChecks = 0;
		}
	}

	private double getPlayerSpeed(Player player){
		Location location = player.getLocation().clone();
		location.setY(0);
		if(AntiCheat.getPlayer(player).lastLocation == null){
			AntiCheat.getPlayer(player).lastSpeedCheck = System.currentTimeMillis();
			AntiCheat.getPlayer(player).lastLocation = location;
		}
		double speed = AntiCheat.getPlayer(player).lastLocation.distance(location)/(System.currentTimeMillis()-AntiCheat.getPlayer(player).lastSpeedCheck)*1000/20;
		AntiCheat.getPlayer(player).lastSpeedCheck = System.currentTimeMillis();
		AntiCheat.getPlayer(player).lastLocation = location;
		return speed;
	}

	private double getPlayerMaxSpeed(Player player){
		return DEFAULT_SPEED*((1f/0.20)*player.getWalkSpeed())*(player.isSprinting() ? SPRINT_MODIFIER : 1)*(player.isSneaking() ? SNEAK_MODIFIER : 1);
	}
}