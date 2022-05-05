package realcraft.bukkit.pets.pet.entity.labels;

import realcraft.bukkit.pets.pet.entity.PetEntity;

public class PetEntityLabels {

    private final PetEntity petEntity;
    private PetEntityLabel currentLabel;

    public PetEntityLabels(PetEntity petEntity) {
        this.petEntity = petEntity;
    }

    public PetEntity getPetEntity() {
        return petEntity;
    }

    public void showText(String text, int duration) {
        if (currentLabel != null && currentLabel.getType() != PetEntityLabel.PetEntityLabelType.TEXT) {
            currentLabel.remove();
        }
    }

    public void clear() {
    }
}
