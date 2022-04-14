package realcraft.bukkit.wrappers;

import org.bukkit.Location;
import ru.beykerykt.minecraft.lightapi.common.LightAPI;
import ru.beykerykt.minecraft.lightapi.common.api.engine.LightFlag;

public class LightApi {

    public static int createLight(Location location, int lightlevel) {
        return LightApi.createLight(location, lightlevel, false);
    }

    public static int createLight(Location location, int lightlevel, boolean async) {
        return LightAPI.get().setLightLevel(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), lightlevel);
    }

    public static int deleteLight(Location location, boolean async) {
        int level = LightAPI.get().getLightLevel(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), LightFlag.SKY_LIGHTING);
        return LightApi.createLight(location, level);
    }
}
