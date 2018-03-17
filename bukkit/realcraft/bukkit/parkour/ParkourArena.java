package realcraft.bukkit.parkour;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.parkour.exceptions.ParkourAlreadyRatedException;
import realcraft.bukkit.parkour.exceptions.ParkourCheckPointsException;
import realcraft.bukkit.parkour.exceptions.ParkourFinishLocationException;
import realcraft.bukkit.parkour.exceptions.ParkourInvalidNameException;
import realcraft.bukkit.parkour.exceptions.ParkourNameExistsException;
import realcraft.bukkit.parkour.exceptions.ParkourNotReadyException;
import realcraft.bukkit.parkour.exceptions.ParkourNotTestedException;
import realcraft.bukkit.parkour.exceptions.ParkourOwnRatingException;
import realcraft.bukkit.parkour.exceptions.ParkourPlayerNotFoundException;
import realcraft.bukkit.parkour.exceptions.ParkourStartLocationException;
import realcraft.bukkit.parkour.menu.ParkourMenuRating;
import realcraft.bukkit.parkour.menu.ParkourMenuType;
import realcraft.bukkit.parkour.utils.LocationUtil;
import realcraft.bukkit.parkour.utils.RegionWrapper;
import realcraft.bukkit.utils.DateUtil;
import realcraft.bukkit.utils.FireworkUtil;
import realcraft.bukkit.utils.JsonUtil;
import realcraft.bukkit.utils.Title;

public class ParkourArena {

	private int id;
	private int author;
	private String authorName;
	private String name;
	private int pros;
	private int cons;
	private int times;
	private long created;
	private ParkourClockType clock;
	private World world = null;
	private boolean ready = false;
	private boolean tested = false;

	private ParkourTime recordTime;
	private ParkourScoreboard scoreboard;

	private Location startLoc;
	private Location finishLoc;
	private ArrayList<Location> checkPoints = new ArrayList<Location>();
	private HashMap<Integer,ParkourOfflinePlayer> collaborators = new HashMap<Integer,ParkourOfflinePlayer>();

