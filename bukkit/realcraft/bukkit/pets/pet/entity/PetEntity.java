package realcraft.bukkit.pets.pet.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Drowned;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;
import realcraft.bukkit.pets.PetsManager;
import realcraft.bukkit.pets.events.pet.PetRemoveEvent;
import realcraft.bukkit.pets.events.pet.PetSpawnEvent;
import realcraft.bukkit.pets.events.pet.PetTeleportEvent;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.entity.labels.PetEntityLabels;
import realcraft.bukkit.utils.ItemUtil;

public class PetEntity {

    private final Pet pet;
    private final PetEntityLabels entityLabels;
    private final PetEntityEffect entityEffect;

    private Drowned entity;

    public PetEntity(Pet pet) {
        this.pet = pet;
        this.entityLabels = new PetEntityLabels(this);
        this.entityEffect = new PetEntityEffect(this);
    }

    public Pet getPet() {
        return pet;
    }

    public PetEntityLabels getEntityLabels() {
        return entityLabels;
    }

    public PetEntityEffect getEntityEffect() {
        return entityEffect;
    }

    public Drowned getEntity() {
        return entity;
    }

    public boolean isSpawned() {
        return entity != null;
    }

    public boolean isLiving() {
        return entity != null && entity.isValid();
    }

    public boolean isTicking() {
        return this.isLiving() && this.getPet().getPetEntity().getEntity().getTrackedPlayers().size() > 0;
    }

    public void setShaking(boolean shaking) {
        entity.setFreezeTicks(shaking ? entity.getMaxFreezeTicks() + 6000 : 0);
    }

    public void spawn(Location location) {
        if (this.isLiving()) {
            this.remove();
        }

        location.setPitch(0f);

        entity = location.getWorld().spawn(location, Drowned.class, new Consumer<>() {
            @Override
            public void accept(Drowned entity) {
                entity.setCustomName("__PET");
            }
        });

        entity.setInvisible(true);
        entity.setRemoveWhenFarAway(false);
        entity.setCustomNameVisible(true);
        entity.setPersistent(false);
        entity.setInvulnerable(false);
        entity.setSilent(true);
        entity.setBaby();
        entity.getEquipment().clear();
        entity.getEquipment().setHelmet(ItemUtil.getHead(this.getPet().getPetData().getSkin().getSkin().getTexture()));
        entity.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
        entity.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
        entity.getEquipment().setItemInMainHandDropChance(0);
        entity.getEquipment().setItemInOffHandDropChance(0);
        entity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(64);

        entity.getPathfinder().stopPathfinding();
        Bukkit.getMobGoals().removeAllGoals(entity);

        PetsManager.registerPet(this.getPet());

        this.getEntityEffect().start();

        Bukkit.getPluginManager().callEvent(new PetSpawnEvent(this.getPet()));
    }

    public void remove() {
        if (this.isLiving()) {
            entity.remove();

            PetsManager.unregisterPet(this.getPet());
        }

        entity = null;

        this.getEntityEffect().cancel();
        this.getEntityLabels().clear();

        Bukkit.getPluginManager().callEvent(new PetRemoveEvent(this.getPet()));
    }

    public void teleport(Location location) {
        if (!this.isSpawned()) {
            return;
        }

        Location from = entity.getLocation();
        entity.teleport(location);
        Bukkit.getPluginManager().callEvent(new PetTeleportEvent(this.getPet(), from, location));
    }
}
