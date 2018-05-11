package realcraft.bukkit.fights.duels;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import net.md_5.bungee.api.ChatColor;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.fights.FightKit;
import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.fights.FightPlayer.FightPlayerState;
import realcraft.bukkit.fights.FightRank;
import realcraft.bukkit.fights.FightScoreboard;
import realcraft.bukkit.fights.FightState;
import realcraft.bukkit.fights.FightType;
import realcraft.bukkit.fights.Fights;
import realcraft.bukkit.fights.arenas.FightDuelArena;
import realcraft.bukkit.fights.events.FightPlayerJoinLobbyEvent;
import realcraft.bukkit.fights.events.FightPlayerLeaveLobbyEvent;
import realcraft.bukkit.fights.spectators.FightDuelSpectator;
import realcraft.bukkit.fights.spectators.FightSpectator.FightSpectatorHotbarItem;
import realcraft.bukkit.utils.BorderUtil;
import realcraft.bukkit.utils.Title;
import realcraft.share.utils.RandomUtil;

public class FightDuel implements Listener {

	private static final String FIGHTS_DUELS = "fights_duels";
	private static final long DUEL_HIT_TIMEOUT = 60;
	public static final String CHAR_HEART = "\u2764";
	public static final String CHAR_SWORDS = "\u2694";

	private FightPlayer fPlayer1;
	private FightPlayer fPlayer2;
	private FightPlayer deadPlayer;

	private int[] healths = new int[]{20,20};

	private int startTime = 7;
	private int endTime = 6;
	private long lastHit;
	private boolean ranked;
	private boolean removed;

	private FightKit kit;
	private FightState state = FightState.STARTING;
	private FightDuelArena arena;
	private FightDuelSpectator spectator;
	private FightDuelScoreboard scoreboard;

