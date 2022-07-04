package realcraft.bukkit.wrappers;

import org.bukkit.Location;

public class LightApi {

    public static int createLight(Location location, int lightlevel) {
        return 0;
        //return LightApi.createLight(location, lightlevel, false);
    }

    public static int createLight(Location location, int lightlevel, boolean async) {
        return 0;
        //return LightAPI.get().setLightLevel(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), lightlevel);
    }

    public static int deleteLight(Location location, boolean async) {
        return 0;
        //int level = LightAPI.get().getLightLevel(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), LightFlag.SKY_LIGHTING);
        //return LightApi.createLight(location, level);
    }
}
