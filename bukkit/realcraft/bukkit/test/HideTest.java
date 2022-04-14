package realcraft.bukkit.test;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.others.AbstractCommand;

public class HideTest extends AbstractCommand implements Listener {

    public HideTest() {
        super("hide");
        Bukkit.getPluginManager().registerEvents(this, RealCraft.getInstance());
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.hasPermission("group.Manazer")) {
            return;
        }

        player.sendBlockChange(player.getLocation().add(2, 0, 0),Material.AIR.createBlockData());

        if (true) {
            return;
        }

        //CustomFallingBlock block = new CustomFallingBlock(((CraftWorld)player.getWorld()).getHandle());
        //((CraftWorld)player.getWorld()).getHandle().b(new BlockPosition(player.getLocation().getX() + 2, player.getLocation().getY() + 2, player.getLocation().getZ()), ibd);
        //((CraftWorld)player.getWorld()).getHandle().b(block);

        ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(4, 0, 0), EntityType.ARMOR_STAND);
        stand.setGravity(false);
        stand.setPersistent(false);
        stand.setSmall(true);

        FallingBlock block = player.getWorld().spawnFallingBlock(player.getLocation().add(2, 2, 0), new MaterialData(Material.GRASS_BLOCK));
        block.setGravity(false);
        block.setInvulnerable(true);
        block.setPersistent(false);
        //FallingBlock block = (FallingBlock) player.getWorld().spawnEntity(player.getLocation().add(2, 0, 0), EntityType.FALLING_BLOCK);

        //((CraftWorld)player.getWorld()).getHandle().addFreshEntity(block, CreatureSpawnEvent.SpawnReason.CUSTOM);

        stand.addPassenger(block);

        Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(),new Runnable() {
            @Override
            public void run(){
                if (block.isDead()) {
                    return;
                }
                if (stand.isDead()) {
                    return;
                }
                if (player.isDead()) {
                    return;
                }

                //block.remove();
                block.setTicksLived(1);
                stand.setSmall(true);
                //((CraftArmorStand)stand).getHandle().teleportTo(((CraftWorld)stand.getWorld()).getHandle(), new BlockPosition(player.getLocation().getX() + 0.0, player.getLocation().getY() - 0.7, player.getLocation().getZ() + 0.0));
                ((CraftArmorStand)stand).getHandle().c(player.getLocation().getX() + 0.0, player.getLocation().getY() - 0.70, player.getLocation().getZ() + 0.0);
            }
        }, 1, 1);
    }

    /*static class CustomFallingBlock extends EntityFallingBlock {

        public FallingBlock(World world) {
            super(EntityTypes.C, world);
            this.teleportTo()
        }
    }*/
}
