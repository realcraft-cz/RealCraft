package realcraft.bukkit.pets.pet.entity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitTask;
import realcraft.bukkit.RealCraft;

public class PetEntityLabel implements Runnable {

    private PetEntity petEntity;
    private ArmorStand standLabel;
    private BukkitTask task;
    private int endTick;

    public PetEntityLabel(PetEntity petEntity) {
        this.petEntity = petEntity;
    }

    public PetEntity getPetEntity() {
        return petEntity;
    }

    public void showText(String label, int duration) {
        this._spawn();

        standLabel.setCustomNameVisible(true);
        standLabel.setCustomName(label);

        endTick = Bukkit.getCurrentTick() + duration;

        if (task == null) {
            task = Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(), this, 1, 1);
        }

        this.run();
    }

    public void showProgressLabel(ProgressLabelOptions options, int duration) {
        String text = options.fillColor() + (options.barChar.repeat(Math.max(0, options.fill()))) +
            options.incrementColor() + (options.barChar.repeat(Math.max(0, options.increment()))) +
            options.emptyColor() + (options.barChar.repeat(Math.max(0, options.length() - options.fill() - options.increment())));

        this.showText(text, duration);
    }

    private void _spawn() {
        if (standLabel != null && standLabel.isValid()) {
            return;
        }

        this.remove();

        standLabel = (ArmorStand) this.getPetEntity().getEntity().getWorld().spawnEntity(this.getPetEntity().getEntity().getLocation(), EntityType.ARMOR_STAND);
        standLabel.setInvisible(true);
        standLabel.setPersistent(false);
        standLabel.setInvulnerable(true);
        standLabel.setGravity(false);
        standLabel.setSmall(true);
        standLabel.setBasePlate(false);
        standLabel.setArms(false);
        standLabel.setCustomNameVisible(false);
    }

    public void remove() {
        if (standLabel != null && standLabel.isValid()) {
            standLabel.remove();
            standLabel = null;
        }

        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public void run() {
        if (!standLabel.isValid()) {
            return;
        }

        if (Bukkit.getCurrentTick() > endTick) {
            this.remove();
            return;
        }

        standLabel.setTicksLived(1);

        Location location = this.getPetEntity().getEntity().getLocation();
        ((CraftArmorStand)standLabel).getHandle().c(location.getX(), location.getY(), location.getZ());
    }

    public record ProgressLabelOptions(int fill, int length, int increment, ChatColor fillColor, ChatColor emptyColor, ChatColor incrementColor, String barChar) {

        public ProgressLabelOptions(int fill, int length, int increment, ChatColor fillColor, ChatColor emptyColor, ChatColor incrementColor) {
            this(fill, length, increment, fillColor, emptyColor, incrementColor, "\u258d");
        }

        public ProgressLabelOptions(int progress, int length, ChatColor fillColor, ChatColor emptyColor) {
            this(progress, length, 0, fillColor, emptyColor, ChatColor.WHITE, "\u258d");
        }
    }
}
