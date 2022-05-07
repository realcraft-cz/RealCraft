package realcraft.bukkit.pets.pet.timers;

import realcraft.bukkit.pets.pet.Pet;

public class PetTimerSave extends PetTimer {

    public PetTimerSave(Pet pet) {
        super(PetTimerType.SAVE, pet);
    }

    @Override
    protected void _run() {
        this.getPet().save();
    }
}
