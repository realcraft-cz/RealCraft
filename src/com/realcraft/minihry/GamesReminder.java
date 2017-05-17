package com.realcraft.minihry;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.realcraft.RealCraft;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class GamesReminder implements Listener, PluginMessageListener {
	RealCraft plugin;

	public GamesReminder(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin,"BungeeCord");
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin,"BungeeCord",this);
	}

	public void onReload(){
	}

	public void printGameStartingMessage(String server,String game,String prefix,int seconds,int players){
		TextComponent message = new TextComponent("§e[\u25ba] §bPrave zacina §e"+game+" §bs §e"+players+" hraci§b, klikni §lZDE§b a pripoj se taky.");
		message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/server "+server));
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§7Klikni pro pripojeni do hry").create()));
		for(Player player : Bukkit.getServer().getOnlinePlayers()){
			if(player.getWorld().getName().equalsIgnoreCase("world") || player.getWorld().getName().equalsIgnoreCase("world_creative")){
				player.spigot().sendMessage(message);
			}
		}
	}

	public static void sendGameStartingMessage(String game,String prefix,int seconds,int players){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		out.writeUTF("ONLINE");
		out.writeUTF("GamesReminder");

		String message = RealCraft.getInstance().serverName+";"+game+";"+prefix+";"+seconds+";"+players;
		byte[] data = message.getBytes();
        out.writeShort(data.length);
        out.write(data);

        for(Player player : Bukkit.getServer().getOnlinePlayers()){
        	player.sendPluginMessage(RealCraft.getInstance(),"BungeeCord",out.toByteArray());
        	break;
        }
	}

	@Override
	public void onPluginMessageReceived(String channel,Player _player,byte[] message){
		if(!channel.equals("BungeeCord")) return;
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
			String subchannel = in.readUTF();
			if(!subchannel.equals("GamesReminder")) return;

			short len = in.readShort();
			byte[] data = new byte[len];
			in.readFully(data);

			String [] messageData = new String(data).split(";");
			String server = messageData[0];
			String game = messageData[1];
			String prefix = messageData[2];
			int seconds = Integer.parseInt(messageData[3]);
			int players = Integer.parseInt(messageData[4]);

			this.printGameStartingMessage(server,game,prefix,seconds,players);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}