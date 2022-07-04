package realcraft.bukkit.pets.events.pet;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.actions.PetAction;

public class PetActionFinishEvent extends PetActionEvent {

    public PetActionFinishEvent(Pet pet, PetAction action) {
        super(pet, action);
    }
}
