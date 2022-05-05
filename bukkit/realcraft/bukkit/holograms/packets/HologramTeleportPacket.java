package realcraft.bukkit.holograms.packets;

import com.comphenix.protocol.PacketType;
import org.bukkit.Location;
import realcraft.bukkit.holograms.Hologram;

public class HologramTeleportPacket extends HologramPacket {

    public HologramTeleportPacket(Hologram hologram) {
        super(hologram, PacketType.Play.Server.ENTITY_TELEPORT);

        this.getPacket().getModifier().writeDefaults();
        this.getPacket().getIntegers().write(0, hologram.getId());
    }

    @Override
    protected void _beforeSend() {
        final Location location = this.getHologram().getLocation();
        this.getPacket().getDoubles().write(0, location.getX());
        this.getPacket().getDoubles().write(1, location.getY());
        this.getPacket().getDoubles().write(2, location.getZ());
    }
}
