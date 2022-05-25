package realcraft.bukkit.pets.commands;

import realcraft.bukkit.pets.PetPlayer;
import realcraft.bukkit.pets.pet.Pet;

import java.util.List;

public class PetCommandTp extends PetCommand {

    public PetCommandTp() {
        super("tp");
    }

    @Override
    public void perform(PetPlayer petPlayer, String[] args) {
        Pet pet = petPlayer.getPet();
        if (pet == null) {
            petPlayer.sendMessage("§cNemas zadneho mazlika");
            return;
        }

        if (!pet.getPetEntity().isLiving()) {
            petPlayer.sendMessage("§cMazlik neni zivy");
            return;
        }

        petPlayer.getPlayer().teleportAsync(pet.getPetEntity().getEntity().getLocation());
    }

    @Override
    public List<String> tabCompleter(PetPlayer petPlayer, String[] args) {
        return null;
    }
}
