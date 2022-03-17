package realcraft.bukkit.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class BorderUtil {

    public static void setBorder(Player player, Location center, double size) {
		PacketContainer packetCenter = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SET_BORDER_CENTER);
		PacketContainer packetSize = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SET_BORDER_SIZE);

		packetCenter.getDoubles().write(0, center.getX());
		packetCenter.getDoubles().write(1, center.getZ());
		packetSize.getDoubles().write(0, size);

		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetCenter);
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetSize);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}