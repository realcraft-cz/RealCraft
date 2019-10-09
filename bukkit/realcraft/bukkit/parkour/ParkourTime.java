package realcraft.bukkit.parkour;

import java.sql.ResultSet;
import java.sql.SQLException;

import realcraft.bukkit.RealCraft;

public class ParkourTime {

	private int id;
	private ParkourArena arena;
	private int author;
	private String authorName;
	private int time;
	private int created;

	public ParkourTime(int id,ParkourArena arena){
		this.id = id;
		this.arena = arena;
		ResultSet rs = RealCraft.getInstance().db.query("SELECT t1.*,t2.user_name FROM "+Parkour.PARKOUR_TIMES+" t1 INNER JOIN authme t2 USING(user_id) WHERE time_id = '"+this.getId()+"'");
		try {
			if(rs.next()){
				this.author = rs.getInt("user_id");
				this.authorName = rs.getString("user_name");
				this.time = rs.getInt("time_value");
				this.created = rs.getInt("time_created");
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	public int getId(){
		return id;
	}

	public ParkourArena getArena(){
		return arena;
	}

	public int getAuthor(){
		return author;
	}

	public String getAuthorName(){
		return authorName;
	}

	public int getTime(){
		return time;
	}

	public int getCreated(){
		return created;
	}

	public String getTimeFormat(){
		int minutes = (int)(Math.floor(((double)this.getTime())/1000/60)%60);
		int seconds = (this.getTime()/1000)%60;
		int hundredths = (this.getTime())%1000;
		return (minutes < 10 ? "0" : "")+minutes+":"+(seconds < 10 ? "0" : "")+seconds+"."+(hundredths < 10 ? "00" : (hundredths < 100 ? "0" : ""))+hundredths;
	}
}