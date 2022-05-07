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
    protected void _clear() {
    }

    @Override
    public void run() {
        //System.out.println("dfg");

        //this.getEntity().setRotation(getEntity().getLocation().getYaw()+70, getEntity().getLocation().getPitch()); //fastest
        //this.getEntity().setRotation(getEntity().getLocation().getYaw()+80, getEntity().getLocation().getPitch());
        //this.getEntity().setRotation(getEntity().getLocation().getYaw()+90, getEntity().getLocation().getPitch());
        //this.getEntity().setRotation(getEntity().getLocation().getYaw()+100, getEntity().getLocation().getPitch());
        //this.getEntity().setRotation(getEntity().getLocation().getYaw()+110, getEntity().getLocation().getPitch());
        //this.getEntity().setRotation(getEntity().getLocation().getYaw()+120, getEntity().getLocation().getPitch());
    }
}
