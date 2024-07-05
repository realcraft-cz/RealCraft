package realcraft.bukkit.holograms.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import realcraft.bukkit.holograms.Hologram;

public class HologramPostSpawnPacket extends HologramPacket {

    public HologramPostSpawnPacket(Hologram hologram) {
        super(hologram, PacketType.Play.Server.ENTITY_METADATA);

        this.getPacket().getModifier().writeDefaults();
        this.getPacket().getIntegers().write(0, hologram.getId());

        // https://github.com/dmulloy2/ProtocolLib/issues/2355

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();

        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x20); //invisible
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), false); //custom name
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class)), true); //no gravity
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x01 | 0x08 | 0x10));

        this.getPacket().getDataValueCollectionModifier().write(0, dataWatcher.toDataValueCollection());
    }
}
