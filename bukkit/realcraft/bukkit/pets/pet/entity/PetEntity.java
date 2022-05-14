package realcraft.bukkit.pets.pet.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import realcraft.bukkit.pets.PetsManager;
import realcraft.bukkit.pets.events.pet.PetRemoveEvent;
import realcraft.bukkit.pets.events.pet.PetSpawnEvent;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.entity.labels.PetEntityLabels;
import realcraft.bukkit.utils.EntityUtil;
import realcraft.bukkit.utils.ItemUtil;

public class PetEntity {

    private final Pet pet;
    private final PetEntityLabels entityLabels;
    private final PetEntityEffect entityEffect;

    private Zombie entity;

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

    public Zombie getEntity() {
        return entity;
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

        entity = (Zombie) location.getWorld().spawnEntity(location, EntityType.DROWNED, false);
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

        EntityUtil.clearPathfinders(entity);
        PetsManager.registerPet(this.getPet());

        this.getEntityEffect().start();

        Bukkit.getPluginManager().callEvent(new PetSpawnEvent(this.getPet()));
    }

    public void remove() {
        if (this.isLiving()) {
            entity.remove();
            entity = null;

            PetsManager.unregisterPet(this.getPet());
        }

        this.getEntityEffect().cancel();
        this.getEntityLabels().clear();

        Bukkit.getPluginManager().callEvent(new PetRemoveEvent(this.getPet()));
    }
}
