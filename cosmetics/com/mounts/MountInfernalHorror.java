package com.mounts;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftSkeletonHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;

import com.nms.WrapperEntityHuman;
import com.nms.WrapperEntityInsentient;
import com.realcraft.utils.Particles;
import com.utils.EntityRegister;
import com.utils.UtilParticles;

import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.EntityHorseSkeleton;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.GenericAttributes;
import net.minecraft.server.v1_12_R1.MathHelper;
import net.minecraft.server.v1_12_R1.World;

public class MountInfernalHorror extends Mount {

    public MountInfernalHorror(MountType type){
        super(type);
        EntityRegister.register("InfernalHorse",this.getType().toEntityType(),CustomHorse.class);
    }

    @Override
    public void onCreate(Player player){
    	CustomHorse entity = new CustomHorse(((CraftWorld)player.getWorld()).getHandle());
    	entity.setLocation(player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ(),0,0);
    	((CraftLivingEntity) entity.getBukkitEntity()).setRemoveWhenFarAway(false);
    	((CraftWorld)player.getWorld()).getHandle().addEntity(entity,SpawnReason.CUSTOM);
    	((SkeletonHorse)entity.getBukkitEntity()).setAdult();
    	((SkeletonHorse)entity.getBukkitEntity()).setTamed(true);
    	((SkeletonHorse)entity.getBukkitEntity()).setDomestication(1);
    	((SkeletonHorse)entity.getBukkitEntity()).setJumpStrength(1);
    	((SkeletonHorse)entity.getBukkitEntity()).getInventory().setItem(0,new ItemStack(Material.SADDLE));
    	((CraftSkeletonHorse) entity.getBukkitEntity()).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.4d);
    	entity.getBukkitEntity().setPassenger(player);
    	this.setEntity(player,entity.getBukkitEntity());
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
    	UtilParticles.display(Particles.FLAME, 0.4f, 0.2f, 0.4f, entity.getLocation().clone().add(0, 1, 0), 5);
    }

    public class CustomHorse extends EntityHorseSkeleton {
    	public CustomHorse(World world){
    		super(world);
    	}

    	@Override
    	public void a(float sideMot, float forMot, float f2){
    		EntityHuman passenger = null;
    		if(!bF().isEmpty()){
    			passenger = (EntityHuman) bF().get(0);
    		}
    		ride(sideMot,forMot,passenger,this);
    	}

    	public float getSpeed(){
    		return 1;
    	}

    	@Override
    	public boolean damageEntity(DamageSource source,float f){
    		return false;
    	}

    	public void ride(float sideMot, float forMot, EntityHuman passenger, EntityInsentient entity){
    		WrapperEntityInsentient wEntity = new WrapperEntityInsentient(entity);
            WrapperEntityHuman wPassenger = new WrapperEntityHuman(passenger);

            if(passenger != null) {
                entity.lastYaw = entity.yaw = passenger.yaw % 360f;
                entity.pitch = (passenger.pitch * 0.5F) % 360f;

                wEntity.setRenderYawOffset(entity.yaw);
                wEntity.setRotationYawHead(entity.yaw);

                sideMot = wPassenger.getMoveStrafing() * 0.25f;
                forMot = wPassenger.getMoveForward() * 0.5f;

                if(forMot <= 0.0F)
                    forMot *= 0.25F;

                wEntity.setJumping(wPassenger.isJumping());

                if(wPassenger.isJumping() && (entity.onGround)) {
                    entity.motY = 0.4D;

                    float f2 = MathHelper.sin(entity.yaw * 0.017453292f);
                    float f3 = MathHelper.cos(entity.yaw * 0.017453292f);
                    entity.motX += -0.4f * f2;
                    entity.motZ += 0.4f * f3;
                }

                wEntity.setStepHeight(1.0f);
                wEntity.setJumpMovementFactor(wEntity.getMoveSpeed() * 0.1f);

                wEntity.setRotationYawHead(entity.yaw);

                wEntity.setMoveSpeed(0.35f);
                super.g(sideMot, forMot);

                wEntity.setPrevLimbSwingAmount(wEntity.getLimbSwingAmount());

                double dx = entity.locX - entity.lastX;
                double dz = entity.locZ - entity.lastZ;

                float f4 = MathHelper.sqrt(dx * dx + dz * dz) * 4;

                if(f4 > 1)
                    f4 = 1;

                wEntity.setLimbSwingAmount(wEntity.getLimbSwingAmount() + (f4 - wEntity.getLimbSwingAmount()) * 0.4f);
                wEntity.setLimbSwing(wEntity.getLimbSwing() + wEntity.getLimbSwingAmount());
            } else {
                wEntity.setStepHeight(0.5f);
                wEntity.setJumpMovementFactor(0.02f);

                super.g(sideMot, forMot);
            }
    	}
    }
}