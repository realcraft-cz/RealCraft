package realcraft.bukkit.pets.exceptions.player;

import realcraft.bukkit.pets.PetPlayer;
import realcraft.bukkit.pets.exceptions.PetsException;

public abstract class PetPlayerException extends PetsException {

    private final PetPlayer petPlayer;

    public PetPlayerException(PetPlayer petPlayer) {
        this.petPlayer = petPlayer;
    }

    public PetPlayer getPetPlayer() {
        return petPlayer;
    }
}
