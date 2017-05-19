package com.realcraft.sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import com.realcraft.RealCraft;

public class SocketManager implements Listener {

	private static final int PORT_INCREMENT = 100;
	private ServerSocket serverSocket = null;

	public SocketManager(){
		try {
			serverSocket = new ServerSocket(SocketManager.getServerSocketPort(Bukkit.getServer().getServerName()));
			Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					while(!serverSocket.isClosed()){
						try {
							Socket socket = serverSocket.accept();
							Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(),new Runnable(){
								@Override
								public void run(){
									try {
										ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
										while(!socket.isClosed()){
											try {
												if(inStream.available() <= 0) continue;
												String server = inStream.readUTF();
												SocketData data = ((SocketData) inStream.readObject());
												Bukkit.getPluginManager().callEvent(new SocketDataEvent(SocketServer.getByName(server),data));
											} catch (IOException | ClassNotFoundException e){
												e.printStackTrace();
											}
										}
									} catch (IOException e){
										e.printStackTrace();
									}
								}
							});
						} catch (IOException e){
							e.printStackTrace();
						}
					}
				}
			});
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public void onDisable(){
		try {
			serverSocket.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public static void send(SocketServer server,SocketData data){
		Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				try {
					Socket socket = new Socket(Inet4Address.getLocalHost(),SocketManager.getServerSocketPort(server.toString()));
					ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
					outStream.writeUTF(Bukkit.getServer().getServerName().toUpperCase());
					outStream.writeObject(data);
					outStream.flush();
					socket.close();
				} catch (Exception e){
				}
			}
		});
	}

	public static void sendToAll(SocketData data){
		for(SocketServer server : SocketServer.values()){
			if(!server.toString().equalsIgnoreCase(Bukkit.getServer().getServerName())) SocketManager.send(server,data);
		}
	}

	public static int getServerSocketPort(String server){
		int port = RealCraft.getServerPortOrder(server);
		if(RealCraft.isTestServer()) port += 24500;
		else port += 25500;
		port += PORT_INCREMENT;
		return port;
	}

	public enum SocketServer {
		LOBBY, SURVIVAL, CREATIVE, BEDWARS, HIDENSEEK, BLOCKPARTY, RAGEMODE, PAINTBALL, DOMINATE, PARKOUR;

		public static SocketServer getByName(String name){
			return SocketServer.valueOf(name);
		}
	}
}