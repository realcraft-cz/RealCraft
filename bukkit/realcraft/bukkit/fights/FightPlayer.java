package realcraft.bukkit.fights;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;
import realcraft.bukkit.fights.arenas.FightArena;
import realcraft.bukkit.fights.duels.FightDuel;
import realcraft.bukkit.fights.duels.FightDuels;
import realcraft.bukkit.fights.duels.FightDuelsRequests.FightDuelRequest;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

public class FightPlayer {

	private User user;
	private Player player;

	private FightPlayerState state;
	private FightArena arena;
	private FightDuel duel;
	private FightKit kit;
	private FightRank rank;

	private FightPlayerData data;
	private FightLobbyScoreboard scoreboard;

	private boolean isLeaving;
	private boolean inQueue;

	private ArrayList<FightDuelRequest> requests = new ArrayList<FightDuelRequest>();

	public FightPlayer(User user){
		this.user = user;
		this.reload();
	}

	public User getUser(){
		return user;
	}

	public FightPlayerData getData(){
		if(data == null) data = new FightPlayerData(this);
		return data;
	}

	public FightLobbyScoreboard getLobbyScoreboard(){
		if(scoreboard == null) scoreboard = new FightLobbyScoreboard(this);
		return scoreboard;
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
		return isLeaving;
	}

	public void setLeaving(boolean isLeaving){
		this.isLeaving = isLeaving;
	}

	public boolean inQueue(){
		return inQueue;
	}

	public void setQueue(boolean inQueue){
		this.inQueue = inQueue;
	}

	public void joinQueue(){
		if(!this.inQueue()){
			this.setQueue(true);
			this.getPlayer().playSound(this.getPlayer().getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,1f);
			FightDuels.sendMessage(this,"Cekas ve fronte, vyckej na protivnika");
		} else {
			FightDuels.sendMessage(this,"§cJiz jsi ve fronte, vyckej na protivnika");
		}
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

	public FightKit getKit(){
		return kit;
	}

	public void setKit(FightKit kit){
		this.kit = kit;
	}

	public FightRank getRank(){
		if((rank == null || this.getData().getScore() < rank.getMinScore() || this.getData().getScore() > rank.getMaxScore()) && this.getData().getRankeds() >= FightRank.MIN_MATCHES) rank = FightRank.fromScore(this.getData().getScore());
		return rank;
	}

	public ArrayList<FightDuelRequest> getRequests(){
		return requests;
	}

	public void addRequest(FightDuelRequest request){
		requests.add(request);
	}

	public void reload(){
		this.getData().reload();
	}

	public void toggleSpectator(){
		if(this.getPlayer() != null){
			player.getInventory().clear();
			player.setGameMode(GameMode.ADVENTURE);
			player.setAllowFlight(true);
			player.setFlying(true);
			player.setFlySpeed(0.2f);
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,Integer.MAX_VALUE,Integer.MAX_VALUE,false,false));
			player.setCollidable(false);
			Fights.getEssentials().getUser(player).setNickname(ChatColor.GRAY+player.getName()+" "+this.getNickRank());
			Fights.getEssentials().getUser(player).setDisplayNick();
		}
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
			this.updateExp();
			for(PotionEffect effect : player.getActivePotionEffects()){
				player.removePotionEffect(effect.getType());
			}
		}
	}

	public void updateExp(){
		if(this.getPlayer() != null){
			float exp = 0;
			if(this.getRank() != null) exp = (this.getData().getScore()-this.getRank().getMinScore())/(float)(this.getRank().getMaxScore()-this.getRank().getMinScore());
			if(exp < 0) exp = 0;
			player.setExp(exp);
			player.setLevel(this.getData().getScore());
		}
	}

	public void updateNick(){
		if(this.getPlayer() != null){
			Fights.getEssentials().getUser(player).setNickname(Fights.getEssentials().getUser(player).getName()+" "+this.getNickRank());
			Fights.getEssentials().getUser(player).setDisplayNick();
		}
	}

	public String getNickRank(){
		return (this.getRank() == null ? "§8[§7--§8]" : "§8["+this.getRank().getChatColor()+this.getRank().getName().charAt(0)+this.getRank().getRankNumber()+"§8]");
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

	public class FightLobbyScoreboard extends FightScoreboard {

		private FightPlayer fPlayer;

		public FightLobbyScoreboard(FightPlayer fPlayer){
			this.fPlayer = fPlayer;
		}

		@Override
		public void addPlayer(FightPlayer fPlayer){
			super.addPlayer(fPlayer);
			this.update();
		}

		@Override
		public void update(){
			this.setTitle("§b§lFights");
			this.setLine(0,"");
			if(fPlayer.getRank() == null){
				this.setLine(1,"§7Zbyva odehrat");
				this.setLine(2,"§7"+(FightRank.MIN_MATCHES-fPlayer.getData().getRankeds())+" duelu do ranku");
			} else {
				this.setLine(1,"§fRank");
				this.setLine(2,fPlayer.getRank().getChatColor()+"§l"+fPlayer.getRank().getName());
			}
			this.setLine(3,"");
			this.setLine(4,"§fVyzvi hrace na");
			this.setLine(5,"§fsouboj prikazem");
			this.setLine(6,"§e/duel <player>");
			this.setLine(7,"");
			this.setLine(8,"§ewww.realcraft.cz");
			super.update();
		}
	}
}