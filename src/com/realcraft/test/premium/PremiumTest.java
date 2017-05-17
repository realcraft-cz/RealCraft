package com.realcraft.test.premium;

import java.security.KeyPair;
import java.util.Random;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import com.comphenix.protocol.ProtocolLibrary;
import com.realcraft.RealCraft;

public class PremiumTest implements Listener {

	public static String serverId;
	public Random random;
	public static final byte[] verifyToken = new byte[4];
	public static final KeyPair keyPair = EncryptionUtil.generateKeyPair();

	public PremiumTest(){
		random = new Random();
		serverId = Long.toString(random.nextLong(),16);
		random.nextBytes(verifyToken);

		System.out.println("serverId: "+serverId);

		RealCraft.getInstance().getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
		ProtocolLibrary.getProtocolManager().getAsynchronousManager().registerAsyncHandler(new StartPacketListener()).start(3);
		ProtocolLibrary.getProtocolManager().getAsynchronousManager().registerAsyncHandler(new EncryptionPacketListener()).start(3);
	}

	@EventHandler
	public void PlayerLoginEvent(PlayerLoginEvent event){
		System.out.println("PlayerLoginEvent");
	}
}