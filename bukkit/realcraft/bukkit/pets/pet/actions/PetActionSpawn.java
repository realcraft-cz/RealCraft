package realcraft.bukkit.pets.pet.actions;

import realcraft.bukkit.pets.pet.Pet;

public class PetActionSpawn extends PetAction {

    public PetActionSpawn(Pet pet) {
        super(PetActionType.SPAWN, pet);
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
    protected void _run() {
        //https://www.spigotmc.org/threads/set-a-players-rotation-without-deleting-his-velocity.535325/
    }
}
