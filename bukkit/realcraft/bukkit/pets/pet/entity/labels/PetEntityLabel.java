package realcraft.bukkit.pets.pet.entity.labels;

import realcraft.bukkit.pets.pet.entity.PetEntity;

public abstract class PetEntityLabel {

    private final PetEntityLabelType type;
    private final PetEntity petEntity;

    public PetEntityLabel(PetEntityLabelType type, PetEntity petEntity) {
        this.type = type;
        this.petEntity = petEntity;
    }

    public PetEntityLabelType getType() {
        return type;
    }

    public PetEntity getPetEntity() {
        return petEntity;
    }

    abstract public void show();
    abstract public void remove();
    abstract public void run();

    public enum PetEntityLabelType {
        TEXT,
        PROGRESS,
        MODES,
        ;
    }
}
