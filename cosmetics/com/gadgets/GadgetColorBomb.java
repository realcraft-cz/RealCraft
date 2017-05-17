package com.gadgets;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.realcraft.RealCraft;
import com.realcraft.utils.Particles;
import com.utils.ItemFactory;
import com.utils.MathUtils;

public class GadgetColorBomb extends Gadget {

	static Random random = new Random();

	public GadgetColorBomb(GadgetType type){
		super(type);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(final Player player){
		ItemStack itemstack = new ItemStack(Material.WOOL,1,(short)0,(byte)random.nextInt(15));
    	ItemMeta meta = itemstack.getItemMeta();
    	meta.setDisplayName(UUID.randomUUID().toString());
    	itemstack.setItemMeta(meta);

		final Item bomb = player.getWorld().dropItem(player.getEyeLocation(),itemstack);
        bomb.setPickupDelay(50000);
        bomb.setVelocity(player.getEyeLocation().getDirection().multiply(0.7532));

		BukkitRunnable runnable = new BukkitRunnable(){
			private boolean running = false;
            @Override
            public void run(){
            	final BukkitRunnable instance = this;
            	if(bomb != null && !running){
            		if(bomb.isOnGround()){
	            		running = true;
	            		bomb.setVelocity(new Vector(0, 0, 0));
	            		Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
	                        @Override
	                        public void run(){
	                        	bomb.remove();
	                        	running = false;
	                        	setRunning(player,false);
	                        	instance.cancel();
	                        }
	                    },100);
            		}
            		else if(bomb.isDead() || bomb.getTicksLived() > 10*20){
            			bomb.remove();
                    	running = false;
                    	setRunning(player,false);
                    	instance.cancel();
            		}
            	}
            	if (running) {
                    Particles effect;
                    switch (random.nextInt(5)) {
                        default:
                            effect = Particles.FIREWORKS_SPARK;
                            break;
                        case 1:
                            effect = Particles.FIREWORKS_SPARK;
                            break;
                        case 4:
                            effect = Particles.FLAME;
                            break;
                        case 5:
                            effect = Particles.SPELL_WITCH;
                            break;
                    }
                    effect.display(0, 0, 0, 0.2f, 1, bomb.getLocation(), 128);
                    try {
                        Bukkit.getScheduler().runTask(RealCraft.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                final Item i = bomb.getWorld().dropItem(bomb.getLocation().add(0, 0.15f, 0), ItemFactory.create(Material.WOOL, (byte) random.nextInt(15), UUID.randomUUID().toString()));
                                i.setPickupDelay(500000);
                                i.setVelocity(new Vector(0, 0.5, 0).add(MathUtils.getRandomCircleVector().multiply(0.1)));

                                i.getWorld().playSound(i.getLocation(), Sound.ENTITY_CHICKEN_EGG, 0.2f, 1.0f);

                                Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
                                    @Override
                                    public void run(){
                                    	i.remove();
                                    }
                                },20);
                            }
                        });
                    } catch (Exception exc) {
                    }
                }
            }
		};
		runnable.runTaskTimerAsynchronously(RealCraft.getInstance(),0,1);
	}
}