package realcraft.bukkit.fights.spectators;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.fights.FightPlayer.FightPlayerState;
import realcraft.bukkit.fights.Fights;

public class FightDuelSpectator extends FightSpectator {

	public ArrayList<FightSpectatorHotbarItem> getHotbarItems(){
		if(hotbarItems == null){
			hotbarItems = new ArrayList<FightSpectatorHotbarItem>();
			hotbarItems.add(new FightSpectatorHotbarItem(8,"§e§lOpustit hru",Material.SLIME_BALL));
		}
		return hotbarItems;
	}

	@EventHandler(ignoreCancelled=false)
	public void PlayerInteractEvent(PlayerInteractEvent event){
		FightPlayer fPlayer = Fights.getFightPlayer(event.getPlayer());
		if(fPlayer.getState() == FightPlayerState.SPECTATOR){
			if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.SLIME_BALL && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
				event.setCancelled(true);
				Fights.joinLobby(fPlayer);
			}
		}
	}
}