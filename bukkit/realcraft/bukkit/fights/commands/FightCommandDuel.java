package realcraft.bukkit.fights.commands;

import org.bukkit.entity.Player;
import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.fights.Fights;
import realcraft.bukkit.fights.duels.FightDuelsRequests;
import realcraft.bukkit.fights.duels.FightDuelsRequests.FightDuelRequest;

import java.util.ArrayList;
import java.util.List;

public class FightCommandDuel extends FightCommand {

	public FightCommandDuel(){
		super("duel");
}

	@Override
	public void perform(Player player,String[] args){
		if(args.length == 0){
			player.sendMessage("Vyzvat hrace na souboj");
			player.sendMessage("§6/fight duel §e<player>");
			return;
		}
		FightPlayer sender = Fights.getFightPlayer(player);
		FightPlayer recipient = this.findFriendPlayer(sender,args[0]);
		if(recipient == null){
			player.sendMessage("§cHrac nenalezen.");
			return;
		}
		for(FightDuelRequest request : sender.getRequests()){
			if(request.getRecipient().equals(recipient) && !request.isExpired()){
				player.sendMessage("§cVyzva o souboj byla jiz odeslana.");
				return;
			}
		}
		FightDuelsRequests.sendRequest(sender,recipient);
	}

	@Override
	public List<String> onTabComplete(Player player,String name){
		FightPlayer fPlayer = Fights.getFightPlayer(player);
		List<String> players = new ArrayList<String>();
		for(FightPlayer fPlayer2 : Fights.getOnlineFightPlayers()){
			if(!fPlayer2.equals(fPlayer)){
				if(fPlayer2.getUser().getName().toLowerCase().startsWith(name.toLowerCase())) players.add(fPlayer2.getUser().getName());
			}
		}
		return players;
	}
}