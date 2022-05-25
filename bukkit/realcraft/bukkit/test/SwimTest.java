package realcraft.bukkit.test;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.others.AbstractCommand;

import java.lang.reflect.InvocationTargetException;

public class SwimTest extends AbstractCommand implements Listener {

    public SwimTest() {
        super("swim");
        Bukkit.getPluginManager().registerEvents(this, RealCraft.getInstance());
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.hasPermission("group.Manazer")) {
            return;
        }

        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

        packet.getModifier().writeDefaults();
        packet.getIntegers().write(0, player.getEntityId());

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(18, WrappedDataWatcher.Registry.get(Integer.class)), 7);
        packet.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void EntityToggleSwimEvent(EntityToggleSwimEvent event) {
        System.out.println(event.isSwimming());
        if (!event.isSwimming()) {
            //event.setCancelled(true);
        }
    }
}
