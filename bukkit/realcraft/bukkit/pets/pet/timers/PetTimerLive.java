package realcraft.bukkit.pets.pet.timers;

import org.bukkit.Location;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.actions.PetAction;
import realcraft.bukkit.pets.pet.actions.PetActionSit;
import realcraft.bukkit.pets.pet.data.PetDataMode;

public class PetTimerLive extends PetTimer {

    public PetTimerLive(Pet pet) {
        super(PetTimerType.LIVE, pet);
    }

    @Override
    protected void _run() {
        if (!this.getPet().getPetEntity().isLiving() && this.getPet().getPetEntity().isSpawned()) {
            if (!this.getPet().getPetEntity().getEntity().getLocation().isChunkLoaded()) {
                return;
            }

            if (this.getPet().getPetData().getMode().getType() == PetDataMode.PetDataModeType.HOME) {
                if (this.getPet().getPetActions().getCurrentAction().getType() == PetAction.PetActionType.HOME) {
                    this.getPet().getPetEntity().spawn(this.getPet().getPetData().getHome().getLocation());
                    this.getPet().getPetActions().setActionType(PetAction.PetActionType.HOME);
                    return;
                }

                this.getPet().getPetData().getMode().setType(PetDataMode.PetDataModeType.FOLLOW);
                return;
            }

            if (this.getPet().getPetData().getMode().getType() == PetDataMode.PetDataModeType.SIT) {
                if (this.getPet().getPetActions().getCurrentAction().getType() == PetAction.PetActionType.SIT) {
                    this.getPet().getPetEntity().spawn(((PetActionSit)this.getPet().getPetActions().getAction(PetAction.PetActionType.SIT)).getSitLocation());
                    this.getPet().getPetActions().setActionType(PetAction.PetActionType.SIT);
                    return;
                }

                this.getPet().getPetData().getMode().setType(PetDataMode.PetDataModeType.FOLLOW);
                return;
            }

            if (this.getPet().getPetData().getMode().getType() == PetDataMode.PetDataModeType.FOLLOW) {
                Location location = this.getPet().getPetPlayer().getPlayer().getLocation();
                location.setPitch(0f);
                location.add(location.getDirection().setY(0).multiply(1.5));
                this.getPet().getPetEntity().spawn(location);
                return;
            }

            return;
        }

        if (this.getPet().getPetEntity().getEntity().isConverting()) {
            this.getPet().getPetEntity().getEntity().setConversionTime(-1);
        }
    }
}
