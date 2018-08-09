package realcraft.bukkit.fights;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.fights.FightPlayer.FightPlayerState;
import realcraft.bukkit.fights.commands.FightCommands;
import realcraft.bukkit.fights.duels.FightDuels;
import realcraft.bukkit.fights.events.FightPlayerJoinLobbyEvent;
import realcraft.bukkit.fights.menu.FightMenu;
import realcraft.bukkit.lobby.LobbyAutoParkour;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.share.users.User;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fights {

	public static final String[] NUMBERS = new String[]{"§c\u278A","§6\u278B","§e\u278C"};

	private static int currentArenaId = 1;

	private static FileConfiguration config;
	private static Essentials essentials;

	private static HashMap<User,FightPlayer> players = new HashMap<User,FightPlayer>();

	private static Location lobbyLocation;

	private static FightPublics publics;
	private static FightDuels duels;

	private static String[] commands;

	public Fights(){
		publics = new FightPublics();
		duels = new FightDuels();
		new FightListeners();
		new FightCommands();
		new FightStands();
		new FightMenu();
		new FightRankBoard();
		new LobbyAutoParkour(RealCraft.getInstance());
	}

	public void onDisable(){
		new Thread(new Runnable(){
			public void run(){
				for(int i=0;i<5;i++){
					try {
						Thread.sleep(i*200);
					} catch (InterruptedException e){
						e.printStackTrace();
					}
					for(World world : Bukkit.getWorlds()){
						File [] mapsFiles = new File(world.getWorldFolder()+"/data/").listFiles();
						if(mapsFiles != null){
							for(File file : mapsFiles){
								if(!file.isDirectory() && (file.getName().startsWith("map_") || file.getName().startsWith("idcounts"))){
									file.delete();
								}
							}
						}
					}
				}
			}
		}).start();
	}

	public static FileConfiguration getConfig(){
		if(config == null){
			File file = new File(RealCraft.getInstance().getDataFolder()+"/fights/"+"config.yml");
			if(file.exists()){
				config = new YamlConfiguration();
				try {
					config.load(file);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		return config;
	}

	public static Essentials getEssentials(){
		if(essentials == null) essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
		return essentials;
	}

	public static FightPlayer getFightPlayer(Player player){
		return Fights.getFightPlayer(Users.getUser(player));
	}

	public static FightPlayer getFightPlayer(String name){
		User user = Users.getUser(name);
		if(user != null) return Fights.getFightPlayer(user);
		return null;
	}

	public static FightPlayer getFightPlayer(User user){
		if(!players.containsKey(user)) players.put(user,new FightPlayer(user));
		return players.get(user);
	}

	public static ArrayList<FightPlayer> getOnlineFightPlayers(){
		ArrayList<FightPlayer> players = new ArrayList<FightPlayer>();
		for(FightPlayer fPlayer : Fights.getFightPlayers()){
			if(fPlayer.getPlayer() != null) players.add(fPlayer);
		}
		return players;
	}

	public static ArrayList<FightPlayer> getFightPlayers(FightType type){
		ArrayList<FightPlayer> players = new ArrayList<FightPlayer>();
		for(FightPlayer fPlayer : Fights.getFightPlayers()){
			if(fPlayer.getPlayer() != null && fPlayer.getState() != FightPlayerState.NONE && fPlayer.getArena().getType() == type) players.add(fPlayer);
		}
		return players;
	}

	public static ArrayList<FightPlayer> getFightPlayers(){
		return new ArrayList<FightPlayer>(players.values());
	}

	public static FightPublics getPublics(){
		return publics;
	}

	public static FightDuels getDuels(){
		return duels;
	}

	public static int getNewArenaId(){
		return currentArenaId++;
	}

	public static void joinLobby(FightPlayer fPlayer){
		Bukkit.getServer().getPluginManager().callEvent(new FightPlayerJoinLobbyEvent(fPlayer));
	}

	public static Location getLobbyLocation(){
		if(lobbyLocation == null) lobbyLocation = LocationUtil.getConfigLocation(Fights.getConfig(),"lobby");
		return lobbyLocation;
	}

	public static String[] getCommands(){
		if(commands == null){
			List<String> tmpcmds = Fights.getConfig().getStringList("commands");
			if(!tmpcmds.isEmpty()) commands = tmpcmds.toArray(new String[tmpcmds.size()]);
		}
		return commands;
	}
}