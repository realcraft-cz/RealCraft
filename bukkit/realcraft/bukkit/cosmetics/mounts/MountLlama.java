package realcraft.bukkit.cosmetics.mounts;

import net.minecraft.server.v1_13_R1.*;
import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.utils.MaterialUtil;
import realcraft.bukkit.utils.RandomUtil;

import java.lang.reflect.Field;

public class MountLlama extends Mount {

	public MountLlama(CosmeticType type){
		super(type);
	}

	@Override
	public Entity create(Player player){
		CustomEntity cEntity = new CustomEntity(((CraftWorld)player.getLocation().getWorld()).getHandle());
		cEntity.setLocation(player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ(),player.getLocation().getPitch(),player.getLocation().getYaw());
		((CraftWorld)player.getLocation().getWorld()).getHandle().addEntity(cEntity,CreatureSpawnEvent.SpawnReason.CUSTOM);
		Llama entity = (Llama)cEntity.getBukkitEntity();
		entity.setAdult();
		entity.setDomestication(1);
		entity.setTamed(true);
		entity.getInventory().setDecor(new ItemStack(MaterialUtil.getCarpet(DyeColor.values()[RandomUtil.getRandomInteger(0,DyeColor.values().length-1)])));
		entity.setColor(Llama.Color.values()[RandomUtil.getRandomInteger(0,Llama.Color.values().length-1)]);
		entity.setJumpStrength(1);
		entity.setNoDamageTicks(Integer.MAX_VALUE);
		entity.setInvulnerable(true);
		entity.addPassenger(player);
		return entity;
	}

	@Override
	public void effect(Player player,Entity entity){
	}

	private class CustomEntity extends EntityLlama {

		private Field jumping;
		private boolean isJumping = false;

		public CustomEntity(World world){
			super(world);

			if (jumping == null) {
				try {
					jumping = EntityLiving.class.getDeclaredField("bg");
					jumping.setAccessible(true);
				} catch (NoSuchFieldException ignore) {
				}
			}
		}

		@Override
		protected boolean isTypeNotPersistent() {
			return false;
		}

		@Override
		public void a(float f, float f1, float f2) {
			EntityPlayer rider = getRider();
			if (rider != null) {


				// do not target anything while being ridden
				setGoalTarget(null, null, false);

				// eject rider if in water or lava
				if (isInWater() || ax()) {
					ejectPassengers();
					rider.stopRiding();
					return;
				}

				// rotation
				setYawPitch(lastYaw = yaw = rider.yaw, pitch = rider.pitch * 0.5F);
				aS = aQ = yaw;

				// controls
				float forward = rider.bj;
				float strafe = rider.bh * 0.5F;
				if (forward <= 0.0F) {
					forward *= 0.25F;
				}

				if (jumping != null && !isJumping) {
					try {
						isJumping = jumping.getBoolean(rider);
					} catch (IllegalAccessException ignore) {
					}
				}

				if (isJumping && onGround) { // !isJumping
					motY = (double) 0.5f;
					MobEffect jump = getEffect(MobEffects.JUMP);
					if (jump != null) {
						motY += (double) ((float) (jump.getAmplifier() + 1) * 0.1F);
					}
					impulse = true;
					if (forward > 0.0F) {
						motX += (double) (-0.4F * MathHelper.sin(yaw * 0.017453292F) * 0.5f);
						motZ += (double) (0.4F * MathHelper.cos(yaw * 0.017453292F) * 0.5f);
					}
				}

				// move
				moveOnLand(this, strafe, f1, forward, 1f);

				if (onGround) {
					isJumping = false;
				}
				return;
			}
			super.a(f, f1, f2);
		}

		private EntityPlayer getRider(){
			if(passengers != null && !passengers.isEmpty()){
				net.minecraft.server.v1_13_R1.Entity entity = passengers.get(0);
				if(entity instanceof EntityPlayer){
					return (EntityPlayer)entity;
				}
			}
			return null;
		}

		public boolean di() {
			return bO() instanceof EntityLiving;
		}

		public boolean dW() {
			return true;
		}

		protected void g(EntityHuman entityhuman) {
			super.g(entityhuman);
		}

		public boolean isLeashed() {
			return getRider() != null || super.isLeashed();
		}

		public net.minecraft.server.v1_13_R1.Entity getLeashHolder() {
			EntityPlayer rider = getRider();
			return rider != null ? rider : super.getLeashHolder();
		}

		// hasCaravan
		public boolean en() {
			return (getRider() != null) || super.en();
		}

		@Override
		public NBTTagCompound save(NBTTagCompound nbttagcompound){
			return null;
		}
	}
}