package realcraft.bukkit.pets.pet.actions;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.data.PetDataMode;
import realcraft.bukkit.utils.EntityUtil;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.bukkit.utils.RandomUtil;

public class PetActionFollow extends PetAction {

    private static final int MAX_DISTANCE = 64;
    private static final int TELEPORT_DISTANCE = 32;
    private static final int MIN_DISTANCE_START = 3;
    private static final int MIN_DISTANCE_FINISH = 2;
    private static final int MAX_DISTANCE_LEVEL = 3;

    private int minDistanceLevel;

    public PetActionFollow(Pet pet) {
        super(PetActionType.FOLLOW, pet);
    }

    @Override
    public boolean isCancellable() {
        return true;
    }

    @Override
    public boolean shouldStart() {
        if (this.getPet().getPetData().getMode().getType() != PetDataMode.PetDataModeType.FOLLOW) {
            return false;
        }

        Location targetLoc = this._getTargetLocation();
        double distance = this.getEntity().getLocation().distanceSquared(targetLoc);

        if (distance < this.getMinDistance() * this.getMinDistance()) {
            return false;
        }

        return true;
    }

    public void addDistanceLevel() {
        this.minDistanceLevel = Math.min(MAX_DISTANCE_LEVEL, this.minDistanceLevel + 1);
    }

    public void resetDistanceLevel() {
        this.minDistanceLevel = 0;
    }

    public int getMinDistance() {
        return MIN_DISTANCE_START * (minDistanceLevel + 1);
    }

    private Location _getTargetLocation() {
        Location location = this.getPet().getPetPlayer().getPlayer().getLocation();
        location.add(this.getEntity().getLocation().subtract(location).toVector().normalize().setY(0));
        return location;
    }

    private Location _getSafeTeleportLocation() {
        Location location = this._getSafeTeleportLocation(TELEPORT_DISTANCE);
        if (location != null) {
            return location;
        }

        return this._getTargetLocation();
    }

    private Location _getSafeTeleportLocation(int minDistance) {
        Location location = this.getPet().getPetPlayer().getPlayer().getLocation();
        location.add(this.getEntity().getLocation().subtract(location).toVector().normalize().multiply(minDistance));

        if (LocationUtil.isBlockUnsafe(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
            return (minDistance > 1 ? this._getSafeTeleportLocation(minDistance - 1) : null);
        }

        return location;
    }

    @Override
    protected void _start() {
        this.getEntity().setAI(true);
        this.getEntity().setGravity(true);
        this.getEntity().setTarget(this.getPet().getPetPlayer().getPlayer());
        this._startTask(0, 10);
    }

    @Override
    protected void _run() {
        Location targetLoc = this._getTargetLocation();
        double distance = this.getEntity().getLocation().distanceSquared(targetLoc);

        if (this.getEntity().isInWater()) {
            this.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 200, 4));
        }

        if (distance > MAX_DISTANCE * MAX_DISTANCE || !this.getPet().getPetEntity().isTicking()) {
            this.getEntity().teleport(this._getSafeTeleportLocation());
        }

        if (distance < MIN_DISTANCE_FINISH * MIN_DISTANCE_FINISH) {
            this.finish();

            if (RandomUtil.getRandomBoolean()) {
                this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_GHAST_AMBIENT, 0.5f, 2f);
            }

            return;
        }

        if (distance < (MIN_DISTANCE_FINISH * MIN_DISTANCE_FINISH) * 6) {
            EntityUtil.navigate(this.getEntity(), targetLoc, 0.6);
            return;
        }

        if (distance < (MIN_DISTANCE_FINISH * MIN_DISTANCE_FINISH) * 8) {
            EntityUtil.navigate(this.getEntity(), targetLoc, 0.7);
            return;
        }

        if (distance < (MIN_DISTANCE_FINISH * MIN_DISTANCE_FINISH) * 10) {
            EntityUtil.navigate(this.getEntity(), targetLoc, 0.8);
            return;
        }

        if (LocationUtil.isSimilar(EntityUtil.getTargetLocation(this.getEntity()), targetLoc)) {
            return;
        }

        EntityUtil.navigate(this.getEntity(), targetLoc, 1.0);
    }

    @Override
    protected void _clear() {
        this.resetDistanceLevel();
        EntityUtil.clearPathfinders(this.getEntity());
    }
}
