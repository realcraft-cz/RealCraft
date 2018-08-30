package realcraft.bukkit.survival.economy;

import realcraft.bukkit.survival.economy.commands.EconomyCommandBalance;
import realcraft.bukkit.survival.economy.commands.EconomyCommandPay;
import realcraft.bukkit.others.AbstractCommand;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class Economy {

	private static final NumberFormat PRETTY_FORMAT = NumberFormat.getInstance(Locale.US);
	static {
		PRETTY_FORMAT.setRoundingMode(RoundingMode.FLOOR);
		PRETTY_FORMAT.setGroupingUsed(true);
	}

	public Economy(){
		new EconomyCommandBalance();
		new EconomyCommandPay();
		new AbstractCommand.NullCommand("eco");
		new AbstractCommand.NullCommand("balancetop","ebalancetop","baltop","ebaltop");
	}

	public static String format(int amount){
		return (amount < 0 ? "-" : "")+"$"+PRETTY_FORMAT.format(Math.abs(amount));
	}

	public static String format(double amount){
		return (amount < 0 ? "-" : "")+"$"+PRETTY_FORMAT.format(Math.abs(round(amount)));
	}

	private static double round(double amount){
		return (double)Math.round((amount*100))/100;
	}
}