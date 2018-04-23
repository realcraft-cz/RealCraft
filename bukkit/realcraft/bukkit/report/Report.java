package realcraft.bukkit.report;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.earth2me.essentials.Essentials;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.banmanazer.BanUtils;
import realcraft.share.ServerType;

public class Report implements CommandExecutor, PluginMessageListener {
	RealCraft plugin;
	Essentials essentials;

	HashMap<Player, Long> lastReported = new HashMap<>();

	boolean enabled = false;
	String warnMessage;
	String reportedMessage;

	public Report(RealCraft realcraft){
		plugin = realcraft;
		if(plugin.config.getBoolean("report.enabled")){
			enabled = true;
			warnMessage = plugin.config.getString("report.warnMessage","&6[REPORT | %server%] &7[%sender% &7-> %reported%&7] &r%reason%");
			reportedMessage = plugin.config.getString("report.reportedMessage","&6Zprava byla uspesne odeslana administratorum.");
			essentials = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
			plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin,"BungeeCord");
			plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin,"BungeeCord",this);
			plugin.getCommand("report").setExecutor(this);
		}
	}

	public void onReload(){
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!enabled) return false;
		Player player = (Player) sender;
		if(essentials.getUser(player).isMuted()){
			player.sendMessage(RealCraft.parseColors("&cNemuzes mluvit, jsi umlceny."));
			return true;
		}
		if(command.getName().equalsIgnoreCase("report")){
			if(args.length < 2){
				player.sendMessage("Nahlasit hackera nebo poruseni pravidel.");
				player.sendMessage("/report <player> <reason>");
				return true;
			}
			Player reported = plugin.getServer().getPlayer(args[0]);
			if(reported == null){
				player.sendMessage(RealCraft.parseColors("&cHrac nenalezen."));
				return true;
			}
			else if(reported == player){
				player.sendMessage(RealCraft.parseColors("&cNemuzes nahlasit sam sebe."));
				return true;
			}
			else if(lastReported.get(player) != null && lastReported.get(player)+(120*1000) > System.currentTimeMillis()){
				player.sendMessage(RealCraft.parseColors("&cPoruseni pravidel muzes nahlasit jen jednou za 2 minuty."));
				return true;
			}
			player.sendMessage(RealCraft.parseColors(reportedMessage));
			sendReport(player,reported,BanUtils.combineSplit(1,args));
		}
		return true;
	}

	private void sendReport(Player sender,Player reported,String message){
		printReport(plugin.serverName,sender.getDisplayName(),reported.getDisplayName(),message);
		lastReported.put(sender,System.currentTimeMillis());

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		out.writeUTF("ONLINE");
		out.writeUTF("RealCraftReport");

		message = plugin.serverName+";"+sender.getDisplayName()+";"+reported.getDisplayName()+";"+message;
		byte[] data = message.getBytes();
        out.writeShort(data.length);
        out.write(data);

		reported.sendPluginMessage(plugin,"BungeeCord",out.toByteArray());
	}

	private void printReport(String server,String sender,String reported,String reason){
		String reportMessage = warnMessage;
		reportMessage = reportMessage.replaceAll("%server%",ServerType.getByName(server).getName());
		reportMessage = reportMessage.replaceAll("%sender%",sender);
		reportMessage = reportMessage.replaceAll("%reported%",reported);
		reportMessage = reportMessage.replaceAll("%reason%",reason);
		reportMessage = RealCraft.parseColors(reportMessage);

		for(Player player : plugin.getServer().getOnlinePlayers()){
			if(player.hasPermission("group.Admin") || player.hasPermission("group.Moderator")){
				player.sendMessage(reportMessage);
				player.playSound(player.getLocation(),Sound.BLOCK_NOTE_PLING,1,1);
			}
		}
	}

	@Override
	public void onPluginMessageReceived(String channel,Player player,byte[] message){
		if(!channel.equals("BungeeCord")) return;
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
			String subchannel = in.readUTF();
			if(!subchannel.equals("RealCraftReport")) return;

			short len = in.readShort();
			byte[] data = new byte[len];
			in.readFully(data);

			String [] messageData = new String(data).split(";");
			String server = messageData[0];
			String sender = messageData[1];
			String reported = messageData[2];
			String reason = BanUtils.combineSplit(3,messageData);

			printReport(server,sender,reported,reason);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}