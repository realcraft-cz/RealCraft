package realcraft.bukkit.pets.commands;

import realcraft.bukkit.pets.PetPlayer;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.PetSkin;
import realcraft.bukkit.pets.pet.actions.PetAction;

import java.util.List;

public class PetCommandSkin extends PetCommand {

    public PetCommandSkin() {
        super("skin");
    }

    @Override
    public void perform(PetPlayer petPlayer, String[] args) {
        Pet pet = petPlayer.getPet();
        if (pet == null) {
            petPlayer.sendMessage("§cNemas zadneho mazlika");
            return;
        }

        pet.getPetData().getSkin().setValue(PetSkin.getRandomSkin().getTexture());
        pet.getPetActions().setActionType(PetAction.PetActionType.SKIN_CHANGE);
    }

    @Override
    public List<String> tabCompleter(PetPlayer petPlayer, String[] args) {
        return null;
    }
}
