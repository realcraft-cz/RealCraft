package realcraft.bukkit.utils;

import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import realcraft.bukkit.RealCraft;
import realcraft.share.ServerType;

public class BungeeMessages {

	public static void connectPlayerToServer(Player player,ServerType server){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(server.toString().toLowerCase());
		player.sendPluginMessage(RealCraft.getInstance(),"BungeeCord",out.toByteArray());
	}
}