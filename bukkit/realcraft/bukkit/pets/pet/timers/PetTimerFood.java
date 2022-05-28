package realcraft.bukkit.pets.pet.timers;

import realcraft.bukkit.pets.pet.Pet;

public class PetTimerFood extends PetTimer {

    private static final int FOOD_DECREMENT_DISTANCE = 300;
    private static final int FOOD_DECREMENT_KILLS = 5;

    private int lastStatDistance;
    private int incrementalDistance;

    private int lastStatKills;
    private int incrementalKills;

    public PetTimerFood(Pet pet) {
        super(PetTimerType.FOOD, pet);
    }

    @Override
    protected void _run() {
        if (!this.getPet().getPetEntity().isLiving()) {
            return;
        }

        this._checkDistance();
        this._checkKills();
    }

    private void _checkDistance() {
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

    private void _checkKills() {
        int statKills = this.getPet().getPetData().getStatKills().getValue();

        if (lastStatKills == 0) {
            lastStatKills = statKills;
        }

        if (statKills == lastStatKills) {
            return;
        }

        incrementalKills += (statKills - lastStatKills);

        if (incrementalKills >= FOOD_DECREMENT_KILLS) {
            incrementalKills = 0;
            this.getPet().getPetData().getFood().setValue(this.getPet().getPetData().getFood().getValue() - 1);
        }

        lastStatKills = statKills;
    }
}
