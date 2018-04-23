package realcraft.bukkit.fights;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.fights.FightPlayer.FightPlayerState;
import realcraft.bukkit.fights.duels.FightDuels;
import realcraft.bukkit.lobby.LobbyAutoParkour;
import realcraft.bukkit.lobby.LobbyMenu;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.bungee.skins.utils.StringUtil;
import realcraft.share.users.User;

public class Fights implements Listener {

	private static FileConfiguration config;

	private static HashMap<User,FightPlayer> players = new HashMap<User,FightPlayer>();

	private static Location lobbyLocation;
	private static FightLobbyScoreboard lobbyScoreboard;

	public Fights(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		new FightListeners();
		new FightCommands();
		new FightPublics();
		new FightDuels();
		new FightStands();
		new LobbyMenu(RealCraft.getInstance());
		new LobbyAutoParkour(RealCraft.getInstance());
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
		for(FightPlayer fPlayer : Fights.getOnlineFightPlayers()){
			if(fPlayer.getState() == FightPlayerState.FIGHT && fPlayer.getArena().getType() == type) players.add(fPlayer);
		}
		return players;
	}

	public static ArrayList<FightPlayer> getFightPlayers(){
		return new ArrayList<FightPlayer>(players.values());
	}

	public static Location getLobbyLocation(){
		if(lobbyLocation == null) lobbyLocation = LocationUtil.getConfigLocation(Fights.getConfig(),"lobby");
		return lobbyLocation;
	}

	public static FightLobbyScoreboard getLobbyScoreboard(){
		if(lobbyScoreboard == null) lobbyScoreboard = new FightLobbyScoreboard();
		return lobbyScoreboard;
	}

	public static class FightLobbyScoreboard extends FightScoreboard {

		@Override
		public void update(){
			int players;
			this.setTitle("§b§lFights");
			this.setLine(0,"");
			players = Fights.getFightPlayers(FightType.PUBLIC).size();
			this.setLine(1,"§e§lFFA: §r"+players+" "+StringUtil.inflect(players,new String[]{"hrac","hraci","hracu"}));
			players = Fights.getFightPlayers(FightType.DUEL).size();
			this.setLine(2,"§b§lDuely: §r"+players+" "+StringUtil.inflect(players,new String[]{"hrac","hraci","hracu"}));
			super.update();
		}
	}
}