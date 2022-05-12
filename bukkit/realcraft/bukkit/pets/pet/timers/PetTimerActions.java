package realcraft.bukkit.pets.pet.timers;

import realcraft.bukkit.pets.pet.Pet;

public class PetTimerActions extends PetTimer {

    public PetTimerActions(Pet pet) {
        super(PetTimerType.ACTIONS, pet);
    }

    @Override
    protected void _run() {
        this.getPet().getPetActions().run();
    }
}
