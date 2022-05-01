package realcraft.bukkit.pets.events.pet;

import org.bukkit.entity.Player;
import realcraft.bukkit.pets.pet.Pet;

public class PetClickEvent extends PetEvent {

    private final Player player;
    private final ClickType clickType;

    public PetClickEvent(Pet pet, Player player, ClickType clickType) {
        super(pet);
        this.player = player;
        this.clickType = clickType;
    }

    public Player getPlayer() {
        return player;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public enum ClickType {
        LEFT, RIGHT
    }
}
