package realcraft.bukkit.fights.duels;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.fights.FightPlayer.FightPlayerState;
import realcraft.bukkit.fights.FightType;
import realcraft.bukkit.fights.Fights;
import realcraft.bukkit.fights.arenas.FightDuelArena;
import realcraft.share.utils.RandomUtil;

public class FightDuels implements Runnable {

	public static final long REQUEST_TIMEOUT_SECONDS = 60;

	private static ArrayList<FightDuel> duels = new ArrayList<FightDuel>();
	private static ArrayList<FightDuelArena> arenas = new ArrayList<FightDuelArena>();

	public FightDuels(){
		this.loadArenas();
		new FightDuelsRequests();
		new FightDuelsSpectator();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,40,40);
	}

	@Override
	public void run(){
		this.checkQueue();
	}

	private void checkQueue(){
		ArrayList<FightPlayer> players = new ArrayList<FightPlayer>();
		for(FightPlayer fPlayer : Fights.getOnlineFightPlayers()){
			if(fPlayer.getState() == FightPlayerState.NONE && fPlayer.inQueue()){
				players.add(fPlayer);
			}
		}
		if(players.size() >= 2){
			FightPlayer[] fPlayers = FightDuels.getTwoRandomPlayers(players);
			FightDuels.createDuel(fPlayers[0],fPlayers[1]);
		}
	}

	public static ArrayList<FightDuel> getDuels(){
		return duels;
	}

	public static ArrayList<FightDuelArena> getArenas(){
		return arenas;
	}

	private void loadArenas(){
		File [] arenasFiles = new File(RealCraft.getInstance().getDataFolder()+"/fights/"+FightType.DUEL.toString()).listFiles();
		if(arenasFiles != null){
			for(File file : arenasFiles){
				if(file.isDirectory()){
					File config = new File(file.getPath()+"/config.yml");
					if(config.exists()){
						arenas.add(new FightDuelArena(file.getName()));
					}
				}
			}
		}
	}

	private static FightDuelArena getRandomArena(){
		return FightDuels.getArenas().get(RandomUtil.getRandomInteger(0,FightDuels.getArenas().size()-1));
	}

	public static void createDuel(FightPlayer fPlayer1,FightPlayer fPlayer2){
		FightDuel duel = new FightDuel(fPlayer1,fPlayer2);
		duels.add(duel);
		duel.setArena(FightDuels.getRandomArena());
		duel.joinPlayer(fPlayer1);
		duel.joinPlayer(fPlayer2);
	}

	public static void removeDuel(FightDuel duel){
		duels.remove(duel);
	}

	private static FightPlayer[] getTwoRandomPlayers(ArrayList<FightPlayer> players){
		FightPlayer[] fPlayers = new FightPlayer[2];
		fPlayers[0] = players.get(RandomUtil.getRandomInteger(0,players.size()-1));
		fPlayers[1] = FightDuels.getRandomPlayerExcept(players,fPlayers[0]);
		return fPlayers;
	}

	private static FightPlayer getRandomPlayerExcept(ArrayList<FightPlayer> players,FightPlayer player){
		FightPlayer fPlayer = players.get(RandomUtil.getRandomInteger(0,players.size()-1));
		if(fPlayer.equals(player)) return FightDuels.getRandomPlayerExcept(players,player);
		return fPlayer;
	}
}