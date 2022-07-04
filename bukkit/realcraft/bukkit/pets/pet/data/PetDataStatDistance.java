package realcraft.bukkit.pets.pet.data;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.utils.json.JsonDataInteger;

public class PetDataStatDistance extends JsonDataInteger {

    private final Pet pet;

    public PetDataStatDistance(Pet pet) {
        super("stat_distance");
        this.pet = pet;
    }

    public Pet getPet() {
        return pet;
    }
}
