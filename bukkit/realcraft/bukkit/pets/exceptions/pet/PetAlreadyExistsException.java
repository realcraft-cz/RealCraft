package realcraft.bukkit.pets.exceptions.pet;

import realcraft.bukkit.pets.pet.Pet;

public class PetAlreadyExistsException extends PetException {

    public PetAlreadyExistsException(Pet pet) {
        super(pet);
    }
}
