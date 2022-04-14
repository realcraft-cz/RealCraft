package realcraft.bukkit.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import realcraft.bukkit.RealCraft;
import realcraft.share.ServerType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class BungeeMessages {

	public static void connectPlayerToServer(Player player,ServerType server){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(server.toString().toLowerCase());
		player.sendPluginMessage(RealCraft.getInstance(),"BungeeCord",out.toByteArray());
	}

	public static boolean isServerOnline(ServerType server) {
		Socket socket = new Socket();

		int port = server.getPortOrder();
		if (RealCraft.isTestServer()) {
			port += 24500;
		} else {
			port += 25500;
		}

		try {
			socket.connect(new InetSocketAddress("localhost", port), 20);
			socket.close();
			return true;
		} catch (IOException e) {
			//e.printStackTrace();
		}

		return false;
	}
}