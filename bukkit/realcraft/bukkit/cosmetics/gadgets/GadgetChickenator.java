package realcraft.bukkit.cosmetics.gadgets;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.utils.FireworkUtil;
import realcraft.bukkit.utils.RandomUtil;

import java.util.ArrayList;
import java.util.UUID;

public class GadgetChickenator extends Gadget {

	public GadgetChickenator(CosmeticType type){
		super(type);
	}

	@Override
	public void trigger(Player player){
		final Chicken CHICKEN = (Chicken) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.CHICKEN);
		CHICKEN.setNoDamageTicks(500);
		CHICKEN.setVelocity(player.getLocation().getDirection().multiply(Math.PI / 1.5));
		player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_AMBIENT, 1.4f, 1.5f);
		player.playSound(player.getLocation(),Sound.ENTITY_GENERIC_EXPLODE, 0.3f, 1.5f);

		Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
			@Override
			public void run() {
				FireworkUtil.spawnFirework(CHICKEN.getLocation(), FireworkEffect.Type.BALL,false);
				player.playSound(player.getLocation(),Sound.ENTITY_CHICKEN_HURT, 1.4f, 1.5f);
				CHICKEN.remove();
				final ArrayList<Item> items = new ArrayList<>();
				for (int i = 0; i < 15; i++) {
					ItemStack itemstack = new ItemStack(Material.COOKED_CHICKEN,1);
					ItemMeta meta = itemstack.getItemMeta();
					meta.setDisplayName(UUID.randomUUID().toString());
					itemstack.setItemMeta(meta);
					final Item ITEM = CHICKEN.getWorld().dropItem(CHICKEN.getLocation(),itemstack);
					ITEM.setPickupDelay(0);
					ITEM.setVelocity(new Vector(RandomUtil.getRandomDouble(-0.5,0.5),RandomUtil.getRandomDouble(0.0,0.5),RandomUtil.getRandomDouble(-0.5,0.5)));
					items.add(ITEM);
				}
				Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
					@Override
					public void run() {
						for (Item i : items) i.remove();
						setGadgetRunning(player,false);
					}
				},10*20);
			}
		}, 9);
	}

	@EventHandler
	public void EntityPickupItemEvent(EntityPickupItemEvent event){
		if(event.getEntityType() == EntityType.PLAYER && event.getItem().getItemStack().getType() == Material.COOKED_CHICKEN && event.getItem().getTicksLived() < 10*20){
			Player player = (Player)event.getEntity();
			event.setCancelled(true);
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,30*20,7));
			player.getWorld().playSound(player.getLocation(),Sound.ENTITY_PLAYER_BURP,1f,1.5f);
			event.getItem().remove();
		}
	}
}