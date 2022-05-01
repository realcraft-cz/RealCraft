package realcraft.bukkit.pets.pet.data;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.utils.json.JsonDataString;

public class PetDataMode extends JsonDataString {

    private final Pet pet;

    public PetDataMode(Pet pet) {
        super("mode");
        this.pet = pet;
        this.setValue(this.getDefaultValue());
    }

    public Pet getPet() {
        return pet;
    }

    public String getDefaultValue() {
        return PetDataModeType.FOLLOW.toString();
    }

    public PetDataModeType getType() {
        return PetDataModeType.getByName(this.getValue());
    }

    public void setType(PetDataModeType type) {
        this.setValue(type.toString());
    }

    public enum PetDataModeType {

        FOLLOW,
        SIT,
        HOME,
        ;

        public static PetDataModeType getByName(String name) {
            try {
                return PetDataModeType.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }

            return PetDataModeType.FOLLOW;
        }

        public String toString() {
            return this.name().toLowerCase();
        }
    }
}
