package realcraft.bukkit.pets.pet.entity.labels;

import realcraft.bukkit.holograms.Hologram;
import realcraft.bukkit.holograms.Holograms;
import realcraft.bukkit.pets.pet.entity.PetEntity;

public class PetEntityLabelText extends PetEntityLabel {

    private final Hologram hologram;
    private String text;

    public PetEntityLabelText(PetEntity petEntity) {
        super(PetEntityLabelType.TEXT, petEntity);

        this.hologram = Holograms.createHologram();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void show() {
        hologram.spawn(this.getPetEntity().getPet().getPetPlayer().getPlayer(), this.getPetEntity().getEntity().getLocation().add(0, 1, 0));
        hologram.setText(this.getText());
    }

    @Override
    public void remove() {
        hologram.remove();
    }

    @Override
    public void run() {
        hologram.setLocation(this.getPetEntity().getEntity().getLocation().add(0, 1, 0));
    }
}
