package realcraft.bukkit.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.earth2me.essentials.Essentials;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.antispam.StringSimilarity;
import realcraft.bukkit.sockets.SocketData;
import realcraft.bukkit.sockets.SocketDataEvent;
import realcraft.bukkit.sockets.SocketManager;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;
import realcraft.share.users.UserRank;
import realcraft.share.utils.StringUtil;

public class ChatPrivate implements Listener, CommandExecutor, TabCompleter {

	private static final String CHANNEL_PM = "privateMsg";

	RealCraft plugin;
	Essentials essentials;

	int maxSameChars;
	public int spamHistory;
	double spamProbability;
	String spamMessage = null;

	private HashMap<User,User> lastMessagePlayers = new HashMap<User,User>();

	public ChatPrivate(RealCraft realcraft){
		plugin = realcraft;
		essentials = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		plugin.getCommand("msg").setExecutor(this);
		plugin.getCommand("reply").setExecutor(this);
		maxSameChars = plugin.config.getInt("antispam.maxSameChars",5);
		spamHistory = plugin.config.getInt("antispam.spamHistory",5);
		spamProbability = plugin.config.getDouble("antispam.spamProbability",0.6);
		spamMessage = plugin.config.getString("antispam.spamMessage",null);
		LastPlayerPrivateMessages.initSettings(spamHistory);
	}

	public void onReload(){
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(essentials.getUser(player).isMuted()){
				player.sendMessage(RealCraft.parseColors("&cNemuzes mluvit, jsi umlceny."));
				return true;
			}
			if(command.getName().equalsIgnoreCase("msg")){
				User fPlayer = Users.getUser(player);
				if(args.length < 2){
					player.sendMessage("Zaslat soukromou zpravu konkretnimu hraci.");
					player.sendMessage("/"+command.getName().toLowerCase()+" <player> <message>");
					return true;
				}
				User recipient = Users.getOnlineUser(args[0]);
				if(recipient == null){
					player.sendMessage(RealCraft.parseColors("&cHrac nenalezen."));
					return true;
				}
				else if(recipient.equals(fPlayer)){
					player.sendMessage(RealCraft.parseColors("&cNemuzes zaslat zpravu sam sobe."));
					return true;
				}
				else if(essentials.getOfflineUser(recipient.getName()).isIgnoredPlayer(essentials.getUser(player))){
					player.sendMessage(RealCraft.parseColors("&cTento hrac te ignoruje."));
					return true;
				}
				String message = StringUtil.combineSplit(1,args);
				String[] messages = LastPlayerPrivateMessages.getMessages(fPlayer,recipient);
				for(String oldMessage : messages){
					if(oldMessage != null && StringSimilarity.similarity(message,oldMessage) > spamProbability){
						if(spamMessage != null) sender.sendMessage(RealCraft.parseColors(spamMessage));
						return true;
					}
				}
				LastPlayerPrivateMessages.addMessage(fPlayer,recipient,message);
				sendPrivateMessage(fPlayer,recipient,message);
			}
			else if(command.getName().equalsIgnoreCase("reply")){
				User fPlayer = Users.getUser(player);
				if(!lastMessagePlayers.containsKey(Users.getUser(player)) || !lastMessagePlayers.get(Users.getUser(player)).isLogged()){
					player.sendMessage(RealCraft.parseColors("&cZadny hrac ti nezaslal soukromou zpravu."));
					return true;
				}
				else if(args.length < 1){
					player.sendMessage("Odeslat odpoved na posledni soukromou zpravu.");
					player.sendMessage("/"+command.getName().toLowerCase()+" <message>");
					return true;
				}
				String message = StringUtil.combineSplit(0,args);
				String[] messages = LastPlayerPrivateMessages.getMessages(fPlayer,lastMessagePlayers.get(Users.getUser(player)));
				for(String oldMessage : messages){
					if(oldMessage != null && StringSimilarity.similarity(message,oldMessage) > spamProbability){
						if(spamMessage != null) sender.sendMessage(RealCraft.parseColors(spamMessage));
						return true;
					}
				}
				LastPlayerPrivateMessages.addMessage(fPlayer,lastMessagePlayers.get(Users.getUser(player)),message);
				sendPrivateMessage(fPlayer,lastMessagePlayers.get(Users.getUser(player)),message);
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender,Command command,String alias,String[] args){
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("msg")){
			List<String> players = new ArrayList<String>();
			for(User user : Users.getOnlineUsers()){
				if(user.getName().toLowerCase().startsWith(alias.toLowerCase()) && !user.getName().equalsIgnoreCase(player.getName())) players.add(user.getName());
			}
			return players;
		}
		return null;
	}

	private void sendPrivateMessage(User sender,User recipient,String message){
		SocketData data = new SocketData(CHANNEL_PM);
		data.setInt("sender",sender.getId());
		data.setInt("recipient",recipient.getId());
		data.setString("message",message);
		SocketManager.sendToAll(data,true);
		plugin.chatlog.onPrivateMessage(sender,recipient,message);
	}

	private void showPrivateMessageToSender(User recipient,User sender,String message){
		Users.getPlayer(sender).sendMessage("§6[ja -> §f"+recipient.getName()+"§6] §r"+message);
	}

	private void showPrivateMessageToRecipient(User recipient,User sender,String message){
		Users.getPlayer(recipient).sendMessage("§6[§f"+sender.getName()+" §6-> ja] §r"+message);
	}

	private void showPrivateMessageToAdmin(User user,User recipient,User sender,String message){
		if(essentials.getUser(Users.getPlayer(user)).isSocialSpyEnabled()) Users.getPlayer(user).sendMessage("§6[§7"+sender.getName()+" §6-> §7"+recipient.getName()+"§6] §r§7"+message);
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(CHANNEL_PM)){
			User sender = Users.getUser(data.getInt("sender"));
			User recipient = Users.getUser(data.getInt("recipient"));
			String message = data.getString("message");
			if(Users.getPlayer(sender) != null) this.showPrivateMessageToSender(recipient,sender,message);
			if(Users.getPlayer(recipient) != null) this.showPrivateMessageToRecipient(recipient,sender,message);
			lastMessagePlayers.put(recipient,sender);
			for(User user : Users.getOnlineUsers()){
				if(user.getRank().isMinimum(UserRank.ADMIN)){
					if(!user.equals(sender) && !user.equals(recipient) && Users.getPlayer(user) != null) this.showPrivateMessageToAdmin(user,recipient,sender,message);
				}
			}
		}
	}
}