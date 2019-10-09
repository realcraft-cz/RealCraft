package realcraft.bukkit.parkour;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_12_R1.DataWatcher;
import net.minecraft.server.v1_12_R1.DataWatcherObject;
import net.minecraft.server.v1_12_R1.DataWatcherRegistry;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldBorder.EnumWorldBorderAction;
import net.minecraft.server.v1_12_R1.WorldBorder;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.parkour.exceptions.ParkourCreateLimitException;
import realcraft.bukkit.parkour.exceptions.ParkourInProgressException;
import realcraft.bukkit.parkour.menu.ParkourMenuType;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.Title;

public class ParkourPlayer {

	private Player player;
	private int id = 0;
	private ParkourArena arena;
	private ParkourArena ratingArena;
	private ParkourArena lastArena;
	private Location checkPoint;
	private long startTime = 0;
	private int page = 1;
	private ParkourPlayerMode mode = ParkourPlayerMode.NONE;

	public ParkourPlayer(Player player){
		this.player = player;
		this.id = Users.getUser(player).getId();
		this.reset();
	}

	public Player getPlayer(){
		return player;
	}

	public int getId(){
		return id;
	}

	public ParkourArena getArena(){
		return arena;
	}

	public void setArena(ParkourArena arena){
		this.arena = arena;
	}

	public ParkourArena getRatingArena(){
		return ratingArena;
	}

	public void setRatingArena(ParkourArena arena){
		this.ratingArena = arena;
	}

	public ParkourArena getLastArena(){
		return lastArena;
	}

	public void setLastArena(ParkourArena arena){
		this.lastArena = arena;
	}

	public ArrayList<ParkourArena> getOwnArenas(){
		ArrayList<ParkourArena> arenas = new ArrayList<ParkourArena>();
		for(ParkourArena arena : Parkour.getArenas().values()){
			if(this.isAuthor(arena) || this.isCollaborant(arena)) arenas.add(arena);
		}
		Collections.sort(arenas,new Comparator<ParkourArena>(){
			@Override
			public int compare(ParkourArena arena1,ParkourArena arena2){
				int compare = Long.compare(arena1.getCreated(),arena2.getCreated());
				if(compare > 0) return -1;
				else if(compare < 0) return 1;
				return 0;
			}
		});
		return arenas;
	}

	public ArrayList<ParkourArena> getLikedArenas(){
		ArrayList<ParkourArena> arenas = new ArrayList<ParkourArena>();
		for(ParkourRating rating : this.getRatings()){
			if(rating.getValue() == 1) arenas.add(rating.getArena());
		}
		return arenas;
	}

	public void setCheckPoint(Location checkPoint){
		this.setCheckPoint(checkPoint,false);
	}

