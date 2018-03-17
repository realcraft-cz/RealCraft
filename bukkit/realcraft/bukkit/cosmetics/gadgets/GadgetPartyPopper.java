package realcraft.bukkit.cosmetics.gadgets;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import realcraft.bukkit.utils.Particles;

public class GadgetPartyPopper extends Gadget {

	public GadgetPartyPopper(GadgetType type){
		super(type);
	}

	@Override
	public void onClick(final Player player){
		this.setRunning(player,false);
		for (int i = 0; i < 40; i++) {
            Vector rand = new Vector(Math.random() - 0.5D,Math.random() - 0.5D, Math.random() - 0.5D);
            Particles.ITEM_CRACK.display(new Particles.ItemData(Material.INK_SACK,randomByte(15)), player.getEyeLocation().getDirection().add(rand.multiply(0.4)).multiply(1.2),0.6f, player.getEyeLocation(), 128);
        }
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
	}

	public static byte randomByte(int max) {
		Random random = new Random();
        return (byte) random.nextInt(max + 1);
    }
}