	public FightDuel(FightPlayer fPlayer1,FightPlayer fPlayer2,boolean ranked){
		this.fPlayer1 = fPlayer1;
		this.fPlayer2 = fPlayer2;
		this.ranked = ranked;
		this.kit = FightKit.values()[RandomUtil.getRandomInteger(0,FightKit.values().length-1)];
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	public void run(){
		if(this.getState() == FightState.STARTING){
			if(startTime > 0){
				startTime --;
				if(startTime == 0) this.start();
				else if(startTime <= 3){
					for(FightPlayer fPlayer : Fights.getFightPlayers(FightType.DUEL)){
						if(fPlayer.getDuel() == this){
							fPlayer.getPlayer().playSound(fPlayer.getPlayer().getLocation(),Sound.BLOCK_NOTE_HAT,1f,1f);
							Title.showTitle(fPlayer.getPlayer(),Fights.NUMBERS[startTime-1],0,1.2,0);
						}
					}
				}
			}
		}
		else if(this.getState() == FightState.INGAME){
			if(fPlayer1.getPlayer() != null) healths[0] = (int)Math.ceil(fPlayer1.getPlayer().getHealth());
			if(fPlayer2.getPlayer() != null) healths[1] = (int)Math.ceil(fPlayer2.getPlayer().getHealth());
			if(lastHit+(DUEL_HIT_TIMEOUT*1000) < System.currentTimeMillis()){
				this.finish();
			}
		}
		else if(this.getState() == FightState.ENDING){
			if(endTime > 0){
				endTime --;
				if(endTime == 0) this.remove();
			}
		}
		this.getScoreboard().update();
	}

	public FightPlayer[] getPlayers(){
		return new FightPlayer[]{fPlayer1,fPlayer2};
	}

	public boolean isDuelPlayer(FightPlayer fPlayer){
		return (fPlayer1.equals(fPlayer) || fPlayer2.equals(fPlayer));
	}

	public boolean isRanked(){
		return ranked;
	}

	public int[] getHealths(){
		return healths;
	}

	public boolean isRemoved(){
		return removed;
	}

	public FightKit getKit(){
		return kit;
	}

	public void setKit(FightKit kit){
		this.kit = kit;
	}

	public FightState getState(){
		return state;
	}

	public void setState(FightState state){
		this.state = state;
	}

	public FightDuelArena getArena(){
		return arena;
	}

	public void setArena(FightDuelArena arena){
		this.arena = arena;
	}

	public FightDuelScoreboard getScoreboard(){
		if(scoreboard == null) scoreboard = new FightDuelScoreboard();
		return scoreboard;
	}

	public FightDuelSpectator getSpectator(){
		if(spectator == null) spectator = new FightDuelSpectator();
		return spectator;
	}

	@SuppressWarnings("deprecation")
	public void joinPlayer(FightPlayer fPlayer){
		if(fPlayer.getState() == FightPlayerState.NONE) Bukkit.getServer().getPluginManager().callEvent(new FightPlayerLeaveLobbyEvent(fPlayer));
		fPlayer.reset();
		fPlayer.setQueue(false);
		fPlayer.setArena(this.getArena());
		fPlayer.setDuel(this);
		this.getScoreboard().addPlayer(fPlayer);
		if(fPlayer.equals(fPlayer1) || fPlayer.equals(fPlayer2)){
			fPlayer.setState(FightPlayerState.FIGHT);
			fPlayer.getPlayer().teleport(this.getArena().getSpawns().get(fPlayer.equals(fPlayer1) ? 0 : 1));
			this.getKit().equipPlayer(fPlayer);
			for(FightPlayer fPlayer2 : this.getPlayers()){
				if(!fPlayer.equals(fPlayer2)) fPlayer.getPlayer().showPlayer(fPlayer2.getPlayer());
			}
		} else {
			this.toggleSpectator(fPlayer);
			this.sendMessageInside("§b"+fPlayer.getUser().getName()+" §7sleduje duel");
		}
	}

	public void leavePlayer(FightPlayer fPlayer){
		if(fPlayer.equals(fPlayer1) || fPlayer.equals(fPlayer2)){
		}
		Fights.joinLobby(fPlayer);
	}

	@SuppressWarnings("deprecation")
	private void toggleSpectator(FightPlayer fPlayer){
		fPlayer.setState(FightPlayerState.SPECTATOR);
		fPlayer.getPlayer().teleport(this.getArena().getSpectatorLocation());
		fPlayer.reset();
		fPlayer.toggleSpectator();
		this.getScoreboard().addSpectator(fPlayer);
		for(FightPlayer fPlayer2 : Fights.getFightPlayers(FightType.DUEL)){
			if(fPlayer2.getDuel() == this){
				if(fPlayer2.getState() != FightPlayerState.SPECTATOR) fPlayer2.getPlayer().hidePlayer(fPlayer.getPlayer());
				else fPlayer.getPlayer().showPlayer(fPlayer2.getPlayer());
			}
		}
		for(FightSpectatorHotbarItem item : this.getSpectator().getHotbarItems()){
			fPlayer.getPlayer().getInventory().setItem(item.getIndex(),item.getItemStack());
		}
		BorderUtil.setBorder(fPlayer.getPlayer(),this.getArena().getSpectatorLocation(),this.getArena().getSpectatorRadius()*2);
	}

	private void start(){
		lastHit = System.currentTimeMillis();
		this.setState(FightState.INGAME);
		for(FightPlayer fPlayer : Fights.getFightPlayers(FightType.DUEL)){
			if(fPlayer.getDuel() == this){
				fPlayer.getPlayer().playSound(fPlayer.getPlayer().getLocation(),Sound.ENTITY_HORSE_ARMOR,1f,1f);
			}
		}
	}

	private void finish(){
		this.setState(FightState.ENDING);
		FightPlayer fWinner = this.getWinner();
		FightPlayer fLoser = this.getLoser();
		if(fWinner != null && fLoser != null){
			int score = 0;
			if(this.isRanked()){
				score = FightRank.getMatchScore(fWinner.getRank(),fLoser.getRank());
				if(fLoser.getRank() != null || (fLoser.getRank() == null && fWinner.getRank() == null)) fWinner.getData().addScore(score);
				if(fWinner.getRank() != null || (fWinner.getRank() == null && fLoser.getRank() == null)) fLoser.getData().addScore(-score);
				fWinner.getData().addRanked();
				fLoser.getData().addRanked();
			}
			fWinner.getData().addKill();
			fLoser.getData().addDeath();
			DB.update("INSERT INTO "+FIGHTS_DUELS+" (duel_winner,duel_loser,duel_score,duel_ranked,duel_created) VALUES('"+fWinner.getUser().getId()+"','"+fLoser.getUser().getId()+"','"+score+"','"+(this.isRanked() ? 1 : 0)+"','"+(System.currentTimeMillis()/1000)+"')");
			FightDuels.sendMessage("§c§l"+CHAR_SWORDS+" §b"+fWinner.getUser().getName()+"§r porazil hrace §b"+fLoser.getUser().getName());
			for(FightPlayer fPlayer : Fights.getFightPlayers(FightType.DUEL)){
				if(fPlayer.getDuel() == this){
					if(fPlayer.equals(fWinner)){
						fPlayer.getPlayer().playSound(fPlayer.getPlayer().getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
						Title.showTitle(fPlayer.getPlayer(),"§a§lVitezstvi",0.5,5,0.5);
						if(this.isRanked()) Title.showSubTitle(fPlayer.getPlayer(),"§a+"+score+" bodu",0.5,5,0.5);
					}
					else if(fPlayer.equals(fLoser)){
						Title.showTitle(fPlayer.getPlayer(),"§c§lProhra",0.5,5,0.5);
						if(this.isRanked()) Title.showSubTitle(fPlayer.getPlayer(),"§c-"+score+" bodu",0.5,5,0.5);
					} else {
						Title.showTitle(fPlayer.getPlayer(),"§c§lKonec hry",0.5,5,0.5);
						Title.showSubTitle(fPlayer.getPlayer(),"§b"+fWinner.getUser().getName()+"§f vyhral",0.5,5,0.5);
					}
				}
			}
		} else {
			for(FightPlayer fPlayer : Fights.getFightPlayers(FightType.DUEL)){
				if(fPlayer.getDuel() == this){
					Title.showTitle(fPlayer.getPlayer(),"§c§lKonec hry",0.5,5,0.5);
					Title.showSubTitle(fPlayer.getPlayer(),"§fNikdo nevyhral",0.5,5,0.5);
				}
			}
		}
	}

	private void remove(){
		for(FightPlayer fPlayer : Fights.getFightPlayers(FightType.DUEL)){
			if(fPlayer.getDuel() == this){
				Fights.joinLobby(fPlayer);
			}
		}
		if(spectator != null) spectator.remove();
		HandlerList.unregisterAll(this);
		removed = true;
	}

	public FightPlayer getWinner(){
		if(deadPlayer != null){
			if(deadPlayer.equals(fPlayer1)) return fPlayer2;
			else if(deadPlayer.equals(fPlayer2)) return fPlayer1;
		}
		return null;
	}

	private FightPlayer getLoser(){
		if(deadPlayer != null){
			if(deadPlayer.equals(fPlayer1)) return fPlayer1;
			else if(deadPlayer.equals(fPlayer2)) return fPlayer2;
		}
		return null;
	}

	public void sendMessageInside(String message){
		for(FightPlayer fPlayer : Fights.getFightPlayers(FightType.DUEL)){
			if(fPlayer.getDuel() == this) FightDuels.sendMessage(fPlayer,message);
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
			FightPlayer fPlayer = Fights.getFightPlayer((Player)event.getEntity());
			FightPlayer fDamager = Fights.getFightPlayer((Player)event.getDamager());
			if(this.isDuelPlayer(fPlayer) && this.isDuelPlayer(fDamager)){
				lastHit = System.currentTimeMillis();
			}
			if(this.getState() != FightState.INGAME){
				if(fPlayer.getDuel() == this || fDamager.getDuel() == this) event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void EntityDamageEvent(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			FightPlayer fPlayer = Fights.getFightPlayer((Player)event.getEntity());
			if(fPlayer.getDuel() == this){
				if(this.getState() != FightState.INGAME){
					event.setCancelled(true);
					if(event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK) event.getEntity().setFireTicks(0);
				}
				if(event.getCause() == DamageCause.VOID) ((Player)event.getEntity()).setHealth(0);
			}
		}
	}

	@EventHandler
	public void PlayerDeathEvent(PlayerDeathEvent event){
		FightPlayer fPlayer = Fights.getFightPlayer(event.getEntity());
		if(this.isDuelPlayer(fPlayer) && this.getState() == FightState.INGAME){
			deadPlayer = fPlayer;
			this.finish();
		}
	}

	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		FightPlayer fPlayer = Fights.getFightPlayer(event.getPlayer());
		if(fPlayer.getDuel() == this){
			event.setRespawnLocation(this.getArena().getSpectatorLocation());
			this.toggleSpectator(fPlayer);
			Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					if(fPlayer.getPlayer() != null && fPlayer.getDuel() == FightDuel.this) FightDuel.this.toggleSpectator(fPlayer);
				}
			});
		}
	}

