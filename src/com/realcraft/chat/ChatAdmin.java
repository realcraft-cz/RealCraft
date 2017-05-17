package com.realcraft.chat;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.earth2me.essentials.Essentials;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.realcraft.RealCraft;
import com.realcraft.banmanazer.BanUtils;

public class ChatAdmin implements CommandExecutor, PluginMessageListener {
	RealCraft plugin;
	Essentials essentials;

	public ChatAdmin(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin,"BungeeCord");
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin,"BungeeCord",this);
		plugin.getCommand("ac").setExecutor(this);
	}

	public void onReload(){
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("ac")){
			if(!player.hasPermission("group.Admin") && !player.hasPermission("group.Moderator") && !player.hasPermission("group.Builder")) return true;
			if(args.length < 1){
				player.sendMessage("/ac <message>");
				return true;
			}
			sendAdminMessage(player,BanUtils.combineSplit(0,args));
		}
		return true;
	}

	private void sendAdminMessage(Player player,String message){
		printAdminMessage(plugin.serverName,player.getDisplayName(),message);
		plugin.chatlog.onPlayerAdminChat(player,message);

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		out.writeUTF("ONLINE");
		out.writeUTF("RealCraftAdmin");

		message = plugin.serverName+";"+player.getDisplayName()+";"+message;
		byte[] data = message.getBytes();
        out.writeShort(data.length);
        out.write(data);

        player.sendPluginMessage(plugin,"BungeeCord",out.toByteArray());
	}

	private void printAdminMessage(String server,String sender,String message){
		message = RealCraft.parseColors("&a[AdminChat] "+sender+": &c"+message);
		for(Player player : plugin.getServer().getOnlinePlayers()){
			if(player.hasPermission("group.Admin") || player.hasPermission("group.Moderator") || player.hasPermission("group.Builder")){
				player.sendMessage(message);
			}
		}
	}

	@Override
	public void onPluginMessageReceived(String channel,Player player,byte[] message){
		if(!channel.equals("BungeeCord")) return;
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
			String subchannel = in.readUTF();
			if(!subchannel.equals("RealCraftAdmin")) return;

			short len = in.readShort();
			byte[] data = new byte[len];
			in.readFully(data);

			String [] messageData = new String(data).split(";");
			String server = messageData[0];
			String sender = messageData[1];
			String reason = BanUtils.combineSplit(2,messageData);

			printAdminMessage(server,sender,reason);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}