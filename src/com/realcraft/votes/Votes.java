package com.realcraft.votes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.realcraft.RealCraft;
import com.realcraft.playermanazer.PlayerManazer;
import com.realcraft.sockets.SocketData;
import com.realcraft.sockets.SocketDataEvent;
import com.realcraft.sockets.SocketManager;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Votes implements Listener, Runnable {

	private RealCraft plugin;
	private static final String VOTES = "votes_GALVotes";
	private static final String CHANNEL_REMINDER = "voteReminder";
	private HashMap<String,Long> voteReminds = new HashMap<String,Long>();

	public Votes(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,this,60*20,60*20);
	}

	public void onReload(){
	}

	@Override
	public void run(){
		for(Player player : Bukkit.getOnlinePlayers()){
			if(RealCraft.getInstance().serverName.equalsIgnoreCase("lobby") ||
				RealCraft.getInstance().serverName.equalsIgnoreCase("survival") ||
				RealCraft.getInstance().serverName.equalsIgnoreCase("creative") ||
				RealCraft.getInstance().serverName.equalsIgnoreCase("parkour")){
				this.checkPlayerReminder(player);
			}
		}
	}

	public void checkPlayerReminder(Player player){
		Calendar time = Calendar.getInstance();
		long evenHour = (System.currentTimeMillis()/1000)-((time.get(Calendar.HOUR_OF_DAY)*3600+time.get(Calendar.MINUTE)*60+time.get(Calendar.SECOND))%7200);
		if(PlayerManazer.getPlayerInfo(player).isActiveVoter() && (voteReminds.get(player.getName()) == null || voteReminds.get(player.getName()) < evenHour)){
			long lastVoted = 0;
			ResultSet rs = RealCraft.getInstance().db.query("SELECT vote_created FROM "+VOTES+" WHERE user_id = '"+PlayerManazer.getPlayerInfo(player).getId()+"' ORDER BY vote_created DESC LIMIT 1");
			try {
				if(rs.next()){
					lastVoted = rs.getLong("vote_created");
				}
				rs.close();
			} catch (SQLException e){
				e.printStackTrace();
			}
			if(lastVoted <= evenHour){
				voteReminds.put(player.getName(),evenHour);
				player.sendMessage("§7----------------------------------------");
				player.sendMessage("§fDekujeme za tvuj hlas pred "+Math.round(((System.currentTimeMillis()/1000)-lastVoted)/60)+" minutami.");
				TextComponent component = new TextComponent("§eHlasuj znovu na §6www.realcraft.cz/hlasovat/");
				component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://www.realcraft.cz/hlasovat/"));
				component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§7Klikni pro hlasovani").create()));
				player.spigot().sendMessage(component);
				player.sendMessage("§7----------------------------------------");
				player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,1f);
				SocketData data = new SocketData(CHANNEL_REMINDER);
				data.setString("name",player.getName());
				data.setLong("time",evenHour);
				SocketManager.sendToAll(data);
			}
		}
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(CHANNEL_REMINDER)){
			voteReminds.put(data.getString("name"),data.getLong("time"));
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
    public void VotifierEvent(VotifierEvent event){
		Vote vote = event.getVote();
		if(vote.getUsername() != null && vote.getUsername().length() > 0){
			PreparedStatement stmt;
			try {
				stmt = plugin.db.conn.prepareStatement("INSERT INTO "+VOTES+" (user_name,user_ip,vote_service,vote_created) VALUES(?,?,?,?)");
				stmt.setString(1,vote.getUsername());
				stmt.setString(2,vote.getAddress());
				stmt.setString(3,vote.getServiceName());
				stmt.setLong(4,Long.parseLong(vote.getTimeStamp()));
				stmt.executeUpdate();
			}
			catch (SQLException e){
				e.printStackTrace();
			}
		}
	}
}