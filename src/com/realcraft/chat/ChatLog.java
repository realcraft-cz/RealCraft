package com.realcraft.chat;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.realcraft.RealCraft;

public class ChatLog implements Listener {
	RealCraft plugin;

	public ChatLog(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	public void onReload(){
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		if(!plugin.db.connected || event.isCancelled()) return;
		Player player = event.getPlayer();
		String message = event.getMessage();

		PreparedStatement stmt;
		try {
			int playerid = plugin.playermanazer.getPlayerInfo(player).getId();
			if(playerid != 0){
				stmt = plugin.db.conn.prepareStatement("INSERT INTO chatlog (user_id,user_ip,message,timestamp,type) VALUES(?,?,?,?,?)");
				stmt.setInt(1,playerid);
				stmt.setString(2,player.getAddress().getAddress().getHostAddress());
				stmt.setString(3,message);
				stmt.setLong(4,System.currentTimeMillis()/1000);
				stmt.setInt(5,0);
				stmt.executeUpdate();
			}
		}
		catch (SQLException e){
			e.printStackTrace();
		}
	}

	public void onPlayerAdminChat(Player player,String message){
		PreparedStatement stmt;
		try {
			int playerid = plugin.playermanazer.getPlayerInfo(player).getId();
			if(playerid != 0){
				stmt = plugin.db.conn.prepareStatement("INSERT INTO chatlog (user_id,user_ip,message,timestamp,type) VALUES(?,?,?,?,?)");
				stmt.setInt(1,playerid);
				stmt.setString(2,player.getAddress().getAddress().getHostAddress());
				stmt.setString(3,message);
				stmt.setLong(4,System.currentTimeMillis()/1000);
				stmt.setInt(5,1);
				stmt.executeUpdate();
			}
		}
		catch (SQLException e){
			e.printStackTrace();
		}
	}

	public void onPrivateMessage(Player sender,Player recipient,String message){
		PreparedStatement stmt;
		try {
			int senderid = plugin.playermanazer.getPlayerInfo(sender).getId();
			int recipientid = plugin.playermanazer.getPlayerInfo(recipient).getId();
			if(senderid != 0 && recipientid != 0){
				stmt = plugin.db.conn.prepareStatement("INSERT INTO chatlog (user_id,recipient_id,user_ip,message,timestamp,type) VALUES(?,?,?,?,?,?)");
				stmt.setInt(1,senderid);
				stmt.setInt(2,recipientid);
				stmt.setString(3,sender.getAddress().getAddress().getHostAddress());
				stmt.setString(4,message);
				stmt.setLong(5,System.currentTimeMillis()/1000);
				stmt.setInt(6,2);
				stmt.executeUpdate();
			}
		}
		catch (SQLException e){
			e.printStackTrace();
		}
	}

	public void onPlayerCommand(Player player,String command){
		PreparedStatement stmt;
		try {
			int playerid = plugin.playermanazer.getPlayerInfo(player).getId();
			if(playerid != 0){
				stmt = plugin.db.conn.prepareStatement("INSERT INTO chatcommands (user_id,user_ip,command,timestamp) VALUES(?,?,?,?)");
				stmt.setInt(1,playerid);
				stmt.setString(2,player.getAddress().getAddress().getHostAddress());
				stmt.setString(3,command);
				stmt.setLong(4,System.currentTimeMillis()/1000);
				stmt.executeUpdate();
			}
		}
		catch (SQLException e){
			e.printStackTrace();
		}
	}
}