	public ParkourArena(int id){
		this.id = id;
		this.world = Bukkit.getServer().getWorld("world_parkour");
		this.scoreboard = new ParkourScoreboard(this);
		ResultSet rs = RealCraft.getInstance().db.query("SELECT t1.*,t2.user_name FROM "+Parkour.PARKOUR_ARENAS+" t1 INNER JOIN authme t2 USING(user_id) WHERE parkour_id = '"+this.id+"'");
		try {
			if(rs.next()){
				this.author = rs.getInt("user_id");
				this.authorName = rs.getString("user_name");
				this.name = rs.getString("parkour_name");
				this.startLoc = JsonUtil.getJSONLocation(rs.getString("parkour_startloc"),this.getWorld());
				this.finishLoc = JsonUtil.getJSONLocation(rs.getString("parkour_finishloc"),this.getWorld());
				this.checkPoints = JsonUtil.getJSONLocationList(rs.getString("parkour_checkpoints"),this.getWorld());
				for(Integer pid : JsonUtil.getJSONIntegerList(rs.getString("parkour_collaborators"))){
					this.collaborators.put(pid,new ParkourOfflinePlayer(pid));
				}
				this.created = rs.getLong("parkour_created");
				this.clock = ParkourClockType.fromId(rs.getInt("parkour_clock"));
				this.ready = rs.getBoolean("parkour_ready");
				this.loadRatings();
				this.loadRecord();
				this.loadTimes();
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	private void save(){
		try {
			PreparedStatement stmt;
			stmt = RealCraft.getInstance().db.conn.prepareStatement("UPDATE "+Parkour.PARKOUR_ARENAS+" SET user_id = ?,parkour_name = ?,parkour_startloc = ?,parkour_finishloc = ?,parkour_checkpoints = ?,parkour_collaborators = ?,parkour_clock = ?,parkour_ready = ? WHERE parkour_id = '"+this.id+"'");
			stmt.setInt(1,this.getAuthor());
			stmt.setString(2,this.getName());
			stmt.setString(3,JsonUtil.toJSONLocation(this.getStartLocation()));
			stmt.setString(4,JsonUtil.toJSONLocation(this.getFinishLocation()));
			stmt.setString(5,JsonUtil.toJSONLocationList(this.getCheckPoints()));
			ArrayList<Integer> pids = new ArrayList<Integer>();
			for(ParkourOfflinePlayer player : this.getCollaborators().values()) pids.add(player.getId());
			stmt.setString(6,JsonUtil.toJSONIntegerList(pids));
			stmt.setInt(7,this.getClock().getId());
			stmt.setBoolean(8,this.isReady());
			stmt.executeUpdate();
		}
		catch (SQLException e){
			e.printStackTrace();
		}
	}

	private void loadRatings(){
		this.pros = 0;
		this.cons = 0;
		ResultSet rs = RealCraft.getInstance().db.query("SELECT rating_value FROM "+Parkour.PARKOUR_RATINGS+" WHERE parkour_id = '"+this.id+"'");
		try {
			while(rs.next()){
				if(rs.getInt("rating_value") > 0) this.pros ++;
				else this.cons ++;
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	private void loadRecord(){
		recordTime = null;
		ResultSet rs = RealCraft.getInstance().db.query("SELECT time_id FROM "+Parkour.PARKOUR_TIMES+" WHERE parkour_id = '"+this.id+"' ORDER BY time_value ASC LIMIT 1");
		try {
			if(rs.next()){
				recordTime = new ParkourTime(rs.getInt("time_id"),this);
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	private void loadTimes(){
		times = 0;
		ResultSet rs = RealCraft.getInstance().db.query("SELECT COUNT(*) AS rows FROM "+Parkour.PARKOUR_TIMES+" WHERE parkour_id = '"+this.id+"'");
		try {
			if(rs.next()){
				times = rs.getInt("rows");
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	public void addRating(ParkourPlayer player,int rating) throws ParkourAlreadyRatedException {
		if(player.isRatedArena(this)) throw new ParkourAlreadyRatedException();
		if(rating > 0) rating = 1;
		else rating = -1;
		RealCraft.getInstance().db.update("INSERT INTO "+Parkour.PARKOUR_RATINGS+" (parkour_id,user_id,rating_value,rating_created) VALUES('"+this.id+"','"+player.getId()+"','"+rating+"','"+(System.currentTimeMillis()/1000)+"')");
		this.loadRatings();
	}

	public void addTime(ParkourPlayer player,int time){
		RealCraft.getInstance().db.update("INSERT INTO "+Parkour.PARKOUR_TIMES+" (parkour_id,user_id,time_value,time_created) VALUES('"+this.id+"','"+player.getId()+"','"+time+"','"+(System.currentTimeMillis()/1000)+"')");
		this.loadTimes();
		this.loadRecord();
	}

	public void create(){
		try {
			this.setName("Parkour"+this.getId());
		} catch (ParkourInvalidNameException | ParkourNameExistsException e){
		}
		this.getWorld().getBlockAt(this.getCenterBottomLocation()).setType(Material.BEDROCK);
	}

	public int getId(){
		return id;
	}

	public int getAuthor(){
		return author;
	}

	public String getAuthorName(){
		return authorName;
	}

	public String getName(){
		return name;
	}

	public void setName(String name) throws ParkourInvalidNameException, ParkourNameExistsException {
		Matcher matcher = Parkour.ALLOWED_NAME_CHARS.matcher(name);
		if(!matcher.matches()) throw new ParkourInvalidNameException();
		matcher = Parkour.FORBIDDEN_NAME.matcher(name.toLowerCase());
		if(matcher.matches() && !name.equalsIgnoreCase("Parkour"+this.getId())) throw new ParkourNameExistsException();
		if(Parkour.getArena(name) != null) throw new ParkourNameExistsException();
		this.name = name;
		this.save();
	}

	public int getPros(){
		return pros;
	}

	public int getCons(){
		return cons;
	}

	public double getRating(){
		double rating = 0;
		try {
			rating = ((this.getPros() + 1.9208) / (this.getPros() + this.getCons()) - 1.96 * Math.sqrt((this.getPros() * this.getCons()) / (this.getPros() + this.getCons()) + 0.9604)/(this.getPros() + this.getCons())) / (1 + 3.8416 / (this.getPros() + this.getCons()));
		} catch (Exception exception){
		}
		return rating;
	}

	public byte getColor(){
		if(!this.isReady()) return (byte)12;
		if(this.getRating() >= Parkour.ARENA_GOODRATING) return (byte)10;
		return (byte)8;
	}

	public ParkourTime getRecord(){
		return recordTime;
	}

	public ParkourScoreboard getScoreboard(){
		return scoreboard;
	}

	public int getTimes(){
		return times;
	}

	public Location getStartLocation(){
		return startLoc;
	}

	public void setStartLocation(Location location){
		startLoc = location;
		this.save();
	}

	public Location getFinishLocation(){
		return finishLoc;
	}

	public void setFinishLocation(Location location){
		finishLoc = location;
		this.save();
	}

	public ArrayList<Location> getCheckPoints(){
		return checkPoints;
	}

	public void addCheckPoint(Location location){
		checkPoints.add(location);
		this.save();
	}

	public boolean removeCheckPoint(Location location){
		boolean result = checkPoints.remove(location);
		this.save();
		return result;
	}

	public Location getCenterBottomLocation(){
		Vector[] arenaBounds = this.getArenaBounds();
		return new Location(Bukkit.getServer().getWorld("world_parkour"),arenaBounds[0].getBlockX()+(Parkour.ARENA_SIZE/2)+0.5,arenaBounds[0].getBlockY(),arenaBounds[0].getBlockZ()+(Parkour.ARENA_SIZE/2)+0.5);
	}

	public Vector[] getArenaBounds(){
		Vector[] arenaBounds = new Vector[2];
		int [] coords = LocationUtil.getIndexCoords(this.getId());
		arenaBounds[0] = new Vector((coords[0]*Parkour.ARENA_FULLSIZE)+Parkour.ARENA_MARGIN,0,(coords[1]*Parkour.ARENA_FULLSIZE)+Parkour.ARENA_MARGIN);
		arenaBounds[1] = new Vector((coords[0]*Parkour.ARENA_FULLSIZE)+Parkour.ARENA_FULLSIZE-Parkour.ARENA_MARGIN,128,(coords[1]*Parkour.ARENA_FULLSIZE)+Parkour.ARENA_FULLSIZE-Parkour.ARENA_MARGIN);
		return arenaBounds;
	}

	public boolean isLocationInArena(Location location){
		Vector[] bounds = this.getArenaBounds();
		return location.toVector().isInAABB(bounds[0],bounds[1]);
	}

	public RegionWrapper getRegionWrapper(){
		Vector[] bounds = this.getArenaBounds();
		return new RegionWrapper(bounds[0].getBlockX(),bounds[1].getBlockX()-1,bounds[0].getBlockY(),bounds[1].getBlockY()-1,bounds[0].getBlockZ(),bounds[1].getBlockZ()-1);
	}

	public ArrayList<ParkourPlayer> getPlayers(){
		ArrayList<ParkourPlayer> players = new ArrayList<ParkourPlayer>();
		for(ParkourPlayer player : Parkour.getPlayers()){
			if(player.getArena() == this) players.add(player);
		}
		return players;
	}

	public ArrayList<String> getInfo(ParkourPlayer player){
		ArrayList<String> lores = new ArrayList<String>();
		lores.add("§7Autor: §f"+this.getAuthorName());
		if(this.isReady()){
			lores.add("§r");
			lores.add("§7Hodnoceni: §a§l"+this.getPros()+"§r §7/ §c§l"+this.getCons());
			lores.add("§7Dokonceno: §f"+this.getTimes()+"x");
			if(this.getRecord() != null){
				lores.add("§r");
				lores.add("§bRekord mapy");
				lores.add("§7- §f"+this.getRecord().getAuthorName());
				lores.add("§7- §e"+this.getRecord().getTimeFormat());
				lores.add("§7- "+DateUtil.lastTime(this.getRecord().getCreated()));
			}
			ParkourTime personalRecord = player.getArenaRecord(this);
			if(personalRecord != null){
				lores.add("§r");
				lores.add("§bOsobni rekord");
				lores.add("§7- §e"+personalRecord.getTimeFormat());
				lores.add("§7- "+DateUtil.lastTime(personalRecord.getCreated()));
			}
		}
		else lores.add("§b§lRozpracovane");
		return lores;
	}

	public HashMap<Integer,String> getScoreboardLines(){
		HashMap<Integer,String> lines = new HashMap<Integer,String>();
		lines.put(lines.size(),"");
		lines.put(lines.size(),"§b§lAutor");
		lines.put(lines.size(),"§f"+this.getAuthorName()+" ");
		if(this.isReady()){
			lines.put(lines.size(),"");
			lines.put(lines.size(),"§fHodnoceni: §a§l"+this.getPros()+"§r §7/ §c§l"+this.getCons());
			lines.put(lines.size(),"§fDokonceno: §e"+this.getTimes()+"x");
			if(this.getRecord() != null){
				lines.put(lines.size(),"");
				lines.put(lines.size(),"§b§lRekord mapy");
				lines.put(lines.size(),"§7- §f"+this.getRecord().getAuthorName());
				lines.put(lines.size(),"§7- §e"+this.getRecord().getTimeFormat());
			}
		} else {
			lines.put(lines.size(),"");
			lines.put(lines.size(),(this.getStartLocation() != null ? "§a\u2714" : "§c\u2715")+" §f§lStart");
			lines.put(lines.size(),(!this.getCheckPoints().isEmpty() ? "§a\u2714" : "§c\u2715")+" §e§lCheckpointy");
			lines.put(lines.size(),(this.getFinishLocation() != null ? "§a\u2714" : "§c\u2715")+" §6§lCil");
			lines.put(lines.size(),"");
			lines.put(lines.size(),(this.isTested() ? "§a\u2714" : "§c\u2715")+" §b§lOtestovano");
		}
		return lines;
	}

	public long getCreated(){
		return created;
	}

	public ParkourClockType getClock(){
		return clock;
	}

	public void setClock(ParkourClockType clock){
		this.clock = clock;
		this.save();
	}

	public World getWorld(){
		return world;
	}

	public boolean isTested(){
		return tested;
	}

	public void setTested(boolean tested){
		this.tested = tested;
	}

	public boolean isReady(){
		return ready;
	}

	public void setReady() throws ParkourStartLocationException, ParkourFinishLocationException, ParkourCheckPointsException, ParkourNotTestedException {
		if(this.getStartLocation() == null) throw new ParkourStartLocationException();
		if(this.getFinishLocation() == null) throw new ParkourFinishLocationException();
		if(this.getCheckPoints().isEmpty()) throw new ParkourCheckPointsException();
		if(!this.isTested()) throw new ParkourNotTestedException();
		ready = true;
		created = System.currentTimeMillis()/1000;
		this.save();
	}

	public HashMap<Integer,ParkourOfflinePlayer> getCollaborators(){
		return collaborators;
	}

	public void addCollaborator(ParkourPlayer player,String name) throws ParkourPlayerNotFoundException {
		Player victim = Bukkit.getServer().getPlayer(name);
		if(victim == null || victim == player.getPlayer()) throw new ParkourPlayerNotFoundException();
		ParkourPlayer player2 = Parkour.getPlayer(victim);
		if(!this.collaborators.containsKey(player2.getId())){
			this.collaborators.put(player2.getId(),new ParkourOfflinePlayer(player2.getId()));
			this.save();
		}
	}

	public void removeCollaborator(int id){
		ParkourOfflinePlayer collaborator = this.collaborators.get(id);
		this.collaborators.remove(id);
		this.save();
		Player player = Bukkit.getServer().getPlayer(collaborator.getName());
		if(player != null && player.isOnline()){
			ParkourPlayer player2 = Parkour.getPlayer(player);
			if(player2.getArena() == this) player2.getArena().leavePlayer(player2);
		}
	}

	public void addPlayer(ParkourPlayer player){
		player.reset();
		player.setArena(this);
		player.getPlayer().setScoreboard(this.getScoreboard().getScoreboard());
		player.getPlayer().setPlayerTime(this.getClock().getTicks(),false);
		if(this.isReady()){
			player.setMode(ParkourPlayerMode.NORMAL);
			player.setCheckPoint(this.getStartLocation(),false);
			player.getPlayer().teleport(this.getStartLocation());
			player.setMode(ParkourPlayerMode.NORMAL);
		} else {
			player.getPlayer().teleport(this.getCenterBottomLocation().add(0,1,0));
			player.setMode(ParkourPlayerMode.BUILD);
		}
	}

	public void removePlayer(ParkourPlayer player){
		player.reset();
	}

	public void joinPlayer(ParkourPlayer player) throws ParkourNotReadyException {
		if(!this.isReady() && !player.isAuthor(this) && !player.isCollaborant(this)) throw new ParkourNotReadyException();
		this.addPlayer(player);
	}

	public void leavePlayer(ParkourPlayer player){
		this.removePlayer(player);
		player.setLastArena(this);
		player.setInventory();
		Parkour.teleportToLobby(player);
	}

	public void testPlayer(ParkourPlayer player) throws ParkourStartLocationException, ParkourFinishLocationException, ParkourCheckPointsException {
		if(this.getStartLocation() == null) throw new ParkourStartLocationException();
		if(this.getFinishLocation() == null) throw new ParkourFinishLocationException();
		if(this.getCheckPoints().isEmpty()) throw new ParkourCheckPointsException();
		player.setMode(ParkourPlayerMode.TEST);
		player.setCheckPoint(this.getStartLocation());
		player.getPlayer().teleport(this.getStartLocation());
		player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_BAT_TAKEOFF,1f,1f);
	}

	public void finishPlayer(ParkourPlayer player){
		if(player.getMode() == ParkourPlayerMode.NORMAL){
			player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,1);
			int time = player.getTime();
			this.addTime(player,time);
			Title.showTitle(player.getPlayer(),"§aParkour dokoncen",0.2,7,0.2);
			Title.showSubTitle(player.getPlayer(),"§fCelkovy cas: §e"+player.getTimeFormat(time),0,7.2,0.6);
			FireworkUtil.spawnFirework(player.getPlayer().getLocation(),FireworkEffect.Type.BALL,Color.WHITE,true,true);
			Parkour.sendMessage(player,"§aParkour dokoncen v case §e"+player.getTimeFormat(time));
			if(player.getLastArenaTime(this).getId() == this.getRecord().getId()){
				TextComponent message = new TextComponent(Parkour.PARKOUR_PREFIX+"§b"+player.getPlayer().getName()+" §dprekonal rekord mapy §e"+this.getName());
				message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/join "+this.getName()));
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§7Klikni pro pripojeni").create()));
				Parkour.sendMessageToAll(message);
			}
			player.getPlayer().getPlayer().getInventory().clear();
			player.setMode(ParkourPlayerMode.NONE);
			player.setRatingArena(this);
			Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					leavePlayer(player);
				}
			},2*20);
			Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					try {
						ParkourMenuRating.openMenu(player);
					} catch (ParkourAlreadyRatedException | ParkourOwnRatingException e){
					}
				}
			},3*20);
		}
		else if(player.getMode() == ParkourPlayerMode.TEST){
			this.setTested(true);
			player.getPlayer().teleport(player.getPlayer().getLocation().add(0,0.2,0));
			player.getPlayer().setVelocity(player.getPlayer().getVelocity().setY(0.5));
			player.setMode(ParkourPlayerMode.BUILD);
			player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,1);
			Title.showActionTitle(player.getPlayer(),"§a\u2714 §fParkour otestovan §a\u2714",3*20);
		}
	}

	public void checkNoBlocks(){
		Bukkit.getServer().getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				Vector[] bounds = getArenaBounds();
				for(int x=bounds[0].getBlockX();x<bounds[1].getBlockX();x++){
					for(int y=bounds[0].getBlockY();y<bounds[1].getBlockY();y++){
						for(int z=bounds[0].getBlockZ();z<bounds[1].getBlockZ();z++){
							if(ParkourArena.this.getWorld().getBlockAt(x,y,z).getType() != Material.AIR){
								return;
							}
						}
					}
				}
				ParkourArena.this.getWorld().getBlockAt(ParkourArena.this.getCenterBottomLocation()).setType(Material.BEDROCK);
			}
		});
	}

