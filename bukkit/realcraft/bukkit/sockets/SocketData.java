package realcraft.bukkit.sockets;

import realcraft.bukkit.RealCraft;

public class SocketData extends realcraft.share.sockets.SocketData {

	public SocketData(String channel){
		super(channel,RealCraft.getServerType().toString());
	}

	public SocketData(String channel,String data){
		super(channel,null,data);
	}
}