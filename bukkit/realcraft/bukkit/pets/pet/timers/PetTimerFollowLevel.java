package realcraft.bukkit.pets.pet.timers;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.actions.PetAction;
import realcraft.bukkit.pets.pet.actions.PetActionFollow;
import realcraft.bukkit.pets.pet.data.PetDataMode;

public class PetTimerFollowLevel extends PetTimer {

    public PetTimerFollowLevel(Pet pet) {
        super(PetTimerType.FOLLOW_LEVEL, pet);
    }

    @Override
    protected void _run() {
        if (this.getPet().getPetData().getMode().getType() != PetDataMode.PetDataModeType.FOLLOW) {
            return;
        }

        ((PetActionFollow)this.getPet().getPetActions().getAction(PetAction.PetActionType.FOLLOW)).addDistanceLevel();
    }
}
