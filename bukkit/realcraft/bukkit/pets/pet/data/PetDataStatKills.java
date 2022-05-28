package realcraft.bukkit.pets.pet.data;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.utils.json.JsonDataInteger;

public class PetDataStatKills extends JsonDataInteger {

    private final Pet pet;

    public PetDataStatKills(Pet pet) {
        super("stat_kills");
        this.pet = pet;
    }

    public Pet getPet() {
        return pet;
    }
}
