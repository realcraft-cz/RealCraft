package realcraft.bukkit.pets.pet.timers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.wrappers.SafeLocation;

public class PetTimerHeat extends PetTimer {

    private static final int MAX_HEAT_SOURCE_DISTANCE = 6;

    private SafeLocation closestHeatSource;

    public PetTimerHeat(Pet pet) {
        super(PetTimerType.HEAT, pet);
    }

    public @Nullable SafeLocation getClosestHeatSource() {
        return closestHeatSource;
    }

    public boolean isFreezing() {
        if (this.getEntity().isInPowderedSnow()) {
            return true;
        }

        if (this.getEntity().isInRain()) {
            return true;
        }

        if (this.getEntity().getLocation().getBlock().getTemperature() < 0) {
            return true;
        }

        if (this.getEntity().getLocation().getBlock().getTemperature() >= 0.5) {
            return false;
        }

        if (closestHeatSource == null || closestHeatSource.distance(this.getEntity().getLocation()) > MAX_HEAT_SOURCE_DISTANCE) {
            return true;
        }

        return false;
    }

    @Override
    protected void _run() {
        if (!this.getPetEntity().isLiving()) {
            return;
        }

        if (this.getEntity().isInPowderedSnow()) {
            this._addHeat(-3);
            return;
        }

        if (this.getEntity().isInRain()) {
            this._addHeat(-1);
            return;
        }

        Location location = this.getEntity().getLocation();
        double temperature = location.getBlock().getTemperature();

        if (temperature >= 0.5) {
            this._addHeat(1);
        } else {
            closestHeatSource = this._getClosestHeatSource(location, MAX_HEAT_SOURCE_DISTANCE);
            if (closestHeatSource != null) {
                this._addHeat(closestHeatSource.distance(location) > 4 ? 1 : 2);
                return;
            }

            if (temperature < 0) {
                this._addHeat(-1);
            }
        }
    }

    private void _addHeat(int value) {
        int oldValue = this.getPet().getPetData().getHeat().getValue();

        this.getPet().getPetData().getHeat().setValue(this.getPet().getPetData().getHeat().getValue() + value);

        if (this.getPet().getPetData().getHeat().getValue() <= this.getPet().getPetData().getHeat().getCriticalValue()) {
            this.getPetEntity().setShaking(true);
            for (int i = 0; i < 5; i++) {
                Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        if (!getPetEntity().isSpawned()) {
                            return;
                        }

                        getPetEntity().getEntity().getWorld().spawnParticle(Particle.SNOWFLAKE, getPetEntity().getEntity().getLocation().add(0, 1, 0), 1, 0.3, 0.2, 0.3, 0);
                    }
                }, i * 20);
            }
        } else {
            this.getPetEntity().setShaking(false);
        }

        if (this.getPet().getPetData().getHeat().getValue() == oldValue) {
            return;
        }

        if (value > 0) {
            for (int i = 0; i < 10; i++) {
                Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        if (!getPetEntity().isSpawned()) {
                            return;
                        }

                        getPetEntity().getEntity().getWorld().spawnParticle(Particle.WAX_ON, getPetEntity().getEntity().getLocation().add(0, 1, 0), 1, 0.3, 0.3, 0.3, 0);
                    }
                }, i * 10);
            }
        }
    }

    protected @Nullable SafeLocation _getClosestHeatSource(Location location, int radius) {
        Location source = null;
        int minDistance = Integer.MAX_VALUE;
        int dist;
        Block block;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    block = location.getBlock().getRelative(x, y, z);
                    if (block.getType() == Material.CAMPFIRE || block.getType() == Material.SOUL_CAMPFIRE || block.getType() == Material.LAVA) {
                        dist = (int) location.distanceSquared(block.getLocation());
                        if (dist < minDistance) {
                            minDistance = dist;
                            source = block.getLocation();
                        }
                    }
                }
            }
        }

        return source != null ? new SafeLocation(source) : null;
    }
}
