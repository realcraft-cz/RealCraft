package realcraft.bukkit.pets.pet.entity.labels;

import org.bukkit.ChatColor;
import realcraft.bukkit.holograms.Hologram;
import realcraft.bukkit.holograms.Holograms;
import realcraft.bukkit.pets.pet.entity.PetEntity;

public class PetEntityLabelProgress extends PetEntityLabel {

    private final Hologram hologramName;
    private final Hologram hologramProgress;

    private String name;
    private ProgressOptions options;

    public PetEntityLabelProgress(PetEntity petEntity) {
        super(PetEntityLabelType.PROGRESS, petEntity);

        this.hologramName = Holograms.createHologram();
        this.hologramProgress = Holograms.createHologram();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOptions(ProgressOptions options) {
        this.options = options;
    }

    @Override
    public void show() {
        if (name != null) {
            hologramName.spawn(this.getPetEntity().getPet().getPetPlayer().getPlayer(), this.getPetEntity().getEntity().getLocation().add(0, 1.3, 0));
            hologramName.setText(name);
        } else {
            hologramName.remove();
        }

        hologramProgress.spawn(this.getPetEntity().getPet().getPetPlayer().getPlayer(), this.getPetEntity().getEntity().getLocation().add(0, 1, 0));

        String text = options.fillColor() + (options.barChar.repeat(Math.max(0, options.fill()))) +
            options.changeColor() + (options.barChar.repeat(Math.max(0, options.change()))) +
            options.emptyColor() + (options.barChar.repeat(Math.max(0, options.length() - options.fill() - options.change())));

        hologramProgress.setText(text);
    }

    @Override
    public void remove() {
        hologramName.remove();
        hologramProgress.remove();
    }

    @Override
    public void run() {
        hologramName.setLocation(this.getPetEntity().getEntity().getLocation().add(0, 1.3, 0));
        hologramProgress.setLocation(this.getPetEntity().getEntity().getLocation().add(0, 1, 0));
    }

    public record ProgressOptions(int fill, int length, int change, ChatColor fillColor, ChatColor emptyColor, ChatColor changeColor, String barChar) {

        public ProgressOptions(int fill, int length, int change, ChatColor fillColor, ChatColor emptyColor, ChatColor changeColor) {
            this(fill, length, change, fillColor, emptyColor, changeColor, "\u258d");
        }

        public ProgressOptions(int progress, int length, ChatColor fillColor, ChatColor emptyColor) {
            this(progress, length, 0, fillColor, emptyColor, ChatColor.WHITE, "\u258d");
        }
    }
}
