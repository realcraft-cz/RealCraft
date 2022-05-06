package realcraft.bukkit.pets.pet.entity.labels;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import realcraft.bukkit.holograms.Hologram;
import realcraft.bukkit.holograms.Holograms;
import realcraft.bukkit.pets.pet.entity.PetEntity;

public abstract class PetEntityLabelRotable extends PetEntityLabel {

    public static final String CHAR_ARROW_LEFT = "\u140A";
    public static final String CHAR_ARROW_RIGHT = "\u1405";

    private final RotableItem[] items;
    private boolean visible;

    public PetEntityLabelRotable(PetEntityLabelType type, PetEntity petEntity, RotableItem[] items) {
        super(type, petEntity);

        this.items = items;

        for (int i = 0; i < items.length; i++) {
            items[i].setHologram(Holograms.createHologram());
            items[i].setIndex(i);
        }
    }

    public RotableItem[] getItems() {
        return items;
    }

    public RotableItem getCurrentItem() {
        for (RotableItem item : items) {
            if (item.getIndex() == this.getCurrentIndex()) {
                return item;
            }
        }

        return null;
    }

    public boolean isVisible() {
        return visible;
    }

    public int getCurrentIndex() {
        return items.length / 2;
    }

    @Override
    public void show() {
        if (visible) {
            for (RotableItem item : items) {
                item.setIndex((item.getIndex() + 1) % items.length);
            }
        }

        for (RotableItem item : items) {
            if (visible) {
                item.getHologram().setLocation(this._getItemLocation(item));
            } else {
                item.getHologram().spawn(this.getPetEntity().getPet().getPetPlayer().getPlayer(), this._getItemLocation(item));
            }

            item.getHologram().setText((item.getIndex() == this.getCurrentIndex() ? "§7" + CHAR_ARROW_RIGHT + " " + item.getCurrentText() + "§r §7" + CHAR_ARROW_LEFT : item.getDisabledText()));
        }

        visible = true;
    }

    @Override
    public void remove() {
        for (RotableItem item : items) {
            item.getHologram().remove();
        }

        visible = false;
    }

    @Override
    public void run() {
        for (RotableItem item : items) {
            item.getHologram().setLocation(this._getItemLocation(item));
        }
    }

    private Location _getItemLocation(RotableItem item) {
        Vector direction = this.getPetEntity().getPet().getPetPlayer().getPlayer().getLocation().getDirection().setY(0).normalize();
        Location location = this.getPetEntity().getEntity().getLocation();
        location.add(item.getOffsetVector(direction));
        return location.add(0, 1, 0);
    }

    public static class RotableItem {

        private final Enum<?> type;
        private final String currentText;
        private final String disabledText;
        private Hologram hologram;
        private int index;

        public RotableItem(Enum<?> type, String currentText, String disabledText) {
            this.type = type;
            this.currentText = currentText;
            this.disabledText = disabledText;
        }

        public Enum<?> getType() {
            return type;
        }

        public String getCurrentText() {
            return currentText;
        }

        public String getDisabledText() {
            return disabledText;
        }

        public Hologram getHologram() {
            return hologram;
        }

        public void setHologram(Hologram hologram) {
            this.hologram = hologram;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public Vector getOffsetVector(Vector direction) {
            Vector offsetVector = new Vector(0, 0, 0);

            if (this.getIndex() == 2) {
                offsetVector.add(direction).add(new Vector(0, -0.3, 0));
                offsetVector.rotateAroundY(1);
            } else if (this.getIndex() == 0) {
                offsetVector.add(direction).add(new Vector(0, -0.3, 0));
                offsetVector.rotateAroundY(-1);
            }

            return offsetVector;
        }
    }

    //https://stackoverflow.com/questions/4839993/how-to-draw-polygons-on-an-html5-canvas
}
