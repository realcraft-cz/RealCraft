package realcraft.bukkit.chat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChatLog implements Listener {
	RealCraft plugin;

	public ChatLog(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	public void onReload(){
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		if(event.isCancelled()) return;
		Player player = event.getPlayer();
		String message = event.getMessage();
		this.onPlayerChat(player,message);
	}

	public void onPlayerChat(Player player,String message){
		int playerid = Users.getUser(player).getId();
		if(playerid != 0){
			Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run() {
					PreparedStatement stmt = null;
					try {
						stmt = DB.conn.prepareStatement("INSERT INTO chatlog (user_id,user_ip,message,timestamp,type) VALUES(?,?,?,?,?)");
						stmt.setInt(1, playerid);
						stmt.setString(2, player.getAddress().getAddress().getHostAddress());
						stmt.setString(3, message);
						stmt.setLong(4, System.currentTimeMillis() / 1000);
						stmt.setInt(5, 0);
						stmt.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						DB.close(stmt);
					}
				}
			});
		}
	}

	public void onPlayerAdminChat(Player player,String message){
		int playerid = Users.getUser(player).getId();
		if(playerid != 0){
			Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(),new Runnable() {
				@Override
				public void run() {
					PreparedStatement stmt = null;
					try {
						stmt = DB.conn.prepareStatement("INSERT INTO chatlog (user_id,user_ip,message,timestamp,type) VALUES(?,?,?,?,?)");
						stmt.setInt(1, playerid);
						stmt.setString(2, player.getAddress().getAddress().getHostAddress());
						stmt.setString(3, message);
						stmt.setLong(4, System.currentTimeMillis() / 1000);
						stmt.setInt(5, 1);
						stmt.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						DB.close(stmt);
					}
				}
			});
		}
	}

	public void onPrivateMessage(User sender,User recipient,String message){
		int senderid = sender.getId();
		int recipientid = recipient.getId();
		if(senderid != 0 && recipientid != 0){
			Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(),new Runnable() {
				@Override
				public void run() {
					PreparedStatement stmt = null;
					try {
						stmt = DB.conn.prepareStatement("INSERT INTO chatlog (user_id,recipient_id,user_ip,message,timestamp,type) VALUES(?,?,?,?,?,?)");
						stmt.setInt(1, senderid);
						stmt.setInt(2, recipientid);
						stmt.setString(3, sender.getAddress());
						stmt.setString(4, message);
						stmt.setLong(5, System.currentTimeMillis() / 1000);
						stmt.setInt(6, 2);
						stmt.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						DB.close(stmt);
					}
				}
			});
		}
	}

	public void onPlayerCommand(Player player,String command){
		int playerid = Users.getUser(player).getId();
		if(playerid != 0){
			Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(),new Runnable() {
				@Override
				public void run() {
					PreparedStatement stmt = null;
					try {
						stmt = DB.conn.prepareStatement("INSERT INTO chatcommands (user_id,user_ip,command,timestamp) VALUES(?,?,?,?)");
						stmt.setInt(1, playerid);
						stmt.setString(2, player.getAddress().getAddress().getHostAddress());
						stmt.setString(3, command);
						stmt.setLong(4, System.currentTimeMillis() / 1000);
						stmt.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						DB.close(stmt);
					}
				}
			});
		}
	}
}