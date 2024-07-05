package realcraft.bukkit.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BlockUtil {

    public static void sendBlockDamage(Player player, Location location, int damage) {
        int identityId = (location.getBlockX() << 10 ^ location.getBlockY() << 5 ^ location.getBlockZ());

        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
        packet.getBlockPositionModifier().write(0, new BlockPosition(location.toVector()));
        packet.getIntegers().write(0, identityId).write(1, damage);

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }
}
