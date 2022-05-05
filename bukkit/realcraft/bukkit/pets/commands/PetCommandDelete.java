package realcraft.bukkit.pets.commands;

import realcraft.bukkit.pets.PetPlayer;
import realcraft.bukkit.pets.exceptions.player.PetPlayerNoPetException;

import java.util.List;

public class PetCommandDelete extends PetCommand {

    public PetCommandDelete() {
        super("delete");
    }

    @Override
    public void perform(PetPlayer petPlayer, String[] args) {
        try {
            petPlayer.deletePet();
        } catch (PetPlayerNoPetException e) {
            petPlayer.sendMessage("§cNemas zadneho mazlika");
            return;
        }

        petPlayer.sendMessage("§dMazlik smazan", true);
    }

    @Override
    public List<String> tabCompleter(PetPlayer petPlayer, String[] args) {
        return null;
    }
}
