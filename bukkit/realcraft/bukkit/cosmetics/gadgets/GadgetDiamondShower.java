package realcraft.bukkit.cosmetics.gadgets;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.cosmetics.utils.MathUtils;
import realcraft.bukkit.utils.RandomUtil;

import java.util.ArrayList;

public class GadgetDiamondShower extends Gadget {

	private static final Material [] MATERIALS = {
			Material.DIAMOND,
			Material.DIAMOND_BLOCK,
			Material.DIAMOND_CHESTPLATE,
			Material.DIAMOND_PICKAXE,
			Material.DIAMOND_ORE,
			Material.DIAMOND_SWORD
	};

	public GadgetDiamondShower(CosmeticType type){
		super(type);
	}

	@Override
	public void trigger(Player player){
		ArrayList<Entity> entities = new ArrayList<>();
		BukkitRunnable runnable = new BukkitRunnable(){
			int step = 0;
			@Override
			public void run(){
				step ++;
				if(!player.isOnline() || !getType().getCategory().isAvailable(player.getWorld())){
					for(Entity entity : entities){
						entity.remove();
					}
					this.cancel();
					setGadgetRunning(player,false);
					return;
				}
				if(step <= 50){
					for(int i=0;i<3;i++){
						Item item = player.getWorld().dropItem(player.getEyeLocation(),new ItemStack(MATERIALS[RandomUtil.getRandomInteger(0,MATERIALS.length-1)]));
						item.setPickupDelay(Integer.MAX_VALUE);
						item.setVelocity(new Vector(0, 0.5, 0).add(MathUtils.getRandomCircleVector().multiply(RandomUtil.getRandomDouble(0.05,0.2))));
						item.getWorld().playSound(item.getLocation(), Sound.ENTITY_CHICKEN_EGG, 0.2f, 1.0f);
						entities.add(item);
					}
				}
				else if(step == 60){
					for(Entity entity : entities){
						entity.remove();
					}
					this.cancel();
					setGadgetRunning(player,false);
				}
			}
		};
		runnable.runTaskTimer(RealCraft.getInstance(),0,3);
	}
}