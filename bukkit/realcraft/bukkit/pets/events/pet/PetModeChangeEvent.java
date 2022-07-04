package realcraft.bukkit.pets.events.pet;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.data.PetDataMode;

public class PetModeChangeEvent extends PetEvent {

    private final PetDataMode.PetDataModeType mode;
    private final PetDataMode.PetDataModeType oldMode;

    public PetModeChangeEvent(Pet pet, PetDataMode.PetDataModeType mode, PetDataMode.PetDataModeType oldMode) {
        super(pet);
        this.mode = mode;
        this.oldMode = oldMode;
    }

    public PetDataMode.PetDataModeType getMode() {
        return mode;
    }

    public PetDataMode.PetDataModeType getOldMode() {
        return oldMode;
    }
}
