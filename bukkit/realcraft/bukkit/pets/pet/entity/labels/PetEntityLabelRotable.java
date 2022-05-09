package realcraft.bukkit.pets.pet.entity.labels;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import realcraft.bukkit.holograms.Hologram;
import realcraft.bukkit.holograms.Holograms;
import realcraft.bukkit.pets.pet.entity.PetEntity;

public abstract class PetEntityLabelRotable extends PetEntityLabel {

    public static final String CHAR_ARROW_LEFT = "\u140A";
    public static final String CHAR_ARROW_RIGHT = "\u1405";
    private static final int SELECTED_INDEX = 0;

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

    public void setCurrentItemType(Enum<?> type) {
        RotableItem currentItem = null;

        for (RotableItem item : items) {
            if (item.getType() == type) {
                currentItem = item;
                break;
            }
        }

        if (currentItem == null) {
            return;
        }

        if (currentItem.getIndex() != SELECTED_INDEX) {
            int currentIndex = currentItem.getIndex();
            for (int i = 0; i < items.length - SELECTED_INDEX - currentIndex; i++) {
                for (RotableItem item : items) {
                    item.setIndex((item.getIndex() + 1) % items.length);
                }
            }
        }

    }

    public RotableItem getSelectedItem() {
        for (RotableItem item : items) {
            if (item.getIndex() == SELECTED_INDEX) {
                return item;
            }
        }

        return null;
    }

    public boolean isVisible() {
        return visible;
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

            item.getHologram().setText((item.getIndex() == SELECTED_INDEX ? "§7" + CHAR_ARROW_RIGHT + " " + item.getSelectedText() + "§r §7" + CHAR_ARROW_LEFT : item.getDisabledText()));
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
        Location location = this.getPetEntity().getEntity().getLocation();
        Vector direction = this.getPetEntity().getEntity().getLocation().subtract(this.getPetEntity().getPet().getPetPlayer().getPlayer().getLocation().toVector().setY(location.getY())).toVector().normalize();
        location.add(item.getOffsetVector(direction, items.length));
        return location.add(0, 1, 0);
    }

    public static class RotableItem {

        private final Enum<?> type;
        private String selectedText;
        private String disabledText;
        private Hologram hologram;
        private int index;

        public RotableItem(Enum<?> type, String currentText, String disabledText) {
            this.type = type;
            this.selectedText = currentText;
            this.disabledText = disabledText;
        }

        public Enum<?> getType() {
            return type;
        }

        public String getSelectedText() {
            return selectedText;
        }

        public void setSelectedText(String selectedText) {
            this.selectedText = selectedText;
        }

        public String getDisabledText() {
            return disabledText;
        }

        public void setDisabledText(String disabledText) {
            this.disabledText = disabledText;
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

        public Vector getOffsetVector(Vector direction, int totalItems) {
            Vector offsetVector = new Vector(0, 0, 0);

            if (totalItems == 3) {
                if (this.getIndex() == 1) {
                    offsetVector
                        .add(direction)
                        .add(new Vector(0, -0.3, 0))
                        .rotateAroundY(1);
                } else if (this.getIndex() == 2) {
                    offsetVector
                        .add(direction)
                        .add(new Vector(0, -0.3, 0))
                        .rotateAroundY(-1);
                }
            } else if (totalItems == 4) {
                if (this.getIndex() == 1) {
                    offsetVector
                        .add(direction)
                        .add(new Vector(0, -0.3, 0))
                        .rotateAroundY(1);
                } else if (this.getIndex() == 2) {
                    offsetVector
                        .add(direction)
                        .add(new Vector(0, -0.3, 0));
                } else if (this.getIndex() == 3) {
                    offsetVector
                        .add(direction)
                        .add(new Vector(0, -0.3, 0))
                        .rotateAroundY(-1);
                }
            }

            return offsetVector;
        }
    }

    //https://stackoverflow.com/questions/4839993/how-to-draw-polygons-on-an-html5-canvas
}
