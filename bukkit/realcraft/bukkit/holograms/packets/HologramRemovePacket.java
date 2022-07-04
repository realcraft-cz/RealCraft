package realcraft.bukkit.holograms.packets;

import com.comphenix.protocol.PacketType;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import realcraft.bukkit.holograms.Hologram;

public class HologramRemovePacket extends HologramPacket {

    public HologramRemovePacket(Hologram hologram) {
        super(hologram, PacketType.Play.Server.ENTITY_DESTROY);

        this.getPacket().getModifier().writeDefaults();
        this.getPacket().getIntLists().write(0, new IntArrayList(new int[]{hologram.getId()}));
    }
}
