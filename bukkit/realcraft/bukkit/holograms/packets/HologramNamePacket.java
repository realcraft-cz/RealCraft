package realcraft.bukkit.holograms.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import realcraft.bukkit.holograms.Hologram;

import java.util.Optional;

public class HologramNamePacket extends HologramPacket {

    public HologramNamePacket(Hologram hologram) {
        super(hologram, PacketType.Play.Server.ENTITY_METADATA);

        this.getPacket().getModifier().writeDefaults();
        this.getPacket().getIntegers().write(0, hologram.getId());
    }

    @Override
    protected void _beforeSend() {
        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();

        Optional<?> opt = Optional.of(WrappedChatComponent.fromChatMessage(this.getHologram().getText())[0].getHandle());
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), opt);
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), this.getHologram().getText() == null ? false : true);

        this.getPacket().getDataValueCollectionModifier().write(0, dataWatcher.toDataValueCollection());
    }
}