	public void checkPlates(){
		if(this.getStartLocation() != null && this.getStartLocation().getBlock().getType() != ParkourMenuType.START.getMaterial()){
			this.setStartLocation(null);
		}
		if(this.getFinishLocation() != null && this.getFinishLocation().getBlock().getType() != ParkourMenuType.FINISH.getMaterial()){
			this.setFinishLocation(null);
		}
		ArrayList<Location> checkPointToRemove = new ArrayList<Location>();
		for(Location checkPoint : checkPoints){
			if(checkPoint.getBlock().getType() != ParkourMenuType.CHECKPOINT.getMaterial()){
				checkPointToRemove.add(checkPoint);
			}
		}
		for(Location checkPoint : checkPointToRemove){
			this.removeCheckPoint(checkPoint);
		}
	}

	@SuppressWarnings("deprecation")
	public void setFloor(Material material,Byte data){
		Vector[] bounds = this.getArenaBounds();
		for(int x=bounds[0].getBlockX();x<bounds[1].getBlockX();x++){
			for(int z=bounds[0].getBlockZ();z<bounds[1].getBlockZ();z++){
				this.getWorld().getBlockAt(x,0,z).setType(material);
				this.getWorld().getBlockAt(x,0,z).setData(data);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void setBiome(Biome biome){
		HashMap<String,Chunk> chunks = new HashMap<String,Chunk>();
		Vector[] bounds = this.getArenaBounds();
		for(int x=bounds[0].getBlockX()-1;x<bounds[1].getBlockX()+1;x++){
			for(int z=bounds[0].getBlockZ()-1;z<bounds[1].getBlockZ()+1;z++){
				this.getWorld().setBiome(x,z,biome);
				Location location = new Location(this.getWorld(),x,0,z);
				String key = this.getWorld().getChunkAt(location).getX()+";"+this.getWorld().getChunkAt(location).getZ();
				if(!chunks.containsKey(key)) chunks.put(key,this.getWorld().getChunkAt(location));
			}
		}

		for(Chunk chunk : chunks.values()){
			this.getWorld().refreshChunk(chunk.getX(),chunk.getZ());
		}
	}
}