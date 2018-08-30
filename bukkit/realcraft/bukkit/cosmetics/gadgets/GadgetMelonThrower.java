package realcraft.bukkit.cosmetics.gadgets;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import realcraft.bukkit.cosmetics.utils.ItemFactory;
import realcraft.bukkit.utils.FireworkUtil;
import realcraft.bukkit.utils.RandomUtil;

import java.util.ArrayList;
import java.util.UUID;

public class GadgetMelonThrower extends Gadget {

	public GadgetMelonThrower(CosmeticType type){
		super(type);
	}

	@Override
	public void trigger(Player player){
		final Item MELON = player.getWorld().dropItem(player.getEyeLocation(), ItemFactory.create(Material.MELON,UUID.randomUUID().toString()));
		MELON.setPickupDelay(1000);
		MELON.setVelocity(player.getEyeLocation().getDirection().multiply(1.3d));
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.4f, 1.5f);

		Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
			@Override
			public void run() {
				FireworkUtil.spawnFirework(MELON.getLocation(),FireworkEffect.Type.BALL,false);
				MELON.remove();
				final ArrayList<Item> items = new ArrayList<>();
				for (int i = 0; i < 15; i++){
					ItemStack itemstack = new ItemStack(Material.MELON_SLICE,1);
					ItemMeta meta = itemstack.getItemMeta();
					meta.setDisplayName(UUID.randomUUID().toString());
					itemstack.setItemMeta(meta);
					final Item ITEM = MELON.getWorld().dropItem(MELON.getLocation(),itemstack);
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
		if(this.getType().getCategory().isAvailable(event.getEntity().getWorld()) && event.getEntityType() == EntityType.PLAYER && event.getItem().getItemStack().getType() == Material.MELON_SLICE && event.getItem().getTicksLived() < 10*20){
			Player player = (Player)event.getEntity();
			event.setCancelled(true);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,20*20,2));
			player.getWorld().playSound(player.getLocation(),Sound.ENTITY_PLAYER_BURP,1f,1.5f);
			event.getItem().remove();
		}
	}
}