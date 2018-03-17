package realcraft.bukkit.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.earth2me.essentials.Essentials;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.antispam.StringSimilarity;
import realcraft.bukkit.banmanazer.BanUtils;
import realcraft.bukkit.playermanazer.PlayerManazer.PlayerInfo;

public class ChatPrivate implements Listener, CommandExecutor {
	RealCraft plugin;
	Essentials essentials;

	int maxSameChars;
	public int spamHistory;
	double spamProbability;
	String spamMessage = null;

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
				if(args.length < 2){
					player.sendMessage("Zaslat soukromou zpravu konkretnimu hraci.");
					player.sendMessage("/"+command.getName().toLowerCase()+" <player> <message>");
					return true;
				}
				Player recipient = plugin.getServer().getPlayer(args[0]);
				if(recipient == null){
					player.sendMessage(RealCraft.parseColors("&cHrac nenalezen."));
					return true;
				}
				else if(recipient == player){
					player.sendMessage(RealCraft.parseColors("&cNemuzes zaslat zpravu sam sobe."));
					return true;
				}
				else if(essentials.getUser(recipient).isIgnoredPlayer(essentials.getUser(player))){
					player.sendMessage(RealCraft.parseColors("&cTento hrac te ignoruje."));
					return true;
				}
				sendPrivateMessage(player,recipient,BanUtils.combineSplit(1,args));
			}
			else if(command.getName().equalsIgnoreCase("reply")){
				PlayerInfo playermanazer = plugin.playermanazer.getPlayerInfo(player);
				if(playermanazer != null){
					if(playermanazer.getLastMessagePlayer() == null){
						player.sendMessage(RealCraft.parseColors("&cZadny hrac ti nezaslal soukromou zpravu."));
						return true;
					}
					if(args.length < 1){
						player.sendMessage("Odeslat odpoved na posledni soukromou zpravu.");
						player.sendMessage("/"+command.getName().toLowerCase()+" <message>");
						return true;
					}
					sendPrivateMessage(player,playermanazer.getLastMessagePlayer(),BanUtils.combineSplit(0,args));
				}
			}
		} else {
			if(command.getName().equalsIgnoreCase("msg")){
				if(args.length < 2) return true;
				Player recipient = plugin.getServer().getPlayer(args[0]);
				if(recipient == null) return true;
				recipient.sendMessage(RealCraft.parseColors(BanUtils.combineSplit(1,args)));
			}
		}
		return true;
	}

	private boolean sendPrivateMessage(Player sender,Player recipient,String message){
		if(!sender.hasPermission("group.Moderator")){
			String[] messages = LastPlayerPrivateMessages.getMessages(sender,recipient);
			for(String oldMessage : messages){
				if(oldMessage != null && StringSimilarity.similarity(message,oldMessage) > spamProbability){
					if(spamMessage != null) sender.sendMessage(RealCraft.parseColors(spamMessage));
					return false;
				}
			}
			LastPlayerPrivateMessages.addMessage(sender,recipient,message);
		}

		message = plugin.chatadvert.checkAdvert(sender,message);

		sender.sendMessage(RealCraft.parseColors("&6[ja -> "+recipient.getDisplayName()+"&6] &r"+message));
		recipient.sendMessage(RealCraft.parseColors("&6["+sender.getDisplayName()+" &6-> ja] &r"+message));
		this.socialSpy(sender,recipient,message);
		plugin.chatlog.onPrivateMessage(sender,recipient,message);

		PlayerInfo playermanazer = plugin.playermanazer.getPlayerInfo(recipient);
		if(playermanazer != null) playermanazer.setLastMessagePlayer(sender);
		return true;
	}

	private void socialSpy(Player sender,Player recipient,String message){
		message = RealCraft.parseColors("&7["+sender.getDisplayName()+" &7-> "+recipient.getDisplayName()+"&7] &r&7"+message);
		for(Player player : plugin.getServer().getOnlinePlayers()){
			if((player != sender && player != recipient) && (player.hasPermission("group.Admin") || player.hasPermission("group.Moderator"))){
				if(essentials.getUser(player).isSocialSpyEnabled()) player.sendMessage(message);
			}
		}
	}
}