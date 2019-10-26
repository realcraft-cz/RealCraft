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
import realcraft.share.users.Users;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FallArena {

	private int id;
	private User owner;
	private FallArenaRegion region;
	private FallArenaDrops drops = new FallArenaDrops(this);
	private int created;
	private int updated;
	private boolean active = false;

	private ArrayList<FallPlayer> trusted = new ArrayList<>();
	private JsonDataList<JsonDataInteger> trustedData = new JsonDataList<>("trusted",JsonDataInteger.class);

	private JsonDataBoolean lockedData = new JsonDataBoolean("locked");
	private JsonDataInteger ticksData = new JsonDataInteger("ticks");

	public FallArena(int id){
		this.id = id;
		this.region = new FallArenaRegion(this);
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

	public boolean isActive(){
		return active;
	}

	public void setActive(boolean active){
		this.active = active;
	}

	public boolean isLocked(){
		return lockedData.getValue();
	}

	public void setLocked(boolean lock){
		lockedData.setValue(lock);
	}

	public int getTicks(){
		return ticksData.getValue();
	}

	public void resetTicks(){
		ticksData.setValue(0);
	}

	public FallArenaPermission getPermission(FallPlayer fPlayer){
		if(fPlayer.getUser().equals(this.getOwner())) return FallArenaPermission.OWNER;
		else if(trusted.contains(fPlayer)) return FallArenaPermission.TRUSTED;
		return FallArenaPermission.NONE;
	}

	public ArrayList<Player> getOnlinePlayers(){
		ArrayList<Player> players = new ArrayList<>();
		for(Player player : Bukkit.getOnlinePlayers()){
			if(this.equals(FallManager.getFallPlayer(player).getArena())){
				players.add(player);
			}
		}
		return players;
	}

	public ArrayList<FallPlayer> getOnlineFallPlayers(){
		ArrayList<FallPlayer> fPlayers = new ArrayList<>();
		for(Player player : this.getOnlinePlayers()){
			fPlayers.add(FallManager.getFallPlayer(player));
		}
		return fPlayers;
	}

	public void create(){
		created = (int)(System.currentTimeMillis()/1000);
		try {
			ResultSet rs = DB.insert("INSERT INTO "+FallManager.FALL_ARENAS+" (user_id,arena_data,arena_created) VALUES(?,?,?)",
					this.getOwner().getId(),
					this.getJsonData(),
					this.getCreated()
			);
			if(rs.next()){
				id = rs.getInt(1);
				rs.close();
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		this.region = new FallArenaRegion(this);
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
		this.getDrops().resetTicks();
	}

	public void save(){
		updated = (int)(System.currentTimeMillis()/1000);
		DB.update("UPDATE "+FallManager.FALL_ARENAS+" SET user_id = ?,arena_data = ?,arena_updated = ? WHERE arena_id = '"+this.getId()+"'",
				this.getOwner().getId(),
				this.getJsonData(),
				this.getUpdated()
		);
	}

	private String getJsonData(){
		JsonData data = new JsonData();
		trustedData.clear();
		for(FallPlayer fPlayer : trusted){
			trustedData.add(new JsonDataInteger(fPlayer.getUser().getId()));
		}
		data.addProperty(trustedData);
		data.addProperty(lockedData);
		data.addProperty(ticksData);
		return data.toString();
	}

	private void loadData(JsonData data){
		trustedData.loadData(data);
		for(JsonDataInteger value : trustedData.getValues()){
			trusted.add(FallManager.getFallPlayer(Users.getUser(value.getValue())));
		}
		lockedData.loadData(data);
		ticksData.loadData(data);
	}

	public void run(){
		if(this.isActive()){
			if(!this.getRegion().isGenerating()){
				this.ticksData.setValue(this.getTicks()+5);
				this.getDrops().drop();
			}
			if(this.getTicks()%600 == 0){
				this.save();
			}
		}
	}

	public void sendMessage(String message){
		this.sendMessage(message,false);
	}

	public void sendMessage(String message,boolean prefix){
		for(Player player : this.getOnlinePlayers()){
			if(prefix){
				FallManager.sendMessage(player,message);
			} else {
				player.sendMessage(message);
			}
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