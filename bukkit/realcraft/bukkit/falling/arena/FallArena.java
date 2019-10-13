package realcraft.bukkit.falling.arena;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

	public boolean hasPlayers(){
		for(Player player : Bukkit.getOnlinePlayers()){
			if(FallManager.getFallPlayer(player).getArena() != null && FallManager.getFallPlayer(player).getArena().equals(this)){
				return true;
			}
		}
		return false;
	}

	public void create(){
		created = (int)(System.currentTimeMillis()/1000);
		try {
			ResultSet rs = DB.insert("INSERT INTO "+FallManager.FALL_ARENAS+" (user_id,arena_created) VALUES(?,?)",
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
				created = rs.getInt("arena_created");
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	public void run(){
		if(this.hasPlayers()){
			this.getRegion().drop();
		}
	}

	@Override
	public int hashCode(){
		return this.getId();
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof FallArena){
			FallArena toCompare = (FallArena) object;
			return (toCompare.getId() == this.getId());
		}
		return false;
	}
}