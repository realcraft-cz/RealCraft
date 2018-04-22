package realcraft.bukkit.friends.commands;

import org.bukkit.entity.Player;

import realcraft.bukkit.friends.FriendList;
import realcraft.bukkit.friends.FriendPlayer;
import realcraft.bukkit.friends.Friends;

public class FriendCommandList extends FriendCommand {

	public FriendCommandList(){
		super("list");
	}

	@Override
	public void perform(Player player,String[] args){
		FriendPlayer fPlayer = Friends.getFriendPlayer(player);
		if(fPlayer.getFriends().size() == 0){
			player.sendMessage("§cNemas zadne pratele.");
			return;
		}
		FriendList.openList(fPlayer);
	}
}