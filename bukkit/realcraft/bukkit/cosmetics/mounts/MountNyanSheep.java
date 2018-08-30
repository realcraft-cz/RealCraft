package realcraft.bukkit.cosmetics.mounts;

import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.entity.CreatureSpawnEvent;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.utils.Particles;
import realcraft.bukkit.utils.RandomUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MountNyanSheep extends Mount {

	private static List<RGBColor> colors = new ArrayList<>();

	static{
		colors.add(new RGBColor(255,0,0));
		colors.add(new RGBColor(255,165,0));
		colors.add(new RGBColor(255,255,0));
		colors.add(new RGBColor(154,205,50));
		colors.add(new RGBColor(30,144,255));
		colors.add(new RGBColor(148,0,211));
	}

	public MountNyanSheep(CosmeticType type){
		super(type);
	}

	@Override
	public Entity create(Player player){
		CustomEntity cEntity = new CustomEntity(((CraftWorld)player.getLocation().getWorld()).getHandle());
		cEntity.setLocation(player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ(),player.getLocation().getPitch(),player.getLocation().getYaw());
		((CraftWorld)player.getLocation().getWorld()).getHandle().addEntity(cEntity,CreatureSpawnEvent.SpawnReason.CUSTOM);
		Sheep entity = (Sheep)cEntity.getBukkitEntity();
		entity.setAdult();
		entity.setNoDamageTicks(Integer.MAX_VALUE);
		entity.setInvulnerable(true);
		entity.addPassenger(player);
		return entity;
	}

	@Override
	public void effect(Player player,Entity entity){
		((Sheep)entity).setColor(DyeColor.values()[RandomUtil.getRandomInteger(0,DyeColor.values().length-1)]);
		float y = 1.2f;
		for (RGBColor rgbColor : colors) {
			for (int i = 0; i < 10; i++){
				Particles.REDSTONE.display(Color.fromRGB(rgbColor.getRed(),rgbColor.getGreen(),rgbColor.getBlue()),entity.getLocation().add(entity.getLocation().getDirection().normalize().multiply(-1).multiply(1.4)).add(0,y,0),64f);
			}
			y -= 0.2;
		}
	}

	private static class RGBColor {

		int red;
		int green;
		int blue;

		public RGBColor(int red, int green, int blue) {
			this.red = red;
			this.green = green;
			this.blue = blue;
		}

		public int getBlue() {
			return blue;
		}

		public int getGreen() {
			return green;
		}

		public int getRed() {
			return red;
		}
	}

	private class CustomEntity extends EntitySheep {

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
				net.minecraft.server.v1_13_R2.Entity entity = passengers.get(0);
				if(entity instanceof EntityPlayer){
					return (EntityPlayer)entity;
				}
			}
			return null;
		}

		@Override
		public NBTTagCompound save(NBTTagCompound nbttagcompound){
			return null;
		}
	}
}