package realcraft.bukkit.friends;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.friends.FriendPlayerSettings.FriendPlayerSettingsType;
import realcraft.bukkit.friends.FriendRequests.FriendsRequest;
import realcraft.bukkit.sockets.SocketData;
import realcraft.bukkit.sockets.SocketManager;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.BungeeMessages;
import realcraft.bukkit.utils.DateUtil;
import realcraft.bukkit.utils.ItemUtil;
import realcraft.share.ServerType;
import realcraft.share.users.User;
import realcraft.share.users.UserRank;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FriendPlayer {

	private int id;
	private ItemStack item;
	private Player player;

	private FriendPlayerSettings settings;

	private ArrayList<FriendPlayer> friends = new ArrayList<FriendPlayer>();
	private ArrayList<FriendsRequest> requests = new ArrayList<FriendsRequest>();

	private FriendPlayerTeleport friendTeleport;

	private boolean friendChat = false;

	public FriendPlayer(int id){
		this.id = id;
	}

	public void reload(){
		this.loadFriends();
		this.loadRequests();
		this.getSettings().reload();
	}

	public void sendReload(){
		SocketData data = new SocketData(Friends.CHANNEL_FRIEND_RELOAD);
		data.setInt("id",this.getId());
		SocketManager.sendToAll(data);
	}

	public int getId(){
		return id;
	}

	public FriendPlayerSettings getSettings(){
		if(settings == null){
			settings = new FriendPlayerSettings(this);
			settings.reload();
		}
		return settings;
	}

	public Player getPlayer(){
		if(player == null || !player.isOnline() || !player.isValid()){
			player = Users.getPlayer(this.getUser());
		}
		return player;
	}

	public User getUser(){
		return Users.getUser(this.getId());
	}

	public FriendPlayerTeleport getFriendTeleport(){
		if(friendTeleport != null && (friendTeleport.created+2000 < System.currentTimeMillis() || friendTeleport.friend.getPlayer() == null)) friendTeleport = null;
		return friendTeleport;
	}

	public void setFriendTeleport(FriendPlayer friend){
		friendTeleport = new FriendPlayerTeleport(friend);
	}

	public boolean hasFriendChat(){
		return friendChat;
	}

	public boolean toggleFriendChat(){
		friendChat = !friendChat;
		return friendChat;
	}

	public void setFriendChat(boolean friendChat){
		this.friendChat = friendChat;
	}

	public int getMaxFriends(){
		return (this.getUser().getRank().isMinimum(UserRank.VIP) ? Friends.MAX_FRIENDS : 7);
	}

	public ArrayList<FriendPlayer> getFriends(){
		return friends;
	}

	public ArrayList<FriendPlayer> getOrderedFriends(){
		Collections.sort(friends,new Comparator<FriendPlayer>(){
			@Override
			public int compare(FriendPlayer player1,FriendPlayer player2){
				int compare = Long.compare(player1.getUser().getLastLogged(),player2.getUser().getLastLogged());
				if(compare > 0) return -1;
				else if(compare < 0) return 1;
				return 0;
			}
		});
		return friends;
	}

	public boolean hasFriend(FriendPlayer fPlayer){
		return friends.contains(fPlayer);
	}

	public void addFriend(FriendPlayer fPlayer){
		DB.update("DELETE FROM "+Friends.FRIENDS+" WHERE (user_id1 = '"+this.getId()+"' AND user_id2 = '"+fPlayer.getId()+"') OR (user_id1 = '"+fPlayer.getId()+"' AND user_id2 = '"+this.getId()+"')");
		DB.update("INSERT INTO "+Friends.FRIENDS+" (user_id1,user_id2) VALUES('"+this.getId()+"','"+fPlayer.getId()+"')");
		friends.add(fPlayer);
	}

	public void removeFriend(FriendPlayer fPlayer){
		DB.update("DELETE FROM "+Friends.FRIENDS+" WHERE (user_id1 = '"+this.getId()+"' AND user_id2 = '"+fPlayer.getId()+"') OR (user_id1 = '"+fPlayer.getId()+"' AND user_id2 = '"+this.getId()+"')");
		friends.remove(fPlayer);
	}

	private void loadFriends(){
		friends.clear();
		ResultSet rs = DB.query("SELECT * FROM "+Friends.FRIENDS+" WHERE user_id1 = '"+this.getId()+"' OR user_id2 = '"+this.getId()+"'");
		try {
			while(rs.next()){
				if(rs.getInt("user_id1") == this.getId()) friends.add(Friends.getFriendPlayer(rs.getInt("user_id2")));
				else friends.add(Friends.getFriendPlayer(rs.getInt("user_id1")));
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	public ArrayList<FriendsRequest> getRequests(){
		return requests;
	}

	private void loadRequests(){
		requests.clear();
		ResultSet rs = DB.query("SELECT * FROM "+Friends.FRIENDS_REQUESTS+" WHERE user_id1 = '"+this.getId()+"' OR user_id2 = '"+this.getId()+"'");
		try {
			while(rs.next()){
				FriendPlayer sender = Friends.getFriendPlayer(rs.getInt("user_id1"));
				FriendPlayer recipient = Friends.getFriendPlayer(rs.getInt("user_id2"));
				long created = rs.getLong("request_created")*1000;
				FriendsRequest request = new FriendsRequest(sender,recipient,created);
				if(!request.isExpired()) requests.add(request);
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	public ItemStack getItemStack(FriendPlayer fPlayer){
		if(item == null) item = ItemUtil.getHead(this.getUser().getSkin().getValue());
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setDisplayName((this.getUser().getRank().isMinimum(UserRank.VIP) ? this.getUser().getRank().getChatColor()+""+ChatColor.BOLD+this.getUser().getRank().getName()+" " : "")+ChatColor.RESET+ChatColor.BOLD+this.getUser().getName());
		ArrayList<String> lore = new ArrayList<String>();
		lore.add((this.getUser().isLogged() ? "§fHraje na serveru "+this.getUser().getServer().getColor()+ChatColor.BOLD+this.getUser().getServer().getName() : "§7Aktivni "+DateUtil.lastTime((int)this.getUser().getLastLogged(),true)));
		if(this.getUser().isLogged()){
			if(this.getPlayer() == null){
				if((this.getUser().getServer() == ServerType.LOBBY || this.getUser().getServer() == ServerType.SURVIVAL || this.getUser().getServer() == ServerType.CREATIVE) && this.getSettings().getValue(FriendPlayerSettingsType.TELEPORTS)){
					lore.add("§7Klikni pro teleportaci k hraci");
				} else {
					lore.add("§7Klikni pro pripojeni na server");
				}
			}
			else if((RealCraft.getServerType() == ServerType.LOBBY || RealCraft.getServerType() == ServerType.SURVIVAL || RealCraft.getServerType() == ServerType.CREATIVE) && this.getSettings().getValue(FriendPlayerSettingsType.TELEPORTS)){
				lore.add("§7Klikni pro teleportaci k hraci");
			}
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public void teleportToFriend(FriendPlayer friend){
		if(friend.getPlayer() == null){
			if((friend.getUser().getServer() == ServerType.LOBBY || friend.getUser().getServer() == ServerType.SURVIVAL || friend.getUser().getServer() == ServerType.CREATIVE || friend.getUser().getServer() == ServerType.MAPS) && friend.getSettings().getValue(FriendPlayerSettingsType.TELEPORTS)){
				SocketData data = new SocketData(FriendList.CHANNEL_TELEPORT);
				data.setInt("player",this.getId());
				data.setInt("friend",friend.getId());
				SocketManager.send(friend.getUser().getServer(),data);
			}
			Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					BungeeMessages.connectPlayerToServer(FriendPlayer.this.getPlayer(),friend.getUser().getServer());
				}
			},5);

		}
		else if(RealCraft.getServerType() == ServerType.LOBBY || RealCraft.getServerType() == ServerType.SURVIVAL || RealCraft.getServerType() == ServerType.CREATIVE || RealCraft.getServerType() == ServerType.MAPS){
			if(friend.getSettings().getValue(FriendPlayerSettingsType.TELEPORTS)){
				this.getPlayer().teleport(friend.getPlayer().getLocation());
				FriendNotices.showFriendTeleport(friend,this);
			}
			else this.getPlayer().sendMessage("§cHrac ma teleportaci zakazanou.");
		}
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof FriendPlayer){
			FriendPlayer toCompare = (FriendPlayer) object;
			return (toCompare.getId() == this.getId());
		}
		return false;
	}

	public class FriendPlayerTeleport {

		public FriendPlayer friend;
		public long created;

		public FriendPlayerTeleport(FriendPlayer friend){
			this.friend = friend;
			this.created = System.currentTimeMillis();
		}
	}
}