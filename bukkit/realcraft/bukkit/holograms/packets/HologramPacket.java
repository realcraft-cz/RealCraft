package realcraft.bukkit.holograms.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;
import realcraft.bukkit.holograms.Hologram;

import java.lang.reflect.InvocationTargetException;

public abstract class HologramPacket {

    private final Hologram hologram;
    private final PacketContainer packet;

    public HologramPacket(Hologram hologram, PacketType packetType) {
        this.hologram = hologram;
        this.packet = new PacketContainer(packetType);
    }

    public final Hologram getHologram() {
        return hologram;
    }

    public final PacketContainer getPacket() {
        return packet;
    }

    public final void send(Player player) {
        this._beforeSend();

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, this.getPacket());
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    protected void _beforeSend() {
    }
}
