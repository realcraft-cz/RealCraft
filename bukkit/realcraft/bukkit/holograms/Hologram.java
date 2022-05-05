package realcraft.bukkit.holograms;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import realcraft.bukkit.holograms.packets.HologramPackets;

import java.util.ArrayList;

public class Hologram {

    private final int id;
    private final HologramPackets packets;
    private final ArrayList<Player> players = new ArrayList<>();

    private Location location;
    private String text;

    public Hologram(int id) {
        this.id = id;
        this.packets = new HologramPackets(this);
    }

    public int getId() {
        return id;
    }

    public HologramPackets getPackets() {
        return packets;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;

        for (Player player : players) {
            this.getPackets().getTeleportPacket().send(player);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(@Nullable String text) {
        this.text = text;

        for (Player player : players) {
            this.getPackets().getNamePacket().send(player);
        }
    }

    public void spawn(Player player, Location location) {
        players.add(player);
        this.location = location;
        this.getPackets().getSpawnPacket().send(player);
        this.getPackets().getPostSpawnPacket().send(player);
    }

    public void remove() {
        for (Player player : players) {
            this.getPackets().getRemovePacket().send(player);
        }

        players.clear();

        Holograms._removeHologram(this);
    }
}
