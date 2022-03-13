package realcraft.bukkit.banmanazer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.DateUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BanManazer implements Listener, CommandExecutor {
	RealCraft plugin;

	public BanManazer(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		plugin.getCommand("ban").setExecutor(this);
	}

	public void onReload(){
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("ban")){
			if(player.hasPermission("group.Admin")){
				if(args.length == 1 && args[0].equalsIgnoreCase("help")){
					player.sendMessage("");
					player.sendMessage(RealCraft.parseColors("Priklad: &6/ban "+player.getName()+" 24h duvod"));
					player.sendMessage(RealCraft.parseColors("Priklad: &6/ban "+player.getName()+" duvod"));
					player.sendMessage(RealCraft.parseColors("Casove jednotky: &6s &r(sekundy), &6m &r(minuty), &6h &r(hodiny), &6d &r(dny)"));
					player.sendMessage("/ban <player> <time> <reason>");
					return true;
				}
				else if(args.length >= 2){
					Player victim = plugin.getServer().getPlayer(args[0]);
					int expire = 0;
					if(victim == null){
						player.sendMessage(RealCraft.parseColors("&cHrac nenalezen."));
						return true;
					}
					else if(victim == player){
						player.sendMessage(RealCraft.parseColors("&cNemuzes zabanovat sam sebe."));
						return true;
					}
					boolean isTimeSet = BanUtils.isNumeric(args[1].substring(0,1));
					if(!isTimeSet){
						banPlayer(victim,expire,BanUtils.combineSplit(1,args),player);
						return true;
					} else {
						try {
							expire = DateUtil.parseDateDiff(args[1],true);
						}
						catch (Exception e){
							player.sendMessage(RealCraft.parseColors("&cNespravny format data."));
							return true;
						}
						String reason = BanUtils.combineSplit(2,args);
						if(reason != null && reason.length() > 0){
							banPlayer(victim,expire,reason,player);
							return true;
						}
					}
				}
				player.sendMessage("/ban <player> <time> <reason>");
				return true;
			}
			else player.sendMessage(RealCraft.parseColors("&cNemas povoleni na tento prikaz."));
		}
		return true;
	}

	@EventHandler(priority=EventPriority.HIGH,ignoreCancelled = true)
	public void PlayerLoginEvent(PlayerLoginEvent event){
		BanInfo ban = getBanInfo(event.getPlayer().getName(),BanUtils.getAddress(event.getAddress()));
		if(ban != null){
			event.disallow(Result.KICK_BANNED,ban.getKickMessage());
		}
	}

	public static void banPlayer(Player player,int expire,String reason,Player admin){
		BanInfo ban = null;
		if(admin != null) ban = new BanInfo(player.getName(),BanUtils.getAddress(player.getAddress().getAddress()),reason,expire,Users.getUser(admin).getId(),admin.getName(),BanUtils.getAddress(admin.getAddress().getAddress()));
		else ban = new BanInfo(player.getName(),BanUtils.getAddress(player.getAddress().getAddress()),reason,expire,0,"","");
		ban.insertToDB();
		player.kickPlayer(ban.getKickMessage());
		if(admin != null) Bukkit.getServer().broadcastMessage("§7Hrac §6"+ban.getName()+"§7 byl zabanovan adminem §6"+admin.getName()+"§7"+(expire > 0 ? " na §6"+DateUtil.formatDateDiff((long)expire*1000)+"§7" : "")+". Duvod: §6"+reason);
		Bukkit.getServer().broadcastMessage("§7Hrac §6"+ban.getName()+"§7 byl zabanovan"+(expire > 0 ? " na §6"+DateUtil.formatDateDiff((long)expire*1000)+"§7" : "")+". Duvod: §6"+reason);
	}

	public BanInfo getBanInfo(String name,String address){
		if(DB.connected){
			ResultSet rs = DB.query("SELECT t1.ban_id,t1.user_name,t1.user_ip,t1.ban_reason,t1.ban_expire,t1.admin_id,t1.admin_ip,t2.user_name AS admin_name FROM bans t1 LEFT JOIN authme t2 ON t1.admin_id = t2.user_id WHERE (ban_expire = '0' OR ban_expire >= '"+(System.currentTimeMillis()/1000)+"') AND (t1.user_name = '"+name.toLowerCase()+"' OR t1.user_ip = '"+address+"')");
			try {
				while(rs.next()){
					if(!this.hasBanException(name,rs.getInt("ban_id"))){
						return new BanInfo(
							rs.getString("user_name"),
							rs.getString("user_ip"),
							rs.getString("ban_reason"),
							rs.getInt("ban_expire"),
							rs.getInt("admin_id"),
							rs.getString("admin_name"),
							rs.getString("admin_ip")
						);
					}
				}
				rs.close();
			} catch (SQLException e){
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean hasBanException(String name,int ban_id) throws SQLException {
		ResultSet rs = DB.query("SELECT t2.user_id FROM authme t1 INNER JOIN bans_exceptions t2 USING(user_id) WHERE user_name = '"+name.toLowerCase()+"' AND ban_id = '"+ban_id+"'");
		return rs.next();
	}

	public static class BanInfo {
		String name;
		String address;
		String reason;
		int admin_id;
		String admin_name;
		String admin_ip;
		int expire;

		public BanInfo(String name,String address,String reason,int expire,int admin_id,String admin_name,String admin_ip){
			this.name = name;
			this.address = address;
			this.reason = reason;
			this.expire = expire;
			this.admin_id = admin_id;
			this.admin_name = admin_name;
			this.admin_ip = admin_ip;
		}

		public void insertToDB(){
			if(DB.connected){
				PreparedStatement stmt;
				try {
					stmt = DB.conn.prepareStatement("INSERT INTO bans (user_name,user_ip,ban_reason,ban_created,ban_expire,admin_id,admin_ip) VALUES(?,?,?,?,?,?,?)");
					stmt.setString(1,name.toLowerCase());
					stmt.setString(2,address);
					stmt.setString(3,reason);
					stmt.setLong(4,System.currentTimeMillis()/1000);
					stmt.setInt(5,expire);
					stmt.setInt(6,admin_id);
					stmt.setString(7,admin_ip);
					stmt.executeUpdate();
				}
				catch (SQLException e){
					e.printStackTrace();
				}
			}
		}

		public String getName(){
			return name;
		}

		public String getAddress(){
			return address;
		}

		public String getReason(){
			return reason;
		}

		public int getExpire(){
			return expire;
		}

		public String getKickMessage(){
			return
					"§r§c\u2716 §fByl jsi zabanovan §c\u2716\n"+
					"§r\n"+
					"§r§7Duvod: §f"+reason+"\n"+
					(expire > 0 ? "§r§7Ban vyprsi §f"+DateUtil.lastTime(expire,true)+"§7\n" : "")+
					"§r\n"+
					"§r§7Zruseni banu muzes ziskat na §6www.realcraft.cz"
			;
		}
	}
}
