package realcraft.bukkit.pets.pet.entity.labels;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.holograms.Hologram;
import realcraft.bukkit.holograms.Holograms;
import realcraft.bukkit.pets.pet.entity.PetEntity;

public class PetEntityLabelText implements Runnable {

    private final PetEntity petEntity;
    private BukkitTask task;
    private Hologram hologram;
    private int endTick;

    public PetEntityLabelText(PetEntity petEntity) {
        this.petEntity = petEntity;
    }

    public PetEntity getPetEntity() {
        return petEntity;
    }

    public boolean isVisible() {
        return hologram != null;
    }

    public void showText(String label, int duration) {
        this._spawn();

        hologram.setText(label);

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
        if (hologram != null) {
            return;
        }

        this.remove();

        hologram = Holograms.createHologram();
        hologram.spawn(this.getPetEntity().getPet().getPetPlayer().getPlayer(), this.getPetEntity().getEntity().getLocation().add(0, 1, 0));
    }

    public void remove() {
        if (hologram != null) {
            hologram.remove();
            hologram = null;
        }

        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public void run() {
        if (Bukkit.getCurrentTick() > endTick) {
            this.remove();
            return;
        }

        hologram.setLocation(this.getPetEntity().getEntity().getLocation().add(0, 1, 0));
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
