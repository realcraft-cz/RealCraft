package realcraft.bukkit.pets.pet.data;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.utils.json.JsonDataLocationSpawn;

public class PetDataHome extends JsonDataLocationSpawn {

    private final Pet pet;

    public PetDataHome(Pet pet) {
        super("home");
        this.pet = pet;
    }

    public Pet getPet() {
        return pet;
    }
}
