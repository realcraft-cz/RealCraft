package realcraft.bukkit.pets.pet.data;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.PetSkin;
import realcraft.bukkit.utils.json.JsonData;
import realcraft.bukkit.utils.json.JsonDataString;

public class PetDataSkin extends JsonDataString {

    private final Pet pet;

    public PetDataSkin(Pet pet) {
        super("skin");
        this.pet = pet;
        this.setValue(this.getDefaultValue());
    }

    public Pet getPet() {
        return pet;
    }

    public String getDefaultValue() {
        return PetSkin.getRandomSkin().getTexture();
    }

    @Override
    public void loadData(JsonData data) {
        super.loadData(data);

        if (this.getValue() == null || this.getValue().length() == 0) {
            this.setValue(this.getDefaultValue());
        }
    }
}
