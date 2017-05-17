package com.pets;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;

public class PetEasterBunny extends Pet {

    public PetEasterBunny(PetType type){
        super(type);
    }

    @Override
    public void onCreate(Player player){
    	Entity entity = player.getWorld().spawnEntity(player.getLocation(),this.getType().toEntityType());
    	((Rabbit)entity).setBaby();
    	((Rabbit)entity).setAgeLock(true);
    	this.setEntity(player,entity);
    }

    @Override
    public void onClear(Player player){
    	Entity entity = this.getEntity(player);
    	if(entity != null){
    		entity.remove();
    		this.setEntity(player,null);
    	}
    }

    @Override
    public void onUpdate(Player player,Entity entity){
    }
}