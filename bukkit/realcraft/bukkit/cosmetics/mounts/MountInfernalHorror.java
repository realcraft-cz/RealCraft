package realcraft.bukkit.cosmetics.mounts;

import net.minecraft.server.v1_13_R1.EntityHorseSkeleton;
import net.minecraft.server.v1_13_R1.GenericAttributes;
import net.minecraft.server.v1_13_R1.NBTTagCompound;
import net.minecraft.server.v1_13_R1.World;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftSkeletonHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.cosmetics2.utils.UtilParticles;
import realcraft.bukkit.utils.Particles;

public class MountInfernalHorror extends Mount {

	public MountInfernalHorror(CosmeticType type){
		super(type);
	}

	@Override
	public Entity create(Player player){
		CustomEntity cEntity = new CustomEntity(((CraftWorld)player.getLocation().getWorld()).getHandle());
		cEntity.setLocation(player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ(),player.getLocation().getPitch(),player.getLocation().getYaw());
		((CraftWorld)player.getLocation().getWorld()).getHandle().addEntity(cEntity,CreatureSpawnEvent.SpawnReason.CUSTOM);
		SkeletonHorse entity = (SkeletonHorse)cEntity.getBukkitEntity();
		entity.setAdult();
		entity.setDomestication(1);
		entity.setTamed(true);
		entity.getInventory().setSaddle(new ItemStack(Material.SADDLE));
		entity.setJumpStrength(1);
		entity.setNoDamageTicks(Integer.MAX_VALUE);
		entity.setInvulnerable(true);
		((CraftSkeletonHorse) entity).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.4d);
		entity.addPassenger(player);
		return entity;
	}

	@Override
	public void effect(Player player,Entity entity){
		UtilParticles.display(Particles.FLAME, 0.4f, 0.2f, 0.4f, entity.getLocation().clone().add(0, 1, 0), 5);
	}

	private class CustomEntity extends EntityHorseSkeleton {

		public CustomEntity(World world){
			super(world);
		}

		@Override
		protected boolean isTypeNotPersistent(){
			return false;
		}

		@Override
		public NBTTagCompound save(NBTTagCompound nbttagcompound){
			return null;
		}
	}
}