package realcraft.bukkit.fights;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;

import realcraft.bukkit.database.DB;
import realcraft.bukkit.fights.events.FightPlayerRankChange;
import realcraft.bukkit.fights.events.FightPlayerRankCreatedEvent;

public class FightPlayerData {

	private static final String FIGHTS_PLAYERS = "fights_players";

	private FightPlayer fPlayer;
	private int score;
	private int kills;
	private int deaths;
	private int rankeds;

	public FightPlayerData(FightPlayer fPlayer){
		this.fPlayer = fPlayer;
		this.reload();
	}

	public int getScore(){
		return score;
	}

	public void addScore(int score){
		FightRank oldRank = fPlayer.getRank();
		this.score += score;
		if(this.score < FightRank.MIN_SCORE) this.score = FightRank.MIN_SCORE;
		this.save();
		fPlayer.updateExp();
		fPlayer.getLobbyScoreboard().update();
		if(fPlayer.getRank() != oldRank){
			Bukkit.getServer().getPluginManager().callEvent(new FightPlayerRankChange(fPlayer,oldRank));
		}
	}

	public int getKills(){
		return kills;
	}

	public void addKill(){
		kills ++;
		this.save();
	}

	public int getDeaths(){
		return deaths;
	}

	public void addDeath(){
		deaths ++;
		this.save();
	}

	public int getRankeds(){
		return rankeds;
	}

	public void addRanked(){
		rankeds ++;
		this.save();
		if(rankeds == FightRank.MIN_MATCHES){
			Bukkit.getServer().getPluginManager().callEvent(new FightPlayerRankCreatedEvent(fPlayer));
		}
	}

	public void save(){
		DB.update("UPDATE "+FIGHTS_PLAYERS+" SET "
				+ "fight_score = '"+this.getScore()+"',"
				+ "fight_kills = '"+this.getKills()+"',"
				+ "fight_deaths = '"+this.getDeaths()+"',"
				+ "fight_rankeds = '"+this.getRankeds()+"'"
				+ "WHERE user_id = '"+fPlayer.getUser().getId()+"'"
		);
	}

	public void reload(){
		ResultSet rs = DB.query("SELECT * FROM "+FIGHTS_PLAYERS+" WHERE user_id = '"+fPlayer.getUser().getId()+"'");
		try {
			if(rs.next()){
				score = rs.getInt("fight_score");
				kills = rs.getInt("fight_kills");
				deaths = rs.getInt("fight_deaths");
				rankeds = rs.getInt("fight_rankeds");
			} else {
				DB.update("INSERT INTO "+FIGHTS_PLAYERS+" (user_id,fight_score) VALUES('"+fPlayer.getUser().getId()+"','"+FightRank.DEFAULT_SCORE+"')");
				score = FightRank.DEFAULT_SCORE;
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}
}