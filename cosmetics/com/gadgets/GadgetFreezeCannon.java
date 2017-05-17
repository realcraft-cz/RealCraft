package com.gadgets;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.realcraft.RealCraft;
import com.realcraft.utils.Particles;
import com.utils.BlockUtils;
import com.utils.UtilParticles;

public class GadgetFreezeCannon extends Gadget {

	public GadgetFreezeCannon(GadgetType type){
		super(type);
	}

	@Override
	public void onClick(final Player player){
		final Item item = player.getWorld().dropItem(player.getEyeLocation(),new ItemStack(Material.ICE));
		item.setPickupDelay(50000);
        item.setVelocity(player.getEyeLocation().getDirection().multiply(0.9));

        BukkitRunnable runnable = new BukkitRunnable(){
            @Override
            public void run(){
            	if(item != null){
            		if(item.isOnGround()){
	            		item.remove();
	                	for (Block b : BlockUtils.getBlocksInRadius(item.getLocation(), 4, false)) BlockUtils.setToRestore(b, Material.ICE, (byte) 0, 50);
	                    UtilParticles.display(Particles.FIREWORKS_SPARK, 4d, 3d, 4d, item.getLocation(), 80);
	                	setRunning(player,false);
	                	this.cancel();
            		}
            		else if(item.isDead() || item.getTicksLived() > 10*20){
            			item.remove();
            			setRunning(player,false);
	                	this.cancel();
            		}
            	}
            }
        };
        runnable.runTaskTimerAsynchronously(RealCraft.getInstance(),0,1);
	}
}