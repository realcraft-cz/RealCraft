package realcraft.bukkit.anticheat.checks;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import realcraft.bukkit.anticheat.AntiCheat;
import realcraft.bukkit.anticheat.utils.BlockProperties;

//https://github.com/NoCheatPlus/NoCheatPlus/blob/806e3c2ef87eb43dee5a0e9dd0adbb1ad185d12d/NCPCore/src/main/java/fr/neatmonster/nocheatplus/utilities/map/BlockProperties.java
//https://github.com/NoCheatPlus/NoCheatPlus/blob/806e3c2ef87eb43dee5a0e9dd0adbb1ad185d12d/NCPCore/src/main/java/fr/neatmonster/nocheatplus/checks/blockbreak/BlockBreakListener.java
//https://github.com/NoCheatPlus/NoCheatPlus/blob/806e3c2ef87eb43dee5a0e9dd0adbb1ad185d12d/NCPCore/src/main/java/fr/neatmonster/nocheatplus/checks/blockbreak/FastBreak.java

public class CheckFastBreak extends Check {

	private static final int BREAK_DELAY = 100;
	private static final int CHECKS_LIMIT = 5;

	public CheckFastBreak(){
		super(CheckType.FASTBREAK);
	}

	@Override
	public void run(){
	}

	@EventHandler
	public void BlockBreakEvent(BlockBreakEvent event){
		if(event.getPlayer().getGameMode() != GameMode.CREATIVE){
			this.check(event.getPlayer(),event.getBlock());
		}
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){

	}

	public void check(Player player,Block block){
		final long now = System.currentTimeMillis();
		final long expectedBreakingTime = Math.max(0,Math.round((double)BlockProperties.getBreakingDuration(block.getType(),player)));
		final long elapsedTime = (AntiCheat.getPlayer(player).fastBreakBreakTime > now) ? 0 : now - AntiCheat.getPlayer(player).fastBreakBreakTime;
		if(elapsedTime+BREAK_DELAY < expectedBreakingTime){
			AntiCheat.getPlayer(player).fastBreakChecks ++;
			if(AntiCheat.getPlayer(player).fastBreakChecks >= CHECKS_LIMIT){
				AntiCheat.getPlayer(player).fastBreakChecks = 0;
				this.detect(player);
			}
		}
		AntiCheat.getPlayer(player).fastBreakBreakTime = now;
	}
}