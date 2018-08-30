package realcraft.bukkit.survival.economy.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import realcraft.bukkit.survival.economy.Economy;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.others.AbstractCommand;

import java.util.ArrayList;
import java.util.List;

public class EconomyCommandPay extends AbstractCommand {

	public EconomyCommandPay(){
		super("pay","epay");
	}

	@Override
	public void perform(Player player,String[] args){
		if(args.length < 2){
			player.sendMessage("Zaslat penize hraci:");
			player.sendMessage("/pay <player> <amount>");
			return;
		}
		Player target = Bukkit.getPlayer(args[0]);
		if(target == null || player.getName().equalsIgnoreCase(target.getName())){
			player.sendMessage("§cHrac nenalezen.");
			return;
		}
		int amount;
		try {
			amount = Integer.valueOf(args[1]);
		} catch (NumberFormatException e){
			player.sendMessage("§cZadej cele cislo.");
			return;
		}
		if(amount < 1 || Users.getUser(player).getMoney() < amount){
			player.sendMessage("§cNemas dostatek penez.");
			return;
		}
		Users.getUser(player).addMoney(-amount);
		Users.getUser(target).addMoney(amount);
		player.sendMessage("§fZaslal jsi §a"+Economy.format(amount)+"§r hraci §6"+target.getName());
		target.sendMessage("§fObdrzel jsi §a"+Economy.format(amount)+"§r od hrace §6"+player.getName());
	}

	@Override
	public List<String> tabCompleter(Player player,String[] args){
		if(args.length == 2) return new ArrayList<>();
		return this.getPlayersCompletions();
	}
}