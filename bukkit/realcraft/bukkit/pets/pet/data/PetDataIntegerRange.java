package realcraft.bukkit.pets.pet.data;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.utils.json.JsonDataInteger;

public abstract class PetDataIntegerRange extends JsonDataInteger {

    private final Pet pet;

    public PetDataIntegerRange(String name, Pet pet) {
        super(name);
        this.pet = pet;
        this.setValue(this.getDefaultValue());
    }

    public Pet getPet() {
        return pet;
    }

    @Override
    public void setValue(int value) {
        if (value > this.getMaxValue()) {
            value = this.getMaxValue();
        }

        if (value < this.getMinValue()) {
            value = this.getMinValue();
        }

        super.setValue(value);
    }

    abstract public int getDefaultValue();
    abstract public int getMinValue();
    abstract public int getMaxValue();
}
