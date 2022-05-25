package realcraft.bukkit.pets.pet.timers;

import realcraft.bukkit.pets.pet.Pet;

public class PetTimerFood extends PetTimer {

    private static final int FOOD_DECREMENT_DISTANCE = 300;

    private int lastStatDistance;
    private int incrementalDistance;

    public PetTimerFood(Pet pet) {
        super(PetTimerType.FOOD, pet);
    }

    @Override
    protected void _run() {
        if (!this.getPet().getPetEntity().isLiving()) {
            return;
        }

        int statDistance = this.getPet().getPetData().getStatDistance().getValue();

        if (lastStatDistance == 0) {
            lastStatDistance = statDistance;
        }

        if (statDistance == lastStatDistance) {
            return;
        }

        incrementalDistance += (statDistance - lastStatDistance);

        if (incrementalDistance >= FOOD_DECREMENT_DISTANCE) {
            incrementalDistance = 0;
            this.getPet().getPetData().getFood().setValue(this.getPet().getPetData().getFood().getValue() - 1);
        }

        lastStatDistance = statDistance;
    }
}
