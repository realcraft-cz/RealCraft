package realcraft.bukkit.parkour;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.parkour.exceptions.ParkourNotReadyException;
import realcraft.bukkit.parkour.menu.ParkourMenuType;

public class Parkour implements Runnable {
	RealCraft plugin;

	public static final String PARKOUR_PREFIX = "§e[Parkour]§r ";
	public static final String PARKOUR_ARENAS = "parkour_arenas";
	public static final String PARKOUR_TIMES = "parkour_times";
	public static final String PARKOUR_RATINGS = "parkour_ratings";
	public static final double ARENA_GOODRATING = 0.5;
	public static final int ARENA_SIZE = 64;
	public static final int ARENA_MARGIN = 128;
	public static final int ARENA_FULLSIZE = ARENA_SIZE+(ARENA_MARGIN*2);
	public static final int PARKOUR_CREATE_LIMIT = 2*86400;
	public static final int PARKOUR_COLLABORATORS_LIMIT = 3;
	public static final Pattern ALLOWED_NAME_CHARS = Pattern.compile("[a-zA-Z0-9]*");
	public static final Pattern FORBIDDEN_NAME = Pattern.compile("parkour([0-9]*)");

	private static HashMap<Integer,ParkourArena> arenas = new HashMap<Integer,ParkourArena>();
	private static HashMap<Player,ParkourPlayer> players = new HashMap<Player,ParkourPlayer>();

	public Parkour(RealCraft realcraft){
		plugin = realcraft;
		Bukkit.getServer().getPluginManager().registerEvents(new ParkourListeners(),plugin);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,this,10,10);
		this.loadArenas();
	}

	public void loadArenas(){
		ResultSet rs = RealCraft.getInstance().db.query("SELECT parkour_id FROM "+Parkour.PARKOUR_ARENAS);
		try {
			while(rs.next()){
				ParkourArena arena = new ParkourArena(rs.getInt("parkour_id"));
				Parkour.addArena(arena);
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	@Override
	public void run(){
		for(ParkourPlayer player : getPlayers()){
			player.run();
		}
	}

	public void onReload(){
	}

	public static ArrayList<ParkourPlayer> getPlayers(){
		return new ArrayList<ParkourPlayer>(players.values());
	}

	public static ParkourPlayer getPlayer(Player player){
		if(!players.containsKey(player)) players.put(player,new ParkourPlayer(player));
		return players.get(player);
	}

	public static void removePlayer(Player player){
		players.remove(player);
	}

	public static HashMap<Integer,ParkourArena> getArenas(){
		return arenas;
	}

	public static ArrayList<ParkourArena> getAvailableArenas(){
		ArrayList<ParkourArena> arenas = new ArrayList<ParkourArena>();
		for(ParkourArena arena : Parkour.getArenas().values()){
			if(arena.isReady()) arenas.add(arena);
		}
		return arenas;
	}

	public static ParkourArena getArena(int id){
		return arenas.get(id);
	}

	public static ParkourArena getArena(String name){
		for(ParkourArena arena : arenas.values()){
			if(arena.getName().equalsIgnoreCase(name)) return arena;
		}
		return null;
	}

	public static ArrayList<ParkourArena> findArenas(String name){
		name = name.toLowerCase();
		ArrayList<ParkourArena> arenas = new ArrayList<ParkourArena>();
		for(ParkourArena arena : Parkour.getAvailableArenas()){
			if(arena.getName().toLowerCase().startsWith(name)) arenas.add(arena);
		}
		return arenas;
	}


	public static void addArena(ParkourArena arena){
		arenas.put(arena.getId(),arena);
	}

	public static void createArena(ParkourPlayer player){
		try {
			PreparedStatement stmt = RealCraft.getInstance().db.conn.prepareStatement("INSERT INTO "+Parkour.PARKOUR_ARENAS+" (user_id,parkour_created) VALUES(?,?)",Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1,player.getId());
			stmt.setInt(2,(int)(System.currentTimeMillis()/1000));
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()){
				int id = rs.getInt(1);
				ParkourArena arena = new ParkourArena(id);
				arenas.put(id,arena);
				arena.create();
				player.setArena(arena);
				try {
					arena.joinPlayer(player);
				} catch (ParkourNotReadyException e){
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	public static void teleportToLobby(ParkourPlayer player){
		player.getPlayer().resetPlayerTime();
		player.getPlayer().teleport(RealCraft.getInstance().lobby.lobbyspawn.getSpawnLocation());
		player.getPlayer().getInventory().setHeldItemSlot(0);
		player.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}

	public static ArrayList<ParkourArena> getSortedArenas(ParkourMenuType sortType){
		ArrayList<ParkourArena> sortedArenas = Parkour.getAvailableArenas();
		Collections.sort(sortedArenas,new Comparator<ParkourArena>(){
			@Override
			public int compare(ParkourArena arena1,ParkourArena arena2){
				if(sortType == ParkourMenuType.NEWEST){
					int compare = Long.compare(arena1.getCreated(),arena2.getCreated());
					if(compare > 0) return -1;
					else if(compare < 0) return 1;
				}
				else if(sortType == ParkourMenuType.BEST){
					int compare = Double.compare(arena1.getRating(),arena2.getRating());
					if(compare > 0) return -1;
					else if(compare < 0) return 1;
				}
				return 0;
			}
		});
		return sortedArenas;
	}

	public static void sendMessage(ParkourPlayer player,String message){
		player.getPlayer().sendMessage(Parkour.PARKOUR_PREFIX+message);
	}

	public static void sendMessageToAll(String message){
		Bukkit.getServer().broadcastMessage(Parkour.PARKOUR_PREFIX+message);
	}

	public static void sendMessageToAll(BaseComponent message){
		Bukkit.getServer().spigot().broadcast(message);
	}

	/*public static void givePlayerFragments(ParkourPlayer player,int amount){
		PlayerInfo playerinfo = RealCraft.getInstance().playermanazer.getPlayerInfo(player.getPlayer());
		if(playerinfo != null){
			playerinfo.givePlayerFragments(amount);
			int fragments = playerinfo.getLobbyFragments();
			if(fragments >= 10){
				playerinfo.givePlayerKeys(1);
				playerinfo.resetPlayerFragments();
			}
		}
	}*/
}