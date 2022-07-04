package realcraft.bukkit.pets.commands;

import realcraft.bukkit.pets.PetPlayer;

import java.util.List;

public abstract class PetCommand {

    private final String[] names;

    public PetCommand(String... names) {
        this.names = names;
    }

    public String getName() {
        return names[0];
    }

    public boolean match(String command) {
        for (String name : names) {
            if (name.equalsIgnoreCase(command)) return true;
        }
        return false;
    }

    public boolean startsWith(String command) {
        for (String name : names) {
            if (name.startsWith(command.toLowerCase())) return true;
        }
        return false;
    }

    public abstract void perform(PetPlayer fPlayer, String[] args);
    public abstract List<String> tabCompleter(PetPlayer fPlayer, String[] args);
}
