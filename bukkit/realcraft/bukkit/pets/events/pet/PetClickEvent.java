package realcraft.bukkit.pets.events.pet;

import realcraft.bukkit.pets.PetPlayer;
import realcraft.bukkit.pets.pet.Pet;

public class PetClickEvent extends PetEvent {

    private final PetPlayer petPlayer;
    private final ClickType clickType;

    public PetClickEvent(Pet pet, PetPlayer petPlayer, ClickType clickType) {
        super(pet);
        this.petPlayer = petPlayer;
        this.clickType = clickType;
    }

    public PetPlayer getPetPlayer() {
        return petPlayer;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public enum ClickType {
        LEFT, RIGHT
    }
}
