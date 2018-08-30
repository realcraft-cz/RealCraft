package realcraft.bukkit.survival.economy.commands;

import org.bukkit.entity.Player;
import realcraft.bukkit.survival.economy.Economy;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.others.AbstractCommand;

import java.util.List;

public class EconomyCommandBalance extends AbstractCommand {

	public EconomyCommandBalance(){
		super("balance","bal","ebal","ebalance","money","emoney","penize");
	}

	@Override
	public void perform(Player player,String[] args){
		player.sendMessage("§fPenize: §a"+Economy.format(Users.getUser(player).getMoney()));
	}

	@Override
	public List<String> tabCompleter(Player player,String[] args){
		return null;
	}
}