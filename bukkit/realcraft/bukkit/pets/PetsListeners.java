package realcraft.bukkit.pets;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.events.pet.PetActionFinishEvent;
import realcraft.bukkit.pets.events.pet.PetClickEvent;
import realcraft.bukkit.pets.events.pet.PetLoadEvent;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.actions.PetAction;
import realcraft.bukkit.pets.pet.data.PetDataMode;
import realcraft.bukkit.pets.pet.entity.labels.PetEntityLabelRotable;

public class PetsListeners implements Listener {

    public PetsListeners() {
        Bukkit.getPluginManager().registerEvents(this, RealCraft.getInstance());
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        PetPlayer petPlayer = PetsManager.getPetPlayer(event.getPlayer());
        petPlayer.load();
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        PetPlayer petPlayer = PetsManager.getPetPlayer(event.getPlayer());
        petPlayer.save();

        if (petPlayer.getPet() != null) {
            petPlayer.getPet().getPetEntity().remove();
        }
    }

    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() != EntityType.PLAYER) {
            return;
        }

        Pet pet = PetsManager.getPet(event.getEntity());
        if (pet != null) {
            event.setCancelled(true);
            Bukkit.getPluginManager().callEvent(new PetClickEvent(pet, (Player) event.getDamager(), PetClickEvent.ClickType.LEFT));
        }
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent event) {
        Pet pet = PetsManager.getPet(event.getEntity());
        if (pet != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (!event.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }

        Pet pet = PetsManager.getPet(event.getRightClicked());
        if (pet != null) {
            event.setCancelled(true);
            Bukkit.getPluginManager().callEvent(new PetClickEvent(pet, event.getPlayer(), PetClickEvent.ClickType.RIGHT));
        }
    }

    @EventHandler
    public void PetActionFinishEvent(PetActionFinishEvent event) {
        Pet pet = event.getPet();

        if (pet.getPetActions().getNextActionType() != null) {
            pet.getPetActions().setActionType(pet.getPetActions().getNextActionType());
            return;
        }

        pet.getPetActions().run();
    }

    @EventHandler
    public void PetLoadEvent(PetLoadEvent event) {
        event.getPet().getPetActions().setActionType(PetAction.PetActionType.SPAWN);
    }

    @EventHandler
    public void PetClickEvent(PetClickEvent event) {
        if (event.getClickType() == PetClickEvent.ClickType.RIGHT) {
            if (event.getPet().getPetEntity().getEntityLabels().showModes(40)) {
                event.getPlayer().playSound(event.getPlayer(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            }
        }

        if (event.getClickType() == PetClickEvent.ClickType.LEFT) {
            PetDataMode.PetDataModeType mode = event.getPet().getPetEntity().getEntityLabels().getSelectedMode();
            event.getPet().getPetEntity().getEntityLabels().showText("§a" + PetEntityLabelRotable.CHAR_ARROW_RIGHT + " " + mode.getColor() + mode.getName() + "§r §a" + PetEntityLabelRotable.CHAR_ARROW_LEFT, 20);

            if (event.getPet().getPetData().getMode().getType() != mode) {
                event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                event.getPet().getPetData().getMode().setType(mode);
            }
        }
    }
}
