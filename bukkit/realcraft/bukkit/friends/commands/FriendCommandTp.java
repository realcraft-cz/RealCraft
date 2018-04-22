package realcraft.bukkit.friends.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import realcraft.bukkit.friends.FriendPlayer;
import realcraft.bukkit.friends.Friends;

public class FriendCommandTp extends FriendCommand {

	public FriendCommandTp(){
		super("tp","tpa");
	}

	@Override
	public void perform(Player player,String[] args){
		if(args.length == 0){
			player.sendMessage("Teleportovat k hraci");
			player.sendMessage("§6/friend tp §e<player>");
			return;
		}
		FriendPlayer sender = Friends.getFriendPlayer(player);
		FriendPlayer recipient = this.findFriendPlayer(sender,args[0]);
		if(recipient == null || !recipient.getUser().isLogged()){
			player.sendMessage("§cHrac nenalezen.");
			return;
		}
		if(!sender.hasFriend(recipient)){
			player.sendMessage("§cHrac nepatri mezi tve pratele.");
			return;
		}
		sender.teleportToFriend(recipient);
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