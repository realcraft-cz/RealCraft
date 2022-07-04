package realcraft.bukkit.pets.events.pet;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.actions.PetAction;

public abstract class PetActionEvent extends PetEvent {

    private final PetAction action;

    public PetActionEvent(Pet pet, PetAction action) {
        super(pet);
        this.action = action;
    }

    public PetAction getAction() {
        return action;
    }
}
