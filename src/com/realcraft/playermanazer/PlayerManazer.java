package com.realcraft.playermanazer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import com.realcraft.RealCraft;
import com.realcraft.lobby.LobbyLottery;
import com.realcraft.utils.Title;

public class PlayerManazer implements Listener {
	RealCraft plugin;
	static HashMap<UUID, PlayerInfo> playerinfo = new HashMap<>();

	public PlayerManazer(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		playerinfo = new HashMap<>();
		for(Player player : plugin.getServer().getOnlinePlayers()){
			playerinfo.put(player.getUniqueId(),new PlayerInfo(player));
		}
	}

	public PlayerInfo getPlayerInfo(UUID uuid){
		return playerinfo.get(uuid);
	}

	public static PlayerInfo getPlayerInfo(Player player){
		if(!playerinfo.containsKey(player.getUniqueId())) playerinfo.put(player.getUniqueId(),new PlayerInfo(player));
		return playerinfo.get(player.getUniqueId());
	}

	@EventHandler(priority=EventPriority.LOWEST,ignoreCancelled = true)
	public void onPlayerJoin(PlayerLoginEvent event){
		Player player = event.getPlayer();
		getPlayerInfo(player).reload(player);
	}

	public static class PlayerInfo {
		Player player;
		String name;
		int id;
		int rank;
		String uuid;
		boolean modchat = false;
		boolean noticechat = false;
		String[] noticewords;
		String noticesound;
		int muteexpire = 0;
		long lastLobbyJump = 0;
		int compassMode = 0;
		Player nearestPlayer;
		Player lastMessagePlayer;
		boolean logged = false;
		int coins = 0;
		int lobbyKeys = 0;
		int lobbyFragments = 0;
		String avatar;
		boolean coinsboost = true;
		int lastLotteryTime = 0;
		boolean activeVoter = false;

		public PlayerInfo(Player player){
			this.player = player;
			uuid = player.getUniqueId().toString();
			getDatabaseData();
			getDatabaseNoticeWords();
			getLobbyData();
			loadLastLotteryTime();
			loadActiveVoter();
		}

		public void reload(Player player){
			this.player = player;
			getDatabaseData();
			getDatabaseNoticeWords();
			getLobbyData();
			loadLastLotteryTime();
			loadActiveVoter();
		}

