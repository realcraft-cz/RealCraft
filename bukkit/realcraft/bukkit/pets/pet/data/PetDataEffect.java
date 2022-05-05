package realcraft.bukkit.pets.pet.data;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.entity.PetEntityEffect;
import realcraft.bukkit.utils.json.JsonDataString;

public class PetDataEffect extends JsonDataString {

    private final Pet pet;

    public PetDataEffect(Pet pet) {
        super("effect");
        this.pet = pet;
        this.setType(PetEntityEffect.PetEntityEffectType.AURA);
    }

    public Pet getPet() {
        return pet;
    }

    public PetEntityEffect.PetEntityEffectType getType() {
        return PetEntityEffect.PetEntityEffectType.getByName(this.getValue());
    }

    public void setType(PetEntityEffect.PetEntityEffectType type) {
        this.setValue(type.toString());
    }
}
