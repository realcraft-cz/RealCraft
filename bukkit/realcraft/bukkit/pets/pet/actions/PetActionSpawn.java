package realcraft.bukkit.pets.pet.actions;

import realcraft.bukkit.pets.pet.Pet;

public class PetActionSpawn extends PetAction {

    public PetActionSpawn(Pet pet) {
        super(PetActionType.SPAWN, pet);
    }

    @Override
    public boolean isCancellable() {
        return false;
    }

    @Override
    protected void _start() {
        this.getPet().getPetEntity().spawn(this.getPet().getPetPlayer().getPlayer().getLocation());
        this.finish();
    }

    @Override
    protected void _clear() {
    }

    @Override
    public void run() {
    }
}
