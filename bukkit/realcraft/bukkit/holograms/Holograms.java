package realcraft.bukkit.holograms;

import java.util.HashMap;

public class Holograms {

    private static int hologramIncrementId = 1200 * 1000;
    private static final HashMap<Integer, Hologram> holograms = new HashMap<>();

    public static Hologram createHologram() {
        Hologram hologram = new Hologram(hologramIncrementId ++);
        holograms.put(hologram.getId(), hologram);
        return hologram;
    }

    public static void _removeHologram(Hologram hologram) {
        holograms.remove(hologram.getId());
    }
}
