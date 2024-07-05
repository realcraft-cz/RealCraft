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
    private Location lastLocation;

    public PetEntityEffect(PetEntity petEntity) {
        this.petEntity = petEntity;
    }

    public PetEntity getPetEntity() {
        return petEntity;
    }

    @Override
    public void run() {
        if (!this.getPetEntity().isLiving()) {
            return;
        }

        if (lastLocation != null) {
            this.getPetEntity().getPet().getPetData().getEffect().getType().run(this);

            if (Bukkit.getCurrentTick() % 10 == 0 && this.getPetEntity().getEntity().isInWater()) {
                lastLocation.getWorld().spawnParticle(Particle.BUBBLE_COLUMN_UP, lastLocation, 2, 0.2, 0.2, 0.2, 0);
            }
        }

        lastLocation = this.getPetEntity().getEntity().getLocation();
    }

    public void start() {
        this.cancel();
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
                petEntityEffect.lastLocation.add(0, 0.6, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.MYCELIUM, petEntityEffect.lastLocation, isMoving ? 5 : 3, 0.1, 0.1, 0.1, 0);
            }

            if (this == PORTAL) {
                petEntityEffect.lastLocation.add(0, 0, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.PORTAL, petEntityEffect.lastLocation, isMoving ? 4 : 2, 0.1, 0, 0.1, 0);
            }

            if (this == MAGIC) {
                petEntityEffect.lastLocation.add(0, 0.6, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.ENCHANTED_HIT, petEntityEffect.lastLocation, isMoving ? 4 : 2, 0.1, 0.1, 0.1, 0);
            }

            if (this == SOUL) {
                petEntityEffect.lastLocation.add(0, 0.6, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.SOUL, petEntityEffect.lastLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0);
            }

            if (this == FLAME) {
                petEntityEffect.lastLocation.add(0, 0.6, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.FLAME, petEntityEffect.lastLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0);
            }

            if (this == FLAME_SOUL) {
                petEntityEffect.lastLocation.add(0, 0.6, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, petEntityEffect.lastLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0);
            }

            if (this == GLOW) {
                petEntityEffect.lastLocation.add(0, 1.4, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.GLOW, petEntityEffect.lastLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0);
            }

            if (this == SPELL) {
                petEntityEffect.lastLocation.add(0, 1.35, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.INSTANT_EFFECT, petEntityEffect.lastLocation, isMoving ? 2 : 1, 0.1, 0, 0.1, 0);
            }

            if (this == SPELL_WITCH) {
                petEntityEffect.lastLocation.add(0, 1.35, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.WITCH, petEntityEffect.lastLocation, isMoving ? 2 : 1, 0.1, 0, 0.1, 0);
            }

            if (this == WAX_OFF) {
                petEntityEffect.lastLocation.add(0, 0.6, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.WAX_OFF, petEntityEffect.lastLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0);
            }

            if (this == SCRAPE) {
                petEntityEffect.lastLocation.add(0, 0.6, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.SCRAPE, petEntityEffect.lastLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0);
            }

            if (this == DOLPHIN) {
                petEntityEffect.lastLocation.add(0, 0.6, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.DOLPHIN, petEntityEffect.lastLocation, isMoving ? 4 : 2, 0.1, 0.1, 0.1, 0);
            }

            if (this == ASH) {
                petEntityEffect.lastLocation.add(0, 0.6, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.ASH, petEntityEffect.lastLocation, isMoving ? 5 : 3, 0.1, 0.1, 0.1, 0);
            }

            if (this == ENCHANTMENT) {
                petEntityEffect.lastLocation.add(0, 0.6, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.ENCHANT, petEntityEffect.lastLocation, isMoving ? 4 : 2, 0.1, 0.1, 0.1, 0);
            }

            if (this == DUST_RED) {
                petEntityEffect.lastLocation.add(0, 0.6, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.DUST, petEntityEffect.lastLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.MAROON, 1f));
            }

            if (this == DUST_BLUE) {
                petEntityEffect.lastLocation.add(0, 0.6, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.DUST, petEntityEffect.lastLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.AQUA, 1f));
            }

            if (this == DUST_GREEN) {
                petEntityEffect.lastLocation.add(0, 0.6, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.DUST, petEntityEffect.lastLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.LIME, 1f));
            }

            if (this == DUST_AQUA) {
                petEntityEffect.lastLocation.add(0, 0.6, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.DUST, petEntityEffect.lastLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.TEAL, 1f));
            }

            if (this == DUST_ORANGE) {
                petEntityEffect.lastLocation.add(0, 0.6, 0);
                petEntityEffect.lastLocation.getWorld().spawnParticle(Particle.DUST, petEntityEffect.lastLocation, isMoving ? 2 : 1, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.ORANGE, 1f));
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
