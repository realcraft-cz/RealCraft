package realcraft.bukkit.friends;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.sockets.SocketData;
import realcraft.bukkit.sockets.SocketDataEvent;
import realcraft.bukkit.sockets.SocketManager;

public class FriendChat implements Listener {

	private static final String CHANNEL_FRIEND_CHAT = "friendsFriendChat";

	public FriendChat(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler(priority = EventPriority.NORMAL,ignoreCancelled=true)
	public void AsyncPlayerChatEvent(AsyncPlayerChatEvent event){
		FriendPlayer fPlayer = Friends.getFriendPlayer(event.getPlayer());
		if(fPlayer.hasFriendChat()){
			event.setCancelled(true);
			String message = event.getMessage();
			RealCraft.getInstance().chatlog.onPlayerChat(event.getPlayer(),message);
			SocketData data = new SocketData(CHANNEL_FRIEND_CHAT);
			data.setInt("sender",fPlayer.getId());
			data.setString("message",message);
			SocketManager.sendToAll(data,true);
		}
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(CHANNEL_FRIEND_CHAT)){
			FriendPlayer fPlayer = Friends.getFriendPlayer(data.getInt("sender"));
			String message = data.getString("message");
			FriendNotices.showFriendChat(fPlayer,fPlayer,message);
			for(FriendPlayer friend : fPlayer.getFriends()){
				FriendNotices.showFriendChat(friend,fPlayer,message);
			}
		}
	}
}