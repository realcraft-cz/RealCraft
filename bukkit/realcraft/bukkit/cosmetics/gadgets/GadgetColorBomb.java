package realcraft.bukkit.cosmetics.gadgets;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.cosmetics2.utils.ItemFactory;
import realcraft.bukkit.cosmetics2.utils.MathUtils;
import realcraft.bukkit.utils.MaterialUtil;
import realcraft.bukkit.utils.Particles;
import realcraft.bukkit.utils.RandomUtil;

import java.util.UUID;

public class GadgetColorBomb extends Gadget {

	public GadgetColorBomb(CosmeticType type){
		super(type);
	}

	@Override
	public void trigger(Player player){
		ItemStack itemstack = new ItemStack(MaterialUtil.getWool(DyeColor.values()[RandomUtil.getRandomInteger(0,DyeColor.values().length-1)]));
		ItemMeta meta = itemstack.getItemMeta();
		meta.setDisplayName(UUID.randomUUID().toString());
		itemstack.setItemMeta(meta);

		final Item bomb = player.getWorld().dropItem(player.getEyeLocation(),itemstack);
		bomb.setPickupDelay(50000);
		bomb.setVelocity(player.getEyeLocation().getDirection().multiply(0.7532));

		BukkitRunnable runnable = new BukkitRunnable(){
			private boolean running = false;
			@Override
			public void run(){
				final BukkitRunnable instance = this;
				if(bomb != null && !running){
					if(bomb.isOnGround()){
						running = true;
						bomb.setVelocity(new Vector(0, 0, 0));
						Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
							@Override
							public void run(){
								bomb.remove();
								running = false;
								setGadgetRunning(player,false);
								instance.cancel();
							}
						},200);
					}
					else if(bomb.isDead() || bomb.getTicksLived() > 10*20){
						bomb.remove();
						running = false;
						setGadgetRunning(player,false);
						instance.cancel();
					}
				}
				if (running) {
					Particles effect;
					switch (RandomUtil.getRandomInteger(0,5)){
						default:
							effect = Particles.FIREWORKS_SPARK;
							break;
						case 1:
							effect = Particles.FIREWORKS_SPARK;
							break;
						case 4:
							effect = Particles.FLAME;
							break;
						case 5:
							effect = Particles.SPELL_WITCH;
							break;
					}
					effect.display(0, 0, 0, 0.2f, 1, bomb.getLocation(), 128);
					try {
						Bukkit.getScheduler().runTask(RealCraft.getInstance(), new Runnable() {
							@Override
							public void run() {
								final Item i = bomb.getWorld().dropItem(bomb.getLocation().add(0, 0.15f, 0), ItemFactory.create(MaterialUtil.getWool(DyeColor.values()[RandomUtil.getRandomInteger(0,DyeColor.values().length-1)]),UUID.randomUUID().toString()));
								i.setPickupDelay(500000);
								i.setVelocity(new Vector(0, 0.5, 0).add(MathUtils.getRandomCircleVector().multiply(0.15)));

								i.getWorld().playSound(i.getLocation(), Sound.ENTITY_CHICKEN_EGG, 0.2f, 1.0f);

								Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
									@Override
									public void run(){
										i.remove();
									}
								},20);

								for(Entity entity : bomb.getNearbyEntities(1.5, 1, 1.5)) {
									if (entity instanceof Player) {
										entity.setVelocity(new Vector(0,0.5,0).add(getRandomCircleVector().multiply(0.1)));
									}
								}
							}
						});
					} catch (Exception exc) {
					}
				}
			}
		};
		runnable.runTaskTimerAsynchronously(RealCraft.getInstance(),0,1);
	}

	public Vector getRandomCircleVector() {
		double rnd = RandomUtil.getRandomDouble(0.0,1.0) * 2.0D * 3.141592653589793D;
		double x = Math.cos(rnd);
		double z = Math.sin(rnd);
		return new Vector(x, 0.0D, z);
	}
}