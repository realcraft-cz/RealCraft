package realcraft.bukkit.pets.pet.data;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.PetSkin;
import realcraft.bukkit.utils.json.JsonDataString;

public class PetDataSkin extends JsonDataString {

    private final Pet pet;

    public PetDataSkin(Pet pet) {
        super("skin");
        this.pet = pet;
        this.setSkin(PetSkin.PetSkinCategory.POKEMONS.getRandomSkin());
    }

    public Pet getPet() {
        return pet;
    }

    public PetSkin getSkin() {
        return PetSkin.getByName(this.getValue());
    }

    public void setSkin(PetSkin skin) {
        this.setValue(skin.toString());
    }
}
