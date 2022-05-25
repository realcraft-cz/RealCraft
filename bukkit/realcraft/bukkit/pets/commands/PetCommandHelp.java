package realcraft.bukkit.pets.commands;

import realcraft.bukkit.pets.PetPlayer;

import java.util.List;

public class PetCommandHelp extends PetCommand {

    public PetCommandHelp() {
        super("help");
    }

    @Override
    public void perform(PetPlayer petPlayer, String[] args) {
    }

    @Override
    public List<String> tabCompleter(PetPlayer petPlayer, String[] args) {
        return null;
    }
}
