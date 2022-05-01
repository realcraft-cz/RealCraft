package realcraft.bukkit.pets.exceptions.pet;

import realcraft.bukkit.pets.exceptions.PetsException;
import realcraft.bukkit.pets.pet.Pet;

public abstract class PetException extends PetsException {

    private final Pet pet;

    public PetException(Pet pet) {
        this.pet = pet;
    }

    public Pet getPet() {
        return pet;
    }
}
