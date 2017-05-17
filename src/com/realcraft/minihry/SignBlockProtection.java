package com.realcraft.minihry;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.material.Sign;
import org.bukkit.util.Vector;

import com.realcraft.RealCraft;

public class SignBlockProtection implements Listener, Runnable {
	RealCraft plugin;

	public SignBlockProtection(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,this,5,5);
	}

	public void onReload(){
	}

	@Override
	public void run(){
		for(Player player : plugin.getServer().getWorld("world").getPlayers()){
			if(player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) this.checkSignProtection(player);
		}
	}

	public void checkSignProtection(Player player){
		Location location = player.getLocation();
		int minX = (int)Math.round(location.getX())-1;
		int minZ = (int)Math.round(location.getZ())-1;
		for(int x = minX; x < minX + 2; x++){
			for(int z = minZ; z < minZ + 2; z++){
				Block block = location.getWorld().getBlockAt(x,location.getBlockY()+1,z);
				if(block.getType() != Material.WALL_SIGN){
					block = location.getWorld().getBlockAt(x,location.getBlockY(),z);
					if(block.getType() != Material.WALL_SIGN) block = null;
				}
				if(block != null){
					Sign sign = (Sign)block.getState().getData();
					block = block.getRelative(sign.getAttachedFace());
					Location blockLocation = block.getLocation().clone();
					blockLocation.add(0.5,0,0.5);
					if(location.distance(blockLocation) < 2.0){
						Location signLocation = block.getLocation().clone();
						signLocation.add(0.5,0,0.5);
						Vector velocity = location.subtract(signLocation).toVector().multiply(0.4).setY(0.2);
						player.setVelocity(velocity);
					}
				}
			}
		}
	}
}