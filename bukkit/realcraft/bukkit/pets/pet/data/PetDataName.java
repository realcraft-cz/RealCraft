package realcraft.bukkit.pets.pet.data;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.utils.json.JsonData;
import realcraft.bukkit.utils.json.JsonDataString;

public class PetDataName extends JsonDataString {

    private final Pet pet;

    public PetDataName(Pet pet) {
        super("name");
        this.pet = pet;
        this.setValue(this.getDefaultValue());
    }

    public Pet getPet() {
        return pet;
    }

    public String getDefaultValue() {
        return this.getPet().getPetPlayer().getUser().getName() + "'s pet";
    }

    @Override
    public void loadData(JsonData data) {
        super.loadData(data);

        if (this.getValue() == null || this.getValue().length() == 0) {
            this.setValue(this.getDefaultValue());
        }
    }
}
