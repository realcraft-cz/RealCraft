package realcraft.bukkit.falling;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.falling.arena.FallArena;
import realcraft.bukkit.falling.arena.FallArenaPermission;
import realcraft.bukkit.falling.commands.FallCommands;
import realcraft.bukkit.lobby.LobbyAutoParkour;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class FallManager implements Runnable {

	public static final String FALL_ARENAS = "falling_arenas";
	private static final String PREFIX = "§6[Falling]§r ";

	private static World world;
	private static HashMap<User,FallPlayer> players = new HashMap<>();
	private static HashMap<Integer,FallArena> arenas = new HashMap<>();

	public FallManager(){
		world = Bukkit.getWorld("world_falling");
		world.setGameRule(GameRule.DO_MOB_SPAWNING,false);
		world.setGameRule(GameRule.DO_WEATHER_CYCLE,false);
		new LobbyAutoParkour(RealCraft.getInstance());
		new FallListeners();
		new FallCommands();
		this.loadArenas();
		Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(),this,5,5);
		Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(),new Runnable() {
			@Override
			public void run(){
				HashMap<Integer,FallArena> arenasToActivate = new HashMap<>();
				for(Player player : Bukkit.getOnlinePlayers()){
					FallArena arena = FallManager.getFallPlayer(player).getArena();
					if(arena != null && arena.getPermission(FallManager.getFallPlayer(player)).isMinimum(FallArenaPermission.TRUSTED)){
						arenasToActivate.put(arena.getId(),arena);
					}
				}
				for(FallArena arena : FallManager.getArenas()){
					if(arenasToActivate.containsKey(arena.getId())){
						arena.setActive(true);
					} else {
						if(arena.isActive()){
							arena.setActive(false);
							arena.save();
						}
					}
				}
			}
		},40,40);
	}

	public static World getWorld(){
		return world;
	}

	public static FallPlayer getFallPlayer(User user){
		if(!players.containsKey(user)) players.put(user,new FallPlayer(user));
		return players.get(user);
	}

	public static FallPlayer getFallPlayer(Player player){
		return FallManager.getFallPlayer(Users.getUser(player));
	}

	public static ArrayList<FallArena> getArenas(){
		return new ArrayList<>(arenas.values());
	}

	public static FallArena getArena(int id){
		return arenas.get(id);
	}

	public static FallArena getArena(Location location){
		for(FallArena arena : arenas.values()){
			if(arena.getRegion().isLocationInsideFull(location)) return arena;
		}
		return null;
	}

	public static FallArena createArena(FallPlayer fPlayer){
		FallArena arena = new FallArena(fPlayer.getUser());
		arena.create();
		arenas.put(arena.getId(),arena);
		return arena;
	}

	private void loadArenas(){
		ResultSet rs = DB.query("SELECT * FROM "+FALL_ARENAS+" ORDER BY arena_id ASC");
		try {
			while(rs.next()){
				int id = rs.getInt("arena_id");
				FallArena arena = new FallArena(id);
				arena.load();
				arenas.put(arena.getId(),arena);
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	@Override
	public void run(){
		for(FallArena arena : FallManager.getArenas()){
			arena.run();
		}
	}

	public static void sendMessage(String message){
		Bukkit.broadcastMessage(PREFIX+message);
	}

	public static void sendMessage(Player player,String message){
		player.sendMessage(PREFIX+message);
	}

	public static void sendMessage(FallPlayer fPlayer,String message){
		FallManager.sendMessage(fPlayer.getPlayer(),message);
	}
}