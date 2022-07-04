package realcraft.bukkit.pets.pet.timers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.data.PetDataHealth;
import realcraft.bukkit.pets.pet.entity.labels.PetEntityLabelProgress;
import realcraft.share.utils.RandomUtil;

public class PetTimerHealth extends PetTimer {

    public PetTimerHealth(Pet pet) {
        super(PetTimerType.HEALTH, pet);
    }

    @Override
    protected void _run() {
        if (!this.getPetEntity().isLiving()) {
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

            this.getPetEntity().getEntityLabels().showProgress(new PetEntityLabelProgress.ProgressOptions(
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
                    if (!getPetEntity().isSpawned()) {
                        return;
                    }

                    getPetEntity().getEntityLabels().showProgress(new PetEntityLabelProgress.ProgressOptions(
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

            this.getEntity().getWorld().spawnParticle(Particle.HEART, this.getEntity().getEyeLocation().add(0, 0.5, 0), 1, 0, 0, 0, 0);
        } else {
            this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_GHAST_SCREAM, 0.5f, (float) RandomUtil.getRandomDouble(1.0, 1.5));
            this.getEntity().getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, this.getEntity().getEyeLocation().add(0, 0.5, 0), 3, 0.1, 0.1, 0.1, 0);
        }
    }
}