	@EventHandler
	public void FightPlayerJoinLobbyEvent(FightPlayerJoinLobbyEvent event){
		FightPlayer fPlayer = event.getPlayer();
		if(this.isDuelPlayer(fPlayer) && this.getState() != FightState.ENDING){
			deadPlayer = fPlayer;
			this.finish();
		}
	}

	public class FightDuelScoreboard extends FightScoreboard {

		private Objective objective;

		public FightDuelScoreboard(){
			objective = this.getScoreboard().registerNewObjective("health","health");
			objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
			objective.setDisplayName(ChatColor.RED+CHAR_HEART);
			objective.getScore(fPlayer1.getUser().getName()).setScore(healths[0]);
			objective.getScore(fPlayer2.getUser().getName()).setScore(healths[1]);
		}

		@Override
		public void update(){
			this.clearLines();
			this.setTitle("§b§lDuel");
			this.setLine(0,"");
			this.setLine(1,"§7Kit: §6§l"+FightDuel.this.getKit().getName());
			this.setLine(2,"");
			this.setLine(3,"§f"+healths[0]+" §c"+CHAR_HEART+" §f"+fPlayer1.getUser().getName());
			this.setLine(4,"§f"+healths[1]+" §c"+CHAR_HEART+" §f"+fPlayer2.getUser().getName());
			if(FightDuel.this.getState() == FightState.STARTING){
				this.setLine(5,"");
				this.setLine(6,"§fSouboj zacina");
				this.setLine(7,"§fza §a"+(startTime > 5 ? 5 : startTime)+" §fsekund");
				this.setLine(8,"");
				this.setLine(9,"§ewww.realcraft.cz");
			}
			else if(FightDuel.this.getState() == FightState.ENDING){
				this.setLine(5,"");
				this.setLine(6,"§a§lVitez");
				this.setLine(7,"§f"+(FightDuel.this.getWinner() == null ? "§7Nikdo nevyhral" : FightDuel.this.getWinner().getUser().getName()));
				this.setLine(8,"");
				this.setLine(9,"§ewww.realcraft.cz");
			}
			else if(FightDuel.this.getState() == FightState.INGAME){
				this.setLine(5,"");
				this.setLine(6,"§ewww.realcraft.cz");
			}
			super.update();
		}
	}
}