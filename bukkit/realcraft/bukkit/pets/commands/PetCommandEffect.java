package realcraft.bukkit.pets.commands;

import org.bukkit.Sound;
import realcraft.bukkit.pets.PetPlayer;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.entity.PetEntityEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PetCommandEffect extends PetCommand {

    public PetCommandEffect() {
        super("effect");
    }

    @Override
    public void perform(PetPlayer petPlayer, String[] args) {
        Pet pet = petPlayer.getPet();
        if (pet == null) {
            petPlayer.sendMessage("§cNemas zadneho mazlika");
            return;
        }

        if (args.length == 0) {
            petPlayer.sendMessage("Nastavit efekt mazlika");
            petPlayer.sendMessage("§6/pet effect §e<effect>");
            return;
        }

        try {
            PetEntityEffect.PetEntityEffectType effect = PetEntityEffect.PetEntityEffectType.valueOf(args[0].toUpperCase());
            pet.getPetData().getEffect().setType(effect);
            petPlayer.sendMessage("§dEfekt mazlika nastaven na §f"+effect);
            petPlayer.getPlayer().playSound(petPlayer.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        } catch (IllegalArgumentException e) {
            petPlayer.sendMessage("§cNeplatny efekt");
            petPlayer.sendMessage("§7Effects: "+String.join(", ", Arrays.toString(PetEntityEffect.PetEntityEffectType.values())).toUpperCase());
        }
    }

    @Override
    public List<String> tabCompleter(PetPlayer petPlayer, String[] args) {
        if (args.length <= 1) {
            ArrayList<String> completions = new ArrayList<>();

            if (args.length == 0) {
                for (PetEntityEffect.PetEntityEffectType effect : PetEntityEffect.PetEntityEffectType.values()) {
                    completions.add(effect.toString());
                }
            } else {
                String search = args[0].toUpperCase();
                for (PetEntityEffect.PetEntityEffectType effect : PetEntityEffect.PetEntityEffectType.values()) {
                    if (effect.toString().startsWith(search)) {
                        completions.add(effect.toString());
                    }
                }
            }

            return completions;
        }

        return null;
    }
}
