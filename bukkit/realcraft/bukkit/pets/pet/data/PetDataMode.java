package realcraft.bukkit.pets.pet.data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import realcraft.bukkit.pets.events.pet.PetModeChangeEvent;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.utils.json.JsonDataString;

public class PetDataMode extends JsonDataString {

    private final Pet pet;

    public PetDataMode(Pet pet) {
        super("mode");
        this.pet = pet;
        this.setType(PetDataModeType.FOLLOW, false);
    }

    public Pet getPet() {
        return pet;
    }

    public PetDataModeType getType() {
        return PetDataModeType.getByName(this.getValue());
    }

    public void setType(PetDataModeType type) {
        this.setType(type, true);
    }

    public void setType(PetDataModeType type, boolean callEvent) {
        PetDataModeType oldMode = this.getType();
        this.setValue(type.toString());

        if (callEvent) {
            Bukkit.getPluginManager().callEvent(new PetModeChangeEvent(this.getPet(), type, oldMode));
        }
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

        public PetDataModeType getNextPreferredType() {
            if (this == HOME) {
                return FOLLOW;
            } else if (this == SIT) {
                return FOLLOW;
            }

            return FOLLOW;
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
