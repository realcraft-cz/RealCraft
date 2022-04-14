package realcraft.bukkit.test;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.utils.EntityUtil;
import realcraft.bukkit.utils.ItemUtil;

public class EntityTest implements Listener {

    public EntityTest() {
        Bukkit.getPluginManager().registerEvents(this, RealCraft.getInstance());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        String command = event.getMessage().substring(1);
        if (command.startsWith("entity") && player.hasPermission("group.Manazer")) {
            event.setCancelled(true);
        } else {
            return;
        }

        Location location = player.getLocation();
        location.setPitch(0f);
        location.add(location.getDirection().setY(0).normalize().multiply(5));

        ItemStack item = ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTQ3MzIyZjgzMWUzYzE2OGNmYmQzZTI4ZmU5MjUxNDRiMjYxZTc5ZWIzOWM3NzEzNDlmYWM1NWE4MTI2NDczIn19fQ");
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("zombik");
        item.setItemMeta(meta);

        Zombie entity = (Zombie) player.getWorld().spawnEntity(location, EntityType.ZOMBIE, false);
        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_VEX_CHARGE, 1f, 1f);
        entity.setSilent(true);
        entity.setBaby();
        entity.getEquipment().clear();
        entity.getEquipment().setHelmet(item);
        entity.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
        entity.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
        entity.getEquipment().setItemInMainHandDropChance(0);
        entity.getEquipment().setItemInOffHandDropChance(0);
		entity.setTarget(null);

        EntityUtil.clearPathfinders(entity);

		Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (!entity.isDead()) {
                    EntityUtil.navigate(entity, player.getLocation(), 0.5);
				}
			}
		}, 20, 20);
    }
}
