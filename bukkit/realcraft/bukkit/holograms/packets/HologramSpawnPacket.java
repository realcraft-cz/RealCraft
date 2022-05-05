package realcraft.bukkit.holograms.packets;

import com.comphenix.protocol.PacketType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import realcraft.bukkit.holograms.Hologram;

import java.util.UUID;

public class HologramSpawnPacket extends HologramPacket {

    public HologramSpawnPacket(Hologram hologram) {
        super(hologram, PacketType.Play.Server.SPAWN_ENTITY);

        this.getPacket().getModifier().writeDefaults();
        this.getPacket().getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        this.getPacket().getIntegers().write(0, hologram.getId());
        this.getPacket().getUUIDs().write(0, UUID.randomUUID());
    }

    @Override
    protected void _beforeSend() {
        final Location location = this.getHologram().getLocation();
        this.getPacket().getDoubles().write(0, location.getX());
        this.getPacket().getDoubles().write(1, location.getY());
        this.getPacket().getDoubles().write(2, location.getZ());
    }
}
