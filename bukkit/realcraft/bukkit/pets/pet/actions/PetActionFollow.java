package realcraft.bukkit.pets.pet.actions;

import org.bukkit.Location;
import org.bukkit.Sound;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.data.PetDataMode;
import realcraft.bukkit.utils.EntityUtil;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.bukkit.utils.RandomUtil;

public class PetActionFollow extends PetAction {

    private static final int MAX_DISTANCE = 64;
    private static final int MIN_DISTANCE_START = 3;
    private static final int MIN_DISTANCE_FINISH = 3;

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

    private int getMinDistance() {
        return MIN_DISTANCE_START * (minDistanceLevel + 1);
    }

    private Location _getTargetLocation() {
        return this.getPet().getPetPlayer().getPlayer().getLocation();
    }

    @Override
    protected void _start() {
        this.getEntity().setAI(true);
        this.getEntity().setGravity(true);
        this._startTask(0, 10);
    }

    @Override
    protected void _run() {
        Location targetLoc = this._getTargetLocation();
        double distance = this.getEntity().getLocation().distanceSquared(targetLoc);

        if (distance > MAX_DISTANCE * MAX_DISTANCE) {
            this.getEntity().teleport(targetLoc);
            this.finish();
            return;
        }

        if (distance < MIN_DISTANCE_FINISH * MIN_DISTANCE_FINISH) {
            this.finish();

            if (RandomUtil.getRandomBoolean()) {
                this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_GHAST_AMBIENT, 0.5f, 2f);
            }

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
