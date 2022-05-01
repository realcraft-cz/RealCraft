package realcraft.bukkit.pets.pet.data;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.utils.json.JsonDataInteger;

public class PetDataFood extends JsonDataInteger {

    private final Pet pet;

    public PetDataFood(Pet pet) {
        super("food");
        this.pet = pet;
        this.setValue(this.getDefaultValue());
    }

    public Pet getPet() {
        return pet;
    }

    public int getDefaultValue() {
        return 10;
    }

    public int getMaxValue() {
        return 10;
    }

    @Override
    public void setValue(int value) {
        if (value > this.getMaxValue()) {
            value = this.getMaxValue();
        }

        if (value < 0) {
            value = 0;
        }

        super.setValue(value);
    }
}
