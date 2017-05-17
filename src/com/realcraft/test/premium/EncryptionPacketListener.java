package com.realcraft.test.premium;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.Sets;
import com.realcraft.RealCraft;

public class EncryptionPacketListener extends PacketAdapter {

	private final BalancedSSLFactory sslFactory;


	public EncryptionPacketListener(){
		super(params(RealCraft.getInstance(),PacketType.Login.Client.ENCRYPTION_BEGIN).optionAsync());
		Set<InetAddress> addresses = Sets.newHashSet();
		sslFactory = new BalancedSSLFactory(HttpsURLConnection.getDefaultSSLSocketFactory(),addresses);
	}

	@Override
	public void onPacketReceiving(PacketEvent packetEvent){
		System.out.println("onPacketReceiving");
		if(packetEvent.isCancelled()) return;

		Player player = packetEvent.getPlayer();
		byte[] sharedSecret = packetEvent.getPacket().getByteArrays().read(0);

		System.out.println("player: "+player.getName());

		packetEvent.getAsyncMarker().incrementProcessingDelay();

		Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				try {
					EncryptionPacketListener.this.hasJoinedServer(player,PremiumTest.serverId);
				} finally {
					synchronized (packetEvent.getAsyncMarker().getProcessingLock()) {
			            packetEvent.setCancelled(true);
			        }
					ProtocolLibrary.getProtocolManager().getAsynchronousManager().signalPacketTransmission(packetEvent);
				}
			}
		});
	}

	private static final String HAS_JOINED_URL = "https://sessionserver.mojang.com/session/minecraft/hasJoined?";

	public boolean hasJoinedServer(Player player, String serverId) {
        try {
            String url = HAS_JOINED_URL + "username=" + "FreeWall" + "&serverId=" + serverId;
            HttpURLConnection conn = getConnection(url);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = reader.readLine();
            if (line != null && !line.equals("null")) {
                //validate parsing
                //http://wiki.vg/Protocol_Encryption#Server
                JSONObject userData = (JSONObject) JSONValue.parseWithException(line);
                String uuid = (String) userData.get("id");
                System.out.println("uuid: "+uuid);

                JSONArray properties = (JSONArray) userData.get("properties");
                JSONObject skinProperty = (JSONObject) properties.get(0);

                String propertyName = (String) skinProperty.get("name");
                if (propertyName.equals("textures")) {
                    String skinValue = (String) skinProperty.get("value");
                    String signature = (String) skinProperty.get("signature");
                    System.out.println("skin data");
                }

                return true;
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }

        this.receiveFakeStartPacket(player,"FreeWall");

        //this connection doesn't need to be closed. So can make use of keep alive in java
        return false;
    }

	public HttpsURLConnection getConnection(String url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(3*1000);
        connection.setReadTimeout(2 * 3*1000);
        //the new Mojang API just uses json as response
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Premium-Checker");

        if(sslFactory != null){
            connection.setSSLSocketFactory(sslFactory);
        }
        return connection;
    }

	private void receiveFakeStartPacket(Player player,String username) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        //see StartPacketListener for packet information
        PacketContainer startPacket = protocolManager.createPacket(PacketType.Login.Client.START);

        //uuid is ignored by the packet definition
        WrappedGameProfile fakeProfile = new WrappedGameProfile(UUID.randomUUID(), username);
        startPacket.getGameProfiles().write(0, fakeProfile);
        try {
            //we don't want to handle our own packets so ignore filters
            protocolManager.recieveClientPacket(player, startPacket, false);
        } catch (InvocationTargetException | IllegalAccessException ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to fake a new start packet", ex);
            //cancel the event in order to prevent the server receiving an invalid packet
        }
    }
}
