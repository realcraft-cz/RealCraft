package realcraft.bukkit.pets.commands;

import org.bukkit.Sound;
import realcraft.bukkit.pets.PetPlayer;
import realcraft.bukkit.pets.exceptions.player.PetPlayerNoPetException;
import realcraft.share.utils.RandomUtil;

import java.util.HashMap;
import java.util.List;

public class PetCommandDelete extends PetCommand {

    private final HashMap<PetPlayer, String> petPlayerCaptcha = new HashMap<>();

    public PetCommandDelete() {
        super("delete");
    }

    @Override
    public void perform(PetPlayer petPlayer, String[] args) {
        if (petPlayer.getPet() == null) {
            petPlayer.sendMessage("§cNemas zadneho mazlika");
            return;
        }

        if (args.length == 0) {
            petPlayerCaptcha.put(petPlayer, RandomUtil.getRandomHex(2));
            petPlayer.sendMessage("");
            petPlayer.sendMessage("Opravdu chces smazat sveho mazlika?");
            petPlayer.sendMessage("Veskery postup a nastaveni bude ztraceno.");
            petPlayer.sendMessage("Pro potvrzeni napis §6/pet delete " + petPlayerCaptcha.get(petPlayer));
            return;
        }

        if (!petPlayerCaptcha.containsKey(petPlayer) || !args[0].equalsIgnoreCase(petPlayerCaptcha.get(petPlayer))) {
            petPlayer.sendMessage("§cKontrolni kod se neshoduje, napis §6/pet delete " + petPlayerCaptcha.get(petPlayer));
            return;
        }

        try {
            petPlayer.deletePet();
        } catch (PetPlayerNoPetException e) {
            petPlayer.sendMessage("§cNemas zadneho mazlika");
            return;
        }

        petPlayer.sendMessage("§dMazlik smazan", true);
        petPlayer.getPlayer().playSound(petPlayer.getPlayer(), Sound.ENTITY_ZOMBIE_INFECT, 1f, 0.5f);
    }

    @Override
    public List<String> tabCompleter(PetPlayer petPlayer, String[] args) {
        return null;
    }
}
