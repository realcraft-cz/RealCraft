package realcraft.bukkit.fights;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import realcraft.bukkit.fights.arenas.FightArena;
import realcraft.bukkit.fights.duels.FightDuel;
import realcraft.bukkit.fights.duels.FightDuelsRequests.FightDuelRequest;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

public class FightPlayer {

	private User user;
	private Player player;

	private FightPlayerState state;
	private FightArena arena;
	private FightDuel duel;

	private boolean leaving;
	private boolean inQueue;

	private ArrayList<FightDuelRequest> requests = new ArrayList<FightDuelRequest>();

	public FightPlayer(User user){
		this.user = user;
	}

	public User getUser(){
		return user;
	}

	public Player getPlayer(){
		if(player == null || !player.isOnline() || !player.isValid()){
			player = Users.getPlayer(this.getUser());
		}
		return player;
	}

	public FightPlayerState getState(){
		return state;
	}

	public void setState(FightPlayerState state){
		this.state = state;
	}

	public boolean isLeaving(){
		return leaving;
	}

	public void setLeaving(boolean leaving){
		this.leaving = leaving;
	}

	public boolean inQueue(){
		return inQueue;
	}

	public void setQueue(boolean inQueue){
		this.inQueue = inQueue;
	}

	public FightArena getArena(){
		return arena;
	}

	public void setArena(FightArena arena){
		this.arena = arena;
	}

	public FightDuel getDuel(){
		return duel;
	}

	public void setDuel(FightDuel duel){
		this.duel = duel;
	}

	public ArrayList<FightDuelRequest> getRequests(){
		return requests;
	}

	public void addRequest(FightDuelRequest request){
		requests.add(request);
	}

	public void reload(){
	}

	public void toggleSpectator(){
		player.getInventory().clear();
		player.setGameMode(GameMode.ADVENTURE);
		player.setAllowFlight(true);
		player.setFlying(true);
		player.setFlySpeed(0.2f);
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,Integer.MAX_VALUE,Integer.MAX_VALUE,false,false));
		player.setCollidable(false);
	}

	public void reset(){
		if(this.getPlayer() != null){
			player.setGameMode(GameMode.SURVIVAL);
			player.setAllowFlight(false);
			player.setFlying(false);
			player.setCollidable(true);
			player.setHealth(20);
			player.setFoodLevel(20);
			player.setSaturation(20);
			player.setWalkSpeed(0.2f);
			player.setFlySpeed(0.1f);
			player.setTotalExperience(0);
			player.closeInventory();
			player.getInventory().clear();
			player.getInventory().setHeldItemSlot(0);
			player.getInventory().setHelmet(null);
			player.getInventory().setChestplate(null);
			player.getInventory().setLeggings(null);
			player.getInventory().setBoots(null);
			for(PotionEffect effect : player.getActivePotionEffects()){
				player.removePotionEffect(effect.getType());
			}
		}
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof FightPlayer){
			FightPlayer toCompare = (FightPlayer) object;
			return (toCompare.getUser().equals(this.getUser()));
		}
		return false;
	}

	public enum FightPlayerState {
		NONE, FIGHT, SPECTATOR;
	}
}