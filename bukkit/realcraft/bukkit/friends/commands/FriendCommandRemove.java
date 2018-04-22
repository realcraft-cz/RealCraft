package realcraft.bukkit.friends.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import realcraft.bukkit.friends.FriendNotices;
import realcraft.bukkit.friends.FriendPlayer;
import realcraft.bukkit.friends.Friends;
import realcraft.bukkit.sockets.SocketData;
import realcraft.bukkit.sockets.SocketManager;

public class FriendCommandRemove extends FriendCommand {

	public FriendCommandRemove(){
		super("remove","delete");
	}

	@Override
	public void perform(Player player,String[] args){
		if(args.length == 0){
			player.sendMessage("Odebrat hrace z pratel");
			player.sendMessage("§6/friend remove §e<player>");
			return;
		}
		FriendPlayer sender = Friends.getFriendPlayer(player);
		FriendPlayer recipient = this.findFriendPlayer(sender,args[0]);
		if(recipient == null){
			player.sendMessage("§cHrac nenalezen.");
			return;
		}
		if(!sender.hasFriend(recipient)){
			player.sendMessage("§cHrac nepatri mezi tve pratele.");
			return;
		}
		sender.removeFriend(recipient);
		SocketData data = new SocketData(FriendNotices.CHANNEL_FRIEND_REMOVE);
		data.setInt("sender",sender.getId());
		data.setInt("recipient",recipient.getId());
		SocketManager.sendToAll(data,true);
	}

	@Override
	public List<String> onTabComplete(Player player,String name){
		FriendPlayer fPlayer = Friends.getFriendPlayer(player);
		List<String> players = new ArrayList<String>();
		for(FriendPlayer friend : fPlayer.getFriends()){
			if(friend.getUser().getName().toLowerCase().startsWith(name.toLowerCase())) players.add(friend.getUser().getName());
		}
		return players;
	}
}