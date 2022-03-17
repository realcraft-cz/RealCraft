package realcraft.bukkit.wrappers;

import me.filoghost.holographicdisplays.api.beta.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.beta.hologram.VisibilitySettings;
import me.filoghost.holographicdisplays.api.beta.hologram.line.ItemHologramLine;
import me.filoghost.holographicdisplays.api.beta.hologram.line.TextHologramLine;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import realcraft.bukkit.RealCraft;

import java.util.ArrayList;

public class HologramsApi {

    private final ArrayList<Hologram> holograms = new ArrayList<>();
    private static HologramsApi instance;

    public HologramsApi(RealCraft plugin) {
        instance = this;
    }

    public static Hologram createHologram(Location location) {
        Hologram hologram = new Hologram(location);
        instance.holograms.add(hologram);
        return hologram;
    }

    private void delete(Hologram hologram) {
        holograms.remove(hologram);
    }

    public static class Hologram {

        private me.filoghost.holographicdisplays.api.beta.hologram.Hologram hologram;

        public Hologram(Location location) {
            hologram = HolographicDisplaysAPI.get(RealCraft.getInstance()).createHologram(location);
        }

        public TextHologramLine insertTextLine(int index, String text) {
            return hologram.getLines().insertText(index, text);
        }

        public ItemHologramLine insertItemLine(int index, ItemStack item) {
            return hologram.getLines().insertItem(index, item);
        }

        public TextHologramLine appendTextLine(String text) {
            return hologram.getLines().appendText(text);
        }

        public ItemHologramLine appendItemLine(ItemStack item) {
            return hologram.getLines().appendItem(item);
        }

        public void removeLine(int index) {
            hologram.getLines().remove(index);
        }

        public TextHologramLine getTextLine(int index) {
            return (TextHologramLine) hologram.getLines().get(index);
        }

        public ItemHologramLine getItemLine(int index) {
            return (ItemHologramLine) hologram.getLines().get(index);
        }

        public void clearLines() {
            if (this.isDeleted()) {
                return;
            }

            hologram.getLines().clear();
        }

        public VisibilitySettings getVisibilityManager() {
            return hologram.getVisibilitySettings();
        }

        public Location getLocation() {
            return hologram.getPosition().toLocation();
        }

        public void setLocation(Location location) {
            hologram.setPosition(location);
        }

        public void delete() {
            instance.delete(this);
            hologram.delete();
        }

        public boolean isDeleted() {
            return hologram.isDeleted();
        }
    }
}
