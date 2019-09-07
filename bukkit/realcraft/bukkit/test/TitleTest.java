package realcraft.bukkit.test;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.utils.StringUtil;
import realcraft.bukkit.utils.Title;

public class TitleTest implements Listener {

	public TitleTest(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1);
		if(command.startsWith("title") && player.hasPermission("group.Manazer")){
			event.setCancelled(true);
			String[] args = command.split(" ");
			if(args.length < 2){
				player.sendMessage("/title <title> [_ subtitle]");
				return;
			}
			String[] params = StringUtil.combineSplit(1,args).split(" _ ");
			if(params.length == 1){
				Title.showTitle(player,ChatColor.translateAlternateColorCodes('&',params[0]),0.2,5,0.2);
			}
			else if(params.length == 2){
				Title.showTitle(player,ChatColor.translateAlternateColorCodes('&',params[0]),0.2,5,0.2);
				Title.showSubTitle(player,ChatColor.translateAlternateColorCodes('&',params[1]),0.2,5,0.2);
			}
		}
	}
}