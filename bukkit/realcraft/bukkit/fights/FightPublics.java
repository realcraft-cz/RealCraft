package realcraft.bukkit.fights;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.fights.arenas.FightPublicArena;
import realcraft.share.utils.RandomUtil;

public class FightPublics implements Runnable {

	private static final int GAME_TIME = 600;

	private int gameTime;
	private FightState state;
	private FightPublicArena arena;
	private ArrayList<FightPublicArena> arenas = new ArrayList<FightPublicArena>();

	public FightPublics(){
		this.loadArenas();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,20,20);
	}

	@Override
	public void run(){
		if(gameTime > 0){
			gameTime --;
			if(gameTime == 0) this.finishArena();
		}
	}

	public int getGameTime(){
		return gameTime;
	}

	public void resetGameTime(){
		gameTime = GAME_TIME;
	}

	public FightState getState(){
		return state;
	}

	public void setState(FightState state){
		this.state = state;
	}

	public FightPublicArena getArena(){
		return arena;
	}

	public void setArena(FightPublicArena arena){
		this.arena = arena;
	}

	public ArrayList<FightPublicArena> getArenas(){
		return arenas;
	}

	private FightPublicArena getRandomArena(){
		return this.getRandomArena(1);
	}

	private FightPublicArena getRandomArena(int step){
		FightPublicArena arena = this.getArenas().get(RandomUtil.getRandomInteger(0,this.getArenas().size()-1));
		if(this.getArena() == arena && step < 100) arena = this.getRandomArena(step+1);
		return arena;
	}

	private void loadArenas(){
		File [] arenasFiles = new File(RealCraft.getInstance().getDataFolder()+"/fights/"+FightType.PUBLIC.toString()).listFiles();
		if(arenasFiles != null){
			for(File file : arenasFiles){
				if(file.isDirectory()){
					File config = new File(file.getPath()+"/config.yml");
					if(config.exists()){
						arenas.add(new FightPublicArena(file.getName()));
					}
				}
			}
		}
	}

	private void startArena(){
		this.setState(FightState.INGAME);
	}

	private void finishArena(){
		this.setState(FightState.ENDING);
	}
}