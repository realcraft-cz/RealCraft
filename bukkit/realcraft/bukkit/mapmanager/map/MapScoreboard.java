package realcraft.bukkit.mapmanager.map;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.utils.DateUtil;

import java.util.HashMap;

public class MapScoreboard implements Runnable {

	private Map map;

	private Scoreboard scoreboard;
	private Objective objective;
	private HashMap<Integer,String> lines = new HashMap<>();

	public MapScoreboard(Map map){
		this.map = map;
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
		Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(),this,20,20);
	}

	public Scoreboard getScoreboard(){
		return scoreboard;
	}

	public void addLine(String name){
		lines.put(lines.size(),name);
	}

	public void clearLines(){
		lines.clear();
	}

	public void update(){
		scoreboard.clearSlot(DisplaySlot.SIDEBAR);
		objective = scoreboard.getObjective("objective");
		if(objective != null) objective.unregister();
		objective = scoreboard.registerNewObjective("objective","dummy","§f§l"+map.getName()+" §7[#"+map.getId()+"]");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		for(java.util.Map.Entry<Integer,String> entry : lines.entrySet()){
			int index = lines.size()-entry.getKey();
			String line = entry.getValue();
			if(line.trim().equals("")){
				for(int i=0;i<index;i++){
					line = line+" ";
				}
			}
			Score score = objective.getScore(line);
			score.setScore(index);
		}
	}

	@Override
	public void run(){
		this.clearLines();
		this.addLine("§7Typ: "+map.getType().getColor()+map.getType().getName());
		this.addLine("§7Stav: "+map.getState().getColor()+"§l"+map.getState().getName());
		this.addLine("");
		map.updateScoreboard(this);
		this.addLine("");
		this.addLine("§7"+DateUtil.lastTime(map.getUpdated()));
		this.update();
	}
}