package realcraft.bukkit.pets.pet.timers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.data.PetDataHealth;
import realcraft.bukkit.pets.pet.entity.labels.PetEntityLabelProgress;

public class PetTimerHealth extends PetTimer {

    public PetTimerHealth(Pet pet) {
        super(PetTimerType.HEALTH, pet);
    }

    @Override
    protected void _run() {
        if (!this.getPet().getPetEntity().isLiving()) {
            return;
        }

        if (this.getPet().getPetData().getFood().getValue() <= this.getPet().getPetData().getFood().getCriticalValue()) {
            this.addHealth(-1);
            return;
        }

        if (this.getPet().getPetData().getHeat().getValue() <= this.getPet().getPetData().getHeat().getCriticalValue()) {
            this.addHealth(-1);
            return;
        }

        this.addHealth(1);
    }

    private void addHealth(int value) {
        int oldValue = this.getPet().getPetData().getHealth().getValue();

        this.getPet().getPetData().getHealth().setValue(this.getPet().getPetData().getHealth().getValue() + value);

        if (value > 0) {
            if (this.getPet().getPetData().getHealth().getValue() == oldValue) {
                return;
            }

            this.getPet().getPetEntity().getEntityLabels().showProgress(new PetEntityLabelProgress.ProgressOptions(
                oldValue,
                this.getPet().getPetData().getHealth().getMaxValue(),
                this.getPet().getPetData().getHealth().getValue() - oldValue,
                PetDataHealth.COLOR,
                ChatColor.GRAY,
                ChatColor.WHITE,
                PetDataHealth.CHAR
            ), 20);

            Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (!getPet().getPetEntity().isSpawned()) {
                        return;
                    }

                    getPet().getPetEntity().getEntityLabels().showProgress(new PetEntityLabelProgress.ProgressOptions(
                        getPet().getPetData().getHealth().getValue(),
                        getPet().getPetData().getHealth().getMaxValue(),
                        0,
                        PetDataHealth.COLOR,
                        ChatColor.GRAY,
                        ChatColor.WHITE,
                        PetDataHealth.CHAR
                    ), 20);
                }
            }, 12);

            this.getPet().getPetEntity().getEntity().getWorld().spawnParticle(Particle.HEART, this.getPet().getPetEntity().getEntity().getEyeLocation().add(0, 0.5, 0), 1, 0, 0, 0, 0);
        } else {
            this.getPet().getPetEntity().getEntity().getWorld().playSound(this.getPet().getPetEntity().getEntity().getLocation(), Sound.ENTITY_GHAST_HURT, 0.5f, 1f);
            this.getPet().getPetEntity().getEntity().getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, this.getPet().getPetEntity().getEntity().getEyeLocation().add(0, 0.5, 0), 3, 0.1, 0.1, 0.1, 0);
        }
    }
}
