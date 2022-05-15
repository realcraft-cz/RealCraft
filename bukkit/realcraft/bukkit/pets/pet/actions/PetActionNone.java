package realcraft.bukkit.pets.pet.actions;

import org.bukkit.Location;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.utils.EntityUtil;
import realcraft.bukkit.utils.RandomUtil;

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

    private Location _getRandomLocation() {
        Location location = this.getEntity().getLocation().add(RandomUtil.getRandomInteger(-4, 4), 0, RandomUtil.getRandomInteger(-4, 4));
        double distance = location.distanceSquared(this.getPet().getPetPlayer().getPlayer().getLocation());

        int minDistance = ((PetActionFollow) this.getPet().getPetActions().getAction(PetActionType.FOLLOW)).getMinDistance();
        if (distance >= minDistance * minDistance) {
            location = this.getEntity().getLocation();
            location.add(this.getPet().getPetPlayer().getPlayer().getLocation().subtract(location).toVector().normalize().multiply(2));
            location.add(RandomUtil.getRandomInteger(-1, 1), 0, RandomUtil.getRandomInteger(-1, 1));
        }

        return location;
    }

    @Override
    protected void _start() {
        this.getEntity().setAI(true);
        this.getEntity().setGravity(true);
        this.getEntity().getPathfinder().stopPathfinding();

        this._startTask(NONE_STATE_DELAY, 20);
    }

    @Override
    protected void _run() {
        if (this.getTicks() % 80 != 0) {
            return;
        }

        EntityUtil.navigate(this.getEntity(), this._getRandomLocation(), 0.5);
    }

    @Override
    protected void _clear() {
        if (this.getEntity() != null) {
            this.getEntity().getPathfinder().stopPathfinding();
        }
    }
}
