package realcraft.bukkit.fights;

import java.io.File;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.fights.arenas.FightArena.FightArenaType;
import realcraft.bukkit.fights.arenas.FightDuelArena;

public class FightDuels {

	public FightDuels(){
	}

	public void loadArenas(){
		File [] arenasFiles = new File(RealCraft.getInstance().getDataFolder()+"/fights/"+FightArenaType.DUEL.toString()).listFiles();
		if(arenasFiles != null){
			for(File file : arenasFiles){
				if(file.isDirectory()){
					File config = new File(file.getPath()+"/config.yml");
					if(config.exists()){
						new FightDuelArena(file.getName());
					}
				}
			}
		}
	}
}