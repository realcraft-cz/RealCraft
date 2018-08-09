package realcraft.bukkit.cosmetics.gadgets;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.utils.Particles;
import realcraft.bukkit.utils.RandomUtil;

public class GadgetPartyPopper extends Gadget {

	private static final Material[] DYES = {
			Material.GRAY_DYE,
			Material.LIME_DYE,
			Material.MAGENTA_DYE,
			Material.CYAN_DYE,
			Material.LIGHT_BLUE_DYE,
			Material.LIGHT_GRAY_DYE,
			Material.ORANGE_DYE,
			Material.PINK_DYE,
			Material.PURPLE_DYE
	};

	public GadgetPartyPopper(CosmeticType type){
		super(type);
	}

	@Override
	public void trigger(Player player){
		this.setGadgetRunning(player,false);
		for (int i = 0; i < 40; i++) {
			//Vector rand = new Vector(Math.random() - 0.5D,Math.random() - 0.5D, Math.random() - 0.5D);
			Vector rand = new Vector(0,0,0);
			Particles.ITEM_CRACK.display(new ItemStack(DYES[RandomUtil.getRandomInteger(0,DYES.length-1)]), player.getEyeLocation().getDirection().add(rand.multiply(0.4)).multiply(1.2),0.6f, player.getEyeLocation(), 128);
			//Vector offset = player.getEyeLocation().getDirection().add(rand.multiply(0.4)).multiply(1.2);
			//player.getWorld().spawnParticle(Particle.ITEM_CRACK,player.getEyeLocation(),1,offset.getX(),offset.getY(),offset.getZ());
		}
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
	}
}