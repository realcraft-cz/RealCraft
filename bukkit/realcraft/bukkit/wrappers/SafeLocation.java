package realcraft.bukkit.wrappers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class SafeLocation extends Location {

    private final String worldName;

    public SafeLocation(Location location) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.worldName = location.getWorld().getName();
    }

    @Override
    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    @Override
    public @NotNull Location add(@NotNull Location vec) {
        try {
            super.add(vec);
        } catch (IllegalArgumentException ignored) {
        }

        return this;
    }

    @Override
    public @NotNull Location subtract(@NotNull Location vec) {
        try {
            super.subtract(vec);
        } catch (IllegalArgumentException ignored) {
        }

        return this;
    }

    public double distanceSquared(@NotNull Location o) {
        try {
            return super.distanceSquared(o);
        } catch (IllegalArgumentException ignored) {
        }

        return Double.MAX_VALUE;
    }

    public @NotNull SafeLocation clone() {
        return (SafeLocation) super.clone();
    }
}