		private void getDatabaseData(){
			if(RealCraft.getInstance().db.connected){
				ResultSet rs = RealCraft.getInstance().db.query("SELECT user_id,user_name,user_rank,user_avatar,user_coins FROM authme WHERE user_uuid = '"+uuid+"'");
				try {
					if(rs.next()){
						id = rs.getInt("user_id");
						name = rs.getString("user_name");
						rank = rs.getInt("user_rank");
						avatar = rs.getString("user_avatar");
						coins = rs.getInt("user_coins");
					}
					rs.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
		}

		public void getDatabaseNoticeWords(){
			noticechat = false;
			if(RealCraft.getInstance().db.connected){
				ResultSet rs = RealCraft.getInstance().db.query("SELECT user_words,user_sound FROM chatnotice WHERE user_id = '"+id+"'");
				try {
					if(rs.next()){
						noticewords = rs.getString("user_words").split("\\r?\\n");
						noticesound = rs.getString("user_sound");
						if(noticewords.length > 0) noticechat = true;
					}
					rs.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
		}

		public void getLobbyData(){
			if(RealCraft.getInstance().db.connected){
				ResultSet rs = RealCraft.getInstance().db.query("SELECT user_lobby_keys,user_lobby_fragments FROM authme WHERE user_id = '"+id+"'");
				try {
					if(rs.next()){
						lobbyKeys = rs.getInt("user_lobby_keys");
						lobbyFragments = rs.getInt("user_lobby_fragments");
					}
					rs.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
		}

		public int getId(){
			if(id == 0) getDatabaseData();
			return id;
		}

		public String getName(){
			return name;
		}

		public String getAvatar(){
			return avatar;
		}

		public int getRank(){
			return rank;
		}

		public String getUUID(){
			return uuid;
		}

		public boolean isLogged(){
			return logged;
		}

		public void setLogged(boolean logged){
			this.logged = logged;
		}

		public boolean toggleModChat(){
			modchat = !modchat;
			return modchat;
		}

		public void setModChat(boolean modchat){
			this.modchat = modchat;
		}

		public boolean getModChat(){
			return modchat;
		}

		public boolean getNoticeChat(){
			return noticechat;
		}

		public String[] getNoticeWords(){
			if(noticewords == null) getDatabaseNoticeWords();
			return noticewords;
		}

		public String getNoticeSound(){
			return noticesound;
		}

		public void setLastLobbyJump(long time){
			this.lastLobbyJump = time;
		}

		public long getLastLobbyJump(){
			return lastLobbyJump;
		}

		public void setCompassMode(int mode){
			this.compassMode = mode;
		}

		public int getCompassMode(){
			return compassMode;
		}

		public void setNearestPlayer(Player player){
			this.nearestPlayer = player;
		}

		public Player getNearestPlayer(){
			if(nearestPlayer == null || !nearestPlayer.isOnline()) nearestPlayer = null;
			return nearestPlayer;
		}

		public void setLastMessagePlayer(Player player){
			this.lastMessagePlayer = player;
		}

		public Player getLastMessagePlayer(){
			if(lastMessagePlayer == null || !lastMessagePlayer.isOnline()) lastMessagePlayer = null;
			return lastMessagePlayer;
		}

		public void updatePing(){

		}

		public int getLobbyKeys(){
			return lobbyKeys;
		}

		public int getLobbyFragments(){
			return lobbyFragments;
		}

		public void givePlayerKeys(int keys){
			lobbyKeys += keys;
			RealCraft.getInstance().db.update("UPDATE authme SET user_lobby_keys='"+lobbyKeys+"' WHERE user_id = '"+id+"'");
		}

		public void removePlayerKeys(int keys){
			lobbyKeys -= keys;
			if(lobbyKeys < 0) lobbyKeys = 0;
			RealCraft.getInstance().db.update("UPDATE authme SET user_lobby_keys='"+lobbyKeys+"' WHERE user_id = '"+id+"'");
		}

		public void givePlayerFragments(int fragments){
			lobbyFragments += fragments;
			RealCraft.getInstance().db.update("UPDATE authme SET user_lobby_fragments='"+lobbyFragments+"' WHERE user_id = '"+id+"'");
		}

		public void resetPlayerFragments(){
			lobbyFragments = 0;
			RealCraft.getInstance().db.update("UPDATE authme SET user_lobby_fragments='"+lobbyFragments+"' WHERE user_id = '"+id+"'");
		}

		public int getCoins(){
			return coins;
		}

		public boolean hasCoinsBoost(){
			return coinsboost;
		}

		public int giveCoins(int coins){
			return this.giveCoins(coins,true);
		}

		public int giveCoins(int coins,boolean boost){
			coins = (coinsboost && boost && coins > 0 ? coins*2 : coins);
			this.coins += coins;
			RealCraft.getInstance().db.update("UPDATE authme SET user_coins='"+this.coins+"' WHERE user_id = '"+id+"'");
			return coins;
		}

		private static final int[] coinsPercentages = new int[]{0,10,20,30,40,50,60,68,75,81,86,90,93,95,96,97,98,99,100};
		private static final int[] coinsPercentages2 = new int[]{0,10,20,30,40,50,60,70,80,90,100};
		private static final int[] coinsTimings = new int[]{2,4,6,8,10,12,14,16,18,20,22,24,26,29,33,38,44,51,59};
		private static final int[] coinsTimings2 = new int[]{18,20,22,24,26,29,33,38,44,51,59};

		public void runCoinsEffect(int coins){
			this.runCoinsEffect(" ",coins,true);
		}

		public void runCoinsEffect(String title,int coins){
			this.runCoinsEffect(" ",coins,true);
		}

		public void runCoinsEffect(String title,int coins,boolean boost){
			int i = 0;
			for(int percent : (coins >= 100 ? coinsPercentages : coinsPercentages2)){
				Bukkit.getScheduler().scheduleSyncDelayedTask(RealCraft.getInstance(),new Runnable(){
					@Override
					public void run(){
						showCoinsEffect(title,(int)Math.round((coins/100.0)*percent),boost);
					}
				},(coins >= 100 ? coinsTimings[i++] : coinsTimings2[i++]-16));
			}
		}

		private void showCoinsEffect(String title,int coins,boolean boost){
			Title.showTitle(player,title,0.0,2,0.5);
			Title.showSubTitle(player,"§a+"+coins+" coins"+(coinsboost && boost ? " §b(2x)" : ""),0.0,2,0.5);
			player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
		}

		public void loadLastLotteryTime(){
			if(RealCraft.getInstance().db.connected){
				ResultSet rs = RealCraft.getInstance().db.query("SELECT lottery_created FROM "+LobbyLottery.LOTTERIES+" WHERE user_id = '"+this.getId()+"' ORDER BY lottery_created DESC LIMIT 1");
				try {
					if(rs.next()){
						lastLotteryTime = rs.getInt("lottery_created");
					}
					rs.close();
				} catch (SQLException e){
				}
			}
		}

		public int getLastLotteryTime(){
			return lastLotteryTime;
		}

		private void loadActiveVoter(){
			if(RealCraft.getInstance().db.connected){
				activeVoter = false;
				ResultSet rs = RealCraft.getInstance().db.query("SELECT user_id FROM votes_GALVotes WHERE user_id = '"+this.getId()+"' AND vote_created > '"+(System.currentTimeMillis()/1000-(2*86400))+"'");
				try {
					if(rs.next()){
						activeVoter = true;
					}
					rs.close();
				} catch (SQLException e){
				}
			}
		}

		public boolean isActiveVoter(){
			return activeVoter;
		}
	}
}