	public void setCheckPoint(Location checkPoint,boolean sound){
		if(this.getMode() != ParkourPlayerMode.NONE && this.getMode() != ParkourPlayerMode.NORMAL && this.getMode() != ParkourPlayerMode.TEST) return;
		if(checkPoint == null || (this.getCheckPoint() != null && this.getCheckPoint().getBlockX() == checkPoint.getBlockX() && this.getCheckPoint().getBlockY() == checkPoint.getBlockY() && this.getCheckPoint().getBlockZ() == checkPoint.getBlockZ())) return;
		checkPoint.setX(checkPoint.getBlockX()+0.5);
		checkPoint.setY(checkPoint.getBlockY());
		checkPoint.setZ(checkPoint.getBlockZ()+0.5);
		this.checkPoint = checkPoint;
		if(sound) this.getPlayer().playSound(this.getPlayer().getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
	}

	public Location getCheckPoint(){
		return checkPoint;
	}

	public void teleportToCheckPoint(){
		if(this.getCheckPoint() == null) return;
		this.getPlayer().setFallDistance(0);
		this.getPlayer().teleport(this.getCheckPoint());
		this.getPlayer().playSound(this.getPlayer().getLocation(),Sound.BLOCK_NOTE_BASS,1,1);
		if(this.getArena() != null && this.getCheckPoint().getBlockX() == this.getArena().getStartLocation().getBlockX() && this.getCheckPoint().getBlockY() == getArena().getStartLocation().getBlockY() && this.getCheckPoint().getBlockZ() == getArena().getStartLocation().getBlockZ()) this.resetTime();
	}

	public int getTime(){
		return (int)(System.currentTimeMillis()-startTime);
	}

	public void resetTime(){
		startTime = System.currentTimeMillis();
	}

	public long getStartTime(){
		return startTime;
	}

	public String getTimeFormat(){
		return this.getTimeFormat(false);
	}

	public String getTimeFormat(boolean simple){
		return this.getTimeFormat(this.getTime(),simple);
	}

	public String getTimeFormat(int time){
		return this.getTimeFormat(time,false);
	}

	public String getTimeFormat(int time,boolean simple){
		int minutes = (int)(Math.floor(((double)time)/1000/60)%60);
		int seconds = (time/1000)%60;
		int hundredths = (time)%1000;
		return (minutes < 10 ? "0" : "")+minutes+":"+(seconds < 10 ? "0" : "")+seconds+(simple ? "" : "."+(hundredths < 10 ? "00" : (hundredths < 100 ? "0" : ""))+hundredths);
	}

	public int getMenuPage(){
		return page;
	}

	public void setMenuPage(int page){
		this.page = page;
		if(this.page < 1) this.page = 1;
	}

	public ParkourPlayerMode getMode(){
		return mode;
	}

	public void setMode(ParkourPlayerMode mode){
		this.mode = mode;
		this.setInventory();
		if(this.getMode() == ParkourPlayerMode.NONE){
			this.getPlayer().setGameMode(GameMode.ADVENTURE);
			this.getPlayer().setAllowFlight(false);
			this.getPlayer().setFlying(false);
		}
		else if(this.getMode() == ParkourPlayerMode.NORMAL){
			this.getPlayer().setGameMode(GameMode.ADVENTURE);
			this.getPlayer().setAllowFlight(false);
			this.getPlayer().setFlying(false);
			this.setGhoast();
		}
		else if(this.getMode() == ParkourPlayerMode.BUILD){
			checkPoint = null;
			this.getPlayer().setGameMode(GameMode.CREATIVE);
			this.getPlayer().setAllowFlight(true);
			this.getPlayer().setFlying(true);
			this.setBorder();
		}
		else if(this.getMode() == ParkourPlayerMode.TEST){
			this.getPlayer().setGameMode(GameMode.ADVENTURE);
			this.getPlayer().setAllowFlight(false);
			this.getPlayer().setFlying(false);
			this.setBorder();
		}
	}

	public boolean isRatedArena(ParkourArena arena){
		boolean result = false;
		ResultSet rs = RealCraft.getInstance().db.query("SELECT parkour_id FROM "+Parkour.PARKOUR_RATINGS+" WHERE parkour_id = '"+arena.getId()+"' AND user_id = '"+this.getId()+"'");
		try {
			if(rs.next()) result = true;
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return result;
	}

	private ArrayList<ParkourRating> getRatings(){
		ArrayList<ParkourRating> ratings = new ArrayList<ParkourRating>();
		ResultSet rs = RealCraft.getInstance().db.query("SELECT * FROM "+Parkour.PARKOUR_RATINGS+" WHERE user_id = '"+this.getId()+"' ORDER BY rating_created DESC");
		try {
			while(rs.next()){
				ratings.add(new ParkourRating(Parkour.getArena(rs.getInt("parkour_id")),this.getId(),rs.getInt("rating_value"),rs.getInt("rating_created")));
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return ratings;
	}

	public ParkourTime getArenaRecord(ParkourArena arena){
		ParkourTime time = null;
		ResultSet rs = RealCraft.getInstance().db.query("SELECT time_id FROM "+Parkour.PARKOUR_TIMES+" WHERE parkour_id = '"+arena.getId()+"' AND user_id = '"+this.getId()+"' ORDER BY time_value ASC LIMIT 1");
		try {
			if(rs.next()){
				time = new ParkourTime(rs.getInt("time_id"),arena);
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return time;
	}

	public ParkourTime getLastArenaTime(ParkourArena arena){
		ParkourTime time = null;
		ResultSet rs = RealCraft.getInstance().db.query("SELECT time_id FROM "+Parkour.PARKOUR_TIMES+" WHERE parkour_id = '"+arena.getId()+"' AND user_id = '"+this.getId()+"' ORDER BY time_created DESC LIMIT 1");
		try {
			if(rs.next()){
				time = new ParkourTime(rs.getInt("time_id"),arena);
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return time;
	}

	public boolean isAuthor(ParkourArena arena){
		return (arena.getAuthor() == this.getId());
	}

	public boolean isCollaborant(ParkourArena arena){
		return (arena.getCollaborators().containsKey(this.getId()));
	}

	public ParkourArena getLastCreatedArena(){
		ParkourArena arena = null;
		ResultSet rs = RealCraft.getInstance().db.query("SELECT parkour_id FROM "+Parkour.PARKOUR_ARENAS+" WHERE user_id = '"+this.getId()+"' ORDER BY parkour_created DESC LIMIT 1");
		try {
			if(rs.next()){
				arena = Parkour.getArena(rs.getInt("parkour_id"));
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return arena;
	}

	public void createArena() throws ParkourInProgressException, ParkourCreateLimitException {
		ParkourArena arena = this.getLastCreatedArena();
		if(arena != null){
			if(!arena.isReady()) throw new ParkourInProgressException();
			else if(arena.getCreated()+Parkour.PARKOUR_CREATE_LIMIT > System.currentTimeMillis()/1000) throw new ParkourCreateLimitException(arena.getCreated());
		}
		Parkour.createArena(this);
	}

	public void reset(){
		arena = null;
		checkPoint = null;
		this.setMode(ParkourPlayerMode.NONE);
		this.resetTime();
		player.setMaxHealth(20);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setSaturation(20);
		player.setLevel(0);
		player.setExp(0);
		player.setTotalExperience(0);
		player.setWalkSpeed(0.2f);
		for(PotionEffect effect : player.getActivePotionEffects()){
			player.removePotionEffect(effect.getType());
		}
	}

	public void setInventory(){
		this.getPlayer().getInventory().clear();
		if(this.getArena() != null){
			if(this.getMode() == ParkourPlayerMode.NORMAL || this.getMode() == ParkourPlayerMode.TEST){
				this.getPlayer().getInventory().setItem(0,ParkourMenuType.RESPAWN.getItemStack());
				if(this.getMode() == ParkourPlayerMode.NORMAL) this.getPlayer().getInventory().setItem(6,ParkourMenuType.RATING.getItemStack());
				this.getPlayer().getInventory().setItem(7,ParkourMenuType.RESET.getItemStack());
				this.getPlayer().getInventory().setItem(8,ParkourMenuType.EXIT.getItemStack());
			}
			else if(this.getMode() == ParkourPlayerMode.BUILD){
				this.getPlayer().getInventory().setItem(0,ParkourMenuType.SETTINGS.getItemStack());
				this.getPlayer().getInventory().setItem(9,new ItemStack(Material.BARRIER));
			}
		} else {
			this.getPlayer().getInventory().setItem(0,ParkourMenuType.MAIN.getItemStack());
			this.getPlayer().getInventory().setItem(8,ParkourMenuType.RESPAWN.getItemStack());
			if(this.getLastArena() != null) this.getPlayer().getInventory().setItem(1,ParkourMenuType.RESET.getItemStack());
		}
	}

	public void setBorder(){
		if(this.getArena() != null && !this.getArena().isReady()){
			Vector [] bounds = this.getArena().getArenaBounds();
			WorldBorder border = new WorldBorder();
			border.setCenter(bounds[0].getX()+(Parkour.ARENA_SIZE/2),bounds[0].getZ()+(Parkour.ARENA_SIZE/2));
			border.setSize(Parkour.ARENA_SIZE);
			border.setWarningDistance(0);
			border.world = ((CraftWorld)this.getPlayer().getWorld()).getHandle();
			PacketPlayOutWorldBorder packet;
			packet = new PacketPlayOutWorldBorder(border,EnumWorldBorderAction.SET_CENTER);
			((CraftPlayer)this.getPlayer()).getHandle().playerConnection.sendPacket(packet);
			packet = new PacketPlayOutWorldBorder(border,EnumWorldBorderAction.SET_SIZE);
			((CraftPlayer)this.getPlayer()).getHandle().playerConnection.sendPacket(packet);
			packet = new PacketPlayOutWorldBorder(border,EnumWorldBorderAction.INITIALIZE);
			((CraftPlayer)this.getPlayer()).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public void run(){
		if(this.getArena() != null){
			if(this.getMode() == ParkourPlayerMode.NORMAL){
				Title.showActionTitle(player.getPlayer(),"?e"+this.getTimeFormat(true));
				if(this.getPlayer().getScoreboard() != this.getArena().getScoreboard().getScoreboard()){
					this.getPlayer().setScoreboard(this.getArena().getScoreboard().getScoreboard());
				}
			}
			if(this.getMode() == ParkourPlayerMode.NORMAL || this.getMode() == ParkourPlayerMode.TEST){
				Block block = this.getPlayer().getLocation().getBlock();
				if(block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA){
					this.teleportToCheckPoint();
				}
			}
			if(this.getMode() == ParkourPlayerMode.NORMAL || this.getMode() == ParkourPlayerMode.TEST || this.getMode() == ParkourPlayerMode.BUILD){
				if(!this.getArena().isLocationInArena(this.getPlayer().getLocation()) && this.getPlayer().getLocation().getBlockY() > 0){
					this.teleportToCheckPoint();
				}
			}
			if(this.getPlayer().getPlayerTime() != this.getArena().getClock().getTicks()){
				this.getPlayer().setPlayerTime(this.getArena().getClock().getTicks(),false);
			}
		}
	}

	public void setGhoast(){
		for(ParkourPlayer player : Parkour.getPlayers()){
			if(player != this){
				EntityPlayer p = ((CraftPlayer)this.getPlayer().getPlayer()).getHandle();
		        DataWatcher w = p.getDataWatcher();
		        w.set(new DataWatcherObject<>(0,DataWatcherRegistry.a),(byte)0x20);
		        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(player.getPlayer().getEntityId(),w,true);
		        ((CraftPlayer)player.getPlayer()).getHandle().playerConnection.sendPacket(packet);
			}
		}
	}
}