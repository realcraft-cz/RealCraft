package realcraft.bukkit.chat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.entity.Player;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.Title;
import realcraft.share.users.User;
import realcraft.share.users.UserRank;

public class ChatTips implements Runnable {
	RealCraft plugin;

	HashMap<Integer,Tip> tips = new HashMap<Integer,Tip>();

	public ChatTips(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,this,60*20,60*20);
		loadTips();
	}

	public void onReload(){
		tips = new HashMap<Integer,Tip>();
		loadTips();
	}

	public void loadTips(){
		if(plugin.db.connected){
			ResultSet rs = plugin.db.query("SELECT tip_id,tip_message,tip_minrank,tip_maxrank,tip_period,tip_servers,tip_actionbar FROM chattips");
			try {
				while(rs.next()){
					int id = rs.getInt("tip_id");
					if(tips.get(id) == null){
						Tip tip = new Tip(rs.getInt("tip_id"),rs.getString("tip_message"),rs.getInt("tip_minrank"),rs.getInt("tip_maxrank"),rs.getInt("tip_period"),rs.getString("tip_servers"),rs.getBoolean("tip_actionbar"));
						tips.put(id,tip);
					} else {
						tips.get(id).update(rs.getString("tip_message"),rs.getInt("tip_minrank"),rs.getInt("tip_maxrank"),rs.getInt("tip_period"),rs.getString("tip_servers"),rs.getBoolean("tip_actionbar"));
					}
				}
				rs.close();
			} catch (SQLException e){
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run(){
		loadTips();
		runTips();
	}

	public void runTips(){
		ArrayList<Tip> currentTips = new ArrayList<Tip>();
		for(Entry<Integer,Tip> entry : tips.entrySet()){
			if(entry.getValue().isRunnable()){
				currentTips.add(entry.getValue());
			}
		}
		if(!currentTips.isEmpty()){
			Random random = new Random();
			int index = random.nextInt(currentTips.size());
			currentTips.get(index).run();
			for(Tip tip : currentTips) tip.updateLastRun();
		}
	}

	public class Tip {
		int tip_id;
		int tip_minrank;
		int tip_maxrank;
		int tip_period;
		String tip_message;
		String [] tip_servers;
		boolean tip_actionbar;

		long lastRun = 0;
		boolean rightServer = false;

		public Tip(int tip_id,String tip_message,int tip_minrank,int tip_maxrank,int tip_period,String tip_servers,boolean tip_actionbar){
			this.tip_id = tip_id;
			this.tip_message = tip_message;
			this.tip_minrank = tip_minrank;
			this.tip_maxrank = tip_maxrank;
			this.tip_servers = tip_servers.split(";");
			this.tip_actionbar = tip_actionbar;
			this.lastRun = System.currentTimeMillis();
			for(String server : this.tip_servers){
				if(server.equalsIgnoreCase("global") || server.equalsIgnoreCase(plugin.serverName)) rightServer = true;
			}
		}

		public void update(String tip_message,int tip_minrank,int tip_maxrank,int tip_period,String tip_servers,boolean tip_actionbar){
			this.tip_message = tip_message;
			this.tip_minrank = tip_minrank;
			this.tip_maxrank = tip_maxrank;
			this.tip_period = tip_period;
			this.tip_servers = tip_servers.split(";");
			this.tip_actionbar = tip_actionbar;
			for(String server : this.tip_servers){
				if(server.equalsIgnoreCase("global") || server.equalsIgnoreCase(plugin.serverName)) rightServer = true;
			}
		}

		public boolean isRunnable(){
			return (rightServer && System.currentTimeMillis()-lastRun >= tip_period*1000);
		}

		public String parseMessage(){
			return RealCraft.parseColors(tip_message);
		}

		public void updateLastRun(){
			lastRun = System.currentTimeMillis();
		}

		public void run(){
			String message = this.parseMessage();
			for(Player player : plugin.getServer().getOnlinePlayers()){
				if(tip_minrank != 0 || tip_maxrank != 0){
					User user = Users.getUser(player);
					if(!user.getRank().isMinimum(UserRank.fromId(tip_minrank)) || (tip_maxrank != 0 && !user.getRank().isMaximum(UserRank.fromId(tip_maxrank)))) continue;
				}
				if(tip_actionbar == false) player.sendMessage(message);
				else {
					Title.sendActionBar(player,message,10*20);
				}
			}
		}
	}
}