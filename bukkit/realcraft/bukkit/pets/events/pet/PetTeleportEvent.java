package realcraft.bukkit.pets.events.pet;

import org.bukkit.Location;
import realcraft.bukkit.pets.pet.Pet;

public class PetTeleportEvent extends PetEvent {

    private final Location from;
    private final Location to;

    public PetTeleportEvent(Pet pet, Location from, Location to) {
        super(pet);
        this.from = from;
        this.to = to;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }
}
