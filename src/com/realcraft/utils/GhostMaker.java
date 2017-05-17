package com.realcraft.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.collect.Lists;

public class GhostMaker {

	private final ProtocolManager pm = ProtocolLibrary.getProtocolManager(); //Hooks into ProtocolLib
	private HashSet<Player> ghosts = new HashSet<Player>(); //Keeps track of players who should be semi-transparent

	public GhostMaker(){
	    /*pm.addPacketListener(new PacketAdapter(RealCraft.getInstance(), PacketType.Play.Server.SPAWN_ENTITY) { //Listen for anytime a player may see another entity
	        @Override
	        public void onPacketSending(PacketEvent event) {
	        	System.out.println("onPacketSending");
	            Entity entity = event.getPacket().getEntityModifier(event).read(0);
	            if (entity != null
	                    && entity instanceof Player
	                    && ghosts.contains(entity.getUniqueId())) { //Player can potentially see a ghost
	                showAsGhost(event.getPlayer(), (Player) entity); //Render the ghost as semi-transparent
	            }
	        }
	    });*/
	}

	public void addGhost(Player player) {
	    if (ghosts.add(player)) {
	        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 2), true); //Apply invisibility to the ghost
	        showAsGhost(player, player); //Let ghost see themselves as a ghost
	        for (Player viewer : pm.getEntityTrackers(player)) { //Send a packet to anyone who can "see" the ghost
	            showAsGhost(viewer, player);
	        }
	    }
	}

	public void removeGhost(Player player) {
	    if (ghosts.remove(player)) {
	        player.removePotionEffect(PotionEffectType.INVISIBILITY); //Remove invisibility
	        for (Player viewer : Bukkit.getServer().getOnlinePlayers()) { //Send removal packets to every player (some that recv'd the addGhost packet may no longer be in range)
	            PacketContainer packet = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM, true);
	            packet.getStrings().write(0, viewer.getEntityId() + "." + player.getEntityId()); //Make the team name unique to both the viewer and the ghost
	            packet.getIntegers().write(1, 1); //We are removing this team
	            try {
	                pm.sendServerPacket(viewer, packet); //Only the viewer needs to be sent the packet
	            } catch (InvocationTargetException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}

	private void showAsGhost(Player viewer, Player player) {
	    PacketContainer packet = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM, true);
	    packet.getStrings().write(0, viewer.getEntityId() + "." + player.getEntityId()); //Make the team name unique to both the viewer and the ghost
	    packet.getIntegers().write(1, 0); //We are creating a new team
	    packet.getModifier().write(7, Lists.newArrayList(viewer.getName(), player.getName())); //Team only consists of the ghost and the viewer
	    packet.getIntegers().write(2, 3); //Ghost can be seen and attacked by the viewer
	    try {
	        pm.sendServerPacket(viewer, packet); //Only the viewer needs to be sent the packet
	    } catch (InvocationTargetException e) {
	        e.printStackTrace();
	    }
	}
}