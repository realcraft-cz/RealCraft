package realcraft.bukkit.pets.pet.data;

import org.bukkit.ChatColor;
import realcraft.bukkit.pets.pet.Pet;

public class PetDataHealth extends PetDataIntegerRange {

    public static final String CHAR = "\u2764";
    public static final ChatColor COLOR = ChatColor.RED;

    public PetDataHealth(Pet pet) {
        super("health", pet);
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
        return 1;
    }

    @Override
    public int getMaxValue() {
        return 5;
    }
}
