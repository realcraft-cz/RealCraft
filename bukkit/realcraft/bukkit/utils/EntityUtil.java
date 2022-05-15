package realcraft.bukkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

public class EntityUtil {

    public static void clearPathfinders(Entity entity) {
        if (entity == null) {
            return;
        }

        ((Mob)entity).getPathfinder().stopPathfinding();
        Bukkit.getMobGoals().removeAllGoals((Mob)entity);
    }

    public static boolean navigate(Entity entity, Location location, double speed) {
        if (entity == null) {
            return false;
        }

        return ((Mob)entity).getPathfinder().moveTo(location, speed);
        //return ((EntityInsentient) ((CraftEntity) entity).getHandle()).D().a(location.getX(), location.getY(), location.getZ(), speed);
    }

    public static void setNavigationSpeed(Entity entity, double speed) {
        if (entity == null) {
            return;
        }

        if (((Mob)entity).getPathfinder().getCurrentPath() != null) {
            ((Mob)entity).getPathfinder().moveTo(((Mob)entity).getPathfinder().getCurrentPath(), speed);
        }
    }

    public static Location getTargetLocation(Entity entity) {
        if (entity == null) {
            return null;
        }

        if (((Mob)entity).getPathfinder().getCurrentPath() == null) {
            return null;
        }

        return ((Mob)entity).getPathfinder().getCurrentPath().getFinalPoint();
    }
}
