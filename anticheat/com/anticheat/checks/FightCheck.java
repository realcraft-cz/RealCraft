package com.anticheat.checks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.util.Vector;

import com.anticheat.AntiCheat;
import com.anticheat.checks.Check.CheckType;
import com.anticheat.events.AntiCheatDetectEvent;
import com.anticheat.utils.CollisionUtil;
import com.anticheat.utils.FightData;
import com.realcraft.RealCraft;

public class FightCheck implements Listener, Runnable {
	AntiCheat anticheat;

	private int tick;

	private Map<String, FightData> data = new HashMap<String, FightData>();

	public FightCheck(AntiCheat anticheat){
		this.anticheat = anticheat;
		Bukkit.getServer().getScheduler().runTaskTimer(RealCraft.getInstance(),this,0,0);
		Bukkit.getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	public FightData getPlayerFightData(Player player){
		String name = player.getName();
		if(!data.containsKey(name)) data.put(name,new FightData());
		return data.get(name);
	}

	@Override
	public void run(){
		tick ++;
	}

	@EventHandler(ignoreCancelled=false)
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
			Player player = (Player)event.getDamager();
			Player victim = (Player)event.getEntity();
			if(!AntiCheat.isPlayerExempted(player)){
				if(event.getCause() == DamageCause.ENTITY_ATTACK){
					this.checkSpeedFight(player);
					//this.checkDirectionFight(player,victim);
					this.checkNoSwing(player);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled=false)
	public void PlayerAnimationEvent(PlayerAnimationEvent event){
		this.getPlayerFightData(event.getPlayer()).noSwingArmSwung = true;
	}

	public void checkSpeedFight(Player player){

		FightData data = this.getPlayerFightData(player);
        // Add to frequency.
		data.speedBuckets.add(System.currentTimeMillis(), 1f);

        // Medium term (normalized to one second), account for server side lag.
        final long fullTime = 333 * 6;
        final float fullLag = 1f;
        final float total = data.speedBuckets.score(1f) * 1000f / (fullLag * fullTime);

        // Short term.
        if (tick < data.speedShortTermTick){
            // Tick task got reset.
        	data.speedShortTermTick = tick;
        	data.speedShortTermCount = 1;
        }
        else if (tick - data.speedShortTermTick < 7){
            // Account for server side lag.
                // Within range, add.
            data.speedShortTermCount ++;
        }
        else{
        	data.speedShortTermTick = tick;
        	data.speedShortTermCount = 1;
        }

        final float shortTerm = data.speedShortTermCount * 1000f / (50f * 7);

        final float max = Math.max(shortTerm, total);

        // Too many attacks?
        if (max > 15) {
        	data.speedViolations ++;
        	if(data.speedViolations > 3){
	        	AntiCheatDetectEvent callevent = new AntiCheatDetectEvent(player.getPlayer(),CheckType.KILLAURA_SPEED);
				Bukkit.getServer().getPluginManager().callEvent(callevent);
				data.speedViolations = 0;
        	}
        	data.speedShortTermTick = tick;
        	data.speedShortTermCount = 1;
        }
	}

	public void checkDirectionFight(Player player,Player damaged){
		Location loc = player.getLocation();
		Location dLoc = damaged.getLocation();

		double width = ((CraftEntity)damaged).getHandle().width;
		double height = ((CraftEntity)damaged).getHandle().getHeadHeight();
		Vector direction = loc.getDirection();

		double off = CollisionUtil.combinedDirectionCheck(loc, player.getEyeHeight(), direction, dLoc.getX(), dLoc.getY() + height / 2D, dLoc.getZ(), width, height, CollisionUtil.DIRECTION_PRECISION, 80.0);
		if(off > 0.1){
			FightData data = this.getPlayerFightData(player);
			data.directionViolations ++;
            if(data.directionViolations > 3){
            	AntiCheatDetectEvent callevent = new AntiCheatDetectEvent(player.getPlayer(),CheckType.KILLAURA_DIR);
				Bukkit.getServer().getPluginManager().callEvent(callevent);
				data.directionViolations = 0;
            }
		}
	}

	public void checkNoSwing(Player player){
		FightData data = this.getPlayerFightData(player);
		if(!data.noSwingArmSwung){
			data.noSwingViolations ++;
			if(data.noSwingViolations > 3){
				AntiCheatDetectEvent callevent = new AntiCheatDetectEvent(player.getPlayer(),CheckType.KILLAURA_NOSWING);
				Bukkit.getServer().getPluginManager().callEvent(callevent);
				data.noSwingViolations = 0;
			}
		} else {
			data.noSwingArmSwung = false;
		}
	}
}