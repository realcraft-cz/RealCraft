package realcraft.bukkit.pets.commands;

import realcraft.bukkit.pets.PetPlayer;
import realcraft.bukkit.pets.exceptions.pet.PetAlreadyExistsException;
import realcraft.bukkit.pets.exceptions.player.PetPlayerVipException;
import realcraft.bukkit.pets.pet.actions.PetAction;

import java.util.List;

public class PetCommandCreate extends PetCommand {

    public PetCommandCreate() {
        super("create");
    }

    @Override
    public void perform(PetPlayer petPlayer, String[] args) {
        try {
            petPlayer.createPet();
        } catch (PetAlreadyExistsException e) {
            petPlayer.sendMessage("§cJednoho mazlika uz mas");
            return;
        } catch (PetPlayerVipException e) {
            petPlayer.sendMessage("§cPouze VIP clenove muzou mit mazlika");
            return;
        }

        petPlayer.sendMessage("§dMazlik vytvoren, za chvili se vyklube", true);
        petPlayer.getPet().getPetActions().setActionType(PetAction.PetActionType.SPAWN);
    }

    @Override
    public List<String> tabCompleter(PetPlayer petPlayer, String[] args) {
        return null;
    }
}
