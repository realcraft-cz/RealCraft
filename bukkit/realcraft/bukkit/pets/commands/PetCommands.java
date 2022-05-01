package realcraft.bukkit.pets.commands;

import org.bukkit.entity.Player;
import realcraft.bukkit.others.AbstractCommand;
import realcraft.bukkit.pets.PetsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PetCommands extends AbstractCommand {

    private final PetCommand[] commands;

    public PetCommands() {
        super("pet", "pets");

        commands = new PetCommand[]{
            new PetCommandInfo(),
            new PetCommandCreate(),
            new PetCommandDelete(),
        };
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage("§7§m" + " ".repeat(10) + "§r §d§lPets §7§m" + " ".repeat(47 - "Pets".length()));
            player.sendMessage("§6/pet info §f- Informace o mazlikovi");
            return;
        }

        String subcommand = args[0].toLowerCase();
        args = Arrays.copyOfRange(args, 1, args.length);

        for (PetCommand command : commands) {
            if (command.match(subcommand)) {
                command.perform(PetsManager.getPetPlayer(player), args);
            }
        }
    }

    @Override
    public List<String> tabCompleter(Player player, String[] args) {
        if (args.length <= 1) {
            ArrayList<String> cmds = new ArrayList<>();

            if (args.length == 0) {
                for (PetCommand command : commands) {
                    cmds.add(command.getName());
                }
            } else {
                for (PetCommand command : commands) {
                    if (command.startsWith(args[0])) {
                        cmds.add(command.getName());
                    }
                }
            }

            return cmds;
        }

        String subcommand = args[0].toLowerCase();
        args = Arrays.copyOfRange(args, 1, args.length);

        for (PetCommand command : commands) {
            if (command.match(subcommand)) {
                return command.tabCompleter(PetsManager.getPetPlayer(player), args);
            }
        }

        return null;
    }
}
