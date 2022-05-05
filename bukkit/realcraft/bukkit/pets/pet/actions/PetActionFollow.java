package realcraft.bukkit.pets.pet.actions;

import org.bukkit.Location;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.data.PetDataMode;
import realcraft.bukkit.utils.EntityUtil;
import realcraft.bukkit.utils.LocationUtil;

public class PetActionFollow extends PetAction {

    private static final int MAX_FOLLOW_DISTANCE = 64;
    private static final int MIN_FOLLOW_DISTANCE_START = 4;
    private static final int MIN_FOLLOW_DISTANCE_FINISH = 3;

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

        Location targetLoc = this.getTargetLocation();
        double distance = this.getEntity().getLocation().distanceSquared(targetLoc);

        if (distance < MIN_FOLLOW_DISTANCE_START * MIN_FOLLOW_DISTANCE_START) {
            return false;
        }

        return true;
    }

    private Location getTargetLocation() {
        return this.getPet().getPetPlayer().getPlayer().getLocation();
    }

    @Override
    protected void _start() {
        this.getEntity().setAI(true);
        this.getEntity().setGravity(true);
    }

    @Override
    protected void _clear() {
        EntityUtil.clearPathfinders(this.getEntity());
    }

    @Override
    public void run() {
        Location targetLoc = this.getTargetLocation();
        double distance = this.getEntity().getLocation().distanceSquared(targetLoc);

        if (distance > MAX_FOLLOW_DISTANCE * MAX_FOLLOW_DISTANCE) {
            this.getEntity().teleport(targetLoc);
            this.finish();
            return;
        }

        if (distance < MIN_FOLLOW_DISTANCE_FINISH * MIN_FOLLOW_DISTANCE_FINISH) {
            this.finish();
            return;
        }

        if (LocationUtil.isSimilar(EntityUtil.getTargetLocation(this.getEntity()), targetLoc)) {
            return;
        }

        EntityUtil.navigate(this.getEntity(), targetLoc, 1.0);
    }
}
