package realcraft.bukkit.wrappers;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.beykerykt.lightapi.chunks.ChunkInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LightApi {

    public static boolean createLight(Location location, int lightlevel, boolean async) {
        return true;
        //return LightAPI.createLight(location, lightlevel, async);
    }

    public static boolean deleteLight(Location location, boolean async) {
        return true;
        //return LightAPI.deleteLight(location, async);
    }

    public static boolean updateChunk(ChunkInfo info) {
        return true;
        //return LightAPI.updateChunk(info);
    }

    public static boolean updateChunk(Location location, Collection<? extends Player> players) {
        return true;
        //return LightAPI.updateChunk(location, players);
    }

    public static List<ChunkInfo> collectChunks(Location location) {
        return new ArrayList<ChunkInfo>();
        //return LightAPI.collectChunks(location);
    }
}
