package realcraft.bukkit.pets.commands;

import org.bukkit.entity.Player;
import realcraft.bukkit.others.AbstractCommand;
import realcraft.bukkit.pets.PetPlayer;
import realcraft.bukkit.pets.PetsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PetCommands extends AbstractCommand {

    private final PetCommand[] commands;

    public PetCommands() {
        super("pet");

        commands = new PetCommand[]{
            new PetCommandInfo(),
            new PetCommandCreate(),
            new PetCommandDelete(),
            new PetCommandEffect(),
            new PetCommandSkin(),
            new PetCommandHome(),
            new PetCommandTp(),
        };
    }

    @Override
    public void perform(Player player, String[] args) {
        PetPlayer petPlayer = PetsManager.getPetPlayer(player);

        if (!petPlayer.hasPermissions()) {
            player.sendMessage("§cPouze VIP clenove muzou mit mazlika");
            return;
        }

        if (args.length == 0) {
            petPlayer.sendMessage("§7§m" + " ".repeat(10) + "§r §d§lMazlik §7§m" + " ".repeat(47 - "Mazlik".length()));
            if (!petPlayer.hasPet()) {
                petPlayer.sendMessage("§7/pet §6create §f- Vytvoreni mazlika");
                petPlayer.sendMessage("§7Dalsi prikazy se zobrazi po vytvoreni mazlika");
                return;
            }

            petPlayer.sendMessage("§7/pet §6info §f- Informace o mazlikovi");
            petPlayer.sendMessage("§7/pet §6tp §f- Teleport k mazlikovi");
            petPlayer.sendMessage("§7/pet §6skin §f- Nastaveni skinu mazlika");
            petPlayer.sendMessage("§7/pet §6effect §f- Nastaveni efektu mazlika");
            petPlayer.sendMessage("§7/pet §6home §f- Nastaveni domova mazlika");
            petPlayer.sendMessage("§7/pet §6delete §f- Smazani mazlika");
            return;
        }

        String subcommand = args[0].toLowerCase();
        args = Arrays.copyOfRange(args, 1, args.length);

        for (PetCommand command : commands) {
            if (command.match(subcommand)) {
                command.perform(petPlayer, args);
                return;
            }
        }

        this.perform(player, new String[]{});
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
