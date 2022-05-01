package realcraft.bukkit.pets.events.pet;

import realcraft.bukkit.pets.events.PetsEvent;
import realcraft.bukkit.pets.pet.Pet;

public abstract class PetEvent extends PetsEvent {

    private final Pet pet;

    public PetEvent(Pet pet) {
        this.pet = pet;
    }

    public Pet getPet() {
        return pet;
    }
}
