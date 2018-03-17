package realcraft.bukkit.cosmetics;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import realcraft.bukkit.RealCraft;

public abstract class Cosmetic implements Listener {

	private String name;
	private CosmeticCategory category;

	HashMap<String,Boolean> isRunning = new HashMap<String,Boolean>();

	public Cosmetic(String name,CosmeticCategory category){
		this.name = name;
		this.category = category;
		RealCraft.getInstance().getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	public String getName(){
		return this.name;
	}

	public CosmeticCategory getCategory(){
		return this.category;
	}

	public boolean isRunning(Player player){
		return (isRunning.containsKey(player.getName()) && isRunning.get(player.getName()) == true);
	}

	public void setRunning(Player player,boolean running){
		isRunning.put(player.getName(),running);
	}

	public abstract void clearCosmetic(Player player);

	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent event){
		this.setRunning(event.getPlayer(),false);
		this.clearCosmetic(event.getPlayer());
	}

	@EventHandler
	public void PlayerDeathEvent(PlayerDeathEvent event){
		this.setRunning(event.getEntity(),false);
		this.clearCosmetic(event.getEntity());
	}

	public enum CosmeticCategory {
		HAT, SUIT, GADGET, PARTICLEEFFECT, PET, MOUNT, POKEMON;

		public String toString(){
			switch(this){
				case HAT: return "§bHelma§r";
				case SUIT: return "§bBrneni§r";
				case GADGET: return "§bGadget§r";
				case PARTICLEEFFECT: return "§bEfekt§r";
				case PET: return "§bMazlik§r";
				case MOUNT: return "§bJezdecke zvire§r";
				case POKEMON: return "§bPokemon§r";
			}
			return null;
		}
	}
}