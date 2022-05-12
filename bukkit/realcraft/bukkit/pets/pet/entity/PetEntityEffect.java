package realcraft.bukkit.pets.pet.entity;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitTask;
import realcraft.bukkit.RealCraft;

public class PetEntityEffect implements Runnable {

    private final PetEntity petEntity;
    private BukkitTask task;
    private Location previousLocation;

    public PetEntityEffect(PetEntity petEntity) {
        this.petEntity = petEntity;
    }

    public PetEntity getPetEntity() {
        return petEntity;
    }

    @Override
    public void run() {
        if (this.previousLocation != null) {
            this.getPetEntity().getPet().getPetData().getEffect().getType().run(this);
        }

        this.previousLocation = this.getPetEntity().getEntity().getLocation();
    }

    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(), this, 3, 3);
    }

    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public enum PetEntityEffectType {

        NONE,
        AURA,
        SNOWFLAKE,
        PORTAL,
        MAGIC,
        SOUL,
        FLAME,
        FLAME_SOUL,
        GLOW,
        SPELL,
        SPELL_WITCH,
        WAX_OFF,
        SCRAPE,
        DOLPHIN,
        ASH,
        ENCHANTMENT,
        DUST_RED,
        DUST_BLUE,
        DUST_GREEN,
        DUST_AQUA,
        DUST_ORANGE,
        ;

        public void run(PetEntityEffect petEntityEffect) {
            if (this == NONE) {
                return;
            }

            boolean isMoving = Math.abs(petEntityEffect.petEntity.getEntity().getVelocity().getX()) > 0.01 || Math.abs(petEntityEffect.petEntity.getEntity().getVelocity().getZ()) > 0.01 || Math.abs(petEntityEffect.petEntity.getEntity().getVelocity().getY()) > 0.1;

            if (this == AURA) {
                petEntityEffect.previousLocation.add(0, 0.6, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.TOWN_AURA, petEntityEffect.previousLocation, isMoving ? 5 : 3, 0.1, 0.1, 0.1, 0);
            }

            if (this == SNOWFLAKE) {
                petEntityEffect.previousLocation.add(0, 0.7, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.SNOWFLAKE, petEntityEffect.previousLocation, isMoving ? 2 : 1, 0, 0, 0, 0);
            }

            if (this == PORTAL) {
                petEntityEffect.previousLocation.add(0, 0, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.PORTAL, petEntityEffect.previousLocation, isMoving ? 4 : 2, 0.1, 0, 0.1, 0);
            }

            if (this == MAGIC) {
                petEntityEffect.previousLocation.add(0, 0.6, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.CRIT_MAGIC, petEntityEffect.previousLocation, isMoving ? 4 : 2, 0.1, 0.1, 0.1, 0);
            }

            if (this == SOUL) {
                petEntityEffect.previousLocation.add(0, 0.6, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.SOUL, petEntityEffect.previousLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0);
            }

            if (this == FLAME) {
                petEntityEffect.previousLocation.add(0, 0.6, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.FLAME, petEntityEffect.previousLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0);
            }

            if (this == FLAME_SOUL) {
                petEntityEffect.previousLocation.add(0, 0.6, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, petEntityEffect.previousLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0);
            }

            if (this == GLOW) {
                petEntityEffect.previousLocation.add(0, 1.4, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.GLOW, petEntityEffect.previousLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0);
            }

            if (this == SPELL) {
                petEntityEffect.previousLocation.add(0, 1.35, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.SPELL_MOB_AMBIENT, petEntityEffect.previousLocation, isMoving ? 2 : 1, 0.1, 0, 0.1, 0);
            }

            if (this == SPELL_WITCH) {
                petEntityEffect.previousLocation.add(0, 1.35, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.SPELL_WITCH, petEntityEffect.previousLocation, isMoving ? 2 : 1, 0.1, 0, 0.1, 0);
            }

            if (this == WAX_OFF) {
                petEntityEffect.previousLocation.add(0, 0.6, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.WAX_OFF, petEntityEffect.previousLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0);
            }

            if (this == SCRAPE) {
                petEntityEffect.previousLocation.add(0, 0.6, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.SCRAPE, petEntityEffect.previousLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0);
            }

            if (this == DOLPHIN) {
                petEntityEffect.previousLocation.add(0, 0.6, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.DOLPHIN, petEntityEffect.previousLocation, isMoving ? 4 : 2, 0.1, 0.1, 0.1, 0);
            }

            if (this == ASH) {
                petEntityEffect.previousLocation.add(0, 0.6, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.ASH, petEntityEffect.previousLocation, isMoving ? 5 : 3, 0.1, 0.1, 0.1, 0);
            }

            if (this == ENCHANTMENT) {
                petEntityEffect.previousLocation.add(0, 0.6, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, petEntityEffect.previousLocation, isMoving ? 4 : 2, 0.1, 0.1, 0.1, 0);
            }

            if (this == DUST_RED) {
                petEntityEffect.previousLocation.add(0, 0.6, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.REDSTONE, petEntityEffect.previousLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.MAROON, 1f));
            }

            if (this == DUST_BLUE) {
                petEntityEffect.previousLocation.add(0, 0.6, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.REDSTONE, petEntityEffect.previousLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.AQUA, 1f));
            }

            if (this == DUST_GREEN) {
                petEntityEffect.previousLocation.add(0, 0.6, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.REDSTONE, petEntityEffect.previousLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.LIME, 1f));
            }

            if (this == DUST_AQUA) {
                petEntityEffect.previousLocation.add(0, 0.6, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.REDSTONE, petEntityEffect.previousLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.TEAL, 1f));
            }

            if (this == DUST_ORANGE) {
                petEntityEffect.previousLocation.add(0, 0.6, 0);
                petEntityEffect.previousLocation.getWorld().spawnParticle(Particle.REDSTONE, petEntityEffect.previousLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.ORANGE, 1f));
            }
        }

        public static PetEntityEffectType getByName(String name) {
            try {
                return PetEntityEffectType.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }

            return PetEntityEffectType.NONE;
        }
    }
}
