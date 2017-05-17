package com.gadgets;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.realcraft.RealCraft;
import com.realcraft.utils.FireworkUtil;
import com.utils.ItemFactory;

public class GadgetMelonThrower extends Gadget {

	static Random random = new Random();

	public GadgetMelonThrower(GadgetType type){
		super(type);
	}

	@Override
	public void onClick(final Player player){
		final Item MELON = player.getWorld().dropItem(player.getEyeLocation(), ItemFactory.create(Material.MELON_BLOCK, (byte) 0x0, UUID.randomUUID().toString()));
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
                	ItemStack itemstack = new ItemStack(Material.MELON,1);
                	ItemMeta meta = itemstack.getItemMeta();
                	meta.setDisplayName(UUID.randomUUID().toString());
                	itemstack.setItemMeta(meta);
                    final Item ITEM = MELON.getWorld().dropItem(MELON.getLocation(),itemstack);
                    ITEM.setPickupDelay(0);
                    ITEM.setVelocity(new Vector(random.nextDouble() - 0.5, random.nextDouble() / 2.0, random.nextDouble() - 0.5));
                    items.add(ITEM);
                }
                Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        for (Item i : items) i.remove();
                        setRunning(player,false);
                    }
                }, 5*20);
            }
        }, 9);
	}

	@EventHandler
    public void PlayerPickupItemEvent(PlayerPickupItemEvent event){
		if(event.getItem().getItemStack().getType() == Material.MELON && event.getItem().getTicksLived() < 5*20){
			event.setCancelled(true);
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED,20*20,2));
			event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_BURP, 1.4f, 1.5f);
            event.getItem().remove();
		}
	}
}