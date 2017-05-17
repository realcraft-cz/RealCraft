package com.realcraft.votes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.realcraft.RealCraft;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class Votes implements Listener {
	RealCraft plugin;

	public Votes(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	public void onReload(){
	}

	@EventHandler(priority=EventPriority.MONITOR)
    public void onVotifierEvent(VotifierEvent event){
		Vote vote = event.getVote();
		if(vote.getUsername() != null && vote.getUsername().length() > 0){
			PreparedStatement stmt;
			try {
				stmt = plugin.db.conn.prepareStatement("INSERT INTO votes_GALVotes (user_name,user_ip,vote_service,vote_created) VALUES(?,?,?,?)");
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