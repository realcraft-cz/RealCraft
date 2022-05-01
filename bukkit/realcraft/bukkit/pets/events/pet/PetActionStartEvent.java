package realcraft.bukkit.pets.events.pet;

import org.bukkit.event.Cancellable;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.actions.PetAction;

public class PetActionStartEvent extends PetActionEvent implements Cancellable {

    private boolean cancelled = false;

    public PetActionStartEvent(Pet pet, PetAction action) {
        super(pet, action);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
