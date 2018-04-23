package realcraft.bukkit.fights.duels;

import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.fights.FightPlayer.FightPlayerState;
import realcraft.bukkit.fights.FightScoreboard;
import realcraft.bukkit.fights.FightState;
import realcraft.bukkit.fights.FightType;
import realcraft.bukkit.fights.Fights;
import realcraft.bukkit.fights.arenas.FightDuelArena;
import realcraft.bungee.skins.utils.StringUtil;

public class FightDuel {

	private FightPlayer fPlayer1;
	private FightPlayer fPlayer2;

	private int startTime = 5;
	private FightState state = FightState.STARTING;
	private FightDuelArena arena;
	private FightDuelScoreboard scoreboard;

	public FightDuel(FightPlayer fPlayer1,FightPlayer fPlayer2){
		this.fPlayer1 = fPlayer1;
		this.fPlayer2 = fPlayer2;
	}

	public FightPlayer[] getPlayers(){
		return new FightPlayer[]{fPlayer1,fPlayer2};
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

	public void joinPlayer(FightPlayer fPlayer){
		if(fPlayer.equals(fPlayer1) || fPlayer.equals(fPlayer2)){
			fPlayer.setState(FightPlayerState.FIGHT);
			fPlayer.setQueue(false);
			fPlayer.reset();
			fPlayer.setDuel(this);
			fPlayer.setArena(this.getArena());
			fPlayer.getPlayer().teleport(arena.getSpawns().get(fPlayer.equals(fPlayer1) ? 0 : 1));
		} else {
			fPlayer.setState(FightPlayerState.SPECTATOR);
			fPlayer.reset();
			fPlayer.toggleSpectator();
			fPlayer.getPlayer().teleport(arena.getSpectatorLocation());
		}
		this.getScoreboard().addPlayer(fPlayer);
	}

	public void start(){
		this.setState(FightState.INGAME);
	}

	public void run(){
		if(startTime > 0){
			startTime --;
			if(startTime == 0) this.start();
		}
	}

	public class FightDuelScoreboard extends FightScoreboard {

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