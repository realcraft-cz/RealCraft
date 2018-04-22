package realcraft.bukkit.friends;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.sockets.SocketData;
import realcraft.bukkit.sockets.SocketDataEvent;
import realcraft.bukkit.sockets.SocketManager;
import realcraft.bukkit.utils.StringUtil;

public class FriendRequests implements Listener {

	public static final long REQUEST_TIMEOUT_SECONDS = 300;

	private static final String CHANNEL_REQUEST_CREATE = "friendsRequestCreate";
	private static final String CHANNEL_REQUEST_ACCEPT = "friendsRequestAccept";
	private static final String CHANNEL_REQUEST_DENY = "friendsRequestDeny";

	public FriendRequests(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		FriendPlayer friendPlayer = Friends.getFriendPlayer(event.getPlayer());
		Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				for(FriendsRequest request : friendPlayer.getRequests()){
					if(request.getRecipient().getId() == friendPlayer.getId() && !request.isExpired()){
						FriendNotices.showRequestToRecipient(friendPlayer,request);
					}
				}
			}
		},2*20);
	}

	public static void sendRequest(FriendPlayer sender,FriendPlayer recipient){
		FriendsRequest request = new FriendsRequest(sender,recipient);
		DB.update("INSERT INTO "+Friends.FRIENDS_REQUESTS+" (user_id1,user_id2,request_created) VALUES('"+sender.getId()+"','"+recipient.getId()+"','"+(request.getCreated()/1000)+"')");
		SocketData data = new SocketData(CHANNEL_REQUEST_CREATE);
		data.setString("request",request.toString());
		SocketManager.sendToAll(data,true);
	}

	public static void acceptRequest(FriendsRequest request){
		request.getRecipient().addFriend(request.getSender());
		request.remove();
		SocketData data = new SocketData(CHANNEL_REQUEST_ACCEPT);
		data.setString("request",request.toString());
		SocketManager.sendToAll(data,true);
	}

	public static void denyRequest(FriendsRequest request){
		request.remove();
		SocketData data = new SocketData(CHANNEL_REQUEST_DENY);
		data.setString("request",request.toString());
		SocketManager.sendToAll(data,true);
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(CHANNEL_REQUEST_CREATE)){
			FriendsRequest request = FriendsRequest.fromString(data.getString("request"));
			request.getSender().reload();
			request.getRecipient().reload();
			FriendNotices.showRequestToSender(request.getSender(),request);
			FriendNotices.showRequestToRecipient(request.getRecipient(),request);
		}
		else if(data.getChannel().equalsIgnoreCase(CHANNEL_REQUEST_ACCEPT)){
			FriendsRequest request = FriendsRequest.fromString(data.getString("request"));
			request.getSender().reload();
			request.getRecipient().reload();
			FriendNotices.showRequestAcceptToSender(request.getSender(),request);
			FriendNotices.showRequestAcceptToRecipient(request.getRecipient(),request);
		}
		else if(data.getChannel().equalsIgnoreCase(CHANNEL_REQUEST_DENY)){
			FriendsRequest request = FriendsRequest.fromString(data.getString("request"));
			request.getSender().reload();
			request.getRecipient().reload();
			FriendNotices.showRequestDenyToSender(request.getSender(),request);
			FriendNotices.showRequestDenyToRecipient(request.getRecipient(),request);
		}
	}

	public static class FriendsRequest {

		private FriendPlayer sender;
		private FriendPlayer recipient;
		private Long created;

		public FriendsRequest(FriendPlayer sender,FriendPlayer recipient){
			this(sender,recipient,System.currentTimeMillis());
		}

		public FriendsRequest(FriendPlayer sender,FriendPlayer recipient,long created){
			this.sender = sender;
			this.recipient = recipient;
			this.created = created;
		}

		public FriendPlayer getSender(){
			return sender;
		}

		public FriendPlayer getRecipient(){
			return recipient;
		}

		public Long getCreated(){
			return created;
		}

		public boolean isExpired(){
			return (this.getCreated()+(FriendRequests.REQUEST_TIMEOUT_SECONDS*1000) < System.currentTimeMillis());
		}

		public long getRemains(){
			return ((this.getCreated()+(FriendRequests.REQUEST_TIMEOUT_SECONDS*1000)-System.currentTimeMillis())/1000);
		}

		public String getExpireText(){
			int minutes = (int)Math.ceil(this.getRemains()/60f);
			return (this.getRemains() <= 60 ? this.getRemains()+" sekund" : (minutes)+" "+StringUtil.inflect(minutes,new String[]{"minuty","minut","minut"}));
		}

		public void remove(){
			DB.update("DELETE FROM "+Friends.FRIENDS_REQUESTS+" WHERE user_id1 = '"+sender.getId()+"' AND user_id2 = '"+recipient.getId()+"'");
		}

		@Override
		public String toString(){
			JsonObject objects = new JsonObject();
			objects.addProperty("sender",sender.getId());
			objects.addProperty("recipient",recipient.getId());
			objects.addProperty("created",created);
			return objects.toString();
		}

		public static FriendsRequest fromString(String data){
			JsonElement element = new JsonParser().parse(data);
			if(element.isJsonObject()){
				JsonObject objects = element.getAsJsonObject();
				FriendPlayer sender = Friends.getFriendPlayer(objects.get("sender").getAsInt());
				FriendPlayer recipient = Friends.getFriendPlayer(objects.get("recipient").getAsInt());
				long created = objects.get("created").getAsLong();
				return new FriendsRequest(sender,recipient,created);
			}
			return null;
		}
	}
}