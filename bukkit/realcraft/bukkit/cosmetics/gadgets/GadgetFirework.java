package realcraft.bukkit.cosmetics.gadgets;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.utils.RandomUtil;

public class GadgetFirework extends Gadget {

	public GadgetFirework(CosmeticType type){
		super(type);
	}

	@Override
	public void trigger(Player player){
		this.setGadgetRunning(player,false);
		Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK_ROCKET);
		FireworkMeta fwm = fw.getFireworkMeta();

		int rt = RandomUtil.getRandomInteger(0,4);
		FireworkEffect.Type type = FireworkEffect.Type.values()[rt];

		Color c1 = Color.fromRGB(RandomUtil.getRandomInteger(0,255),RandomUtil.getRandomInteger(0,255),RandomUtil.getRandomInteger(0,255));
		Color c2 = Color.fromRGB(RandomUtil.getRandomInteger(0,255),RandomUtil.getRandomInteger(0,255),RandomUtil.getRandomInteger(0,255));

		FireworkEffect effect = FireworkEffect.builder().flicker(RandomUtil.getRandomBoolean())
				.withColor(c1).withFade(c2).with(type)
				.trail(RandomUtil.getRandomBoolean()).build();

		fwm.addEffect(effect);
		fwm.setPower(RandomUtil.getRandomInteger(0,2));
		fw.setFireworkMeta(fwm);
	}
}