package com.mounts;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.util.Vector;

import com.realcraft.RealCraft;
import com.utils.MathUtils;

public class MountSnake extends Mount {

	private HashMap<String, ArrayList<Entity>> tailMap = new HashMap<String,ArrayList<Entity>>();

    public MountSnake(MountType type){
        super(type);
    }

    @Override
    public void onCreate(Player player){
    	Entity entity = player.getWorld().spawnEntity(player.getLocation(),this.getType().toEntityType());
    	entity.setPassenger(player);
    	int color = MathUtils.randomRangeInt(0,14);
        ((LivingEntity) entity).setNoDamageTicks(Integer.MAX_VALUE);
        ((Sheep) entity).setColor(DyeColor.values()[color]);
        tailMap.put(player.getName(),new ArrayList<Entity>());
        tailMap.get(player.getName()).add(entity);
        this.addSheepToTail(player,color,4);
    	this.setEntity(player,entity);
    }

    @Override
    public void onClear(Player player){
    	Entity entity = this.getEntity(player);
    	if(entity != null){
    		entity.remove();
    		this.setEntity(player,null);
    		for(Entity tail : tailMap.get(player.getName())){
    			if(tail != null) tail.remove();
    		}
    	}
    }

    @Override
    public void onUpdate(Player player,Entity entity){
    	this.move(player);
    	if(this.isRunning(player)){
	    	Bukkit.getScheduler().runTask(RealCraft.getInstance(), new Runnable() {
	            @Override
	            public void run() {
	                if (player != null) {
	                    Vector vel = player.getLocation().getDirection().setY(0).normalize().multiply(4);

	                    Creature before = null;
	                    for (int i = 0; i < tailMap.get(player.getName()).size(); i++) {
	                        Creature tail = (Creature) tailMap.get(player.getName()).get(i);
	                        if(tail  != null){
		                        Location loc = player.getLocation().add(vel);
		                        if (i == 0)
		                            loc = tail.getLocation().add(vel);
		                        if (before != null)
		                            loc = before.getLocation();
		                        if (loc.toVector().subtract(tail.getLocation().toVector()).length() > 12.0D)
		                            loc = tail.getLocation().add(traj(tail.getLocation(), loc).multiply(12));
		                        if (before != null) {
		                            Location tp = before.getLocation().add(traj2D(before, tail).multiply(1.4D));
		                            tp.setPitch(tail.getLocation().getPitch());
		                            tp.setYaw(tail.getLocation().getYaw());
		                            tail.teleport(tp);
		                        }

		                        Mount.move(tail,loc);
		                        before = tail;
	                        }
	                    }
	                }
	            }
	        });
    	}
    }

    public void addSheepToTail(Player player,int color,int amount){
        for (int i = 0; i < amount; i++) {
            Location loc = player.getLocation();
            if (!(tailMap.get(player.getName())).isEmpty()) {
                loc = ((Creature) (tailMap.get(player.getName())).get((tailMap.get(player.getName())).size() - 1)).getLocation();
            }
            if ((tailMap.get(player.getName())).size() > 1) {
                loc.add(traj((tailMap.get(player.getName())).get((tailMap.get(player.getName())).size() - 2), (tailMap.get(player.getName())).get((tailMap.get(player.getName())).size() - 1)));
            } else {
                loc.subtract(player.getLocation().getDirection().setY(0));
            }
            Sheep tail = (loc.getWorld().spawn(loc, Sheep.class));
            tail.setNoDamageTicks(Integer.MAX_VALUE);
            tail.setRemoveWhenFarAway(false);
            tail.teleport(loc);
            (tailMap.get(player.getName())).add(tail);
            tail.setColor(DyeColor.values()[color]);
        }
    }

    public Vector traj2D(Entity a, Entity b){
        return b.getLocation().toVector().subtract(a.getLocation().toVector()).setY(0).normalize();
    }

    public Vector traj(Location a, Location b){
        return b.toVector().subtract(a.toVector()).setY(0).normalize();
    }

    public Vector traj(Entity a, Entity b){
        return b.getLocation().toVector().subtract(a.getLocation().toVector()).setY(0).normalize();
    }
}