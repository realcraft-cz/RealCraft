package realcraft.bukkit.sockets;

import org.bukkit.Bukkit;
import realcraft.bukkit.RealCraft;
import realcraft.share.ServerType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketManager {

	private static final int PORT_INCREMENT = 100;
	private ServerSocket serverSocket;

	public SocketManager(){
		try {
			serverSocket = new ServerSocket(SocketManager.getServerSocketPort(RealCraft.getServerType()));
			serverSocket.setSoTimeout(2000);
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
										DataInputStream inStream = new DataInputStream(socket.getInputStream());
										while(!socket.isClosed()){
											if(inStream.available() <= 0) continue;
											SocketData data = new SocketData(null,inStream.readUTF());
											Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
												@Override
												public void run(){
													Bukkit.getPluginManager().callEvent(new SocketDataEvent(ServerType.getByName(data.getServer()),data));
												}
											});
											socket.close();
										}
									} catch (Exception e){
										e.printStackTrace();
									}
								}
							});
						} catch (Exception e){
						}
					}
				}
			});
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void onDisable(){
		try {
			if(serverSocket != null) serverSocket.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public static void send(ServerType server,SocketData data){
		Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				try {
					Socket socket = new Socket(Inet4Address.getLocalHost(),SocketManager.getServerSocketPort(server));
					socket.setSoTimeout(2000);
					DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
					outStream.writeUTF(data.toString());
					outStream.flush();
					socket.close();
				} catch (Exception e){
				}
			}
		});
	}

	public static void sendToAll(SocketData data){
		SocketManager.sendToAll(data,false);
	}

	public static void sendToAll(SocketData data,boolean yourself){
		for(ServerType server : ServerType.values()){
			if(yourself || !server.toString().equalsIgnoreCase(Bukkit.getServer().getMotd())) SocketManager.send(server,data);
		}
	}

	public static int getServerSocketPort(ServerType server){
		int port = server.getPortOrder();
		if(RealCraft.isTestServer()) port += 24500;
		else port += 25500;
		port += PORT_INCREMENT;
		return port;
	}
}