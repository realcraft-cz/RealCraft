package com.parkour;

import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.realcraft.RealCraft;

public class ParkourScoreboard implements Runnable {

	private ParkourArena arena;
	private Scoreboard scoreboard;
	private Objective objective;

	public ParkourScoreboard(ParkourArena arena){
		this.arena = arena;
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,40,40);
	}

	public ParkourArena getArena(){
		return arena;
	}

	public Scoreboard getScoreboard(){
		return scoreboard;
	}

	@Override
	public void run(){
		if(scoreboard != null) this.update();
	}

	public void update(){
		scoreboard.clearSlot(DisplaySlot.SIDEBAR);

		objective = scoreboard.getObjective("parkour");
		if(objective != null){
			objective.unregister();
		}

		objective = scoreboard.registerNewObjective("parkour","dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("§e§l"+(this.getArena().getName().length() > 28 ? this.getArena().getName().substring(0,28) : this.getArena().getName()));

		Set<Entry<Integer,String>> lines = this.getArena().getScoreboardLines().entrySet();
		for(Entry<Integer,String> entry : lines){
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
		this.disableCollisions();
	}

	@SuppressWarnings("deprecation")
	public void disableCollisions(){
		Team noCollision = this.getScoreboard().getTeam("noCollision");
		if(noCollision == null){
			noCollision = this.getScoreboard().registerNewTeam("noCollision");
			noCollision.setOption(Team.Option.COLLISION_RULE,Team.OptionStatus.NEVER);
			noCollision.setCanSeeFriendlyInvisibles(true);
		}
		for(ParkourPlayer player : Parkour.getPlayers()){
			Team playerTeam = this.getScoreboard().getPlayerTeam(player.getPlayer());
	        if(playerTeam == null){
	            noCollision = this.getScoreboard().getTeam("noCollision");
	            noCollision.addPlayer(player.getPlayer());
	        }
		}
	}
}
