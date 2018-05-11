package realcraft.bungee.sockets;

import realcraft.share.ServerType;

public class SocketData extends realcraft.share.sockets.SocketData {

	public SocketData(String channel){
		super(channel,ServerType.BUNGEE.toString());
	}

	public SocketData(String channel,String data){
		super(channel,null,data);
	}
}