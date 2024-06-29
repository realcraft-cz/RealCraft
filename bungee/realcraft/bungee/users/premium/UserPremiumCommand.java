package realcraft.bungee.users.premium;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import realcraft.bungee.RealCraftBungee;
import realcraft.bungee.users.Users;

public class UserPremiumCommand extends Command {

	public UserPremiumCommand() {
		super("premium","");
		RealCraftBungee.getInstance().getProxy().getPluginManager().registerCommand(RealCraftBungee.getInstance(), this);
	}

	@Override
	public void execute(CommandSender sender,String[] args){
		if(sender instanceof ProxiedPlayer){
			ProxiedPlayer player = (ProxiedPlayer) sender;
			if (Users.getUser(player).isPremium()) {
				player.sendMessage("§cPremium prihlaseni mas jiz aktivni");
				return;
			}

			player.sendMessage("");
			player.sendMessage("§e§lPremium prihlaseni §a§laktivovane.");
			player.sendMessage("§fNyni se odpoj a znovu pripoj na server.");
			player.sendMessage("§7Pokud se nepripojis do 5 minut,");
			player.sendMessage("§7bude premium prihlaseni deaktivovane.");

			Users.getUser(player).setPremiumAttempt((System.currentTimeMillis() / 1000) + 60 * 5);
		}
	}
}
