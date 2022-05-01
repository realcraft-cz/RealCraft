package realcraft.bukkit.pets.pet.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import realcraft.bukkit.pets.PetsManager;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.utils.EntityUtil;
import realcraft.bukkit.utils.ItemUtil;

public class PetEntity {

    private final Pet pet;
    private final PetEntityLabel entityLabel;
    private Zombie entity;

    public PetEntity(Pet pet) {
        this.pet = pet;
        this.entityLabel = new PetEntityLabel(this);
    }

    public Pet getPet() {
        return pet;
    }

    public PetEntityLabel getEntityLabel() {
        return entityLabel;
    }

    public Zombie getEntity() {
        return entity;
    }

    public boolean isLiving() {
        return entity != null && entity.isValid();
    }

    public void spawn(Location location) {
        if (this.isLiving()) {
            this.remove();
        }

        location.setPitch(0f);

        entity = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE, false);
        entity.setRemoveWhenFarAway(false);
        entity.setPersistent(false);
        entity.setInvulnerable(false);
        entity.setInvisible(true);
        entity.setSilent(true);
        entity.setBaby();
        entity.getEquipment().clear();
        entity.getEquipment().setHelmet(ItemUtil.getHead(this.getPet().getPetData().getSkin().getValue()));
        entity.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
        entity.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
        entity.getEquipment().setItemInMainHandDropChance(0);
        entity.getEquipment().setItemInOffHandDropChance(0);

        EntityUtil.clearPathfinders(entity);

        if (PetsManager.isDebug()) {
            entity.setInvisible(false);
            entity.setCustomNameVisible(true);
        }

        PetsManager.registerPet(this.getPet());
    }

    public void remove() {
        if (this.isLiving()) {
            entity.remove();
            entity = null;

            PetsManager.unregisterPet(this.getPet());
        }

        this.getEntityLabel().remove();
    }
}
