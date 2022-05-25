package realcraft.bukkit.pets.pet.data;

import realcraft.bukkit.pets.pet.Pet;

public class PetDataHeat extends PetDataIntegerRange {

    public PetDataHeat(Pet pet) {
        super("heat", pet);
    }

    @Override
    public int getDefaultValue() {
        return this.getMaxValue();
    }

    public int getCriticalValue() {
        return 1;
    }

    @Override
    public int getMinValue() {
        return 0;
    }

    @Override
    public int getMaxValue() {
        return 10;
    }
}
