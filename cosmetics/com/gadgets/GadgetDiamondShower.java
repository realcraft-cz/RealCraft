package com.gadgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.realcraft.RealCraft;
import com.realcraft.utils.RandomUtil;
import com.utils.MathUtils;

public class GadgetDiamondShower extends Gadget {

	HashMap<Player,ArrayList<Entity>> entities = new HashMap<Player,ArrayList<Entity>>();
	static Random random = new Random();

	final Material [] materials = {
		Material.DIAMOND,
		Material.DIAMOND_BLOCK,
		Material.DIAMOND_CHESTPLATE,
		Material.DIAMOND_PICKAXE,
		Material.DIAMOND_ORE,
		Material.DIAMOND_SWORD
	};

	public GadgetDiamondShower(GadgetType type){
		super(type);
	}

	@Override
	public void onClick(final Player player){
		entities.put(player,new ArrayList<Entity>());
		BukkitRunnable runnable = new BukkitRunnable(){
			int step = 0;
            @Override
            public void run(){
            	step ++;
            	if(!player.isOnline()){
            		this.cancel();
            		return;
            	}
            	if(step <= 50){
            		for(int i=0;i<3;i++){
		                Item item = player.getWorld().dropItem(player.getEyeLocation(),new ItemStack(materials[random.nextInt(materials.length)]));
		                item.setPickupDelay(Integer.MAX_VALUE);
		                item.setVelocity(new Vector(0, 0.5, 0).add(MathUtils.getRandomCircleVector().multiply(RandomUtil.getRandomDouble(0.05,0.2))));
		                item.getWorld().playSound(item.getLocation(), Sound.ENTITY_CHICKEN_EGG, 0.2f, 1.0f);
		                entities.get(player).add(item);
            		}
            	}
            	else if(step == 60){
            		for(Entity entity : entities.get(player)){
            			entity.remove();
            		}
            		entities.remove(player);
            		this.cancel();
            		setRunning(player,false);
            	}
            }
        };
		runnable.runTaskTimer(RealCraft.getInstance(),0,3);
	}

	@Override
	public void setRunning(Player player,boolean running){
		super.setRunning(player,running);
		if(!running){
			if(entities.containsKey(player)){
				for(Entity entity : entities.get(player)){
					entity.remove();
				}
				entities.remove(player);
			}
		}
	}
}