package realcraft.bukkit.fights.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.fights.Fights;
import realcraft.bukkit.fights.duels.FightDuelsRequests;
import realcraft.share.users.User;
import realcraft.share.users.Users;

public class FightCommandDuel extends FightCommand {

	public FightCommandDuel(){
		super("duel");
	}

	@Override
	public void perform(Player player,String[] args){
		if(args.length == 0){
			player.sendMessage("Vyzvat hrace na duel");
			player.sendMessage("§6/fight duel §e<player>");
			return;
		}
		FightPlayer sender = Fights.getFightPlayer(player);
		FightPlayer recipient = this.findFriendPlayer(sender,args[0]);
		if(recipient == null){
			player.sendMessage("§cHrac nenalezen.");
			return;
		}
		FightDuelsRequests.sendRequest(sender,recipient);
	}

	@Override
	public List<String> onTabComplete(Player player,String name){
		FightPlayer fPlayer = Fights.getFightPlayer(player);
		List<String> players = new ArrayList<String>();
		for(User user : Users.getOnlineUsers()){
			if(!user.equals(fPlayer.getUser())){
				if(user.getName().toLowerCase().startsWith(name.toLowerCase())) players.add(user.getName());
			}
		}
		return players;
	}
}