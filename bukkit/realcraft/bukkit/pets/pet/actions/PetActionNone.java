package realcraft.bukkit.pets.pet.actions;

import realcraft.bukkit.pets.pet.Pet;

public class PetActionNone extends PetAction {

    public PetActionNone(Pet pet) {
        super(PetActionType.NONE, pet);
    }

    @Override
    public boolean isCancellable() {
        return true;
    }

    @Override
    public boolean shouldStart() {
        return this.getPet().getPetActions().getCurrentAction().getState() != PetAction.PetActionState.RUNNING;
    }

    @Override
    protected void _start() {
        this.getEntity().setAI(true);
        this.getEntity().setGravity(true);
    }

    @Override
    protected void _run() {
    }

    @Override
    protected void _clear() {
    }
}
