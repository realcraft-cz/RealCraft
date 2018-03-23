package realcraft.bukkit.test;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import realcraft.bukkit.RealCraft;

public class SpectatorTest implements Listener {

	private HashMap<Player,Boolean> spectators = new HashMap<Player,Boolean>();
	private Scoreboard scoreboard;
	private Team team;

	public SpectatorTest(){
		RealCraft.getInstance().getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		team = scoreboard.registerNewTeam("0Spectators");
		team.setAllowFriendlyFire(false);
		team.setColor(ChatColor.GRAY);
		team.setCanSeeFriendlyInvisibles(true);
		team.setOption(Option.COLLISION_RULE,OptionStatus.NEVER);
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1).toLowerCase();
		if(command.equalsIgnoreCase("spectest")){
			event.setCancelled(true);
			if(spectators.containsKey(player)){
				player.sendMessage("Spectator disabled");
				spectators.remove(player);
				this.disableSpectator(player);
			} else {
				player.sendMessage("Spectator enabled");
				spectators.put(player,true);
				this.enableSpectator(player);
			}
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void ProjectileHitEvent(ProjectileHitEvent event){
	}

	public void enableSpectator(Player player){
		player.setGameMode(GameMode.ADVENTURE);
		player.setAllowFlight(true);
		player.setFlying(true);
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,20*300,999,false,false));
		player.setCollidable(false);
		for(Player player2 : Bukkit.getOnlinePlayers()){
			player2.hidePlayer(RealCraft.getInstance(),player);
		}
		player.setScoreboard(scoreboard);
		team.addEntry(player.getName());
	}

	public void disableSpectator(Player player){
		player.setGameMode(GameMode.SURVIVAL);
		player.setAllowFlight(false);
		player.setFlying(false);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
		for(Player player2 : Bukkit.getOnlinePlayers()){
			player2.showPlayer(RealCraft.getInstance(),player);
		}
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		team.removeEntry(player.getName());
	}
}