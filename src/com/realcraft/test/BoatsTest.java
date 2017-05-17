package com.realcraft.test;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

import com.realcraft.RealCraft;

import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EntityBoat;
import net.minecraft.server.v1_11_R1.World;

public class BoatsTest implements Listener {

	public BoatsTest(){
		RealCraft.getInstance().getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	public class CustomBoat extends EntityBoat {

		public CustomBoat(World world){
			super(world);
		}

		@Override
		public void collide(Entity entity){
		}
	}

	@EventHandler
	public void VehicleEntityCollisionEvent(VehicleEntityCollisionEvent event){
		event.setCancelled(true);
		event.setCollisionCancelled(true);
		if(event.getEntity() != null) System.out.println(event.getEntity().getType().toString());
	}


}