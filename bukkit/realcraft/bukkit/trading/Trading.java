package realcraft.bukkit.trading;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realcraft.bukkit.RealCraft;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Trading implements CommandExecutor {
	RealCraft plugin;
	private static HashMap<Player,Trade> tradingPlayers = new HashMap<Player,Trade>();
	private static HashMap<Player,Long> lastTradeRequest = new HashMap<Player,Long>();
	public static final long REQUEST_TIMEOUT_SECONDS = 15;
	public static final long REQUEST_WAIT_SECONDS = 60;

	public Trading(RealCraft realcraft){
		plugin = realcraft;
		plugin.getCommand("trade").setExecutor(this);
	}

	public void onReload(){
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("trade")){
			if(args.length == 0){
				player.sendMessage("§e----- §a§lTRADE §e----------------------------");
				player.sendMessage("§6/trade §e<player> §f- Odeslat zadost o obchodovani");
				player.sendMessage("§6/trade accept §f- Prijmout zadost");
				player.sendMessage("§6/trade deny §f- Odmitnout zadost");
				player.sendMessage("§6/trade ignore §e<player> §f- Ignorovat zadosti od hrace");
				return true;
			}
			else if(args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("yes")){
				Trade trade = Trading.getPlayerTrade(player);
				if(trade != null) trade.accept(player);
				else player.sendMessage("§cNemas zadnou zadost o obchodovani.");
			}
			else if(args[0].equalsIgnoreCase("deny") || args[0].equalsIgnoreCase("no")){
				Trade trade = Trading.getPlayerTrade(player);
				if(trade != null) trade.deny(player);
				else player.sendMessage("§cNemas zadnou zadost o obchodovani.");
			}
			else if(args[0].equalsIgnoreCase("ignore")){
				if(args.length == 1){
					player.sendMessage("Ignorovat zadosti o obchodovani od hrace:");
					player.sendMessage("§6/trade ignore §e<player>");
					return true;
				}
				Player victim = plugin.getServer().getPlayer(args[1]);
				if(victim == null || player == victim){
					player.sendMessage("§cHrac nenalezen.");
					return true;
				}
				boolean ignored = Trading.togglePlayerIgnore(player,victim);
				if(ignored) player.sendMessage("§6Zadosti od hrace "+victim.getDisplayName()+" §czakazany§6.");
				else player.sendMessage("§6Zadosti od hrace "+victim.getDisplayName()+" §apovoleny§6.");
			}
			else {
				Player victim = plugin.getServer().getPlayer(args[0]);
				new Trade(this,player,victim);
			}
		}
		return true;
	}

	public static void addPlayer(Player player,Trade trade){
		tradingPlayers.put(player,trade);
	}

	public static void removePlayer(Player player){
		tradingPlayers.remove(player);
	}

	public static Trade getPlayerTrade(Player player){
		return tradingPlayers.get(player);
	}

	public static boolean isPlayerTrading(Player player){
		return tradingPlayers.containsKey(player);
	}

	public static void setLastTradeRequest(Player player){
		lastTradeRequest.put(player,System.currentTimeMillis());
	}

	public static long getLastTradeRequest(Player player){
		return lastTradeRequest.get(player);
	}

	public static boolean isPlayerAbleToTradeRequest(Player player){
		return (lastTradeRequest.get(player) == null || lastTradeRequest.get(player)+(REQUEST_WAIT_SECONDS*1000) < System.currentTimeMillis());
	}

	public static boolean togglePlayerIgnore(Player player,Player victim){
		PermissionUser user = PermissionsEx.getUser(player);
		String option = user.getOption("trading."+victim.getName());
		if(option != null && option.equalsIgnoreCase("true")){
			user.setOption("trading."+victim.getName(),"false");
			return false;
		}
		user.setOption("trading."+victim.getName(),"true");
		return true;
	}

	public static boolean isPlayerIgnorePlayer(Player player,Player victim){
		PermissionUser user = PermissionsEx.getUser(player);
		String option = user.getOption("trading."+victim.getName());
		return (option != null && option.equalsIgnoreCase("true"));
	}
}