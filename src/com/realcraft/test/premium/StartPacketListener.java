package com.realcraft.test.premium;

import java.lang.reflect.InvocationTargetException;
import java.security.PublicKey;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.realcraft.RealCraft;

public class StartPacketListener extends PacketAdapter {

	public StartPacketListener(){
		super(params(RealCraft.getInstance(),PacketType.Login.Client.START).optionAsync());
	}

	@Override
	public void onPacketReceiving(PacketEvent packetEvent){
		if(packetEvent.isCancelled()) return;

		Player player = packetEvent.getPlayer();
		String sessionKey = player.getAddress().toString();

		PacketContainer packet = packetEvent.getPacket();
		String username = packet.getGameProfiles().read(0).getName();

		packetEvent.getAsyncMarker().incrementProcessingDelay();

		System.out.println("sessionKey: "+sessionKey);
		System.out.println("username: "+username);

		Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				try {
					try {
						StartPacketListener.this.sentEncryptionRequest(player);

						synchronized (packetEvent.getAsyncMarker().getProcessingLock()) {
				            packetEvent.setCancelled(true);
				        }
						ProtocolLibrary.getProtocolManager().getAsynchronousManager().signalPacketTransmission(packetEvent);
					} catch (Exception e){
						e.printStackTrace();
					}
				} finally {
					ProtocolLibrary.getProtocolManager().getAsynchronousManager().signalPacketTransmission(packetEvent);
				}
			}
		});
	}

	public void sentEncryptionRequest(Player player) throws InvocationTargetException {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

		PacketContainer newPacket = protocolManager.createPacket(PacketType.Login.Server.ENCRYPTION_BEGIN);
		newPacket.getStrings().write(0, PremiumTest.serverId);
		newPacket.getSpecificModifier(PublicKey.class).write(0, PremiumTest.keyPair.getPublic());
		newPacket.getByteArrays().write(0, PremiumTest.verifyToken);
		protocolManager.sendServerPacket(player, newPacket);

		System.out.println("sentEncryptionRequest");
	}
}