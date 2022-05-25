package realcraft.bukkit.pets.pet.timers;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.wrappers.SafeLocation;

public class PetTimerStatDistance extends PetTimer {

    private static final int MAX_DISTANCE_INCREMENT = 32;

    private SafeLocation lastLocation;

    public PetTimerStatDistance(Pet pet) {
        super(PetTimerType.STAT_DISTANCE, pet);
    }

    @Override
    protected void _run() {
        SafeLocation location = new SafeLocation(this.getPet().getPetEntity().getEntity().getLocation());

        if (lastLocation != null) {
            if (location.equals(lastLocation)) {
                return;
            }

            int distance = (int) location.distance(lastLocation);

            if (distance > 0 && distance < MAX_DISTANCE_INCREMENT) {
                this.getPet().getPetData().getStatDistance().setValue(this.getPet().getPetData().getStatDistance().getValue() + distance);
            }
        }

        lastLocation = location;
    }
}
