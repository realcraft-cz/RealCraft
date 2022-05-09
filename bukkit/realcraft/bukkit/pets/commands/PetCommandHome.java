package realcraft.bukkit.pets.commands;

import org.bukkit.Sound;
import realcraft.bukkit.pets.PetPlayer;
import realcraft.bukkit.pets.pet.Pet;

import java.util.List;

public class PetCommandHome extends PetCommand {

    public PetCommandHome() {
        super("home");
    }

    @Override
    public void perform(PetPlayer petPlayer, String[] args) {
        Pet pet = petPlayer.getPet();
        if (pet == null) {
            petPlayer.sendMessage("§cNemas zadneho mazlika");
            return;
        }

        pet.getPetData().getHome().setLocation(petPlayer.getPlayer().getLocation());
        petPlayer.sendMessage("§dDomov mazlika nastaven");
        petPlayer.getPlayer().playSound(petPlayer.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
    }

    @Override
    public List<String> tabCompleter(PetPlayer petPlayer, String[] args) {
        return null;
    }
}
