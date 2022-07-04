package realcraft.bukkit.holograms.packets;

import realcraft.bukkit.holograms.Hologram;

public class HologramPackets {

    private final Hologram hologram;

    private final HologramSpawnPacket spawnPacket;
    private final HologramPostSpawnPacket postSpawnPacket;
    private final HologramRemovePacket removePacket;
    private final HologramTeleportPacket teleportPacket;
    private final HologramNamePacket namePacket;

    public HologramPackets(Hologram hologram) {
        this.hologram = hologram;

        this.spawnPacket = new HologramSpawnPacket(hologram);
        this.postSpawnPacket = new HologramPostSpawnPacket(hologram);
        this.teleportPacket = new HologramTeleportPacket(hologram);
        this.removePacket = new HologramRemovePacket(hologram);
        this.namePacket = new HologramNamePacket(hologram);
    }

    public Hologram getHologram() {
        return hologram;
    }

    public HologramSpawnPacket getSpawnPacket() {
        return spawnPacket;
    }

    public HologramPostSpawnPacket getPostSpawnPacket() {
        return postSpawnPacket;
    }

    public HologramRemovePacket getRemovePacket() {
        return removePacket;
    }

    public HologramTeleportPacket getTeleportPacket() {
        return teleportPacket;
    }

    public HologramNamePacket getNamePacket() {
        return namePacket;
    }
}
