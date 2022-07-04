package realcraft.bukkit.pets.exceptions.player;

import realcraft.bukkit.pets.PetPlayer;

public class PetPlayerNoPetException extends PetPlayerException {

    public PetPlayerNoPetException(PetPlayer petPlayer) {
        super(petPlayer);
    }
}
