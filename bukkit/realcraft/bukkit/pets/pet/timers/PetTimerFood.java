package realcraft.bukkit.pets.pet.timers;

import realcraft.bukkit.pets.pet.Pet;

public class PetTimerFood extends PetTimer {

    public PetTimerFood(Pet pet) {
        super(PetTimerType.FOOD, pet);
    }

    @Override
    protected void _run() {
        this.getPet().getPetData().getFood().setValue(this.getPet().getPetData().getFood().getValue() - 1);
    }
}
