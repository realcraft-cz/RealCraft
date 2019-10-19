package realcraft.bukkit.falling.arena;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.falling.FallManager;
import realcraft.bukkit.falling.FallPlayer;
import realcraft.bukkit.falling.arena.drops.FallArenaDrops;
import realcraft.bukkit.utils.json.JsonData;
import realcraft.bukkit.utils.json.JsonDataBoolean;
import realcraft.bukkit.utils.json.JsonDataInteger;
import realcraft.bukkit.utils.json.JsonDataList;
import realcraft.share.users.User;
import realcraft.share.users.UserRank;
import realcraft.share.users.Users;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FallArena {

	private int id;
	private User owner;
	private FallArenaRegion region = new FallArenaRegion(this);
	private FallArenaDrops drops = new FallArenaDrops(this);
	private int created;
	private int updated;
	private boolean hasPlayersCached = false;

	private ArrayList<FallPlayer> trusted = new ArrayList<>();
	private JsonDataList<JsonDataInteger> trustedData = new JsonDataList<>("trusted",JsonDataInteger.class);

	private JsonDataBoolean locked = new JsonDataBoolean("locked");

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

	public FallArenaDrops getDrops(){
		return drops;
	}

	public ArrayList<FallPlayer> getTrusted(){
		return trusted;
	}

	public int getCreated(){
		return created;
	}

	public int getUpdated(){
		return updated;
	}

	public boolean isLocked(){
		return locked.getValue();
	}

	public void setLocked(boolean lock){
		locked.setValue(lock);
		this.save();
	}

	public FallArenaPermission getPermission(FallPlayer fPlayer){
		if(fPlayer.getUser().equals(this.getOwner()) || fPlayer.getUser().getRank().isMinimum(UserRank.ADMIN)) return FallArenaPermission.OWNER;
		else if(trusted.contains(fPlayer)) return FallArenaPermission.TRUSTED;
		return FallArenaPermission.NONE;
	}

	public void checkHasPlayers(){
		hasPlayersCached = false;
		for(Player player : Bukkit.getOnlinePlayers()){
			if(this.equals(FallManager.getFallPlayer(player).getArena())){
				hasPlayersCached = true;
				break;
			}
		}
	}

	public boolean hasPlayersCached(){
		return hasPlayersCached;
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
				this.loadData(new JsonData(rs.getString("arena_data")));
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	public void save(){
		updated = (int)(System.currentTimeMillis()/1000);
		this.putData();
		DB.update("UPDATE "+FallManager.FALL_ARENAS+" SET user_id = ?,arena_data = ?,arena_updated = ? WHERE arena_id = '"+this.getId()+"'",
				this.getOwner().getId(),
				this.getJsonData(),
				this.getUpdated()
		);
	}

	private String getJsonData(){
		JsonData data = new JsonData();
		data.addProperty(trustedData);
		return data.toString();
	}

	private void loadData(JsonData data){
		trustedData.loadData(data);
		for(JsonDataInteger value : trustedData.getValues()){
			trusted.add(FallManager.getFallPlayer(Users.getUser(value.getValue())));
		}
	}

	private void putData(){
		trustedData.clear();
		for(FallPlayer fPlayer : trusted){
			trustedData.add(new JsonDataInteger(fPlayer.getUser().getId()));
		}
	}

	int ticks5 = 0;
	public void run(){
		ticks5 ++;
		if(this.hasPlayersCached() && !this.getRegion().isGenerating()){
			this.getDrops().drop();
		}
		if(ticks5%8 == 0){
			this.checkHasPlayers();
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