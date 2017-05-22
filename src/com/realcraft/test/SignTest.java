package com.realcraft.test;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Sign;

import com.realcraft.RealCraft;
import com.realcraft.utils.Particles;
import com.realcraft.utils.Particles.OrdinaryColor;


public class SignTest implements Listener {

	public SignTest(){
		RealCraft.getInstance().getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasBlock() && event.getClickedBlock().getType() == Material.WALL_SIGN){
			Block block = event.getClickedBlock();
			Sign sign = (Sign)block.getState().getData();
			double minX = block.getLocation().getBlockX();
			double minY = block.getLocation().getBlockY()+(1/4f);
			double minZ = block.getLocation().getBlockZ();
			if(sign.getFacing() == BlockFace.SOUTH){
				minZ += (1/8f);
			}
			else if(sign.getFacing() == BlockFace.NORTH){
				minZ += 1-(1/8f);
			}
			else if(sign.getFacing() == BlockFace.EAST){
				minX += (1/8f);
			}
			else if(sign.getFacing() == BlockFace.WEST){
				minX += 1-(1/8f);
			}
			ArrayList<Location> locations = new ArrayList<Location>();
			if(sign.getFacing() == BlockFace.SOUTH || sign.getFacing() == BlockFace.NORTH){
				for(double x=minX;x<=minX+1.01;x+=0.1){
					for(double y=minY;y<=minY+0.501;y+=0.1){
						if(((x >= minX-0.01 && x <= minX+0.01) || (x >= minX+1-0.01 && x <= minX+1+0.01)) || ((y >= minY-0.01 && y <= minY+0.01) || (y >= minY+0.5-0.01 && y <= minY+0.5+0.01))){
							locations.add(new Location(block.getWorld(),x,y,minZ));
						}
					}
				}
			}
			else if(sign.getFacing() == BlockFace.EAST || sign.getFacing() == BlockFace.WEST){
				for(double z=minZ;z<=minZ+1.01;z+=0.1){
					for(double y=minY;y<=minY+0.501;y+=0.1){
						if(((z >= minZ-0.01 && z <= minZ+0.01) || (z >= minZ+1-0.01 && z <= minZ+1+0.01)) || ((y >= minY-0.01 && y <= minY+0.01) || (y >= minY+0.5-0.01 && y <= minY+0.5+0.01))){
							locations.add(new Location(block.getWorld(),minX,y,z));
						}
					}
				}
			}
			Collections.shuffle(locations);
			for(int i=0;i<20;i++){
				final int index = i;
				Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
					public void run(){
						Particles.REDSTONE.display(new OrdinaryColor(85,255,85),locations.get(index),60);
					}
				},i);
			}
		}
	}
}