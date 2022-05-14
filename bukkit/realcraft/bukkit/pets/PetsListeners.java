package realcraft.bukkit.pets;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.events.pet.*;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.actions.PetAction;
import realcraft.bukkit.pets.pet.actions.PetActionFollow;
import realcraft.bukkit.pets.pet.data.PetDataMode;
import realcraft.bukkit.pets.pet.entity.labels.PetEntityLabelRotable;

public class PetsListeners implements Listener {

    public PetsListeners() {
        Bukkit.getPluginManager().registerEvents(this, RealCraft.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerSpawnLocationEvent(PlayerSpawnLocationEvent event) {
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
    public void PetLoadEvent(PetLoadEvent event) {
        if (event.getPet().getPetData().getMode().getType() == PetDataMode.PetDataModeType.SIT) {
            event.getPet().getPetData().getMode().setType(PetDataMode.PetDataModeType.FOLLOW);
        }

        Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (event.getPet().getPetPlayer().getPlayer() == null || !event.getPet().getPetPlayer().getPlayer().isValid()) {
                    return;
                }

                if (event.getPet().getPetData().getMode().getType() == PetDataMode.PetDataModeType.HOME && event.getPet().getPetData().getHome().getLocation() != null) {
                    event.getPet().getPetEntity().spawn(event.getPet().getPetData().getHome().getLocation());
                    event.getPet().getPetActions().setActionType(PetAction.PetActionType.HOME);
                    return;
                }

                event.getPet().getPetActions().setActionType(PetAction.PetActionType.SPAWN);
            }
        }, 80);
    }

    @EventHandler
    public void PetSpawnEvent(PetSpawnEvent event) {
        event.getPet().getPetTimers().start();
    }

    @EventHandler
    public void PetRemoveEvent(PetRemoveEvent event) {
        event.getPet().getPetTimers().cancel();
    }

    @EventHandler
    public void PetActionFinishEvent(PetActionFinishEvent event) {
        Pet pet = event.getPet();

        if (event.getAction().getType() == PetAction.PetActionType.SPAWN || event.getAction().getType() == PetAction.PetActionType.SKIN_CHANGE) {
            ((PetActionFollow)event.getPet().getPetActions().getAction(PetAction.PetActionType.FOLLOW)).resetDistanceLevel();
        }

        if (pet.getPetActions().getNextActionType() != null) {
            pet.getPetActions().setActionType(pet.getPetActions().getNextActionType());
            return;
        }

        pet.getPetActions().run();
    }

    @EventHandler
    public void PetClickEvent(PetClickEvent event) {
        if (event.getPet().getPetActions().getCurrentAction().getType() == PetAction.PetActionType.NONE) {
            event.getPet().getPetActions().getCurrentAction().start();
        }

        if (event.getClickType() == PetClickEvent.ClickType.RIGHT) {
            if (event.getPet().getPetEntity().getEntityLabels().showModes(event.getPet().getPetData().getMode().getType().getNextPreferredType(), 40)) {
                event.getPlayer().playSound(event.getPlayer(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            }
        }

        if (event.getClickType() == PetClickEvent.ClickType.LEFT) {
            if (!event.getPet().getPetEntity().getEntityLabels().getLabelModes().isVisible()) {
                event.getPet().getPetEntity().getEntityLabels().getLabelModes().setCurrentItemType(event.getPet().getPetData().getMode().getType().getNextPreferredType());
            }

            PetDataMode.PetDataModeType mode = (PetDataMode.PetDataModeType) event.getPet().getPetEntity().getEntityLabels().getLabelModes().getSelectedItem().getType();
            boolean failed = false;

            if (mode == PetDataMode.PetDataModeType.HOME && event.getPet().getPetData().getHome().getLocation() == null) {
                failed = true;
                event.getPet().getPetPlayer().sendMessage("§cMazlik nema domov, nastavis ho prikazem §6/pet home");
            }

            if (mode == PetDataMode.PetDataModeType.HOME && event.getPet().getPetData().getMode().getType() == PetDataMode.PetDataModeType.HOME) {
                failed = true;
            }

            if (mode == PetDataMode.PetDataModeType.SIT && event.getPet().getPetData().getMode().getType() == PetDataMode.PetDataModeType.SIT) {
                failed = true;
            }

            if (mode == PetDataMode.PetDataModeType.SIT && event.getPet().getPetData().getMode().getType() == PetDataMode.PetDataModeType.HOME) {
                failed = true;
            }

            if (mode == PetDataMode.PetDataModeType.SIT && event.getPet().getPetActions().getCurrentAction().getType() == PetAction.PetActionType.SIT_BESIDE) {
                failed = true;
            }

            if (failed) {
                event.getPet().getPetEntity().getEntityLabels().showText("§c" + PetEntityLabelRotable.CHAR_ARROW_RIGHT + " " + mode.getColor() + mode.getName() + "§r §c" + PetEntityLabelRotable.CHAR_ARROW_LEFT, 20);
                return;
            }

            event.getPet().getPetEntity().getEntityLabels().showText("§a" + PetEntityLabelRotable.CHAR_ARROW_RIGHT + " " + mode.getColor() + mode.getName() + "§r §a" + PetEntityLabelRotable.CHAR_ARROW_LEFT, 20);

            if (event.getPet().getPetData().getMode().getType() != mode) {
                event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            }

            event.getPet().getPetData().getMode().setType(mode);
        }
    }

    @EventHandler
    public void PetModeChangeEvent(PetModeChangeEvent event) {
        if (event.getMode() == PetDataMode.PetDataModeType.FOLLOW) {
            ((PetActionFollow)event.getPet().getPetActions().getAction(PetAction.PetActionType.FOLLOW)).resetDistanceLevel();
        }
    }
}
