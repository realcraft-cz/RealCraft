package realcraft.bukkit.utils;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class BlockUtil {

    public static void sendBlockDamage(Player player, Location location, int damage) {
        int identityId = (location.getBlockX() << 10 ^ location.getBlockY() << 5 ^ location.getBlockZ());
        PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(identityId, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), damage);
        ((CraftPlayer)player).getHandle().b.a(packet);
    }
}
