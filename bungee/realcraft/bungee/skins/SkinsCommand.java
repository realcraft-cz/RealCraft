package realcraft.bungee.skins;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import realcraft.bungee.skins.exceptions.SkinsLimitException;
import realcraft.bungee.skins.exceptions.SkinsNotFoundException;
import realcraft.bungee.skins.utils.StringUtil;

public class SkinsCommand extends Command {

	public SkinsCommand(){
		super("skin","","skinset","setskin","skins");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender,String[] args){
		if(sender instanceof ProxiedPlayer){
			ProxiedPlayer player = (ProxiedPlayer) sender;
			if(args.length != 1){
				player.sendMessage("§e[Skins] §fZmenit skin");
				player.sendMessage("§e[Skins] §f/skin <name>");
			} else {
				ProxyServer.getInstance().getScheduler().runAsync(Skins.plugin,new Runnable(){
					@Override
					public void run(){
						try {
							Skins.changeSkin(player,args[0]);
							player.sendMessage("§e[Skins] §aSkin nastaven.");
						} catch (SkinsLimitException e){
							player.sendMessage("§e[Skins] §cDalsi zmenu skinu je mozne provest za "+e.getRemainingSeconds()+" "+StringUtil.inflect(e.getRemainingSeconds(),new String[]{"sekundu","sekundy","sekund"})+".");
						} catch (SkinsNotFoundException e){
							player.sendMessage("§e[Skins] §cSkin nenalezen.");
						}
					}
				});
			}
		}
	}
}