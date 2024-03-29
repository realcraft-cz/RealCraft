package realcraft.bukkit.friends.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import realcraft.bukkit.friends.FriendPlayer;
import realcraft.bukkit.friends.FriendRequests;
import realcraft.bukkit.friends.FriendRequests.FriendsRequest;
import realcraft.bukkit.friends.Friends;

public class FriendCommandAccept extends FriendCommand {

	public FriendCommandAccept(){
		super("accept","yes");
	}

	@Override
	public void perform(Player player,String[] args){
		if(args.length == 0){
			player.sendMessage("Prijmout zadost od hrace");
			player.sendMessage("�6/friend accept �e<player>");
			return;
		}
		FriendPlayer fPlayer = Friends.getFriendPlayer(player);
		FriendPlayer sender = this.findFriendPlayer(fPlayer,args[0]);
		if(sender == null){
			player.sendMessage("�cHrac nenalezen.");
			return;
		}
		if(fPlayer.hasFriend(sender)){
			player.sendMessage("�cTento hrac jiz patri mezi tve pratele.");
			return;
		}
		if(fPlayer.getFriends().size() >= fPlayer.getMaxFriends()){
			player.sendMessage("�cMuzes mit pouze "+fPlayer.getMaxFriends()+" pratel.");
			return;
		}
		for(FriendsRequest request : sender.getRequests()){
			if(request.getSender().equals(sender) && !request.isExpired()){
				FriendRequests.acceptRequest(request);
				return;
			}
		}
		player.sendMessage("�cOd tohoto hrace nemas zadost o pratelstsvi.");
	}

	@Override
	public List<String> onTabComplete(Player player,String name){
		FriendPlayer fPlayer = Friends.getFriendPlayer(player);
		List<String> players = new ArrayList<String>();
		for(FriendsRequest request : fPlayer.getRequests()){
			if(!request.getSender().equals(fPlayer) && !request.isExpired()){
				if(request.getSender().getUser().getName().toLowerCase().startsWith(name.toLowerCase())) players.add(request.getSender().getUser().getName());
			}
		}
		return players;
	}
}