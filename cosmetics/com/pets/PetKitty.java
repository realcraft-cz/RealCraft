package com.pets;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;

public class PetKitty extends Pet {

    public PetKitty(PetType type){
        super(type);
    }

    @Override
    public void onCreate(Player player){
    	final Entity entity = player.getWorld().spawnEntity(player.getLocation(),this.getType().toEntityType());
    	((Ocelot)entity).setBaby();
    	((Ocelot)entity).setTamed(true);
    	((Ocelot)entity).setSitting(false);
    	((Ocelot)entity).setCatType(Ocelot.Type.RED_CAT);
    	((Ocelot)entity).setAgeLock(true);
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
    	if(((Ocelot)entity).isSitting()) ((Ocelot)entity).setSitting(false);
    }
}