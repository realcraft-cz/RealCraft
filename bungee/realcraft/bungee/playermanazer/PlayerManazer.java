package realcraft.bungee.playermanazer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import realcraft.bungee.RealCraftBungee;

public class PlayerManazer {
	static RealCraftBungee plugin;
	static ConcurrentHashMap<UUID,PlayerInfo> playerinfo = new ConcurrentHashMap<UUID,PlayerInfo>();

	static String [] loginMessages;
	static String [] registerMessages;

	public PlayerManazer(RealCraftBungee realcraft){
		plugin = realcraft;
		List<String> messagesTmp = plugin.config.getStringList("messages.loginMessages");
		loginMessages = messagesTmp.toArray(new String[messagesTmp.size()]);
		List<String> messagesTmp2 = plugin.config.getStringList("messages.registerMessages");
		registerMessages = messagesTmp2.toArray(new String[messagesTmp2.size()]);
	}

	public static PlayerInfo getPlayerInfo(ProxiedPlayer player){
		return getPlayerInfo(player.getPendingConnection());
	}

	public static PlayerInfo getPlayerInfo(PendingConnection connection){
		if(!playerinfo.containsKey(connection.getUniqueId())) playerinfo.put(connection.getUniqueId(),new PlayerInfo(connection));
		return playerinfo.get(connection.getUniqueId());
	}

	public static void removePlayerInfo(ProxiedPlayer player){
		removePlayerInfo(player.getPendingConnection());
	}

	public static void removePlayerInfo(PendingConnection connection){
		playerinfo.remove(connection.getUniqueId());
	}

	public static void updatePlayTime(){
		for(PlayerInfo playerinfo : playerinfo.values()){
			playerinfo.performPlayTime();
		}
	}

	public static class PlayerInfo {
		ProxiedPlayer player = null;
		int id;
		int rank;
		UUID uuid;
		int version;
		String address = "";
		String skin = "";
		long lastSkinned = 0;
		boolean logged;
		boolean registered = false;
		boolean premium = false;
		long lastPlayTime = 0;
		String passwordHash;
		ServerInfo server;
		int attempts = 0;
		long tooManyAttemptsTime = 0;

