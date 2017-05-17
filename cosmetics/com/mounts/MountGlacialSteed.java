package com.mounts;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.realcraft.utils.Particles;
import com.utils.UtilParticles;

import net.minecraft.server.v1_11_R1.GenericAttributes;

public class MountGlacialSteed extends Mount {

    public MountGlacialSteed(MountType type){
        super(type);
    }

    @Override
    public void onCreate(Player player){
    	Entity entity = player.getWorld().spawnEntity(player.getLocation(),this.getType().toEntityType());
    	((Horse)entity).setAdult();
    	((Horse)entity).setDomestication(1);
    	((Horse)entity).setTamed(true);
        ((Horse)entity).getInventory().setSaddle(new ItemStack(Material.SADDLE));
    	((Horse)entity).setColor(Horse.Color.WHITE);
    	((Horse)entity).setJumpStrength(1);
    	((CraftHorse) (Horse)entity).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.4d);
        ((LivingEntity) entity).setNoDamageTicks(Integer.MAX_VALUE);
        entity.setPassenger(player);
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
    	UtilParticles.display(Particles.SNOW_SHOVEL, 0.4f, 0.2f, 0.4f, entity.getLocation().clone().add(0, 1, 0), 5);
    	/*for (Block b : BlockUtils.getBlocksInRadius(player.getLocation(), 3, false)){
            if (b.getLocation().getBlockY() == player.getLocation().getBlockY() - 1){
                BlockUtils.setToRestore(b, Material.SNOW_BLOCK, (byte) 0x0, 20);
            }
    	}*/
    }
}