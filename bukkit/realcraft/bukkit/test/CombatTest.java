package realcraft.bukkit.test;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import realcraft.bukkit.RealCraft;

public class CombatTest implements Listener {

	public CombatTest(){
		RealCraft.getInstance().getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1).toLowerCase();
		if(command.startsWith("pvp") && player.hasPermission("group.Manazer")){
			event.setCancelled(true);
			String[] args = command.split(" ");
			if(args.length < 2){
				player.sendMessage("§f/pvp <float>");
				return;
			}
			double value = Double.valueOf(args[1]);
			player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(value);
			player.saveData();
		}
	}
}