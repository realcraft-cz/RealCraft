package realcraft.bukkit.falling.arena;

import realcraft.bukkit.database.DB;
import realcraft.bukkit.falling.FallManager;
import realcraft.bukkit.falling.FallPlayer;
import realcraft.share.users.User;
import realcraft.share.users.UserRank;
import realcraft.share.users.Users;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FallArena {

	private int id;
	private User owner;
	private FallArenaRegion region = new FallArenaRegion(this);
	private int created;

	public FallArena(int id){
		this.id = id;
	}

	public FallArena(User owner){
		this.owner = owner;
	}

	public int getId(){
		return id;
	}

	public User getOwner(){
		return owner;
	}

	public FallArenaRegion getRegion(){
		return region;
	}

	public int getCreated(){
		return created;
	}

	public FallArenaPermission getPermission(FallPlayer fPlayer){
		if(fPlayer.getUser().equals(this.getOwner()) || fPlayer.getUser().getRank().isMinimum(UserRank.ADMIN)) return FallArenaPermission.OWNER;
		//else if(trusted.getValues().contains(new MapDataInteger(fPlayer.getUser().getId()))) return MapPermission.BUILD;
		return FallArenaPermission.NONE;
	}

	public void create(){
		created = (int)(System.currentTimeMillis()/1000);
		try {
			ResultSet rs = DB.insert("INSERT INTO "+FallManager.FALL_ARENAS+" (user_id,map_created) VALUES(?,?)",
					this.getOwner().getId(),
					this.getCreated()
			);
			if(rs.next()){
				id = rs.getInt(1);
				rs.close();
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		this.getRegion().generate();
	}

	public void load(){
		ResultSet rs = DB.query("SELECT * FROM "+FallManager.FALL_ARENAS+" WHERE arena_id = '"+this.getId()+"'");
		try {
			if(rs.next()){
				owner = Users.getUser(rs.getInt("user_id"));
				created = rs.getInt("map_created");
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	public void run(){
		this.getRegion().dropBlocks();
	}
}