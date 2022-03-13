package realcraft.bukkit.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class BorderUtil {

    public static void setBorder(Player player, Location center, double size) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.WORLD_BORDER);

		packet.getWorldBorderActions().writeSafely(0, EnumWrappers.WorldBorderAction.INITIALIZE);
		packet.getWorldBorderActions().writeDefaults();
		packet.getDoubles().write(0, center.getX());
		packet.getDoubles().write(1, center.getZ());
		packet.getDoubles().write(3, size);
		packet.getIntegers().write(1, 0);

		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}