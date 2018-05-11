package realcraft.bukkit.fights.duels;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Bukkit;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.fights.FightPlayer.FightPlayerState;
import realcraft.bukkit.fights.FightType;
import realcraft.bukkit.fights.Fights;
import realcraft.bukkit.fights.arenas.FightDuelArena;
import realcraft.bukkit.fights.spectators.FightDuelSpectator;
import realcraft.share.utils.RandomUtil;

public class FightDuels implements Runnable {

	public static final String PREFIX = "§7[§bDuely§7]§r ";
	private static final int DUPLICATE_ARENAS = 10;

	private ArrayList<FightDuel> duels = new ArrayList<FightDuel>();
	private ArrayList<FightDuelArena> arenas = new ArrayList<FightDuelArena>();

	public FightDuels(){
		this.loadArenas();
		new FightDuelSpectator();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,20,20);
		Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				for(FightDuelArena arena : FightDuels.this.getArenas()){
					arena.getRegion().reset();
				}
			}
		},20);
	}

	@Override
	public void run(){
		this.runDuels();
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
			FightPlayer[] fPlayers = this.getTwoRandomPlayers(players);
			this.createDuel(fPlayers[0],fPlayers[1],true);
		}
	}

	private void runDuels(){
		for(Iterator<FightDuel> iterator = this.getDuels().iterator();iterator.hasNext();){
			FightDuel duel = iterator.next();
			duel.run();
			if(duel.isRemoved()) iterator.remove();
		}
	}

	public ArrayList<FightDuel> getDuels(){
		return duels;
	}

	public ArrayList<FightDuelArena> getArenas(){
		return arenas;
	}

	private void loadArenas(){
		File [] arenasFiles = new File(RealCraft.getInstance().getDataFolder()+"/fights/"+FightType.DUEL.toString()).listFiles();
		if(arenasFiles != null){
			for(File file : arenasFiles){
				if(file.isDirectory()){
					File config = new File(file.getPath()+"/config.yml");
					if(config.exists()){
						for(int i=0;i<DUPLICATE_ARENAS;i++){
							arenas.add(new FightDuelArena(Fights.getNewArenaId(),i,file.getName()));
						}
					}
				}
			}
		}
	}

	private FightDuelArena getRandomArena(){
		return this.getRandomArena(1);
	}

	private FightDuelArena getRandomArena(int step){
		FightDuelArena arena = this.getArenas().get(RandomUtil.getRandomInteger(0,this.getArenas().size()-1));
		if(arena.isUsed() && step < 100) arena = this.getRandomArena(step+1);
		else if(step >= 100) return null;
		return arena;
	}

	public boolean createDuel(FightPlayer fPlayer1,FightPlayer fPlayer2,boolean ranked){
		FightDuelArena arena = this.getRandomArena();
		if(arena != null){
			FightDuel duel = new FightDuel(fPlayer1,fPlayer2,ranked);
			duels.add(duel);
			duel.setArena(arena);
			duel.joinPlayer(fPlayer1);
			duel.joinPlayer(fPlayer2);
			return true;
		}
		return false;
	}

	private FightPlayer[] getTwoRandomPlayers(ArrayList<FightPlayer> players){
		FightPlayer[] fPlayers = new FightPlayer[2];
		fPlayers[0] = players.get(RandomUtil.getRandomInteger(0,players.size()-1));
		fPlayers[1] = this.getRandomPlayerExcept(players,fPlayers[0]);
		return fPlayers;
	}

	private FightPlayer getRandomPlayerExcept(ArrayList<FightPlayer> players,FightPlayer player){
		FightPlayer fPlayer = players.get(RandomUtil.getRandomInteger(0,players.size()-1));
		if(fPlayer.equals(player)) return this.getRandomPlayerExcept(players,player);
		return fPlayer;
	}

	public static void sendMessage(String message){
		Bukkit.broadcastMessage(PREFIX+message);
	}

	public static void sendMessage(FightPlayer fPlayer,String message){
		fPlayer.getPlayer().sendMessage(PREFIX+message);
	}
}