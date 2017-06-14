package com.realcraft.sockets;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.realcraft.ServerType;

public class SocketDataEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
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

	@Override
	public HandlerList getHandlers(){
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}
}