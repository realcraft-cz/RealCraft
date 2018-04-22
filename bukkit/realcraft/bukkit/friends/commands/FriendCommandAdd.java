package realcraft.bukkit.friends.commands;

import org.bukkit.entity.Player;

import realcraft.bukkit.friends.FriendPlayer;
import realcraft.bukkit.friends.FriendPlayerSettings.FriendPlayerSettingsType;
import realcraft.bukkit.friends.FriendRequests;
import realcraft.bukkit.friends.FriendRequests.FriendsRequest;
import realcraft.bukkit.friends.Friends;

public class FriendCommandAdd extends FriendCommand {

	public FriendCommandAdd(){
		super("add");
	}

	@Override
	public void perform(Player player,String[] args){
		if(args.length == 0){
			player.sendMessage("Odeslat zadost o pratelstvi");
			player.sendMessage("§6/friend add §e<player>");
			return;
		}
		FriendPlayer sender = Friends.getFriendPlayer(player);
		FriendPlayer recipient = this.findFriendPlayer(sender,args[0]);
		if(recipient == null){
			player.sendMessage("§cHrac nenalezen.");
			return;
		}
		if(sender.hasFriend(recipient)){
			player.sendMessage("§cTento hrac jiz patri mezi tve pratele.");
			return;
		}
		if(!recipient.getSettings().getValue(FriendPlayerSettingsType.REQUESTS)){
			player.sendMessage("§cTento hrac neprijima zadosti o pratelstvi.");
			return;
		}
		for(FriendsRequest request : sender.getRequests()){
			if(request.getRecipient().getId() == recipient.getId() && request.getCreated()+(FriendRequests.REQUEST_TIMEOUT_SECONDS*1000) > System.currentTimeMillis()){
				player.sendMessage("§cZadost o pratelstvi byla jiz odeslana.");
				return;
			}
		}
		if(sender.getFriends().size() >= sender.getMaxFriends()){
			player.sendMessage("§cMuzes mit pouze "+sender.getMaxFriends()+" pratel.");
			return;
		}
		FriendRequests.sendRequest(sender,recipient);
	}
}