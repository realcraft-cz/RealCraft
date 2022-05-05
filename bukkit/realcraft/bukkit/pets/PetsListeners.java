package realcraft.bukkit.pets;

import org.bukkit.Bukkit;
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
        PetsManager.debug(event.getClickType().toString());
    }
}
