package realcraft.bungee.sockets;

import net.md_5.bungee.api.plugin.Event;
import realcraft.share.ServerType;

public class SocketDataEvent extends Event {

	private ServerType server;
	private SocketData data;

	public SocketDataEvent(ServerType server,SocketData data){
		this.server = server;
		this.data = data;
	}

	public ServerType getServer(){
		return server;
	}

	public SocketData getData(){
		return data;
	}
}