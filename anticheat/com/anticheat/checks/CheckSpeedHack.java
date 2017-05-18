package com.anticheat.checks;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffectType;

import com.anticheat.AntiCheat;
import com.anticheat.utils.Utils;

public class CheckSpeedHack extends Check {

	private static final double SPEED_LIMIT = 0.4;
	private static final int CHECKS_LIMIT = 20;

	public CheckSpeedHack(){
		super(CheckType.SPEEDHACK);
	}

	@Override
	public void run(){
	}

	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(!AntiCheat.isPlayerExempted(player)){
			this.check(player,event.getFrom().clone(),event.getTo().clone());
		}
	}

	@EventHandler
	public void PlayerTeleportEvent(PlayerTeleportEvent event){
		AntiCheat.getPlayer(event.getPlayer()).reset();
	}

	public void check(Player player,Location from,Location to){
		from.setY(0);
		to.setY(0);
		if(!Utils.canPlayerFly(player) && !player.hasPotionEffect(PotionEffectType.SPEED)){
			if(from.distance(to) > SPEED_LIMIT*((1f/0.2)*player.getWalkSpeed()) && !Utils.cantStandAtBetter(player.getLocation().getBlock()) && !Utils.isAboveIce(player.getLocation().getBlock())){
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
}