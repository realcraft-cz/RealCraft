package realcraft.bukkit.pets.pet.data;

import org.bukkit.ChatColor;
import realcraft.bukkit.pets.pet.Pet;

public class PetDataFood extends PetDataIntegerRange {

    public static final String CHAR = "\u2726";
    public static final ChatColor COLOR = ChatColor.GOLD;

    public PetDataFood(Pet pet) {
        super("food", pet);
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
        return 5;
    }
}