		public PlayerInfo(PendingConnection connection){
			this.uuid = connection.getUniqueId();
			this.version = connection.getVersion();
			address = connection.getAddress().getAddress().getHostAddress().replace("/", "");
			logged = false;
			registered = false;
			lastPlayTime = System.currentTimeMillis();
			if(plugin.db.connected){
				ResultSet rs = plugin.db.query("SELECT user_id,user_rank,user_password,user_premium,user_skin,user_last_skinned FROM authme WHERE user_uuid = '"+uuid.toString()+"'");
				try {
					if(rs.next()){
						registered = true;
						id = rs.getInt("user_id");
						rank = rs.getInt("user_rank");
						passwordHash = rs.getString("user_password");
						premium = rs.getBoolean("user_premium");
						skin = rs.getString("user_skin");
						lastSkinned = rs.getLong("user_last_skinned");
					}
					rs.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
		}

		public ProxiedPlayer getPlayer(){
			if(player == null) player = BungeeCord.getInstance().getPlayer(uuid);
			return player;
		}

		public int getId(){
			return id;
		}

		public int getRank(){
			return rank;
		}

		public UUID getUUID(){
			return uuid;
		}

		public String getAddress(){
			return address;
		}

		public boolean isPremium(){
			return premium;
		}

		public void setPremium(boolean premium){
			this.premium = premium;
		}

		public String getSkin(){
			return skin;
		}

		public void setSkin(String name){
			skin = name;
			lastSkinned = System.currentTimeMillis()/1000;
			plugin.db.update("UPDATE authme SET user_skin = '"+name+"',user_last_skinned = '"+lastSkinned+"' WHERE user_uuid = '"+uuid.toString()+"'");
		}

		public long getLastSkinned(){
			return lastSkinned;
		}

		public boolean isLogged(){
			return logged;
		}

		public boolean isRegistered(){
			return registered;
		}

		public ServerInfo getServer(){
			return server;
		}

		public String getPasswordHash(){
			return passwordHash;
		}

		public long getTooManyAttemptsSeconds(){
			return (tooManyAttemptsTime-System.currentTimeMillis())/1000;
		}

		public boolean tooManyAttempts(){
			if(tooManyAttemptsTime < System.currentTimeMillis()) return false;
			return true;
		}

		public void addAttempt(){
			attempts ++;
			if(attempts >= 3){
				attempts = 0;
				tooManyAttemptsTime = System.currentTimeMillis()+(30*1000);
			}
		}

		public boolean checkPassword(String password){
			if(passwordHash.length() < 1 || password.length() < 1) return false;
			if((passwordHash.length() == 60 && BCrypt.checkPassword(password,passwordHash)) || SHA256.comparePassword(passwordHash,password)){
				return true;
			}
			return false;
		}

		@SuppressWarnings("deprecation")
		public void performLogin(String pass){
			logged = true;
			attempts = 0;
			plugin.db.update("UPDATE authme SET user_logged = '1',user_lastlogin = '"+System.currentTimeMillis()+"',user_ip = '"+address+"',"+(passwordHash.length() > 60 ? "user_password = '"+BCrypt.hashPassword(pass)+"'," : "")+"user_shortpass = '"+MD5.getMD5(pass).substring(4,8)+"',user_server = 'lobby',user_version = '"+version+"' WHERE user_uuid = '"+uuid.toString()+"'");
			for(String message : loginMessages) this.getPlayer().sendMessage(RealCraftBungee.parseColors(message));
			loggedNotify();
		}

		@SuppressWarnings("deprecation")
		public void performTestLogin(){
			logged = true;
			attempts = 0;
			plugin.db.update("UPDATE authme SET user_logged = '1',user_lastlogin = '"+System.currentTimeMillis()+"',user_ip = '"+address+"',user_server = 'lobby',user_version = '"+version+"' WHERE user_uuid = '"+uuid.toString()+"'");
			for(String message : loginMessages) this.getPlayer().sendMessage(RealCraftBungee.parseColors(message));
			loggedNotify();
		}

		@SuppressWarnings("deprecation")
		public void performPremiumLogin(){
			logged = true;
			attempts = 0;
			plugin.db.update("UPDATE authme SET user_logged = '1',user_lastlogin = '"+System.currentTimeMillis()+"',user_ip = '"+address+"',user_server = 'lobby',user_version = '"+version+"' WHERE user_uuid = '"+uuid.toString()+"'");
			for(String message : loginMessages) this.getPlayer().sendMessage(RealCraftBungee.parseColors(message));
			loggedNotify();
		}

		@SuppressWarnings("deprecation")
		public boolean performRegister(String pass1,String pass2){
			if(pass1.equals(pass2) && !registered){
				PreparedStatement stmt;
				try {
					stmt = plugin.db.conn.prepareStatement("INSERT INTO authme (user_name,user_lowername,user_uuid,user_password,user_shortpass,user_ip,user_firstlogin,user_lastlogin,user_logged,user_server,user_version) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
					stmt.setString(1,this.getPlayer().getName());
					stmt.setString(2,this.getPlayer().getName().toLowerCase());
					stmt.setString(3,uuid.toString());
					stmt.setString(4,BCrypt.hashPassword(pass1));
					//stmt.setString(4,SHA256.getHash(pass1,rs.nextString()));
					stmt.setString(5,MD5.getMD5(pass1).substring(4,8));
					stmt.setString(6,address);
					stmt.setInt(7,(int)(System.currentTimeMillis()/1000));
					stmt.setLong(8,System.currentTimeMillis());
					stmt.setInt(9,1);
					stmt.setString(10,"lobby");
					stmt.setInt(11,version);
					stmt.executeUpdate();
					registered = true;
					logged = true;
					for(String message : registerMessages) this.getPlayer().sendMessage(RealCraftBungee.parseColors(message));
					loggedNotify();
					return true;
				}
				catch (SQLException e){
					e.printStackTrace();
				}
			}
			return false;
		}

		public void performLogout(){
			logged = false;
			plugin.db.update("UPDATE authme SET user_logged = '0',user_lastlogin = '"+System.currentTimeMillis()+"' WHERE user_uuid = '"+uuid.toString()+"'");
			loggedOutNotify();
		}

		public void performSwitch(){
			server = this.getPlayer().getServer().getInfo();
			plugin.db.update("UPDATE authme SET user_server = '"+server.getName()+"' WHERE user_uuid = '"+uuid.toString()+"'");
		}

		public void performPlayTime(){
			if(logged){
				int playtime = (int)((System.currentTimeMillis()-lastPlayTime)/1000);
				lastPlayTime = System.currentTimeMillis();
				plugin.db.update("UPDATE authme SET user_playtime = user_playtime + "+playtime+" WHERE user_uuid = '"+uuid.toString()+"'");
			}
		}

		public void loggedNotify(){
			 try {
                 final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                 DataOutputStream out = new DataOutputStream(bytes);
                 out.writeUTF("LoggedIn");
                 out.writeUTF(this.getUUID().toString());
                 final ServerInfo server = this.getPlayer().getServer().getInfo();
                 plugin.getProxy().getScheduler().runAsync(plugin,new Runnable(){
					@Override
					public void run(){
						server.sendData("RealCraftAuth",bytes.toByteArray());
					}
                 });
             }
             catch (IOException e){
                 e.printStackTrace();
             }
		}

		public void loggedOutNotify(){
			try {
                final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(bytes);
                out.writeUTF("LoggedOut");
                out.writeUTF(this.getUUID().toString());
                final ServerInfo server = plugin.getProxy().getServerInfo("lobby");
                plugin.getProxy().getScheduler().runAsync(plugin,new Runnable(){
					@Override
					public void run(){
						server.sendData("RealCraftAuth",bytes.toByteArray());
					}
                });
            }
            catch (IOException e){
                e.printStackTrace();
            }
		}
	}
}