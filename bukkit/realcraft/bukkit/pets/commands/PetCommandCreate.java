package realcraft.bukkit.pets.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import realcraft.bukkit.RealCraft;
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

        petPlayer.sendMessage("§dMazlik vytvoren, za chvili vyleze", true);
        petPlayer.getPlayer().playSound(petPlayer.getPlayer(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);

        Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
            @Override
            public void run() {
                petPlayer.getPet().getPetActions().setActionType(PetAction.PetActionType.SPAWN);
            }
        }, 20);
    }

    @Override
    public List<String> tabCompleter(PetPlayer petPlayer, String[] args) {
        return null;
    }
}
