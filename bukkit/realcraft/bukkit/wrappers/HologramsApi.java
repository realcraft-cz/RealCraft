package realcraft.bukkit.wrappers;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.utils.StringUtil;

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

        private final eu.decentsoftware.holograms.api.holograms.Hologram hologram;

        public Hologram(Location location) {
            hologram = DHAPI.createHologram(StringUtil.getRandomChars(32), location);
        }

        public Hologram(String name, Location location) {
            hologram = DHAPI.createHologram(name, location);
        }

        public HologramLine insertTextLine(int index, String text) {
            return DHAPI.addHologramLine(hologram, text);
        }

        public HologramLine insertItemLine(int index, ItemStack item) {
            // TODO: fix
            return DHAPI.addHologramLine(hologram, item);
        }

        public HologramLine appendItemLine(ItemStack item) {
            return DHAPI.addHologramLine(hologram, item);
        }

        public void removeLine(int index) {
            DHAPI.removeHologramLine(hologram, index);
        }

        public HologramLine getTextLine(int index) {
            return DHAPI.getHologramLine(hologram.getPage(0), index);
        }

        public HologramLine getItemLine(int index) {
            return DHAPI.getHologramLine(hologram.getPage(0), index);
        }

        public void clearLines() {
            while (!hologram.getPage(0).getLines().isEmpty()) {
                DHAPI.removeHologramLine(hologram, 0);
            }
        }

        public Location getLocation() {
            return hologram.getLocation();
        }

        public void setLocation(Location location) {
            hologram.setLocation(location);
        }

        public void delete() {
            instance.delete(this);
            hologram.delete();
        }
    }
}

/*
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

        public Hologram(Location location) {
            // hologram = DHAPI.createHologram(StringUtil.getRandomChars(32), location);
        }

        public Hologram(String name, Location location) {
            // hologram = DHAPI.createHologram(name, location);
        }

        public void insertTextLine(int index, String text) {
            // return DHAPI.addHologramLine(hologram, text);
        }

        public void insertItemLine(int index, ItemStack item) {
            // return DHAPI.addHologramLine(hologram, item);
        }

        public void appendItemLine(ItemStack item) {
            // return DHAPI.addHologramLine(hologram, item);
        }

        public void removeLine(int index) {
            // DHAPI.removeHologramLine(hologram, index);
        }

        public HologramLine getTextLine(int index) {
            return new HologramLine();
            // return DHAPI.getHologramLine(hologram.getPage(0), index);
        }

        public HologramLine getItemLine(int index) {
            return new HologramLine();
            // return DHAPI.getHologramLine(hologram.getPage(0), index);
        }

        public void clearLines() {

        }

        public void delete() {

        }
    }

    public static class HologramLine {

        public void setText(String f) {

        }
    }
}
*/
