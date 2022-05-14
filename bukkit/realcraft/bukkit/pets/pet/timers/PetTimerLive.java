package realcraft.bukkit.pets.pet.timers;

import org.bukkit.Location;
import realcraft.bukkit.pets.pet.Pet;

public class PetTimerLive extends PetTimer {

    public PetTimerLive(Pet pet) {
        super(PetTimerType.LIVE, pet);
    }

    @Override
    protected void _run() {
        if (!this.getPet().getPetEntity().isLiving()) {
            Location location = this.getPet().getPetPlayer().getPlayer().getLocation();
            location.setPitch(0f);
            location.add(location.getDirection().setY(0).multiply(1.5));

            this.getPet().getPetEntity().spawn(location);
            return;
        }

        if (this.getPet().getPetEntity().getEntity().isConverting()) {
            this.getPet().getPetEntity().getEntity().setConversionTime(-1);
        }
    }
}
