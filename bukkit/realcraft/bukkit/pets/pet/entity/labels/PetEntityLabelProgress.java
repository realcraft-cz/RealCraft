package realcraft.bukkit.pets.pet.entity.labels;

import org.bukkit.ChatColor;
import realcraft.bukkit.holograms.Hologram;
import realcraft.bukkit.holograms.Holograms;
import realcraft.bukkit.pets.pet.entity.PetEntity;

public class PetEntityLabelProgress extends PetEntityLabel {

    private final Hologram hologram;
    private ProgressOptions options;

    public PetEntityLabelProgress(PetEntity petEntity) {
        super(PetEntityLabelType.PROGRESS, petEntity);

        this.hologram = Holograms.createHologram();
    }

    public void setOptions(ProgressOptions options) {
        this.options = options;
    }

    @Override
    public void show() {
        hologram.spawn(this.getPetEntity().getPet().getPetPlayer().getPlayer(), this.getPetEntity().getEntity().getLocation().add(0, 1, 0));

        String text = options.fillColor() + (options.barChar.repeat(Math.max(0, options.fill()))) +
            options.incrementColor() + (options.barChar.repeat(Math.max(0, options.increment()))) +
            options.emptyColor() + (options.barChar.repeat(Math.max(0, options.length() - options.fill() - options.increment())));

        hologram.setText(text);
    }

    @Override
    public void remove() {
        hologram.remove();
    }

    @Override
    public void run() {
        hologram.setLocation(this.getPetEntity().getEntity().getLocation().add(0, 1, 0));
    }

    public record ProgressOptions(int fill, int length, int increment, ChatColor fillColor, ChatColor emptyColor, ChatColor incrementColor, String barChar) {

        public ProgressOptions(int fill, int length, int increment, ChatColor fillColor, ChatColor emptyColor, ChatColor incrementColor) {
            this(fill, length, increment, fillColor, emptyColor, incrementColor, "\u258d");
        }

        public ProgressOptions(int progress, int length, ChatColor fillColor, ChatColor emptyColor) {
            this(progress, length, 0, fillColor, emptyColor, ChatColor.WHITE, "\u258d");
        }
    }
}
