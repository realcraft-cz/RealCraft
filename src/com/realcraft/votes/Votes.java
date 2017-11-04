package com.realcraft.votes;

public class Votes {

}

/*public class Votes implements Listener, Runnable {

	private RealCraft plugin;
	private static final String VOTES = "votes";
	private static final String CHANNEL_CHECKVOTES = "voteCheckVotes";
	private static final String CHANNEL_PRINTVOTES = "votePrintVotes";
	private static final String CHANNEL_REMINDER = "voteReminder";
	private static final int REWARD = 20;
	private HashMap<String,Long> voteReminds = new HashMap<String,Long>();

	public Votes(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,this,60*20,60*20);
		if(RealCraft.getServerType() == ServerType.LOBBY) new VotifierEventClass(this);
	}

	public void onReload(){
	}

	@Override
	public void run(){
		if(RealCraft.getServerType() == ServerType.LOBBY || RealCraft.getServerType() == ServerType.SURVIVAL || RealCraft.getServerType() == ServerType.CREATIVE || RealCraft.getServerType() == ServerType.PARKOUR){
			for(Player player : Bukkit.getOnlinePlayers()){
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

	public void checkUnconfirmedVotes(){
		if(RealCraft.getServerType() == ServerType.LOBBY || RealCraft.getServerType() == ServerType.SURVIVAL || RealCraft.getServerType() == ServerType.CREATIVE || RealCraft.getServerType() == ServerType.PARKOUR){
			for(Player player : Bukkit.getOnlinePlayers()){
				this.checkPlayerVotes(player);
			}
		}
		if(RealCraft.getServerType() == ServerType.LOBBY){
			SocketData data = new SocketData(CHANNEL_CHECKVOTES);
			SocketManager.send(ServerType.SURVIVAL,data);
			SocketManager.send(ServerType.CREATIVE,data);
			SocketManager.send(ServerType.PARKOUR,data);
		}
	}

	public void checkPlayerVotes(Player player){
		int count = 0;
		ResultSet rs = RealCraft.getInstance().db.query("SELECT COUNT(*) AS rows FROM "+VOTES+" WHERE user_id = '"+PlayerManazer.getPlayerInfo(player).getId()+"' AND vote_confirmed = '0'");
		try {
			if(rs.next()){
				count = rs.getInt("rows");
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		if(count > 0){
			RealCraft.getInstance().db.update("UPDATE "+VOTES+" SET vote_confirmed = '"+(System.currentTimeMillis()/1000)+"' WHERE user_id = '"+PlayerManazer.getPlayerInfo(player).getId()+"' AND vote_confirmed = '0'");
			TextComponent component = new TextComponent("§3[Hlasovani] §fDekujeme hraci §6"+player.getName()+"§f za jeho "+(count > 1 ? "hlasy ("+count+"x)" : "hlas")+".");
			component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://www.realcraft.cz/hlasovat/"));
			component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§7Klikni pro hlasovani").create()));
			Bukkit.spigot().broadcast(component);
			SocketData data = new SocketData(CHANNEL_PRINTVOTES);
			data.setString("name",player.getName());
			data.setInt("count",count);
			SocketManager.sendToAll(data);
			if(RealCraft.isTestServer()){
				int reward = PlayerManazer.getPlayerInfo(player).giveCoins(REWARD*count);
				player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
				Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
					public void run(){
						PlayerManazer.getPlayerInfo(player).runCoinsEffect("§eOdmena za hlasovani",reward);
					}
				},20);
			}
		}
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				if(event.getPlayer().isOnline()) Votes.this.checkPlayerVotes(event.getPlayer());
			}
		},3*20);
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		if(RealCraft.getServerType() == ServerType.LOBBY || RealCraft.getServerType() == ServerType.SURVIVAL || RealCraft.getServerType() == ServerType.CREATIVE || RealCraft.getServerType() == ServerType.PARKOUR){
			SocketData data = event.getData();
			if(data.getChannel().equalsIgnoreCase(CHANNEL_REMINDER)){
				voteReminds.put(data.getString("name"),data.getLong("time"));
			}
			else if(data.getChannel().equalsIgnoreCase(CHANNEL_CHECKVOTES)){
				this.checkUnconfirmedVotes();
			}
			else if(data.getChannel().equalsIgnoreCase(CHANNEL_PRINTVOTES)){
				int count = data.getInt("count");
				TextComponent component = new TextComponent("§3[Hlasovani] §fDekujeme hraci §6"+data.getString("name")+"§f za jeho "+(count > 1 ? "hlasy ("+count+"x)" : "hlas")+".");
				component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://www.realcraft.cz/hlasovat/"));
				component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§7Klikni pro hlasovani").create()));
				Bukkit.spigot().broadcast(component);
			}
		}
	}

	public class VotifierEventClass implements Listener {

		public VotifierEventClass(Votes votes){
			plugin.getServer().getPluginManager().registerEvents(this,plugin);
		}

		@EventHandler(priority=EventPriority.MONITOR)
	    public void VotifierEvent(VotifierEvent event){
			Vote vote = event.getVote();
			if(vote.getUsername() != null && vote.getUsername().length() > 0){
				int id = 0;
				ResultSet rs = RealCraft.getInstance().db.query("SELECT user_id FROM authme WHERE user_name = '"+vote.getUsername()+"'");
				try {
					if(rs.next()){
						id = rs.getInt("user_id");
					}
					rs.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
				if(id != 0){
					try {
						PreparedStatement stmt;
						stmt = plugin.db.conn.prepareStatement("INSERT INTO "+VOTES+" (user_id,vote_created) VALUES(?,?)");
						stmt.setInt(1,id);
						stmt.setLong(2,Long.parseLong(vote.getTimeStamp()));
						stmt.executeUpdate();
					}
					catch (SQLException e){
						e.printStackTrace();
					}
					Votes.this.checkUnconfirmedVotes();
				}
			}
		}
	}
}*/