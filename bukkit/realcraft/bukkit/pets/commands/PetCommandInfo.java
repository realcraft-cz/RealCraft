package realcraft.bukkit.pets.commands;

import realcraft.bukkit.pets.PetPlayer;
import realcraft.bukkit.pets.pet.Pet;

import java.util.List;

public class PetCommandInfo extends PetCommand {

    public PetCommandInfo() {
        super("info");
    }

    @Override
    public void perform(PetPlayer petPlayer, String[] args) {
        Pet pet = petPlayer.getPet();
        if (pet == null) {
            petPlayer.sendMessage("§cNemas zadneho mazlika");
            return;
        }

        petPlayer.sendMessage("§7Jmeno: §f"+ pet.getPetData().getName());
        petPlayer.sendMessage("§7Jidlo: §f"+ pet.getPetData().getFood());
    }

    @Override
    public List<String> tabCompleter(PetPlayer petPlayer, String[] args) {
        return null;
    }
}
