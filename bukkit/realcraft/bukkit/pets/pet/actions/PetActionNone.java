package realcraft.bukkit.pets.pet.actions;

import org.bukkit.Location;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.timers.PetTimer;
import realcraft.bukkit.pets.pet.timers.PetTimerHeat;
import realcraft.bukkit.utils.EntityUtil;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.bukkit.utils.RandomUtil;
import realcraft.bukkit.wrappers.SafeLocation;

public class PetActionNone extends PetAction {

    private static final int NONE_STATE_DELAY = 8 * 20;

    public PetActionNone(Pet pet) {
        super(PetActionType.NONE, pet);
    }

    @Override
    public boolean isCancellable() {
        return true;
    }

    @Override
    public boolean shouldStart() {
        return this.getPet().getPetActions().getCurrentAction().getState() != PetAction.PetActionState.RUNNING;
    }

    private SafeLocation _getRandomLocation() {
        SafeLocation location = new SafeLocation(this.getEntity().getLocation().add(RandomUtil.getRandomInteger(-4, 4), 0, RandomUtil.getRandomInteger(-4, 4)));
        double distance = location.distanceSquared(this.getPet().getPetPlayer().getPlayer().getLocation());

        int minDistance = ((PetActionFollow) this.getPet().getPetActions().getAction(PetActionType.FOLLOW)).getMinDistance();
        if (distance >= minDistance * minDistance) {
            location = new SafeLocation(this.getEntity().getLocation());
            location.add(new SafeLocation(this.getPet().getPetPlayer().getPlayer().getLocation()).subtract(location).toVector().normalize().multiply(2));
            location.add(RandomUtil.getRandomInteger(-1, 1), 0, RandomUtil.getRandomInteger(-1, 1));
        }

        if (location.getWorld().hasStorm()) {
             if (LocationUtil.isLocationInRain(location) || LocationUtil.isBlockUnsafe(location)) {
                 location = new SafeLocation(this.getEntity().getLocation());
             }
        }

        if (this.getEntity().isInRain()) {
            Location noRainLocation = LocationUtil.getClosestNoRainLocation(this.getPet().getPetPlayer().getPlayer().getLocation(), minDistance);
            if (noRainLocation != null) {
                location = new SafeLocation(noRainLocation.toCenterLocation());
                location.add(new SafeLocation(this.getEntity().getLocation()).subtract(location).toVector().normalize().multiply(-1));
                return location;
            }
        }

        PetTimerHeat heatTimer = ((PetTimerHeat)this.getPet().getPetTimers().getTimer(PetTimer.PetTimerType.HEAT));
        if (heatTimer.isFreezing() && heatTimer.getClosestHeatSource() != null) {
            location = heatTimer.getClosestHeatSource().clone();
            location.add(RandomUtil.getRandomInteger(-3, 3), 0, RandomUtil.getRandomInteger(-3, 3));
        }

        return location;
    }

    private boolean _isCriticalState() {
        return this.getEntity().isInRain() || ((PetTimerHeat)this.getPet().getPetTimers().getTimer(PetTimer.PetTimerType.HEAT)).isFreezing();
    }

    @Override
    protected void _start() {
        this.getEntity().setAI(true);
        this.getEntity().setGravity(true);
        this.getEntity().getPathfinder().stopPathfinding();

        this._startTask(this._isCriticalState() ? NONE_STATE_DELAY / 2 : NONE_STATE_DELAY, 20);
    }

    @Override
    protected void _run() {
        if (this.getTicks() % 80 != 0) {
            return;
        }

        if (!this.getPet().getPetEntity().isLiving()) {
            return;
        }

        EntityUtil.navigate(this.getEntity(), this._getRandomLocation(), this._isCriticalState() ? 0.7 : 0.5);
    }

    @Override
    protected void _clear() {
        if (this.getEntity() != null) {
            this.getEntity().getPathfinder().stopPathfinding();
        }
    }
}
