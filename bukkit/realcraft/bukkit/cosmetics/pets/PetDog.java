package realcraft.bukkit.cosmetics.pets;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

public class PetDog extends Pet {

    public PetDog(PetType type){
        super(type);
    }

    @Override
    public void onCreate(Player player){
    	final Entity entity = player.getWorld().spawnEntity(player.getLocation(),this.getType().toEntityType());
    	((Wolf)entity).setBaby();
    	((Wolf)entity).setTamed(true);
    	((Wolf)entity).setSitting(false);
    	((Wolf)entity).setAgeLock(true);
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
    	if(((Wolf)entity).isSitting()) ((Wolf)entity).setSitting(false);
    }
}