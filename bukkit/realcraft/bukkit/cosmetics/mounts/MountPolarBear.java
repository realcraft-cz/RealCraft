package realcraft.bukkit.cosmetics.mounts;

import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
import org.bukkit.event.entity.CreatureSpawnEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.utils.Particles;

import java.lang.reflect.Field;

public class MountPolarBear extends Mount {

	public MountPolarBear(CosmeticType type){
		super(type);
	}

	@Override
	public Entity create(Player player){
		CustomEntity cEntity = new CustomEntity(((CraftWorld)player.getLocation().getWorld()).getHandle());
		cEntity.setLocation(player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ(),player.getLocation().getPitch(),player.getLocation().getYaw());
		((CraftWorld)player.getLocation().getWorld()).getHandle().addEntity(cEntity,CreatureSpawnEvent.SpawnReason.CUSTOM);
		PolarBear entity = (PolarBear)cEntity.getBukkitEntity();
		entity.setAdult();
		entity.setNoDamageTicks(Integer.MAX_VALUE);
		entity.setInvulnerable(true);
		entity.addPassenger(player);
		return entity;
	}

	@Override
	public void effect(Player player,Entity entity){
		Particles.CLOUD.display(0.5f, 0.2f, 0.5f, 0f, 5, entity.getLocation().clone().add(0, 1, 0));
	}

	private class CustomEntity extends EntityPolarBear {

		private Field jumping;
		private boolean isJumping = false;

		public CustomEntity(World world){
			super(world);
			Q = 1f;

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
				Q = 1f;

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
						if (isJumping && onGround && !isStanding() && forward == 0 && strafe == 0) {
							isJumping = false;
							setStanding(true);
							this.a(SoundEffects.ENTITY_POLAR_BEAR_WARNING, 1.0F, 1.0F);
							Bukkit.getServer().getScheduler().runTaskLater(
									RealCraft.getInstance(),
									() -> setStanding(false), 20);
						}
					} catch (IllegalAccessException ignore) {
					}
				}

				if (isJumping && onGround && !isStanding()) {
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

		private EntityPlayer getRider() {
			if (passengers != null && !passengers.isEmpty()) {
				net.minecraft.server.v1_14_R1.Entity entity = passengers.get(0);
				if (entity instanceof EntityPlayer) {
					return (EntityPlayer) entity;
				}
			}
			return null;
		}

		private boolean isStanding() {
			return dz();
		}

		private void setStanding(boolean standing){
			s(standing);
		}

		@Override
		public NBTTagCompound save(NBTTagCompound nbttagcompound){
			return null;
		}
	}
}