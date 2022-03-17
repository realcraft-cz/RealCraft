package realcraft.bukkit.cosmetics.gadgets;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.cosmetics.utils.BlockUtils;
import realcraft.bukkit.cosmetics.utils.UtilParticles;
import realcraft.bukkit.utils.Particles;

public class GadgetFreezeCannon extends Gadget {

	public GadgetFreezeCannon(CosmeticType type){
		super(type);
	}

	@Override
	public void trigger(Player player){
		final Item item = player.getWorld().dropItem(player.getEyeLocation(),new ItemStack(Material.ICE));
		item.setPickupDelay(50000);
		item.setVelocity(player.getEyeLocation().getDirection().multiply(0.9));

		BukkitRunnable runnable = new BukkitRunnable(){
			@Override
			public void run(){
				if(item != null){
					if(item.isOnGround()){
						item.remove();
						for (Block b : BlockUtils.getBlocksInRadius(item.getLocation(), 4, false)) BlockUtils.setToRestore(b, Material.ICE, 100);
						UtilParticles.display(Particles.FIREWORKS_SPARK, 4d, 3d, 4d, item.getLocation(), 80);
						setGadgetRunning(player,false);
						this.cancel();
					}
					else if(item.isDead() || item.getTicksLived() > 10*20){
						item.remove();
						setGadgetRunning(player,false);
						this.cancel();
					}
				}
			}
		};
		runnable.runTaskTimer(RealCraft.getInstance(),0,1);
	}
}