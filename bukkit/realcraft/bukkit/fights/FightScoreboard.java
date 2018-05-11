package realcraft.bukkit.fights;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public abstract class FightScoreboard {

	private Scoreboard scoreboard;
	private Objective objective;
	private Team spectatorTeam;

	private String title;
	private HashMap<Integer,String> lines = new HashMap<Integer,String>();

	public FightScoreboard(){
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
		spectatorTeam = scoreboard.registerNewTeam("xSpectator");
		spectatorTeam.setAllowFriendlyFire(false);
		spectatorTeam.setColor(ChatColor.GRAY);
		spectatorTeam.setPrefix(ChatColor.GRAY.toString());
		spectatorTeam.setCanSeeFriendlyInvisibles(true);
		spectatorTeam.setOption(Option.COLLISION_RULE,OptionStatus.NEVER);
	}

	public Scoreboard getScoreboard(){
		return scoreboard;
	}

	public Objective getObjective(){
		return objective;
	}

	public String getTitle(){
		return title;
	}

	public void setTitle(String name){
		this.title = name;
	}

	public HashMap<Integer,String> getLines(){
		return lines;
	}

	public String getLine(int index){
		return lines.get(index);
	}

	public void setLine(int index,String name){
		lines.put(index,name);
	}

	public void clearLines(){
		lines.clear();
	}

	public void update(){
		scoreboard.clearSlot(DisplaySlot.SIDEBAR);
		objective = scoreboard.getObjective("objective");
		if(objective != null) objective.unregister();
		objective = scoreboard.registerNewObjective("objective","dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(title);
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

	public void addPlayer(FightPlayer fPlayer){
		this.removeSpectator(fPlayer);
		if(fPlayer.getPlayer().getScoreboard() != scoreboard) fPlayer.getPlayer().setScoreboard(scoreboard);
	}

	public void addSpectator(FightPlayer fPlayer){
		spectatorTeam.addEntry(fPlayer.getPlayer().getName());
	}

	public void removeSpectator(FightPlayer fPlayer){
		spectatorTeam.removeEntry(fPlayer.getPlayer().getName());
	}
}