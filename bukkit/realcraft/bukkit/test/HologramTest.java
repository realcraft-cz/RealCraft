package realcraft.bukkit.test;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import realcraft.bukkit.others.AbstractCommand;

import java.util.ArrayList;
import java.util.UUID;

public class HologramTest extends AbstractCommand implements Listener {

	public HologramTest() {
		super("holotest");
	}

	@Override
	public void perform(Player player,String[] args){
		Location location = player.getLocation();
		location.add(2, 2, 2);

		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

		try {
			PacketContainer spawnPacket = protocolManager.createPacket(com.comphenix.protocol.PacketType.Play.Server.SPAWN_ENTITY);

			int entityId = (int) (Math.random() * Integer.MAX_VALUE);
			spawnPacket.getIntegers()
				.write(0, entityId) // Entity ID
				.write(1, 1) // Type ID for ArmorStand
				.write(2, (int) (location.getX() * 32))
				.write(3, (int) (location.getY() * 32))
				.write(4, (int) (location.getZ() * 32));
			spawnPacket.getUUIDs().write(0, UUID.randomUUID()); // Unique ID

			protocolManager.sendServerPacket(player, spawnPacket);

			// Metadata Packet
			PacketContainer metadataPacket = protocolManager.createPacket(com.comphenix.protocol.PacketType.Play.Server.ENTITY_METADATA);

			metadataPacket.getIntegers().write(0, entityId); // Entity ID

			WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
			dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x20); // Invisible and small

			ArrayList<WrappedDataValue> values = Lists.newArrayList(
				new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), (byte) 0x20) // Invisible and small
			);

			metadataPacket.getDataValueCollectionModifier().write(0, dataWatcher.toDataValueCollection());
			// metadataPacket.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());

			protocolManager.sendServerPacket(player, metadataPacket);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

