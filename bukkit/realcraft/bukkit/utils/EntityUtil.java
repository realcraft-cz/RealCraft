package realcraft.bukkit.utils;

import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class EntityUtil {

    public static void clearPathfinders(Entity entity) {
        ((EntityInsentient) ((CraftEntity) entity).getHandle()).bQ.a();
    }

    public static boolean navigate(Entity entity, Location location, double speed) {
        return ((EntityInsentient) ((CraftEntity) entity).getHandle()).D().a(location.getX(), location.getY(), location.getZ(), speed);
    }

    public static boolean navigate(Entity entity, Entity targetEntity, double speed) {
        return ((EntityInsentient) ((CraftEntity) entity).getHandle()).D().a(((CraftEntity)targetEntity).getHandle(), speed);
    }

    public static void setNavigationSpeed(Entity entity, double speed) {
        ((EntityInsentient) ((CraftEntity) entity).getHandle()).D().a(speed);
    }
}
