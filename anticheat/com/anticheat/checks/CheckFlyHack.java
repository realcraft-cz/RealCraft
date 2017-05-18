package com.anticheat.checks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.anticheat.AntiCheat;
import com.anticheat.utils.Utils;

public class CheckFlyHack extends Check {

	private static final int HEIGHT_LIMIT = 100;
	private static final int CHECKS_LIMIT = 10;
	private static final double BLOCKS_LIMIT = 30.0;

	public CheckFlyHack(){
		super(CheckType.FLYHACK);
	}

	@Override
	public void run(){
		for(Player player : Bukkit.getOnlinePlayers()){
			AntiCheat.getPlayer(player).run();
		}
	}

	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(!AntiCheat.isPlayerExempted(player)){
	        this.check(player,event.getFrom(),event.getTo());
		}
	}

	@EventHandler
	public void PlayerTeleportEvent(PlayerTeleportEvent event){
		AntiCheat.getPlayer(event.getPlayer()).reset();
	}

	@EventHandler
	public void EntityDamageEvent(EntityDamageEvent event){
		if(event.getEntity() instanceof Player && event.getCause() == DamageCause.ENTITY_EXPLOSION){
			AntiCheat.getPlayer((Player)event.getEntity()).reset();
		}
	}

	public void check(Player player,Location from,Location to){
		if(!Utils.canPlayerFly(player) && Math.abs(from.getY()-to.getY()) < HEIGHT_LIMIT){
			if(Utils.cantStandAtBetter(player.getLocation().getBlock()) && !AntiCheat.getPlayer(player).isAboveSlimeBlocks()){
				AntiCheat.getPlayer(player).blocksOverFlight += Math.abs(from.getX()-to.getX()) + Math.abs(from.getY()-to.getY()) + Math.abs(from.getZ()-to.getZ());
				AntiCheat.getPlayer(player).flightChecks ++;
				if(from.getY() > to.getY()) AntiCheat.getPlayer(player).blocksOverFlight += (-Math.abs(from.getY()-to.getY())*2);
				if(AntiCheat.getPlayer(player).blocksOverFlight > BLOCKS_LIMIT && AntiCheat.getPlayer(player).flightChecks >= CHECKS_LIMIT){
					AntiCheat.getPlayer(player).blocksOverFlight = 0;
					AntiCheat.getPlayer(player).flightChecks = 0;
					this.detect(player);
				}
			} else {
				AntiCheat.getPlayer(player).blocksOverFlight = 0;
				AntiCheat.getPlayer(player).flightChecks = 0;
			}
		} else {
			AntiCheat.getPlayer(player).blocksOverFlight = 0;
			AntiCheat.getPlayer(player).flightChecks = 0;
		}
	}
}