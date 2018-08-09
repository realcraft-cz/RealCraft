package realcraft.bukkit.lobby;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.auth.AuthLoginEvent;
import realcraft.bukkit.users.Users;

import java.util.HashMap;
import java.util.Map.Entry;

public class LobbyScoreboard implements Listener, Runnable {
	Lobby lobby;

	Player player;
	Scoreboard scoreboard;
	Objective objective;

	HashMap<Integer,String> lines = new HashMap<Integer,String>();

	int scheduleTask = -1;
	int infoState = 0;

	public LobbyScoreboard(Lobby lobby,Player player){
		this.lobby = lobby;
		this.player = player;
		RealCraft.getInstance().getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler
	public void AuthLoginEvent(AuthLoginEvent event){
		Player player = event.getPlayer();
		if(this.player.equals(player)){
			if(scoreboard == null) scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
			player.setScoreboard(scoreboard);
			if(scheduleTask == -1) scheduleTask = RealCraft.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,20,20);
		}
	}

	@EventHandler
	public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event){
		if(scoreboard != null && this.player.equals(player) && event.getPlayer().getWorld().getName().equalsIgnoreCase("world")){
			player.setScoreboard(scoreboard);
		}
	}

	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent event){
		if(scoreboard != null && this.player.equals(event.getPlayer())){
			scoreboard = null;
			RealCraft.getInstance().getServer().getScheduler().cancelTask(scheduleTask);
		}
	}

	@Override
	public void run(){
		if(scoreboard != null && player.getWorld().getName().equalsIgnoreCase("world")) this.update();
	}

	public void update(){
		infoState ++;
		if(infoState == 16) infoState = 0;

		scoreboard.clearSlot(DisplaySlot.SIDEBAR);

		objective = scoreboard.getObjective("lobby");
		if(objective != null){
			objective.unregister();
		}

		objective = scoreboard.registerNewObjective("lobby","dummy","§e§lRealCraft.cz");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		this.resetLines();

		this.addLine(" ");
		this.addLine("§7§lPripojeno");
		this.addLine("§f"+LobbyMenu.getAllPlayersCount()+"/100");
		this.addLine(" ");
		this.addLine("§a§lCoins");
		this.addLine("§f"+Users.getUser(player).getCoins());
		this.addLine(" ");
		this.addLine("§c§lWeb");
		this.addLine("§fwww.realcraft.cz");

		for(Entry<Integer,String> entry : lines.entrySet()){
			int index = lines.size()-entry.getKey();
			String line = entry.getValue();
			if(line.trim().equals("")){
				for(int i=0;i<index;i++){
					line = line + " ";
				}
			}
			Score score = objective.getScore(line);
			score.setScore(index);
		}
	}

	public void resetLines(){
		lines = new HashMap<Integer,String>();
	}

	public void addLine(String text){
		lines.put(lines.size(),text);
	}
}