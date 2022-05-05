package realcraft.bukkit.pets.pet.data;

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

        HOME    ("Misto"),
        FOLLOW  ("Ke me"),
        SIT     ("Sedni"),
        ;

        private final String name;

        private PetDataModeType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
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
