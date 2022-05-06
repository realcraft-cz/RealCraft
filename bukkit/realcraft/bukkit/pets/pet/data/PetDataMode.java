package realcraft.bukkit.pets.pet.data;

import org.bukkit.ChatColor;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.utils.json.JsonDataString;

public class PetDataMode extends JsonDataString {

    private final Pet pet;

    public PetDataMode(Pet pet) {
        super("mode");
        this.pet = pet;
        this.setType(PetDataModeType.FOLLOW);
    }

    public Pet getPet() {
        return pet;
    }

    public PetDataModeType getType() {
        return PetDataModeType.getByName(this.getValue());
    }

    public void setType(PetDataModeType type) {
        this.setValue(type.toString());
    }

    public enum PetDataModeType {

        HOME    ("Domu", ChatColor.RED),
        FOLLOW  ("Ke me", ChatColor.LIGHT_PURPLE),
        SIT     ("Sedni", ChatColor.YELLOW),
        ;

        private final String name;
        private final ChatColor color;

        private PetDataModeType(String name, ChatColor color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public ChatColor getColor() {
            return color;
        }

        public static PetDataModeType getByName(String name) {
            try {
                return PetDataModeType.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }

            return PetDataModeType.FOLLOW;
        }
    }
}
