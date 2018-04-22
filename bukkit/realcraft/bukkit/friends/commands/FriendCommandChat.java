package realcraft.bukkit.friends.commands;

import org.bukkit.entity.Player;

import realcraft.bukkit.friends.FriendNotices;
import realcraft.bukkit.friends.FriendPlayer;
import realcraft.bukkit.friends.FriendPlayerSettings.FriendPlayerSettingsType;
import realcraft.bukkit.friends.Friends;
import realcraft.bukkit.sockets.SocketData;
import realcraft.bukkit.sockets.SocketManager;

public class FriendCommandChat extends FriendCommand {

	public FriendCommandChat(){
		super("chat");
	}

	@Override
	public void perform(Player player,String[] args){
		FriendPlayer fPlayer = Friends.getFriendPlayer(player);
		if(!fPlayer.getSettings().getValue(FriendPlayerSettingsType.CHATS)){
			player.sendMessage("§cNemas povolene psani do soukromeho chatu.");
			return;
		}
		boolean enabled = fPlayer.toggleFriendChat();
		FriendNotices.showToggleFriendChat(fPlayer);
		SocketData data = new SocketData(Friends.CHANNEL_FRIEND_SETCHAT);
		data.setInt("id",fPlayer.getId());
		data.setBoolean("enabled",enabled);
		SocketManager.sendToAll(data);
	}
}