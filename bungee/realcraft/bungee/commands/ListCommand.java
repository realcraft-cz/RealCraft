package realcraft.bungee.commands;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import realcraft.bungee.RealCraftBungee;
import realcraft.bungee.users.Users;
import realcraft.share.users.UserRank;

public class ListCommand extends Command {
	RealCraftBungee plugin;

	public ListCommand(RealCraftBungee plugin){
		super("glist","","list","players");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender,String[] args){
		if(sender instanceof ProxiedPlayer){
			ProxiedPlayer cmdsender = (ProxiedPlayer) sender;
			boolean isAdmin = Users.getUser(cmdsender).getRank().isMinimum(UserRank.ADMIN);
			Map<String,ServerInfo> servers = plugin.getProxy().getServers();
			int players = 0;
			for(Entry<String, ServerInfo> entry : servers.entrySet()){
				ArrayList<BaseComponent> messages = new ArrayList<BaseComponent>();
				ServerInfo server = entry.getValue();
				messages.add(new TextComponent("§a["+server.getName()+"] §e("+server.getPlayers().size()+"): "));
				int index = 0;
				for(ProxiedPlayer player : server.getPlayers()){
					String color = Users.getUser(player).getRank().getChatColor();
					if(Users.getUser(player).getRank() == UserRank.HRAC) color = "§f";
					TextComponent message = new TextComponent((index == 0 ? "" : ", ")+color+player.getName()+"§r");
					message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder(
									color+""+player.getDisplayName()+"\n"+
									(isAdmin ? "§7IP:§r "+player.getAddress().getAddress().getHostAddress().replace("/", "")+"\n" : "")+
									"§7Ping:§r "+player.getPing()+" ms"
							).create()
					));
					message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/msg "+player.getName()+" "));
					messages.add(message);
					index ++;
				}
				players += index;

				BaseComponent [] components = messages.toArray(new BaseComponent[messages.size()]);
				sender.sendMessage(components);
			}
			sender.sendMessage(new ComponentBuilder("Celkovy pocet hracu: §7"+players+"/100").create());
		}
	}
}