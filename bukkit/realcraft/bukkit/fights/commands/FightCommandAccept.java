package realcraft.bukkit.fights.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.fights.Fights;
import realcraft.bukkit.fights.duels.FightDuelsRequests;
import realcraft.bukkit.fights.duels.FightDuelsRequests.FightDuelRequest;

public class FightCommandAccept extends FightCommand {

	public FightCommandAccept(){
		super("accept");
	}

	@Override
	public void perform(Player player,String[] args){
		if(args.length == 0){
			player.sendMessage("Prijmout vyzvu od hrace");
			player.sendMessage("§6/fight accept §e<player>");
			return;
		}
		FightPlayer sender = Fights.getFightPlayer(player);
		if(sender.getDuel() != null){
			player.sendMessage("§cPrave jsi v probihajicim duelu.");
			return;
		}
		FightPlayer recipient = this.findFriendPlayer(sender,args[0]);
		if(recipient == null){
			player.sendMessage("§cHrac nenalezen.");
			return;
		}
		for(FightDuelRequest request : sender.getRequests()){
			if(request.getSender().equals(recipient) && !request.isExpired()){
				if(request.getSender().getDuel() != null){
					player.sendMessage("§cHrac je v probihajicim duelu.");
					return;
				}
				FightDuelsRequests.acceptRequest(request);
				return;
			}
		}
		player.sendMessage("§cOd tohoto hrace nemas zadnou vyzvu.");
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